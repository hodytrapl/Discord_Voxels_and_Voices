package org.hodytrapl.discord_linker.utils.config;

import org.hodytrapl.discord_linker.config.general.MainConfig;
import org.hodytrapl.discord_linker.config.general.MainEntryConfig;

import java.util.Map;

import static org.hodytrapl.discord_linker.utils.Utils.formatPlaceholder;

/**
 * Утилитарный класс для работы с основной конфигурацией мода.
 * <p>
 * Предоставляет методы для получения параметров из {@link MainConfig},
 * включая настройки бота, каналов, присутствия и событий обновления
 * названий голосовых каналов.
 * </p>
 */
public class MainConfigHelper {

    // ------------------- Основные настройки -------------------

    /**
     * Возвращает выбранный язык (код локали).
     *
     * @return строка кода языка (например, "en_us" или "ru_ru")
     */
    public static String getLang() {
        return MainConfig.INSTANCE.langSelect.get();
    }

    /**
     * Проверяет, включён ли Discord бот.
     *
     * @return true если бот включён, иначе false
     */
    public static boolean isBotEnabled() {
        return MainConfig.INSTANCE.enableBot.get();
    }

    /**
     * Возвращает токен Discord бота.
     *
     * @return строка токена
     */
    public static String getBotToken() {
        return MainConfig.INSTANCE.botToken.get();
    }

    /**
     * Возвращает сырой ID основного канала Discord.
     *
     * @return ID канала в виде строки
     */
    public static String getRawChannelId() {
        return MainConfig.INSTANCE.channelID.get();
    }

    /**
     * Возвращает сырой ID канала для событий.
     *
     * @return ID канала в виде строки
     */
    public static String getRawEventsId() {
        return MainConfig.INSTANCE.eventsID.get();
    }

    /**
     * Возвращает сырой ID канала для команд.
     *
     * @return ID канала в виде строки
     */
    public static String getRawCommandsId() {
        return MainConfig.INSTANCE.commandsID.get();
    }

    /**
     * Проверяет, включено ли Discord Rich Presence.
     *
     * @return true если включено, иначе false
     */
    public static boolean isPresenceEnabled() {
        return MainConfig.INSTANCE.enablePresence.get();
    }

    /**
     * Возвращает сообщение для Discord Presence.
     *
     * @return строка сообщения
     */
    public static String getPresenceMessage() {
        return MainConfig.INSTANCE.presenceMessage.get();
    }

    // ------------------- Настройки событий обновления каналов -------------------

    /**
     * Проверяет, включено ли событие обновления канала.
     *
     * @param event конфигурация события (MainEntryConfig)
     * @return true если включено, иначе false
     */
    public static Boolean getEnableField(MainEntryConfig event) {
        return event.enable.get();
    }

    /**
     * Возвращает ID канала для события.
     *
     * @param event конфигурация события
     * @return ID канала в виде строки
     */
    public static String getChannelIdField(MainEntryConfig event) {
        return event.channelId.get();
    }

    /**
     * Возвращает интервал обновления для события (в секундах).
     *
     * @param event конфигурация события
     * @return интервал в секундах, -1 для разового обновления, 0 для отключения
     */
    public static Integer getUpdateIntervalField(MainEntryConfig event) {
        return event.updateInterval.get();
    }

    // ==================== Сырые (raw) геттеры – без подстановки ====================

    /**
     * Возвращает сырой шаблон имени канала (без подстановки плейсхолдеров).
     *
     * @param event конфигурация события
     * @return строка шаблона
     */
    public static String getRawNameChannel(MainEntryConfig event) {
        return event.name_channel.get();
    }

    // ==================== Форматированные (formatted) геттеры – с подстановкой ====================

    /**
     * Возвращает отформатированное имя канала с подстановкой плейсхолдеров.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedNameChannelField(MainEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.name_channel.get(), placeholders);
    }

    /**
     * Возвращает отформатированное имя канала с подстановкой пар ключ-значение.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedNameChannelField(MainEntryConfig event, String... pairs) {
        return formatPlaceholder(event.name_channel.get(), pairs);
    }
}