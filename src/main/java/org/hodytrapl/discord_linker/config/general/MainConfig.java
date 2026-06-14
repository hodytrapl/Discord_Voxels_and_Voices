package org.hodytrapl.discord_linker.config.general;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class MainConfig {
    // --- Базовая настройка ---
    public static final MainConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        // Создаём Builder и сразу передаём в нашу лямбду
        Pair<MainConfig, ModConfigSpec> commonPair = new ModConfigSpec.Builder().configure(MainConfig::new);
        INSTANCE = commonPair.getLeft();
        SPEC = commonPair.getRight();
    }

    // --- Параметры конфига (основные) ---
    public final ModConfigSpec.ConfigValue<String> langSelect;
    public final ModConfigSpec.BooleanValue enableBot;
    public final ModConfigSpec.ConfigValue<String> botToken;
    public final ModConfigSpec.ConfigValue<String> channelID;
    public final ModConfigSpec.BooleanValue enablePresence;
    public final ModConfigSpec.ConfigValue<String> presenceMessage;

    // Конструктор, куда билдер передаёт сам себя
    public MainConfig(ModConfigSpec.Builder builder){
        /*здесь будет создание главных настроек конфига основных данных*/
        // группа главных настроек
        builder.comment("General settings").push("general");
        langSelect = builder
                .comment("support [en_us, ru_ru]")
                .define("lang", "en_us");
        enableBot = builder
                .comment("Enable Bot")
                .define("active", false);

        botToken = builder
                .comment("-")
                .define("bot_token", "BOT_TOKEN_HERE");
        channelID = builder
                .comment("-")
                .define("channel_id", "0000000000000000000");
        //вложенность в bot
            builder.comment("General settings").push("discord_presence");
            enablePresence = builder
                    .comment("Enable discord presence")
                    .define("show_discord_presence", false);

            presenceMessage = builder
                    .comment("-")
                    .define("message", "play.example.com");
            builder.pop();
        builder.pop();
    }
}
