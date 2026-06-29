package org.hodytrapl.discord_linker.config;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.config.ModConfig;
import org.hodytrapl.discord_linker.config.commands.CommandsConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;
import org.hodytrapl.discord_linker.config.general.MainConfig;
import org.slf4j.Logger;

/**
 * Менеджер конфигурации для Discord Linker.
 * <p>
 * Этот класс отвечает за регистрацию всех конфигурационных файлов мода
 * в системе NeoForge и управление путями к ним.
 * </p>
 */
public class ConfigManager {
    private final ModContainer modContainer;
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Конструктор менеджера конфигурации.
     * <p>
     * Регистрирует три конфигурационных файла:
     * <ul>
     *   <li>general.toml - основные настройки</li>
     *   <li>events.toml - настройки событий</li>
     *   <li>commands.toml - настройки команд</li>
     * </ul>
     * </p>
     *
     * @param modContainer контейнер мода для регистрации конфигураций
     */
    public ConfigManager(ModContainer modContainer){
        this.modContainer = modContainer;
        ModPaths.getConfigDir();
        String modId = modContainer.getModId();
        modContainer.registerConfig(
                ModConfig.Type.SERVER,
                MainConfig.SPEC,
                modId+"/general.toml"
        );
        modContainer.registerConfig(
                ModConfig.Type.SERVER,
                EventsConfig.SPEC,
                modId+"/events.toml"
        );
        modContainer.registerConfig(
                ModConfig.Type.SERVER,
                CommandsConfig.SPEC,
                modId+"/commands.toml"
        );
    }

}
