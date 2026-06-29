package org.hodytrapl.discord_linker.utils;

/**
 * Утилитарный класс для валидации идентификаторов (ID).
 */
public class ValidationUtils {
    private ValidationUtils() {} // статический класс, запрещаем создание экземпляров

    /**
     * Проверяет, является ли строка корректным ID.
     * <p>
     * Корректным считается ID, который не null, не пуст, не равен
     * "0000000000000000000" (заглушка) и не равен "BOT_TOKEN_HERE".
     * </p>
     *
     * @param id строка для проверки
     * @return true если ID валидный, иначе false
     */
    public static boolean isValidId(String id) {
        return id != null && !id.isEmpty() && !id.equals("0000000000000000000") && !id.equals("BOT_TOKEN_HERE");
    }
}