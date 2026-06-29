package org.hodytrapl.discord_linker.discord.enums;

/**
 * Перечисление типов сообщений, отправляемых в Discord.
 * <p>
 * Определяет способ форматирования сообщения в зависимости от его назначения.
 * </p>
 */
public enum DiscordMessageType {
    /** Ответ на команду пользователя – оборачивается в блок кода */
    COMMAND_RESPONSE,
    /** Обычное чат-сообщение (например, от игрока Minecraft) */
    CHAT_MESSAGE,
    /** Системное уведомление/предупреждение/ошибка – с префиксом [System] */
    SYSTEM_NOTIFICATION
}