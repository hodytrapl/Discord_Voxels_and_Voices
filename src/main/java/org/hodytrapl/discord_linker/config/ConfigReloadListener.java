package org.hodytrapl.discord_linker.config;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.LanguageManager;
import org.slf4j.Logger;

/**
 * Слушатель перезагрузки конфигурации для Discord Linker.
 * <p>
 * Этот класс обрабатывает события загрузки и перезагрузки конфигурации,
 * автоматически обновляя настройки бота и языковые файлы при изменении
 * конфигурационных файлов.
 * </p>
 */
@EventBusSubscriber(modid = "discord_linker")
public class ConfigReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Обрабатывает событие первоначальной загрузки конфигурации.
     * <p>
     * Вызывается при первом запуске сервера или при активации мода.
     * </p>
     *
     * @param event событие загрузки конфигурации
     */
    @SubscribeEvent
    public static void onConfigLoad(ModConfigEvent.Loading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            LOGGER.info("Loading Discord Linker configuration...");
            reloadEverything();
        }
    }

    /**
     * Обрабатывает событие перезагрузки конфигурации.
     * <p>
     * Вызывается при изменении конфигурационных файлов или при выполнении
     * команды {@code /reload} на сервере.
     * </p>
     *
     * @param event событие перезагрузки конфигурации
     */
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            LOGGER.info("Reloading Discord Linker configuration...");
            reloadEverything();
        }
    }

    /**
     * Выполняет полную перезагрузку всех компонентов мода.
     * <p>
     * Перезагружает языковые файлы и перезапускает Discord бота
     * с новыми настройками.
     * </p>
     */
    private static void reloadEverything() {
        try {
            LanguageManager.reload(); // Теперь конфиг загружен, можно читать langSelect
            Discord_linker.getBotManager().reloadBot();
            LOGGER.info("Reload completed.");
        } catch (Exception e) {
            LOGGER.error("Failed to reload", e);
        }
    }
}