package org.hodytrapl.discord_linker.config;

import net.neoforged.fml.loading.FMLPaths;
import org.hodytrapl.discord_linker.Discord_linker;

import java.nio.file.Path;

/**
 * Утилита для управления путями к файлам мода.
 * <p>
 * Этот класс предоставляет доступ к директории конфигурации мода
 * и обеспечивает её создание при необходимости.
 * </p>
 */
public class ModPaths {
    //храним гдето в своем файле, пример с плагинов
    private static final String MOD_DIR_NAME = Discord_linker.MODID;
    private static Path configDir = null;

    /**
     * Возвращает путь к директории конфигурации мода.
     * <p>
     * Если директория не существует, она будет создана автоматически.
     * </p>
     *
     * @return путь к директории {@code config/discord_linker/}
     */
    public static Path getConfigDir() {
        if (configDir == null) {
            configDir = FMLPaths.CONFIGDIR.get().resolve(MOD_DIR_NAME);
            if (!configDir.toFile().exists()) {
                configDir.toFile().mkdirs();
            }
        }
        return configDir;
    }
}