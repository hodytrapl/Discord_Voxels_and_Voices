package org.hodytrapl.discord_linker.config.events;

import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация отдельного события для Discord Linker.
 * <p>
 * Этот класс содержит настройки для конкретного события, включая
 * текстовое сообщение, настройки embed-сообщений и все связанные
 * с ними поля.
 * </p>
 */
public class EventEntryConfig {
    // Основные настройки события
    public final ModConfigSpec.BooleanValue eventEnable;
    public final ModConfigSpec.ConfigValue<String> eventMessage;
    public ModConfigSpec.BooleanValue embedEnable;

    // Автор embed
    public ModConfigSpec.ConfigValue<String> embedAuthorName;
    public ModConfigSpec.ConfigValue<String> embedAuthorIcon;

    // Основные поля embed
    public ModConfigSpec.ConfigValue<String> embedTitle;
    public ModConfigSpec.ConfigValue<String> embedDescription;
    public ModConfigSpec.ConfigValue<String> embedColor;
    public ModConfigSpec.ConfigValue<String> embedThumbnail;
    public ModConfigSpec.ConfigValue<String> embedImage;
    public ModConfigSpec.ConfigValue<String> embedFooterIcon;
    public ModConfigSpec.ConfigValue<String> embedFooter;
    public ModConfigSpec.BooleanValue embedOnTimestamp;

    // Динамические поля
    public  ModConfigSpec.ConfigValue<List<? extends String>> embedFields;

    /**
     * Конструктор конфигурации события.
     *
     * @param builder построитель конфигурации NeoForge
     * @param eventName имя события для группировки настроек
     * @param payload карта с значениями по умолчанию для всех полей
     * @param embedWorking флаг, указывающий, поддерживает ли событие embed-сообщения
     */
    @SuppressWarnings("deprecation")
    public EventEntryConfig(ModConfigSpec.Builder builder, String eventName, Map<String, String> payload,Boolean embedWorking) {
        builder.comment("Configuration for " + eventName).push(eventName);

        // Включение события – из payload, по умолчанию true
        boolean defaultEnable = Boolean.parseBoolean(payload.getOrDefault("event_enable", "true"));
        eventEnable = builder
                .comment("Enable/disable this event")
                .define("event_enable", defaultEnable);

        // Текстовое сообщение – из payload, по умолчанию пустая строка
        String defaultMessage = payload.getOrDefault("message", "");
        eventMessage = builder
                .comment("Plain text message (supports %username% placeholder)")
                .define("message", defaultMessage);
        if(!embedWorking){
            builder.pop(); // завершаем событие
            return;
        }

        // Включение embed – из payload, по умолчанию false
        boolean defaultEmbedEnable = Boolean.parseBoolean(payload.getOrDefault("embed_enable", "false"));
        embedEnable = builder
                .comment("Send an embed message to Discord instead of plain text")
                .define("embed_enable", defaultEmbedEnable);
        // --- Настройки embed (используются только если embed_enable = true) ---
        builder.comment("Embed appearance (Discord rich embed)").push("embed");

        // Все строковые поля с дефолтами из payload или пустой строкой
        embedAuthorName = builder
                .comment("Author name displayed at the top of embed")
                .define("author_name", payload.getOrDefault("embed_author_name", ""));

        embedAuthorIcon = builder
                .comment("Author icon URL (optional)")
                .define("author_icon_url", payload.getOrDefault("embed_author_icon_url", ""));

        embedTitle = builder
                .comment("Embed title (supports %username%)")
                .define("title", payload.getOrDefault("embed_title", defaultMessage)); // если нет, берём из обычного message

        embedDescription = builder
                .comment("Main text of the embed")
                .define("description", payload.getOrDefault("embed_description", defaultMessage));

        embedThumbnail = builder
                .comment("Thumbnail image URL (small image, top-right corner)")
                .define("thumbnail", payload.getOrDefault("embed_thumbnail", ""));

        // Цвет – строка, проверка HEX формата может быть добавлена отдельно
        embedColor = builder
                .comment("Color of the embed left border (HEX format, e.g. #ffff00)")
                .define("color", payload.getOrDefault("embed_color", "#ffff00"));

        embedImage = builder
                .comment("Large image inside the embed")
                .define("image", payload.getOrDefault("embed_image", ""));

        embedFooterIcon = builder
                .comment("Footer icon URL")
                .define("footer_icon", payload.getOrDefault("embed_footer_icon", ""));

        embedFooter = builder
                .comment("Footer text")
                .define("footer", payload.getOrDefault("embed_footer", "Powered by Discord Linker"));

        // Показывать ли timestamp – из payload, по умолчанию true
        boolean defaultTimestamp = Boolean.parseBoolean(payload.getOrDefault("embed_timestamp_enable", "true"));
        embedOnTimestamp = builder
                .comment("Show current timestamp in embed footer")
                .define("timestamp_enable", defaultTimestamp);


        List<String> defaultFields = new ArrayList<>();
        String fieldsStr = payload.get("embed_fields");
        if (fieldsStr != null && !fieldsStr.isEmpty()) {
            defaultFields = Arrays.asList(fieldsStr.split(";"));
        }
        embedFields = builder
                .comment("Additional embed fields. Each string format: \"name|value|inline\"")
                .defineListAllowEmpty("fields", defaultFields,
                        obj -> obj instanceof String && ((String) obj).split("\\|", 3).length == 3);

        builder.pop(); // завершаем embed
        builder.pop(); // завершаем событие
    }

    /**
     * Вспомогательный класс для представления поля embed-сообщения.
     */
    public static class EmbedField {
        public final String name;
        public final String value;
        public final boolean inline;
        public EmbedField(String name, String value, boolean inline) {
            this.name = name;
            this.value = value;
            this.inline = inline;
        }
    }
}
