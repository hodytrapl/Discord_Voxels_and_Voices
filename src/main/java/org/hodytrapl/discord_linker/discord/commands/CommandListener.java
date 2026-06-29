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

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;
import static org.hodytrapl.discord_linker.utils.ValidationUtils.isValidId;

/**
 * Слушатель команд из Discord чата.
 * <p>
 * Этот класс обрабатывает сообщения из Discord, определяет, являются ли они командами,
 * и выполняет соответствующие действия в Minecraft. Поддерживает различные префиксы
 * команд и проверку прав доступа.
 * </p>
 */
public class CommandListener extends ListenerAdapter {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final String commandPrefix;
    private final java.util.List<? extends String> otherPrefixes;
    private final String allowedChannelId;

    /**
     * Конструктор слушателя команд.
     * <p>
     * Инициализирует префиксы команд и ID разрешенного канала из конфигурации.
     * Если канал для команд не задан, используется основной канал.
     * </p>
     */
    public CommandListener() {
        this.commandPrefix = CommandsConfigHelper.getCommandPrefix();
        this.otherPrefixes = CommandsConfigHelper.getOtherBotsPrefixes();

        String commandsId = MainConfigHelper.getRawCommandsId();
        String channelId = MainConfigHelper.getRawChannelId();
        String resolvedId;

        if (isValidId(commandsId)) {
            resolvedId = commandsId;
            LOGGER.info(getMessage("mod.typelogger.discord.commands.using.commandid", resolvedId));
        } else if (isValidId(channelId)) {
            resolvedId = channelId;
            LOGGER.info(getMessage("mod.typelogger.discord.commands.commandsID.notset", resolvedId));
        } else {
            LOGGER.warn(getMessage("mod.typelogger.discord.commands.notconfigured.commandid.channelid"));
            resolvedId = "DISABLED";
        }

        this.allowedChannelId = resolvedId;
        if (allowedChannelId.equals("DISABLED")) {
            LOGGER.info(getMessage("mod.typelogger.discord.commands.notconfigured.ignorecommand"));
        } else {
            LOGGER.info(getMessage("mod.typelogger.discord.commands.accept.channelid", allowedChannelId));
        }
    }


    /**
     * Обрабатывает полученное сообщение из Discord.
     * <p>
     * Проверяет, является ли сообщение командой, и если да - выполняет её.
     * Поддерживает проверку прав доступа для управленческих команд.
     * </p>
     *
     * @param event событие получения сообщения из Discord
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;
        if (allowedChannelId.equals("DISABLED")) return;
        if (!event.getChannel().getId().equals(allowedChannelId)) return;

        String rawMessage = event.getMessage().getContentRaw();

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

        if (usedPrefix == null) return;

        if (usedPrefix.equals(commandPrefix)) {
            String rest = rawMessage.substring(commandPrefix.length());
            for (String other : otherPrefixes) {
                if (rest.startsWith(other)) {
                    return;
                }
            }
        }

        String withoutPrefix = rawMessage.substring(usedPrefix.length());
        String[] parts = withoutPrefix.split("\\s+");
        if (parts.length == 0) return;
        String command = parts[0].toLowerCase();

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

        if (CommandsConfigHelper.getEventManagementCommand(targetConfig)) {
            Member member = event.getMember();
            if (member == null) {
                LOGGER.warn(getMessage("mod.typelogger.logger.error.membernull"));
                return;
            }

            String roleIdStr = CommandsConfigHelper.getDiscordManagementUserRole();
            long roleId;
            try {
                roleId = Long.parseLong(roleIdStr);
            } catch (NumberFormatException e) {
                LOGGER.error(getMessage("mod.typelogger.logger.error.invalid.idrole", roleIdStr));
                event.getChannel().sendMessage(getMessage("mod.typediscord.logger.error.corrupted.idrole")).queue();
                return;
            }

            boolean hasRole = member.getRoles().stream()
                    .anyMatch(role -> role.getIdLong() == roleId);
            if (!hasRole) {
                event.getChannel().sendMessage(
                        getMessage("mod.typediscord.logger.error.nohavepermissions") + "\n" +
                                getMessage("mod.typediscord.logger.needrole", roleId)
                ).queue();
                return;
            }
        }

        CommandsEntryConfig finalTargetConfig = targetConfig;
        event.getChannel().sendMessage(getMessage("mod.typediscord.discord.commands.sending.commands")).queue(msg -> {
            MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
            if (server == null) {
                msg.editMessage(getMessage("mod.typediscord.logger.error.serverunavailable")).queue();
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