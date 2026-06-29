package org.hodytrapl.discord_linker.utils.config;

import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;

import java.util.*;

import static org.hodytrapl.discord_linker.utils.Utils.formatPlaceholder;

/**
 * Утилитарный класс для работы с конфигурацией событий.
 * <p>
 * Предоставляет методы для получения сырых и отформатированных
 * (с подстановкой плейсхолдеров) значений из конфигурации событий.
 * </p>
 */
public class EventsConfigHelper {

    // ------------------- Основные настройки события -------------------

    /**
     * Проверяет, включено ли событие.
     *
     * @param event конфигурация события
     * @return true если включено, иначе false
     */
    public static Boolean isEventEnabled(EventEntryConfig event) {
        return event.eventEnable.get();
    }

    /**
     * Проверяет, включена ли отправка embed-сообщений для события.
     *
     * @param event конфигурация события
     * @return true если embed включён, иначе false
     */
    public static Boolean isEmbedEnable(EventEntryConfig event) {
        return event.embedEnable.get();
    }

    // ==================== Сырые (raw) геттеры – без подстановки ====================

    /**
     * Возвращает сырое текстовое сообщение события (без подстановки плейсхолдеров).
     *
     * @param event конфигурация события
     * @return строка сообщения
     */
    public static String getRawEventMessage(EventEntryConfig event) {
        return event.eventMessage.get();
    }

    /**
     * Возвращает сырое имя автора embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка имени автора
     */
    public static String getRawEmbedAuthorName(EventEntryConfig event) {
        return event.embedAuthorName.get();
    }

    /**
     * Возвращает сырую URL иконки автора embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка URL
     */
    public static String getRawEmbedAuthorIcon(EventEntryConfig event) {
        return event.embedAuthorIcon.get();
    }

    /**
     * Возвращает сырой заголовок embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка заголовка
     */
    public static String getRawEmbedTitle(EventEntryConfig event) {
        return event.embedTitle.get();
    }

    /**
     * Возвращает сырое описание embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка описания
     */
    public static String getRawEmbedDescription(EventEntryConfig event) {
        return event.embedDescription.get();
    }

    /**
     * Возвращает сырой цвет embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка цвета (HEX)
     */
    public static String getRawEmbedColor(EventEntryConfig event) {
        return event.embedColor.get();
    }

    /**
     * Возвращает сырую URL миниатюры embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка URL
     */
    public static String getRawEmbedThumbnail(EventEntryConfig event) {
        return event.embedThumbnail.get();
    }

    /**
     * Возвращает сырую URL основного изображения embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка URL
     */
    public static String getRawEmbedImage(EventEntryConfig event) {
        return event.embedImage.get();
    }

    /**
     * Возвращает сырую URL иконки футера embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка URL
     */
    public static String getRawEmbedFooterIcon(EventEntryConfig event) {
        return event.embedFooterIcon.get();
    }

    /**
     * Возвращает сырой текст футера embed (без подстановки).
     *
     * @param event конфигурация события
     * @return строка текста футера
     */
    public static String getRawEmbedFooter(EventEntryConfig event) {
        return event.embedFooter.get();
    }

    /**
     * Проверяет, включена ли метка времени в embed.
     *
     * @param event конфигурация события
     * @return true если включена, иначе false
     */
    public static boolean isEmbedTimestampEnabled(EventEntryConfig event) {
        return event.embedOnTimestamp.get();
    }

    /**
     * Возвращает сырые поля embed (без подстановки).
     *
     * @param event конфигурация события
     * @return список строк формата "name|value|inline"
     */
    public static List<? extends String> getRawEmbedFields(EventEntryConfig event) {
        return event.embedFields.get();
    }

    // ==================== Форматированные (formatted) геттеры – с подстановкой ====================

    /**
     * Возвращает отформатированное текстовое сообщение события с подстановкой плейсхолдеров.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров (ключ -> значение)
     * @return отформатированная строка
     */
    public static String getFormattedEventMessage(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.eventMessage.get(), placeholders);
    }

    /**
     * Возвращает отформатированное текстовое сообщение события с подстановкой пар ключ-значение.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEventMessage(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.eventMessage.get(), pairs);
    }

    /**
     * Возвращает отформатированное имя автора embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedAuthorName(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedAuthorName.get(), placeholders);
    }

    /**
     * Возвращает отформатированное имя автора embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedAuthorName(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedAuthorName.get(), pairs);
    }

    /**
     * Возвращает отформатированную иконку автора embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedAuthorIcon(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedAuthorIcon.get(), placeholders);
    }

    /**
     * Возвращает отформатированную иконку автора embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedAuthorIcon(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedAuthorIcon.get(), pairs);
    }

    /**
     * Возвращает отформатированный заголовок embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedTitle(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedTitle.get(), placeholders);
    }

    /**
     * Возвращает отформатированный заголовок embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedTitle(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedTitle.get(), pairs);
    }

    /**
     * Возвращает отформатированное описание embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedDescription(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedDescription.get(), placeholders);
    }

    /**
     * Возвращает отформатированное описание embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedDescription(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedDescription.get(), pairs);
    }

    /**
     * Возвращает отформатированный цвет embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedColor(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedColor.get(), placeholders);
    }

    /**
     * Возвращает отформатированный цвет embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedColor(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedColor.get(), pairs);
    }

    /**
     * Возвращает отформатированную миниатюру embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedThumbnail(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedThumbnail.get(), placeholders);
    }

    /**
     * Возвращает отформатированную миниатюру embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedThumbnail(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedThumbnail.get(), pairs);
    }

    /**
     * Возвращает отформатированное основное изображение embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedImage(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedImage.get(), placeholders);
    }

    /**
     * Возвращает отформатированное основное изображение embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedImage(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedImage.get(), pairs);
    }

    /**
     * Возвращает отформатированную иконку футера embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedFooterIcon(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedFooterIcon.get(), placeholders);
    }

    /**
     * Возвращает отформатированную иконку футера embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedFooterIcon(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedFooterIcon.get(), pairs);
    }

    /**
     * Возвращает отформатированный текст футера embed.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return отформатированная строка
     */
    public static String getFormattedEmbedFooter(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedFooter.get(), placeholders);
    }

    /**
     * Возвращает отформатированный текст футера embed.
     *
     * @param event конфигурация события
     * @param pairs массив чередующихся ключей и значений
     * @return отформатированная строка
     */
    public static String getFormattedEmbedFooter(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedFooter.get(), pairs);
    }

    /**
     * Возвращает список отформатированных полей embed.
     * <p>
     * Каждое поле парсится из строки формата "name|value|inline",
     * после чего производится подстановка плейсхолдеров в name и value.
     * </p>
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров
     * @return список объектов {@link EventEntryConfig.EmbedField}
     */
    public static List<EventEntryConfig.EmbedField> getFormattedEmbedFields(
            EventEntryConfig event,
            Map<String, String> placeholders) {
        List<? extends String> rawFields = event.embedFields.get();
        if (rawFields == null || rawFields.isEmpty()) {
            return Collections.emptyList();
        }
        List<EventEntryConfig.EmbedField> result = new ArrayList<>();
        for (String raw : rawFields) {
            String[] parts = raw.split("\\|", 3);
            if (parts.length == 3) {
                String name = formatPlaceholder(parts[0], placeholders);
                String value = formatPlaceholder(parts[1], placeholders);
                boolean inline = Boolean.parseBoolean(parts[2].trim());
                result.add(new EventEntryConfig.EmbedField(name, value, inline));
            }
        }
        return result;
    }
}