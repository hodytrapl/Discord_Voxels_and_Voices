package org.hodytrapl.discord_linker;

import com.mojang.logging.LogUtils;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

import org.hodytrapl.discord_linker.commands.CommandManager;
import org.hodytrapl.discord_linker.config.ConfigManager;
import org.hodytrapl.discord_linker.discord.DiscordBotManager;
import org.slf4j.Logger;

/**
 * Главный класс мода Discord Linker.
 * <p>
 * Отвечает за инициализацию мода, регистрацию команд, управление
 * конфигурацией и Discord ботом.
 * </p>
 */
@Mod(Discord_linker.MODID)
public class Discord_linker {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String MODID = "discord_linker";
    private static ConfigManager configManager;
    private static DiscordBotManager botManager;
    private static MinecraftServer currentServer;

    /**
     * Конструктор мода, вызываемый при загрузке.
     *
     * @param modEventBus шина событий мода
     * @param modContainer контейнер мода
     */
    public Discord_linker(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);

        configManager = new ConfigManager(modContainer);
        botManager = new DiscordBotManager();
    }

    /**
     * Обрабатывает событие регистрации команд Minecraft.
     *
     * @param event событие регистрации команд
     */
    @SubscribeEvent
    private void onRegisterCommands(RegisterCommandsEvent event) {
        // Передаем диспетчер в класс нашей главной команды
        CommandManager.register(event.getDispatcher());
    }

    /**
     * Возвращает экземпляр менеджера Discord бота.
     *
     * @return экземпляр {@link DiscordBotManager}
     */
    public static DiscordBotManager getBotManager() {
        return botManager;
    }

    /**
     * Возвращает экземпляр менеджера конфигурации.
     *
     * @return экземпляр {@link ConfigManager}
     */
    public static ConfigManager getConfigManager() {
        return configManager;
    }

    /**
     * Устанавливает текущий экземпляр Minecraft сервера.
     *
     * @param server экземпляр сервера
     */
    public static void setServer(MinecraftServer server) {
        currentServer = server;
    }

    /**
     * Возвращает текущий экземпляр Minecraft сервера.
     *
     * @return экземпляр сервера или null, если сервер ещё не запущен
     */
    public static MinecraftServer getServer() {
        return currentServer;
    }
}