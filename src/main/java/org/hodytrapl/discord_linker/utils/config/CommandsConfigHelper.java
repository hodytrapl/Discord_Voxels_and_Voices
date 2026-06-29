package org.hodytrapl.discord_linker.utils.config;

import org.hodytrapl.discord_linker.config.commands.CommandsConfig;
import org.hodytrapl.discord_linker.config.commands.CommandsEntryConfig;

import java.util.List;

/**
 * Утилитарный класс для работы с конфигурацией команд.
 * <p>
 * Предоставляет статические методы для получения параметров команд
 * из конфигурационного файла {@link CommandsConfig}.
 * </p>
 */
public class CommandsConfigHelper {

    // ------------------- Основные настройки -------------------

    /**
     * Возвращает префикс команд Minecraft.
     *
     * @return строка префикса (например, "/" или "!")
     */
    public static String getCommandPrefix() {
        return CommandsConfig.INSTANCE.commandPrefix.get();
    }

    /**
     * Возвращает ID роли Discord для управленческих команд.
     *
     * @return ID роли в виде строки
     */
    public static String getDiscordManagementUserRole() {
        return CommandsConfig.INSTANCE.discordManagementUserRole.get();
    }

    /**
     * Возвращает уровень разрешений для управленческих команд в Minecraft.
     *
     * @return уровень разрешений (0-4)
     */
    public static int getMinecraftManagementUserPermissionLevel() {
        return CommandsConfig.INSTANCE.minecraftManagementUserPermissionLevel.get();
    }

    /**
     * Возвращает список префиксов других ботов для избежания конфликтов.
     *
     * @return список префиксов
     */
    public static List<? extends String> getOtherBotsPrefixes() {
        return CommandsConfig.INSTANCE.otherBotsPrefixes.get();
    }

    // ------------------- Настройки отдельных команд -------------------

    /**
     * Проверяет, включена ли команда.
     *
     * @param event конфигурация команды
     * @return true если включена, иначе false
     */
    public static boolean isEventEnabled(CommandsEntryConfig event) {
        return event.enabled.get();
    }

    /**
     * Возвращает Minecraft-команду для выполнения.
     *
     * @param event конфигурация команды
     * @return строка команды Minecraft
     */
    public static String getEventMinecraftCommand(CommandsEntryConfig event) {
        return event.minecraftCommand.get();
    }

    /**
     * Возвращает Discord-команду (слэш-команду).
     *
     * @param event конфигурация команды
     * @return строка Discord команды
     */
    public static String getEventDiscordCommand(CommandsEntryConfig event) {
        return event.discordCommand.get();
    }

    /**
     * Проверяет, является ли команда управленческой (требует роль).
     *
     * @param event конфигурация команды
     * @return true если управленческая, иначе false
     */
    public static boolean getEventManagementCommand(CommandsEntryConfig event) {
        return event.managementCommand.get();
    }
}