package org.hodytrapl.discord_linker.events;

import net.dv8tion.jda.api.entities.MessageEmbed;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ServerChatEvent;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.events.EventsConfig;
import org.hodytrapl.discord_linker.discord.DiscordBotManager;
import org.hodytrapl.discord_linker.discord.GeneratorEmbedMessage;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.utils.config.EventsConfigHelper;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hodytrapl.discord_linker.utils.Utils.cleanFormatting;
import static org.hodytrapl.discord_linker.utils.Utils.formatPlaceholder;

/**
 * Слушатель событий чата для пересылки сообщений из Minecraft в Discord.
 * <p>
 * Этот класс перехватывает сообщения игроков в Minecraft, форматирует их
 * с учётом префиксов/суффиксов и отправляет в Discord в виде обычного
 * текста или embed-сообщения в зависимости от конфигурации.
 * </p>
 */
@EventBusSubscriber(modid = "discord_linker")
public class ChatListener {

    /**
     * Регулярное выражение для разбора отображаемого имени игрока
     * с поддержкой префиксов и суффиксов в формате [префикс] имя [суффикс].
     */
    private static final Pattern DISPLAY_NAME_PATTERN =
            Pattern.compile("^\\[(.*?)\\]\\s*(.*?)(?:\\s*\\[(.*?)\\])?$");

    /**
     * Обрабатывает событие отправки сообщения в чат Minecraft.
     * <p>
     * Извлекает префикс, имя пользователя, суффикс и текст сообщения,
     * формирует плейсхолдеры и отправляет в Discord через бота.
     * </p>
     *
     * @param event событие чата сервера Minecraft
     */
    @SubscribeEvent
    public static void receiveMCtoDC(ServerChatEvent event){
        // проверка на срабатывания ивента
        if (!EventsConfigHelper.isEventEnabled(EventsConfig.INSTANCE.MCtoDC)) return;

        Player player = event.getPlayer();
        String displayName = player.getDisplayName().getString();
        String username = event.getUsername();
        String message = event.getMessage().getString();

        //обработка префиксов и прочего
        Matcher matcher = DISPLAY_NAME_PATTERN.matcher(displayName);
        String prefix = "";
        String suffix = "";

        if (matcher.matches()) {
            prefix = cleanFormatting(matcher.group(1));
            suffix = cleanFormatting(matcher.group(3) != null ? matcher.group(3) : "");
        }

        //находим бота
        DiscordBotManager botManager = Discord_linker.getBotManager();
        Map<String,String> payload=new HashMap<>();
        payload.put("prefix", prefix);
        payload.put("username", username);
        payload.put("suffix", suffix);
        payload.put("message",message);
        payload.put("headplayer","https://minotar.net/avatar/"+username+"/64");

        //даем запрос

        // проверка embed включен
        if (EventsConfigHelper.isEmbedEnable(EventsConfig.INSTANCE.MCtoDC)) {
            MessageEmbed embed = GeneratorEmbedMessage.buildEmbed(EventsConfig.INSTANCE.MCtoDC, payload);
            botManager.sendEmbed(MainConfigHelper.getRawChannelId(), embed);
        } else {
            String discordMessage = EventsConfigHelper.getFormattedEventMessage(EventsConfig.INSTANCE.MCtoDC,payload);
            botManager.sendMessage(MainConfigHelper.getRawChannelId(), discordMessage, DiscordMessageType.CHAT_MESSAGE);
        }
    }
}
