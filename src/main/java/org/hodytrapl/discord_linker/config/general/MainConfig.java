package org.hodytrapl.discord_linker.config.general;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;
import org.hodytrapl.discord_linker.config.events.EventEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventsConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * Основная конфигурация мода Discord Linker.
 * <p>
 * Этот класс содержит глобальные настройки мода, включая настройки бота,
 * языковые настройки, Discord Presence и другие общие параметры.
 * </p>
 */
public class MainConfig {
    // --- базовая настройка ---
    public static final MainConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        // создаем билдера
        Pair<MainConfig, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(MainConfig::new);
        INSTANCE = commonPair.getLeft();
        SPEC = commonPair.getRight();
    }

    // --- переменные конфигурации ---
    public final ModConfigSpec.ConfigValue<String> langSelect;
    public final ModConfigSpec.BooleanValue enableBot;
    public final ModConfigSpec.ConfigValue<String> botToken;
    public final ModConfigSpec.ConfigValue<String> channelID;
    public final ModConfigSpec.ConfigValue<String> eventsID;
    public final ModConfigSpec.ConfigValue<String> commandsID;

    public final ModConfigSpec.BooleanValue enablePresence;
    public final ModConfigSpec.ConfigValue<String> presenceMessage;

    public final MainEntryConfig PlayerOnline;
    public final MainEntryConfig StaffOnline;
    public final MainEntryConfig VersionServer;

    /**
     * Конструктор основной конфигурации.
     *
     * @param builder построитель конфигурации NeoForge
     */
    public MainConfig(ModConfigSpec.Builder builder){
        // Основные настройки
        builder.comment("General settings").push("general");
        langSelect = builder
                .comment("Language selection for the mod. Supported values: [en_us, ru_ru]")
                .define("lang", "en_us");
        enableBot = builder
                .comment("Whether to enable the Discord bot integration. If false, the bot will not start.")
                .define("active", false);

        botToken = builder
                .comment("Your Discord bot token. Obtain it from the Discord Developer Portal under your application's Bot section.")
                .define("bot_token", "BOT_TOKEN_HERE");

        channelID = builder
                .comment("The ID of the Discord text channel where general bot messages (e.g., status updates) will be sent. Must be a valid snowflake ID.")
                .define("channel_id", "0000000000000000000");
        eventsID = builder
                .comment("The ID of the Discord text channel where in-game events (e.g., player join/leave, achievements) will be relayed.")
                .define("events_id", "0000000000000000000");
        commandsID = builder
                .comment("The ID of the Discord text channel where slash-command interactions and responses will be handled.")
                .define("commands_id", "0000000000000000000");

        // Активность бота
        builder.comment("Discord Rich Presence settings").push("discord_presence");
        enablePresence = builder
                .comment("Enable or disable Discord Rich Presence. When enabled, the bot will display a custom status message in Discord.")
                .define("show_discord_presence", false);

        presenceMessage = builder
                .comment("The custom message to display in the Discord Rich Presence status (e.g., the server IP or a custom text).")
                .define("message", "play.example.com");
        builder.pop();

        //базовый payload всевозможных полей
        Map<String, String> basePayload = new HashMap<>();
        basePayload.put("enable", "false");
        basePayload.put("channel_id", "0000000000000000000");
        basePayload.put("update_interval", "30");
        basePayload.put("name_channel","");
        Map<String, String> payload = new HashMap<>(basePayload);

        // Создаём изменяемую копию для настроек
        payload = new HashMap<>(basePayload);
        payload.put("name_channel","Online Players: %onlineplayer%/%maxplayer%");
        // Счетчик людей на сервере
        PlayerOnline = new MainEntryConfig(builder,"field1",payload);

        // Создаём изменяемую копию для настроек
        payload = new HashMap<>(basePayload);
        payload.put("name_channel","Staff Players: %onlinestaff%/%maxstaff%");
        // админы в онлайн
        StaffOnline = new MainEntryConfig(builder,"field2",payload);

        // Создаём изменяемую копию для настроек
        payload = new HashMap<>(basePayload);
        basePayload.put("update_interval", "-1");
        payload.put("name_channel","Version: %version_server%");
        // Счетчик людей на сервере
        VersionServer = new MainEntryConfig(builder,"field3",payload);

        builder.pop();
    }
}