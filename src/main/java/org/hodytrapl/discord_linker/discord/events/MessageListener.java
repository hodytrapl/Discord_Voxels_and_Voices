package org.hodytrapl.discord_linker.discord.events;

import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;
import org.hodytrapl.discord_linker.utils.config.EventsConfigHelper;

import net.dv8tion.jda.api.entities.Member;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;

/**
 * Слушатель сообщений из Discord для пересылки в Minecraft.
 * <p>
 * Этот класс перехватывает сообщения из Discord канала и отправляет их
 * в Minecraft чат, используя форматирование из конфигурации.
 * </p>
 */
public class MessageListener extends ListenerAdapter {// храним ссылку

    /**
     * Конструктор слушателя сообщений.
     */
    public MessageListener() {}
    private static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Обрабатывает полученное сообщение из Discord.
     * <p>
     * Если сообщение не от бота и событие DCtoMC включено,
     * сообщение форматируется и отправляется в Minecraft.
     * </p>
     *
     * @param event событие получения сообщения из Discord
     */
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Игнорируем сообщения от других ботов, чтобы избежать бесконечных циклов
        if (event.getAuthor().isBot()) return;
        if (!EventsConfigHelper.isEventEnabled(EventsConfig.INSTANCE.DCtoMC)) return;
        if (!MainConfigHelper.getRawChannelId().equals(event.getChannel().getId())) return;


        // Получаем данные о пользователе
        Member member = event.getMember();
        if (member==null) return;
        String username = member.getUser().getName();

        username = username+":0000";
        String messageContent = event.getMessage().getContentRaw();

        MinecraftServer server = Discord_linker.getServer();
        if (server == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.error.serverminecraft.null"));
            return;
        }
        // Вызываем отдельный метод для отправки
        sendMessageToMinecraft(server,username,messageContent);
    }

    /**
     * Отправляет отформатированное сообщение в Minecraft чат.
     * <p>
     * Метод форматирует сообщение с использованием плейсхолдеров
     * из конфигурации и отправляет его всем игрокам на сервере.
     * </p>
     *
     * @param server экземпляр Minecraft сервера
     * @param discordName имя пользователя Discord
     * @param message текст сообщения
     */
    private void sendMessageToMinecraft(MinecraftServer server, String discordName, String message) {
        if (server == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.error.serverminecraft.null"));
            return;
        }
        // 1. Формируем строку с возможными цветовыми кодами в формате '&'
        String rawMessage = EventsConfigHelper.getFormattedEventMessage(EventsConfig.INSTANCE.DCtoMC, "username",discordName,"message",message);

        // 2. Заменяем '&' на '§', чтобы Minecraft мог их понять
        String legacyMessage = rawMessage.replace('&', '§');

        // 3. Создаём Component из этой "легаси" строки
        //    Важно: этот метод может не поддерживать все сложные форматы,
        //    но для стандартных цветов и стилей работает отлично.
        Component formattedComponent = Component.literal(legacyMessage);

        // 4. Отправляем сообщение
        server.getPlayerList().broadcastSystemMessage(formattedComponent, false);
    }
}