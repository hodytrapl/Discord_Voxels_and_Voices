package org.hodytrapl.discord_linker.discord.commands;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;

/**
 * Исполнитель Minecraft команд из Discord.
 * <p>
 * Этот класс выполняет команды Minecraft и захватывает их вывод для отправки
 * обратно в Discord. Использует собственный источник команд для перехвата
 * вывода команд.
 * </p>
 */
public class MinecraftCommandExecutor {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Выполняет команду Minecraft и возвращает её вывод.
     * <p>
     * Метод создает специальный источник команд, который перехватывает
     * все сообщения, отправляемые командой, и возвращает их как строку.
     * </p>
     *
     * @param command команда для выполнения (с или без префикса "/")
     * @return строка с выводом команды или сообщение об ошибке
     */
    public static String executeCommandWithOutput(String command) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            LOGGER.error(getMessage("mod.typelogger.discord.commands.serverunavailable", command));
            return getMessage("mod.typediscord.logger.error.serverunavailable");
        }

        if (command == null || command.isBlank()) {
            LOGGER.warn(getMessage("mod.typelogger.logger.error.emptycommand"));
            return "Error: " + getMessage("mod.typediscord.logger.error.emptycommand");
        }

        String normalizedCommand = command.startsWith("/") ? command : "/" + command;
        StringBuilder outputBuffer = new StringBuilder();

        CommandSource capturingSource = new CommandSource() {
            @Override
            public void sendSystemMessage(Component message) {
                String text = message.getString();
                if (text != null && !text.isEmpty()) {
                    if (!outputBuffer.isEmpty()) {
                        outputBuffer.append('\n');
                    }
                    outputBuffer.append(text);
                }
            }

            @Override
            public boolean acceptsSuccess() {
                return true;
            }

            @Override
            public boolean acceptsFailure() {
                return true;
            }

            @Override
            public boolean shouldInformAdmins() {
                return false;
            }
        };

        CommandSourceStack original = server.createCommandSourceStack();
        CommandSourceStack wrappedStack = new CommandSourceStack(
                capturingSource,
                original.getPosition(),
                original.getRotation(),
                original.getLevel(),
                4,
                original.getTextName(),
                original.getDisplayName(),
                server,
                original.getEntity()
        );

        try {
            server.getCommands().performPrefixedCommand(wrappedStack, normalizedCommand);
        } catch (Exception e) {
            LOGGER.error(getMessage("mod.typelogger.logger.error.runtime", normalizedCommand, e.getMessage()), e);
            return getMessage("mod.typediscord.logger.error.errortext", e.getMessage());
        }

        String result = outputBuffer.toString();
        return result.isEmpty() ? getMessage("mod.typediscord.discord.command.success.emptyresult") : result;
    }
}