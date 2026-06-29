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

/**
 * Генератор embed-сообщений для Discord.
 * <p>
 * Этот класс создает форматированные embed-сообщения на основе конфигурации
 * событий и переданных плейсхолдеров.
 * </p>
 */
public class GeneratorEmbedMessage extends ListenerAdapter {

    /**
     * Создает embed-сообщение на основе конфигурации события и плейсхолдеров.
     *
     * @param event конфигурация события
     * @param placeholders карта плейсхолдеров для подстановки в текст
     * @return готовый MessageEmbed для отправки в Discord
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
     * Создает embed-сообщение с подстановкой имени пользователя.
     * <p>
     * Удобный метод для быстрого создания embed с плейсхолдером username.
     * </p>
     *
     * @param event конфигурация события
     * @param username имя пользователя для подстановки в плейсхолдеры
     * @return готовый MessageEmbed для отправки в Discord
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