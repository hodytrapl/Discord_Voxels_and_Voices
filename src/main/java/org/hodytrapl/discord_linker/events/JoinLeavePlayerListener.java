package org.hodytrapl.discord_linker.events;

import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.events.EventsConfig;
import org.hodytrapl.discord_linker.discord.DiscordBotManager;
import org.hodytrapl.discord_linker.discord.GeneratorEmbedMessage;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.utils.ValidationUtils;
import org.hodytrapl.discord_linker.utils.config.EventsConfigHelper;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;

import java.util.Map;

import static org.hodytrapl.discord_linker.utils.Utils.formatPlaceholder;

/**
 * Слушатель событий входа и выхода игроков на сервер.
 * <p>
 * Отправляет уведомления в Discord при подключении или отключении игрока,
 * используя конфигурацию событий {@code playerJoin} и {@code playerLeave}.
 * </p>
 */
@EventBusSubscriber(modid = "discord_linker")
public class JoinLeavePlayerListener {
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Обрабатывает событие входа игрока на сервер.
     *
     * @param event событие входа игрока
     */
    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        // проверка на срабатывания ивента
        if (!EventsConfigHelper.isEventEnabled(EventsConfig.INSTANCE.playerJoin)) return;

        //форматируем сообщение
        Player player = event.getEntity();
        String username = player.getName().getString();
        String message = EventsConfigHelper.getFormattedEventMessage(EventsConfig.INSTANCE.playerJoin,"username",username);

        //находим бота
        DiscordBotManager botManager = Discord_linker.getBotManager();
        String channelId = MainConfigHelper.getRawChannelId();
        String eventId = MainConfigHelper.getRawEventsId();
        String correctId=eventId;
        if(!ValidationUtils.isValidId(eventId)){
            if(!ValidationUtils.isValidId(channelId)){
                return;
            }
            correctId=channelId;
        }

        //даем запрос
        if (ValidationUtils.isValidId(correctId)) {
            // проверка embed включен
            if (EventsConfigHelper.isEmbedEnable(EventsConfig.INSTANCE.playerJoin)) {
                MessageEmbed embed = GeneratorEmbedMessage.buildEmbed(EventsConfig.INSTANCE.playerJoin, username);
                botManager.sendEmbed(correctId, embed);
            } else {
                botManager.sendMessage(correctId, message, DiscordMessageType.CHAT_MESSAGE);
            }
        }
    }

    /**
     * Обрабатывает событие выхода игрока с сервера.
     *
     * @param event событие выхода игрока
     */
    @SubscribeEvent
    public static void onPlayerLeave(PlayerEvent.PlayerLoggedOutEvent event) {
        // проверка на срабатывания ивента
        if (!EventsConfigHelper.isEventEnabled(EventsConfig.INSTANCE.playerLeave)) return;

        //форматируем сообщение
        Player player = event.getEntity();
        String username = player.getName().getString();
        String message = EventsConfigHelper.getFormattedEventMessage(EventsConfig.INSTANCE.playerLeave,"username",username);

        //находим бота
        DiscordBotManager botManager = Discord_linker.getBotManager();
        String channelId = MainConfigHelper.getRawChannelId();
        String eventId = MainConfigHelper.getRawEventsId();
        String correctId=eventId;
        if(!ValidationUtils.isValidId(eventId)){
            if(!ValidationUtils.isValidId(channelId)){
                return;
            }
            correctId=channelId;
        }

        //даем запрос
        if (ValidationUtils.isValidId(correctId)) {
            // проверка embed включен
            if (EventsConfigHelper.isEmbedEnable(EventsConfig.INSTANCE.playerLeave)) {
                MessageEmbed embed = GeneratorEmbedMessage.buildEmbed(EventsConfig.INSTANCE.playerLeave, username);
                botManager.sendEmbed(correctId, embed);
            } else {
                botManager.sendMessage(correctId, message, DiscordMessageType.CHAT_MESSAGE);
            }
        }
    }
}