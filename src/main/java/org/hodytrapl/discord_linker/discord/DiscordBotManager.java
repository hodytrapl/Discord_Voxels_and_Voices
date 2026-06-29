package org.hodytrapl.discord_linker.discord;

import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.minecraft.server.MinecraftServer;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.discord.commands.CommandListener;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.discord.events.ChannelUpdateService;
import org.hodytrapl.discord_linker.discord.events.MessageListener;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;


import static org.hodytrapl.discord_linker.LanguageManager.getMessage;
import static org.hodytrapl.discord_linker.utils.ValidationUtils.isValidId;

/**
 * Менеджер Discord бота для интеграции с Minecraft сервером.
 * <p>
 * Этот класс управляет жизненным циклом Discord бота, включая его инициализацию,
 * отправку сообщений, управление присутствием и перезагрузку.
 * </p>
 */
public class DiscordBotManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private JDA jda;
    private boolean initialized = false;
    private final Object lock = new Object();
    private static MinecraftServer currentServer;
    private ChannelUpdateService channelUpdateService;

    /**
     * Инициализирует Discord бота с указанным Minecraft сервером.
     * <p>
     * Метод создает экземпляр бота, регистрирует слушателей событий,
     * устанавливает присутствие и запускает сервис обновления каналов.
     * </p>
     *
     * @param server экземпляр Minecraft сервера
     */
    public void initializeBot(MinecraftServer server) {
        synchronized (lock) {
            if (initialized) return;
            if (!MainConfigHelper.isBotEnabled()) return;
            String token = MainConfigHelper.getBotToken();
            if (token.equals("INSERT_BOT_TOKEN_HERE") || token.equals("BOT_TOKEN_HERE")) {
                LOGGER.warn(getMessage("mod.typelogger.discord.bot.dontconfigurated"));
                return;
            }

            try {
                JDABuilder builder = JDABuilder.createDefault(token);
                builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
                builder.addEventListeners(new CommandListener());

                jda = builder.build().awaitReady();
                setBotPresence();
                initialized = true;
                currentServer = server;
                jda.addEventListener(new MessageListener());

                channelUpdateService = ChannelUpdateService.getInstance();
                channelUpdateService.initialize(jda);

                LOGGER.info(getMessage("mod.typelogger.discord.bot.loggedin",jda.getSelfUser().getName()));
            } catch (Exception e) {
                LOGGER.error(getMessage("mod.typelogger.discord.bot.startfailed",e.getMessage()));
            }
        }
    }

    /**
     * Возвращает экземпляр JDA бота.
     *
     * @return экземпляр JDA или null, если бот не инициализирован
     */
    public JDA getJda() {
        return jda;
    }

    /**
     * Возвращает сервис обновления каналов.
     *
     * @return экземпляр ChannelUpdateService
     */
    public ChannelUpdateService getChannelUpdateService() {
        return channelUpdateService;
    }

    /**
     * Останавливает бота и освобождает ресурсы.
     */
    public void shutdown() {
        if (channelUpdateService != null) {
            channelUpdateService.shutdown();
        }
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
        initialized = false;
    }

    /**
     * Перезагружает бота с текущим сервером.
     */
    public void reloadBot() {
        LOGGER.info(getMessage("mod.typelogger.discord.bot.reloading"));
        synchronized (lock) {
            if (channelUpdateService != null) {
                channelUpdateService.reload();
            }
            shutdown();
            // Небольшая задержка для корректного завершения
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            initializeBot(currentServer);
        }
    }

    /**
     * Отправляет сообщение в Discord канал.
     * <p>
     * Метод форматирует сообщение в зависимости от типа и отправляет его
     * в указанный канал. Длина сообщения ограничена 1900 символами.
     * </p>
     *
     * @param channelId ID канала Discord
     * @param content содержимое сообщения
     * @param type тип сообщения для форматирования
     */
    public void sendMessage(String channelId, String content, DiscordMessageType type) {
        if (jda == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.message.notinitialized"));
            return;
        }

        // 2. Validate channel ID using the same logic as CommandListener
        if (!isValidId(channelId)) {
            LOGGER.warn(getMessage("mod.typelogger.discord.message.invalidchannel",channelId));
            return;
        }

        // 3. Handle null/empty content
        if (content == null) content = "";
        if (content.isEmpty()) {
            LOGGER.debug(getMessage("mod.typelogger.discord.message.emptycontent"));
            return;
        }

        // 1. Apply formatting based on type
        String formatted;
        switch (type) {
            case COMMAND_RESPONSE:
                formatted = "```\n" + content + "\n```";
                break;
            case CHAT_MESSAGE:
                formatted = content;
                break;
            case SYSTEM_NOTIFICATION:
                formatted = "[System] " + content;
                break;
            default:
                formatted = content;
        }

        String trimmed = formatted.length() > 1900 ? formatted.substring(0, 1900) + "..." : formatted;

        jda.getTextChannelById(channelId)
                .sendMessage(trimmed)
                .queue(null, failure -> LOGGER.error(getMessage("mod.typelogger.discord.message.sendfailed",failure)));
    }

    /**
     * Устанавливает присутствие бота в Discord.
     * <p>
     * Если присутствие включено в конфигурации, устанавливает статус
     * и пользовательское сообщение активности.
     * </p>
     */
    private void setBotPresence() {
        if (jda != null && MainConfigHelper.isPresenceEnabled()) {
            String presenceMessage = MainConfigHelper.getPresenceMessage();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(presenceMessage));
            LOGGER.info(getMessage("mod.typelogger.discord.presence.set",presenceMessage));
        }
    }

    /**
     * Отправляет embed-сообщение в Discord канал.
     *
     * @param channelId ID канала Discord
     * @param embed embed-сообщение для отправки
     */
    public void sendEmbed(String channelId, MessageEmbed embed) {
        if (jda == null || !isValidId(channelId)) {
            LOGGER.warn(getMessage("mod.typelogger.discord.embed.notready"));
            return;
        }
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.notfound",channelId));
            return;
        }
        channel.sendMessageEmbeds(embed).queue(
                null,
                failure -> LOGGER.error(getMessage("mod.typelogger.discord.embed.sendfailed", failure))
        );
    }

    /**
     * Отправляет сообщение в Discord канал синхронно.
     * <p>
     * Метод блокирует поток до завершения отправки.
     * </p>
     *
     * @param channelId ID канала Discord
     * @param content содержимое сообщения
     * @param type тип сообщения для форматирования
     */
    public void sendMessageSync(String channelId, String content, DiscordMessageType type) {
        if (jda == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.message.notinitialized"));
            return;
        }
        if (!isValidId(channelId)) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.notfound", channelId));
            return;
        }
        if (content == null) content = "";
        if (content.isEmpty()) return;

        String formatted;
        switch (type) {
            case COMMAND_RESPONSE:
                formatted = "```\n" + content + "\n```";
                break;
            case SYSTEM_NOTIFICATION:
                formatted = "[System] " + content;
                break;
            default:
                formatted = content;
        }
        String trimmed = formatted.length() > 1900 ? formatted.substring(0, 1900) + "..." : formatted;

        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.notfound", channelId));
            return;
        }
        try {
            channel.sendMessage(trimmed).complete(); // синхронно!
            LOGGER.info(getMessage("mod.typelogger.discord.embed.sentsync",channelId));
        } catch (Exception e) {
            LOGGER.error(getMessage("mod.typelogger.discord.message.syncfailed",e));
        }
    }

    /**
     * Отправляет embed-сообщение в Discord канал синхронно.
     *
     * @param channelId ID канала Discord
     * @param embed embed-сообщение для отправки
     */
    public void sendEmbedSync(String channelId, MessageEmbed embed) {
        if (jda == null || !isValidId(channelId)) {
            LOGGER.warn(getMessage("mod.typelogger.discord.embed.syncnotready"));
            return;
        }
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.notfound", channelId));
            return;
        }
        try {
            channel.sendMessageEmbeds(embed).complete(); // синхронно!
            LOGGER.info(getMessage("mod.typelogger.discord.embed.sentsync",channelId));
        } catch (Exception e) {
            LOGGER.error(getMessage("mod.typelogger.discord.message.syncfailed",e));
        }
    }
}
