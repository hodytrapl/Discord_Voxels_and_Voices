package org.hodytrapl.discord_linker.discord;

import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.hodytrapl.discord_linker.config.general.MainConfig;
import org.hodytrapl.discord_linker.discord.commands.CommandListener;
import org.hodytrapl.discord_linker.discord.enums.DiscordMessageType;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;

import javax.security.auth.login.LoginException;

import static org.hodytrapl.discord_linker.utils.ValidationUtils.isValidId;

public class DiscordBotManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private JDA jda;
    private boolean initialized = false;
    private final Object lock = new Object();

    public void initializeBot() {
        synchronized (lock) {
            if (initialized) return;
            if (!MainConfigHelper.isBotEnabled()) return;
            String token = MainConfig.INSTANCE.botToken.get();
            if (token.equals("INSERT_BOT_TOKEN_HERE") || token.equals("BOT_TOKEN_HERE")) {
                LOGGER.warn("Discord bot token not configured. Bot will not start.");
                return;
            }

            try {
                JDABuilder builder = JDABuilder.createDefault(token);
                builder.enableIntents(GatewayIntent.MESSAGE_CONTENT);
                // При каждом запуске создаём новый CommandListener (он прочитает свежие настройки)
                builder.addEventListeners(new CommandListener());
                jda = builder.build().awaitReady();
                setBotPresence();
                initialized = true;
                LOGGER.info("Discord bot logged in as {}", jda.getSelfUser().getName());
            } catch (Exception e) {
                LOGGER.error("Failed to start Discord bot: {}", e.getMessage());
            }
        }
    }

    public JDA getJda() {
        return jda;
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            jda = null;
        }
        initialized = false;
    }

    public void reloadBot() {
        LOGGER.info("Reloading Discord bot due to config change...");
        synchronized (lock) {
            shutdown();
            // Небольшая задержка для корректного завершения
            try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
            initializeBot();
        }
    }
    public void sendMessage(String channelId, String content, DiscordMessageType type) {
        if (jda == null) {
            LOGGER.warn("Cannot send Discord message – bot not initialized");
            return;
        }

        // 2. Validate channel ID using the same logic as CommandListener
        if (!isValidId(channelId)) {
            LOGGER.warn("Invalid or disabled channel ID: {}. Message not sent.", channelId);
            return;
        }

        // 3. Handle null/empty content
        if (content == null) content = "";
        if (content.isEmpty()) {
            LOGGER.debug("Empty message content – nothing to send");
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
                .queue(null, failure -> LOGGER.error("Failed to send Discord message", failure));
    }

    private void setBotPresence() {
        if (jda != null && MainConfigHelper.isPresenceEnabled()) {
            String presenceMessage = MainConfigHelper.getPresenceMessage();
            jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(presenceMessage));
            LOGGER.info("Discord presence set to: Playing {}", presenceMessage);
        }
    }

    public void sendEmbed(String channelId, MessageEmbed embed) {
        if (jda == null || !isValidId(channelId)) {
            LOGGER.warn("Cannot send embed: bot not ready or invalid channel");
            return;
        }
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.warn("Channel not found: {}", channelId);
            return;
        }
        channel.sendMessageEmbeds(embed).queue(
                null,
                failure -> LOGGER.error("Failed to send embed", failure)
        );
    }

    public void sendMessageSync(String channelId, String content, DiscordMessageType type) {
        if (jda == null) {
            LOGGER.warn("Cannot send Discord message – bot not initialized");
            return;
        }
        if (!isValidId(channelId)) {
            LOGGER.warn("Invalid channel ID: {}", channelId);
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
            LOGGER.warn("Channel not found: {}", channelId);
            return;
        }
        try {
            channel.sendMessage(trimmed).complete(); // синхронно!
            LOGGER.debug("Message sent synchronously to {}", channelId);
        } catch (Exception e) {
            LOGGER.error("Failed to send message sync", e);
        }
    }

    public void sendEmbedSync(String channelId, MessageEmbed embed) {
        if (jda == null || !isValidId(channelId)) {
            LOGGER.warn("Cannot send embed sync: bot not ready or invalid channel");
            return;
        }
        var channel = jda.getTextChannelById(channelId);
        if (channel == null) {
            LOGGER.warn("Channel not found: {}", channelId);
            return;
        }
        try {
            channel.sendMessageEmbeds(embed).complete(); // синхронно!
            LOGGER.debug("Embed sent synchronously to {}", channelId);
        } catch (Exception e) {
            LOGGER.error("Failed to send embed sync", e);
        }
    }
}
