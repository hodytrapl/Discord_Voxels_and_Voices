package org.hodytrapl.discord_linker.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.utils.config.EventsConfigHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GeneratorEmbedMessage extends ListenerAdapter {

    /**
     * Основной метод сборки embed с произвольным набором плейсхолдеров.
     * @param event конфиг события
     * @param placeholders карта замен (ключ – без процентов, значение – подставляемый текст)
     * @return готовый MessageEmbed
     */
    public static MessageEmbed buildEmbed(EventEntryConfig event, Map<String, String> placeholders) {
        // Если карта null, используем пустую (чтобы избежать NPE)
        if (placeholders == null) {
            placeholders = new HashMap<>();
        }

        EmbedBuilder builder = new EmbedBuilder();

        // Получаем все значения из конфига с автоматической подстановкой плейсхолдеров
        String title = EventsConfigHelper.getFormattedEmbedTitle(event, placeholders);
        String description = EventsConfigHelper.getFormattedEmbedDescription(event, placeholders);
        String authorName = EventsConfigHelper.getFormattedEmbedAuthorName(event, placeholders);
        String authorIcon = EventsConfigHelper.getFormattedEmbedAuthorIcon(event, placeholders);
        String thumbnail = EventsConfigHelper.getFormattedEmbedThumbnail(event, placeholders);
        String image = EventsConfigHelper.getFormattedEmbedImage(event, placeholders);
        String footer = EventsConfigHelper.getFormattedEmbedFooter(event, placeholders);
        String footerIcon = EventsConfigHelper.getFormattedEmbedFooterIcon(event, placeholders);
        String colorStr = EventsConfigHelper.getFormattedEmbedColor(event, placeholders);
        boolean timestamp = EventsConfigHelper.isEmbedTimestampEnabled(event);
        List<EventEntryConfig.EmbedField> fields = EventsConfigHelper.getFormattedEmbedFields(event, placeholders);

        // Устанавливаем поля (все уже отформатированы)
        if (authorName != null && !authorName.isEmpty()) {
            builder.setAuthor(authorName, null, authorIcon != null && !authorIcon.isEmpty() ? authorIcon : null);
        }

        if (title != null && !title.isEmpty()) {
            builder.setTitle(title);
        }

        if (description != null && !description.isEmpty()) {
            builder.setDescription(description);
        }

        if (colorStr != null && !colorStr.isEmpty()) {
            try {
                builder.setColor(Color.decode(colorStr));
            } catch (NumberFormatException ignored) {
                // Невалидный цвет — игнорируем
            }
        }

        if (thumbnail != null && !thumbnail.isEmpty()) {
            builder.setThumbnail(thumbnail);
        }

        if (image != null && !image.isEmpty()) {
            builder.setImage(image);
        }

        if (footer != null && !footer.isEmpty()) {
            builder.setFooter(footer, footerIcon != null && !footerIcon.isEmpty() ? footerIcon : null);
        }

        if (timestamp) {
            builder.setTimestamp(java.time.Instant.now());
        }

        // Добавляем поля (уже отформатированные внутри хелпера)
        if (fields != null) {
            for (EventEntryConfig.EmbedField field : fields) {
                builder.addField(field.name, field.value, field.inline);
            }
        }

        return builder.build();
    }

    /**
     * Удобная перегрузка для случаев, когда нужен только %username%.
     * Создаёт карту с ключом "username" и значением, а также автоматически добавляет
     * "headplayer" для совместимости со старым поведением.
     */
    public static MessageEmbed buildEmbed(EventEntryConfig event, String username) {
        Map<String, String> placeholders = new HashMap<>();
        if (username != null) {
            placeholders.put("username", username);
            placeholders.put("headplayer", "https://minotar.net/avatar/" + username + "/64");
        }
        return buildEmbed(event, placeholders);
    }
}