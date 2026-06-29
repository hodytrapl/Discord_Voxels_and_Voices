package org.hodytrapl.discord_linker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import org.hodytrapl.discord_linker.config.general.MainConfig;
import org.slf4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Менеджер локализации для загрузки и получения сообщений на разных языках.
 * <p>
 * Загружает JSON-файлы с переводами из ресурсов мода и предоставляет
 * методы для получения форматированных сообщений по ключу.
 * </p>
 */
public class LanguageManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new Gson();
    private static final Map<String, String> messages = new HashMap<>();
    private static String currentLang = null;

    /**
     * Перезагружает языковые файлы на основе текущей настройки в конфиге.
     * <p>
     * Если язык изменился или сообщения ещё не загружены, загружает
     * соответствующий JSON-файл. В случае отсутствия запрошенного языка
     * использует английский (en_us) как запасной.
     * </p>
     */
    public static void reload() {
        if (MainConfig.INSTANCE == null) {
            LOGGER.warn("MainConfig not initialized yet, skipping language reload");
            return;
        }
        String lang = MainConfig.INSTANCE.langSelect.get(); // например "ru_ru"
        if (lang.equals(currentLang) && !messages.isEmpty()) {
            return; // уже загружено
        }
        messages.clear();
        currentLang = lang;

        // Путь внутри JAR: assets/discord_linker/lang/ru_ru.json
        String path = "/assets/discord_linker/lang/" + lang + ".json";
        try (InputStream is = LanguageManager.class.getResourceAsStream(path)) {
            if (is == null) {
                // fallback на en_us, если файл не найден
                try (InputStream fallback = LanguageManager.class.getResourceAsStream("/assets/discord_linker/lang/en_us.json")) {
                    if (fallback == null) {
                        throw new RuntimeException("No language files found!");
                    }
                    JsonObject obj = GSON.fromJson(new InputStreamReader(fallback, StandardCharsets.UTF_8), JsonObject.class);
                    obj.entrySet().forEach(e -> messages.put(e.getKey(), e.getValue().getAsString()));
                }
            } else {
                JsonObject obj = GSON.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), JsonObject.class);
                obj.entrySet().forEach(e -> messages.put(e.getKey(), e.getValue().getAsString()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Возвращает локализованное сообщение по ключу с подстановкой аргументов.
     * <p>
     * Если сообщение для данного ключа не найдено, возвращает строку вида
     * {@code !!ключ!!}. Поддерживает форматирование через {@link MessageFormat}.
     * </p>
     *
     * @param key ключ сообщения
     * @param args аргументы для подстановки в шаблон
     * @return отформатированное сообщение
     */
    public static String getMessage(String key, Object... args) {
        if (messages.isEmpty()) {
            reload(); // на всякий случай
        }
        String pattern = messages.get(key);
        if (pattern == null) {
            return "!!" + key + "!!"; // или вернуть сам ключ
        }
        return MessageFormat.format(pattern, args);
    }
}