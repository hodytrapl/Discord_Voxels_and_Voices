package org.hodytrapl.discord_linker.utils;

import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;

/**
 * Общие утилитные методы для работы со строками, форматированием и плейсхолдерами.
 */
public class Utils {

    /**
     * Очищает строку от цветовых кодов форматирования Minecraft.
     * <p>
     * Заменяет символы {@code &} на {@code §}, удаляет все коды форматирования
     * и возвращает чистый текст без форматирования.
     * </p>
     *
     * @param text исходная строка с возможными цветовыми кодами
     * @return строка без форматирования
     */
    public static String cleanFormatting(String text) {
        if (text == null || text.isEmpty()) return "";

        // 1. Приводим все & к § для единообразной обработки
        String withSection = text.replace('&', '§');

        // 2. Удаляем все коды форматирования (§ + следующий символ)
        String stripped = ChatFormatting.stripFormatting(withSection);

        // 3. Если после удаления остались символы §, значит они не были частью кодов
        //    (например, обычный текст), и их нужно вернуть в & (так как изначально они были &)
        return stripped.replace('§', '&');
    }

    /**
     * Заменяет плейсхолдеры вида {@code %ключ%} на соответствующие значения из карты.
     *
     * @param text строка с плейсхолдерами
     * @param placeholders карта соответствий ключ -> значение
     * @return строка с заменёнными плейсхолдерами
     */
    public static String formatPlaceholder(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty()) return text;
        if (placeholders == null || placeholders.isEmpty()) return text;

        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if (key != null && value != null) {
                text = text.replace("%" + key + "%", value);
            }
        }
        return text;
    }

    /**
     * Заменяет плейсхолдеры, используя массив чередующихся ключей и значений.
     *
     * @param text строка с плейсхолдерами
     * @param pairs массив вида {ключ1, значение1, ключ2, значение2, ...}
     * @return строка с заменёнными плейсхолдерами
     * @throws IllegalArgumentException если массив имеет нечётную длину
     */
    public static String formatPlaceholder(String text, String... pairs) {
        if (pairs == null || pairs.length % 2 != 0) {
            throw new IllegalArgumentException(getMessage("mod.typelogger.utils.error.placeholder.arg"));
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return formatPlaceholder(text, map);
    }

    /**
     * Упрощённая версия для быстрой подстановки имени пользователя.
     * <p>
     * Также автоматически добавляет плейсхолдер {@code headplayer} с ссылкой
     * на аватар через Minotar.
     * </p>
     *
     * @param text строка с плейсхолдерами
     * @param username имя пользователя для подстановки в {@code %username%}
     * @return строка с заменёнными плейсхолдерами
     */
    public static String formatPlaceholder(String text, String username) {
        if (text == null || text.isEmpty()) return text;
        if (username == null || username.isEmpty()) return text;
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("headplayer", "https://minotar.net/avatar/" + username + "/64");
        return formatPlaceholder(text, map);
    }
}