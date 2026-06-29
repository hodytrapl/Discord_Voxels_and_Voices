package org.hodytrapl.discord_linker.config.events;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Конфигурация всех событий для Discord Linker.
 * <p>
 * Этот класс содержит настройки для всех событий, которые могут быть
 * отправлены из Minecraft в Discord и обратно.
 * </p>
 */
public class EventsConfig {
    public static final EventsConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    static {
        Pair<EventsConfig, ModConfigSpec> pair = new ModConfigSpec.Builder()
                .configure(EventsConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }

    public final EventEntryConfig MCtoDC;
    public final EventEntryConfig DCtoMC;
    public final EventEntryConfig playerJoin;
    public final EventEntryConfig playerLeave;
    public final EventEntryConfig serverStarted;
    public final EventEntryConfig serverStopped;
    public final EventEntryConfig serverCrashed;


    /**
     * Конструктор конфигурации событий.
     *
     * @param builder построитель конфигурации NeoForge
     */
    public EventsConfig(ModConfigSpec.Builder builder) {
        builder.comment("...").push("events");

        // Базовый payload всех возможных полей
        Map<String, String> basePayload = new HashMap<>();
        basePayload.put("event_enable", "true");
        basePayload.put("message", "");
        basePayload.put("embed_enable", "false");
        basePayload.put("embed_author_name", "");
        basePayload.put("embed_author_icon_url", "");
        basePayload.put("embed_title", "");
        basePayload.put("embed_description", "%message%");
        basePayload.put("embed_thumbnail", "");
        basePayload.put("embed_color", "#ffff00");
        basePayload.put("embed_image", "");
        basePayload.put("embed_footer_icon", "");
        basePayload.put("embed_footer", "");
        basePayload.put("embed_timestamp_enable", "true");
        basePayload.put("embed_fields", "");

        // Создаём изменяемую копию для настроек
        Map<String, String> payload = new HashMap<>(basePayload);
        // Для первого события
        payload.put("message", "<%username%>: %message%");
        payload.put("embed_title", "%username%");
        payload.put("embed_description", "%message%");
        payload.put("embed_color","#0c9200");
        payload.put("embed_image", "%headplayer%");
        payload.put("embed_fields", "Prefix:|%prefix%|true;-|-|true;Suffix:|%suffix%|true");
        MCtoDC = new EventEntryConfig(builder, "minecraft_to_discord", payload,true);

        // Для второго события - сбрасываем на базовые, меняем нужные
        payload = new HashMap<>(basePayload);
        payload.put("message", "[%username%]: %message%");
        payload.put("embed_description", "how you think its worked?");
        DCtoMC = new EventEntryConfig(builder, "discord_to_minecraft", payload,false);

        payload = new HashMap<>(basePayload);
        payload.put("message", "%username% joined in game!");
        payload.put("embed_description", "%username% joined in game!");
        payload.put("embed_image", "%headplayer%");
        playerJoin = new EventEntryConfig(builder, "player_join", payload,true);

        payload = new HashMap<>(basePayload);
        payload.put("message", "%username% leave in game!");
        payload.put("embed_description", "%username% leave in game!");
        payload.put("embed_image", "%headplayer%");
        playerLeave = new EventEntryConfig(builder, "player_leave", payload,true);

        payload = new HashMap<>(basePayload);
        payload.put("message", "Server Started!");
        payload.put("embed_description", "Server Started!");
        payload.put("embed_color","#09ff00");
        serverStarted = new EventEntryConfig(builder, "server_started", payload,true);

        payload = new HashMap<>(basePayload);
        payload.put("message", "Server Stopped!");
        payload.put("embed_description", "Server Stopped!");
        payload.put("embed_color","#727272");
        serverStopped = new EventEntryConfig(builder, "server_stopped", payload,true);

        payload = new HashMap<>(basePayload);
        payload.put("message", "Server Crashed!");
        payload.put("embed_description", "Server Crashed!");
        payload.put("embed_color","#ff0000");
        serverCrashed = new EventEntryConfig(builder, "server_crashed", payload,true);

        builder.pop();
    }
}