package org.hodytrapl.discord_linker.utils.config;

import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;

import java.util.*;

import static org.hodytrapl.discord_linker.utils.Utils.formatPlaceholder;



public class EventsConfigHelper {


    public static Boolean isEventEnabled(EventEntryConfig event) {
        return event.eventEnable.get();
    }
    public static Boolean isEmbedEnable(EventEntryConfig event) {
        return event.embedEnable.get();
    }
    // ==================== Сырые (raw) геттеры – без подстановки ====================

    public static String getRawEventMessage(EventEntryConfig event) {
        return event.eventMessage.get();
    }

    public static String getRawEmbedAuthorName(EventEntryConfig event) {
        return event.embedAuthorName.get();
    }

    public static String getRawEmbedAuthorIcon(EventEntryConfig event) {
        return event.embedAuthorIcon.get();
    }

    public static String getRawEmbedTitle(EventEntryConfig event) {
        return event.embedTitle.get();
    }

    public static String getRawEmbedDescription(EventEntryConfig event) {
        return event.embedDescription.get();
    }

    public static String getRawEmbedColor(EventEntryConfig event) {
        return event.embedColor.get();
    }

    public static String getRawEmbedThumbnail(EventEntryConfig event) {
        return event.embedThumbnail.get();
    }

    public static String getRawEmbedImage(EventEntryConfig event) {
        return event.embedImage.get();
    }

    public static String getRawEmbedFooterIcon(EventEntryConfig event) {
        return event.embedFooterIcon.get();
    }

    public static String getRawEmbedFooter(EventEntryConfig event) {
        return event.embedFooter.get();
    }

    public static boolean isEmbedTimestampEnabled(EventEntryConfig event) {
        return event.embedOnTimestamp.get();
    }

    public static List<? extends String> getRawEmbedFields(EventEntryConfig event) {
        return event.embedFields.get();
    }

    // ==================== Форматированные (formatted) геттеры – с подстановкой ====================


    public static String getFormattedEventMessage(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.eventMessage.get(), placeholders);
    }

    public static String getFormattedEventMessage(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.eventMessage.get(), pairs);
    }

    public static String getFormattedEmbedAuthorName(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedAuthorName.get(), placeholders);
    }

    public static String getFormattedEmbedAuthorName(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedAuthorName.get(), pairs);
    }

    public static String getFormattedEmbedAuthorIcon(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedAuthorIcon.get(), placeholders);
    }

    public static String getFormattedEmbedAuthorIcon(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedAuthorIcon.get(), pairs);
    }

    public static String getFormattedEmbedTitle(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedTitle.get(), placeholders);
    }

    public static String getFormattedEmbedTitle(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedTitle.get(), pairs);
    }

    public static String getFormattedEmbedDescription(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedDescription.get(), placeholders);
    }

    public static String getFormattedEmbedDescription(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedDescription.get(), pairs);
    }

    public static String getFormattedEmbedColor(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedColor.get(), placeholders);
    }

    public static String getFormattedEmbedColor(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedColor.get(), pairs);
    }

    public static String getFormattedEmbedThumbnail(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedThumbnail.get(), placeholders);
    }

    public static String getFormattedEmbedThumbnail(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedThumbnail.get(), pairs);
    }

    public static String getFormattedEmbedImage(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedImage.get(), placeholders);
    }

    public static String getFormattedEmbedImage(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedImage.get(), pairs);
    }

    public static String getFormattedEmbedFooterIcon(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedFooterIcon.get(), placeholders);
    }

    public static String getFormattedEmbedFooterIcon(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedFooterIcon.get(), pairs);
    }

    public static String getFormattedEmbedFooter(EventEntryConfig event, Map<String, String> placeholders) {
        return formatPlaceholder(event.embedFooter.get(), placeholders);
    }

    public static String getFormattedEmbedFooter(EventEntryConfig event, String... pairs) {
        return formatPlaceholder(event.embedFooter.get(), pairs);
    }

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

    // Также можно добавить вариант с varargs для полей, но это редко нужно, так как поля обычно сложнее.
}