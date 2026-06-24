package org.hodytrapl.discord_linker.discord.commands;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.commands.CommandsConfig;
import org.hodytrapl.discord_linker.config.commands.CommandsEntryConfig;
import org.hodytrapl.discord_linker.discord.DiscordBotManager;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.utils.config.CommandsConfigHelper;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

import static org.hodytrapl.discord_linker.utils.ValidationUtils.isValidId;

public class CommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String commandPrefix;
    private final java.util.List<? extends String> otherPrefixes;
    private final String allowedChannelId;

    public CommandListener() {
        this.commandPrefix = CommandsConfigHelper.getCommandPrefix();

        this.otherPrefixes = CommandsConfigHelper.getOtherBotsPrefixes();

        String commandsId = MainConfigHelper.getRawCommandsId();
        String channelId = MainConfigHelper.getRawChannelId();
        String resolvedId;

        if (isValidId(commandsId)) {
            resolvedId = commandsId;
            LOGGER.info("Using commands channel: {}", resolvedId);
        } else if (isValidId(channelId)) {
            resolvedId = channelId;
            LOGGER.info("commandsID not set, falling back to channelID: {}", resolvedId);
        } else {
            LOGGER.warn("Neither commandsID nor channelID is configured. Commands will be ignored.");
            resolvedId = "DISABLED";
        }

        this.allowedChannelId = resolvedId;
        if (allowedChannelId.equals("DISABLED")) {
            LOGGER.info("Discord commands are disabled because no channel ID is set.");
        } else {
            LOGGER.info("Commands will only be accepted in channel ID: {}", allowedChannelId);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // проверки того что бот ли это, включена ли она и прочее
        if (event.getAuthor().isBot()) return;
        if (allowedChannelId.equals("DISABLED")) return;
        if (!event.getChannel().getId().equals(allowedChannelId)) return;

        String rawMessage = event.getMessage().getContentRaw();

        // 1. Определяем, какой префикс использован
                String usedPrefix = null;
                if (rawMessage.startsWith(commandPrefix)) {
                    usedPrefix = commandPrefix;
                } else {
                    for (String other : otherPrefixes) {
                        if (rawMessage.startsWith(other)) {
                            usedPrefix = other;
                            break;
                        }
                    }
                }

        // Если ни один префикс не подошёл — выходим
                if (usedPrefix == null) return;

        // 2. Проверка на комбинацию: если использован основной префикс, и после него идёт другой префикс — игнорируем
                if (usedPrefix.equals(commandPrefix)) {
                    String rest = rawMessage.substring(commandPrefix.length());
                    for (String other : otherPrefixes) {
                        if (rest.startsWith(other)) {
                            return; // игнорируем сообщения вида "/!tps"
                        }
                    }
                }

        // 3. Удаляем использованный префикс и разбираем команду
                String withoutPrefix = rawMessage.substring(usedPrefix.length());
                String[] parts = withoutPrefix.split("\\s+");
                if (parts.length == 0) return;
                String command = parts[0].toLowerCase();


        // Определяем, какая команда запрошена, и проверяем её включённость
        CommandsEntryConfig targetConfig = null;
        String discordCommand1 = CommandsConfigHelper.getEventDiscordCommand(CommandsConfig.INSTANCE.TPSCommand);
        String discordCommand2 = CommandsConfigHelper.getEventDiscordCommand(CommandsConfig.INSTANCE.modListCommand);
        String discordCommand3 = CommandsConfigHelper.getEventDiscordCommand(CommandsConfig.INSTANCE.onlineListCommand);

        if (command.equals(discordCommand1)) {
            targetConfig = CommandsConfig.INSTANCE.TPSCommand;
        } else if (command.equals(discordCommand2)) {
            targetConfig = CommandsConfig.INSTANCE.modListCommand;
        } else if (command.equals(discordCommand3)) {
            targetConfig = CommandsConfig.INSTANCE.onlineListCommand;
        } else {
            targetConfig = null;
            return;
        }

        //проверка команда для админов и иявляется пользователь админом
        if(CommandsConfigHelper.getEventManagementCommand(targetConfig)){
            Member member = event.getMember();
            if (member == null) {
                LOGGER.warn("Member is null, cannot check role.");
                return;
            }

            String roleIdStr = CommandsConfigHelper.getDiscordManagementUserRole();
            long roleId;
            try {
                roleId = Long.parseLong(roleIdStr);
            } catch (NumberFormatException e) {
                LOGGER.error("Некорректный ID роли в конфиге: {}", roleIdStr);
                event.getChannel().sendMessage("Ошибка конфигурации: роль не задана корректно.").queue();
                return;
            }

            boolean hasRole = member.getRoles().stream()
                    .anyMatch(role -> role.getIdLong() == roleId);
            if(!hasRole) {
                event.getChannel().sendMessage("**Ошибка:** у вас недостаточно прав для выполнения этой команды.\n" +
                        "Требуется роль <@&" + roleId + ">.").queue();
                return;
            }
        }

        // Если дошли сюда – команда известна и включена, выполняем
        CommandsEntryConfig finalTargetConfig = targetConfig;
        event.getChannel().sendMessage("Выполняю команду...").queue(msg -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                msg.editMessage("Ошибка: сервер недоступен").queue();
                return;
            }

            server.execute(() -> {
                String mcCommand = CommandsConfigHelper.getEventMinecraftCommand(finalTargetConfig);
                String output = MinecraftCommandExecutor.executeCommandWithOutput(mcCommand);

                DiscordBotManager botManager = Discord_linker.getBotManager();
                botManager.sendMessage(event.getChannel().getId(), output, DiscordMessageType.COMMAND_RESPONSE);

                msg.delete().queue();
            });
        });
    }
}
