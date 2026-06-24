package org.hodytrapl.discord_linker.discord.commands;

import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.slf4j.Logger;

public class MinecraftCommandExecutor {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static String executeCommandWithOutput(String command) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            LOGGER.error("Сервер недоступен для команды: {}", command);
            return "Ошибка: сервер не доступен";
        }

        if (command == null || command.isBlank()) {
            LOGGER.warn("Пустая команда");
            return "Ошибка: команда пуста";
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
            LOGGER.error("Ошибка выполнения {}: {}", normalizedCommand, e.getMessage(), e);
            return "Ошибка: " + e.getMessage();
        }

        String result = outputBuffer.toString();
        return result.isEmpty() ? "Команда выполнена, но вывода нет." : result;
    }
}
