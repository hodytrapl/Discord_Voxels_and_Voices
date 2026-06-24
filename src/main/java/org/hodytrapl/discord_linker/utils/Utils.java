package org.hodytrapl.discord_linker.utils;

import net.minecraft.ChatFormatting;

import java.util.HashMap;
import java.util.Map;


public class Utils {
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

    //по парам ключ-значение,ключ-значение
    public static String formatPlaceholder(String text, String... pairs) {
        if (pairs == null || pairs.length % 2 != 0) {
            throw new IllegalArgumentException("Pairs must be even number of arguments");
        }
        Map<String, String> map = new HashMap<>();
        for (int i = 0; i < pairs.length; i += 2) {
            map.put(pairs[i], pairs[i + 1]);
        }
        return formatPlaceholder(text, map);
    }

    // Перегрузка для совместимости со старым вызовом (только username)
    public static String formatPlaceholder(String text, String username) {
        if (text == null || text.isEmpty()) return text;
        if (username == null || username.isEmpty()) return text;
        Map<String, String> map = new HashMap<>();
        map.put("username", username);
        map.put("headplayer", "https://minotar.net/avatar/" + username + "/64");
        return formatPlaceholder(text, map);
    }
}
