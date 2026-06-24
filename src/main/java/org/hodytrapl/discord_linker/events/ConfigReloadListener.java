package org.hodytrapl.discord_linker.events;

import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.bus.api.SubscribeEvent;
import org.hodytrapl.discord_linker.Discord_linker;

@EventBusSubscriber(modid = "discord_linker")
public class ConfigReloadListener {

    //перезагружаем все конфиги сервера и бота
    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getType() == ModConfig.Type.SERVER) {
            Discord_linker.getBotManager().reloadBot();
        }
    }
}
