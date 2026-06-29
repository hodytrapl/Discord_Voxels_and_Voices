package org.hodytrapl.discord_linker.events;

import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.server.*;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;
import org.hodytrapl.discord_linker.discord.DiscordBotManager;
import org.hodytrapl.discord_linker.discord.GeneratorEmbedMessage;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.utils.ValidationUtils;
import org.hodytrapl.discord_linker.utils.config.EventsConfigHelper;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;

/**
 * Слушатель событий жизненного цикла сервера (запуск, остановка, краш).
 * <p>
 * Отправляет уведомления в Discord о старте, штатной остановке или
 * аварийном завершении сервера. При краше используется синхронная
 * отправка, чтобы сообщение гарантированно ушло перед закрытием бота.
 * </p>
 */
@EventBusSubscriber(modid = "discord_linker")
public class ServerOCCListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static boolean isGracefulShutdown = false;

    /**
     * Обрабатывает событие запуска сервера.
     *
     * @param event событие запуска сервера
     */
    @SubscribeEvent
    public static void onServerStarting(ServerStartingEvent event) {
        MinecraftServer server = event.getServer();
        isGracefulShutdown = false; // сброс при старте
        LOGGER.info(getMessage("mod.typelogger.events.server.starting"));
        Discord_linker.setServer(server);
        Discord_linker.getBotManager().initializeBot(server);
        // Для старта можно оставить асинхронную отправку (сервер ещё работает)
        sendEventMessage(EventsConfig.INSTANCE.serverStarted, false);
    }

    /**
     * Обрабатывает событие начала остановки сервера.
     * <p>
     * Устанавливает флаг корректной остановки, чтобы при {@code ServerStoppedEvent}
     * можно было отличить штатное завершение от краша.
     * </p>
     *
     * @param event событие остановки сервера
     */
    @SubscribeEvent
    public static void onServerStopping(ServerStoppingEvent event) {
        isGracefulShutdown = true; // нормальная остановка
    }

    /**
     * Обрабатывает событие полной остановки сервера.
     * <p>
     * Отправляет сообщение о краше или штатной остановке в зависимости
     * от флага {@code isGracefulShutdown}, затем завершает работу бота.
     * </p>
     *
     * @param event событие остановки сервера
     */
    @SubscribeEvent
    public static void onServerStopped(ServerStoppedEvent event) {
        boolean crashed = !isGracefulShutdown;
        if (crashed) {
            LOGGER.error(getMessage("mod.typelogger.events.server.crashed"));
            sendEventMessage(EventsConfig.INSTANCE.serverCrashed, true); // синхронно
        } else {
            LOGGER.info(getMessage("mod.typelogger.events.server.stopped"));
            sendEventMessage(EventsConfig.INSTANCE.serverStopped, true); // синхронно
        }
        // Закрываем бота после отправки
        Discord_linker.getBotManager().shutdown();
    }

    /**
     * Отправляет сообщение о событии сервера в Discord.
     *
     * @param eventConfig конфигурация события
     * @param sync флаг синхронной отправки (блокирующий поток)
     */
    private static void sendEventMessage(EventEntryConfig eventConfig, boolean sync) {
        if (!EventsConfigHelper.isEventEnabled(eventConfig)) return;
        String message = EventsConfigHelper.getRawEventMessage(eventConfig);
        DiscordBotManager botManager = Discord_linker.getBotManager();
        if (botManager == null) return;

        String channelId = MainConfigHelper.getRawChannelId();
        String eventId = MainConfigHelper.getRawEventsId();
        String correctId = ValidationUtils.isValidId(eventId) ? eventId :
                (ValidationUtils.isValidId(channelId) ? channelId : null);
        if (correctId == null) return;

        if (EventsConfigHelper.isEmbedEnable(eventConfig)) {
            MessageEmbed embed = GeneratorEmbedMessage.buildEmbed(eventConfig, "");
            if (sync) {
                botManager.sendEmbedSync(correctId, embed);
            } else {
                botManager.sendEmbed(correctId, embed);
            }
        } else {
            if (sync) {
                botManager.sendMessageSync(correctId, message, DiscordMessageType.CHAT_MESSAGE);
            } else {
                botManager.sendMessage(correctId, message, DiscordMessageType.CHAT_MESSAGE);
            }
        }
    }
}