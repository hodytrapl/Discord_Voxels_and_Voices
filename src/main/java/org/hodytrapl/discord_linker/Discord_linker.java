package org.hodytrapl.discord_linker;

import com.mojang.logging.LogUtils;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.event.server.ServerStoppedEvent;
import org.hodytrapl.discord_linker.commands.CommandManager;
import org.hodytrapl.discord_linker.config.ConfigManager;
import org.hodytrapl.discord_linker.events.JoinLeavePlayerListener;
import org.slf4j.Logger;

@Mod(Discord_linker.MODID)
public class Discord_linker {
    public static final String MODID = "discord_linker";
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ConfigManager configManager;

    public Discord_linker(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        configManager = new ConfigManager(modContainer);

    }

    @SubscribeEvent
    private void onRegisterCommands(RegisterCommandsEvent event) {
        // Передаем диспетчер в класс нашей главной команды
        CommandManager.register(event.getDispatcher());
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
}
