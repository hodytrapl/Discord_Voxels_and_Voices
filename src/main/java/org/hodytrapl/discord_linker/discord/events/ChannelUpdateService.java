package org.hodytrapl.discord_linker.discord.events;


import com.mojang.logging.LogUtils;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.hodytrapl.discord_linker.Discord_linker;
import org.hodytrapl.discord_linker.config.general.MainConfig;
import org.hodytrapl.discord_linker.config.general.MainEntryConfig;
import org.hodytrapl.discord_linker.utils.config.MainConfigHelper;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;


/**
 * Сервис для автоматического обновления названий голосовых каналов в Discord.
 * <p>
 * Этот класс управляет периодическим обновлением названий голосовых каналов
 * на основе данных с Minecraft сервера, таких как количество игроков,
 * онлайн персонала и версия сервера.
 * </p>
 */
public class ChannelUpdateService {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static ChannelUpdateService instance;
    private static final int INITIAL_DELAY_SECONDS = 30; //ожидание перед тем как обновим каналы в первый раз

    private final ScheduledExecutorService scheduler;          // Планировщик для периодических задач
    private final Map<MainEntryConfig, ScheduledFuture<?>> scheduledTasks; // Активные задачи по событиям
    private final Map<String, String> placeholders;           // Текущие значения плейсхолдеров
    private JDA jda;                                          // Инстанс Discord бота
    private boolean initialized = false;                      // Флаг инициализации сервиса

    private ChannelUpdateService() {
        this.scheduler = Executors.newScheduledThreadPool(4);
        this.scheduledTasks = new ConcurrentHashMap<>();
        this.placeholders = new ConcurrentHashMap<>();
    }

    /**
     * Возвращает экземпляр сервиса (паттерн Singleton).
     *
     * @return экземпляр ChannelUpdateService
     */
    public static ChannelUpdateService getInstance() {
        if (instance == null) {
            instance = new ChannelUpdateService();
        }
        return instance;
    }

    /**
     * Инициализирует сервис с экземпляром JDA.
     *
     * @param jda экземпляр Discord бота
     */
    public void initialize(JDA jda) {
        if (initialized) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.alreadyinitialized"));
            return;
        }

        this.jda = jda;
        this.initialized = true;

        // Проверяем, включен ли бот в конфиге
        if (!MainConfigHelper.isBotEnabled()) {
            LOGGER.info(getMessage("mod.typelogger.discord.bot.disabled"));
            return;
        }

        LOGGER.info(getMessage("mod.typelogger.discord.channel.initializing"));
        initializeAllEvents();
    }

    /**
     * Инициализирует все события из конфигурации.
     */
    private void initializeAllEvents() {
        // Получаем все события из конфига
        List<MainEntryConfig> events = getEventsFromConfig();

        if (events.isEmpty()) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.noevents"));
            return;
        }

        // Инициализируем каждое событие
        for (MainEntryConfig event : events) {
            initializeEventWithDelay(event, INITIAL_DELAY_SECONDS);
        }

        LOGGER.info(getMessage("mod.typelogger.discord.channel.initialized", events.size()));
    }

    /**
     * Инициализирует отдельное событие с указанной задержкой.
     *
     * @param event конфигурация события
     * @param delaySeconds задержка в секундах перед первым обновлением
     */
    private void initializeEventWithDelay(MainEntryConfig event, int delaySeconds) {
        // Проверяем, включено ли событие
        if (!MainConfigHelper.getEnableField(event)) {
            LOGGER.debug(getMessage("mod.typelogger.discord.channel.eventdisabled"));
            return;
        }

        // Получаем параметры конфигурации
        String channelId = MainConfigHelper.getChannelIdField(event);
        Integer interval = MainConfigHelper.getUpdateIntervalField(event);
        String nameTemplate = MainConfigHelper.getRawNameChannel(event);

        // Валидация ID канала
        if (channelId == null || channelId.equals("0000000000000000000")) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.invalidid", channelId));
            return;
        }

        // Проверка шаблона имени
        if (nameTemplate == null || nameTemplate.isEmpty()) {
            LOGGER.debug(getMessage("mod.typelogger.discord.channel.emptytemplate"));
            return;
        }

        // Обработка разных режимов обновления
        if (interval == -1) {
            // Режим 1: Разовое обновление с задержкой
            scheduleOnceWithDelay(event, 30); // 30 секунд задержки
            LOGGER.debug(getMessage("mod.typelogger.discord.channel.onceupdated", channelId));

        } else if (interval == 0) {
            // Режим 2: Обновления отключены
            LOGGER.debug(getMessage("mod.typelogger.discord.channel.autoupdatedisabled"));

        } else if (interval > 0) {
            // Режим 3: Периодическое обновление с задержкой
            schedulePeriodicUpdate(event, interval, 30); // 30 секунд задержки
            LOGGER.info(getMessage("mod.typelogger.discord.channel.scheduled", channelId, interval));

        } else {
            // Некорректное значение интервала
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.invalidinterval", interval));
        }
    }

    /**
     * Планирует разовое обновление канала с указанной задержкой.
     *
     * @param event конфигурация события
     * @param delaySeconds задержка в секундах
     */
    private void scheduleOnceWithDelay(MainEntryConfig event, int delaySeconds) {
        scheduler.schedule(() -> {
            try {
                // Обновляем плейсхолдеры актуальными данными
                updatePlaceholders();
                // Форматируем имя с подстановкой плейсхолдеров
                String formattedName = MainConfigHelper.getFormattedNameChannelField(event, placeholders);
                // Отправляем обновление в Discord
                updateDiscordChannel(event, formattedName);
            } catch (Exception e) {
                LOGGER.error(getMessage("mod.typelogger.discord.channel.updatefailed", e.getMessage()));
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * Планирует периодическое обновление канала.
     *
     * @param event конфигурация события
     * @param intervalSeconds интервал обновления в секундах
     * @param delaySeconds начальная задержка в секундах
     */
    private void schedulePeriodicUpdate(MainEntryConfig event,int intervalSeconds, int delaySeconds) {
        // Отменяем существующую задачу для этого события, если она есть
        ScheduledFuture<?> existing = scheduledTasks.remove(event);
        if (existing != null && !existing.isDone()) {
            existing.cancel(false);
        }

        // Создаем новую периодическую задачу
        ScheduledFuture<?> task = scheduler.scheduleAtFixedRate(() -> {
            try {
                // Обновляем данные для плейсхолдеров
                updatePlaceholders();
                // Форматируем имя
                String formattedName = MainConfigHelper.getFormattedNameChannelField(event, placeholders);
                // Обновляем канал
                updateDiscordChannel(event, formattedName);
            } catch (Exception e) {
                LOGGER.error(getMessage("mod.typelogger.discord.channel.updatefailed"), e);
            }
        }, delaySeconds, intervalSeconds, TimeUnit.SECONDS);

        // Сохраняем задачу для возможности отмены
        scheduledTasks.put(event, task);
    }

    /**
     * Обновляет название голосового канала в Discord.
     *
     * @param event конфигурация события
     * @param newName новое название канала
     */
    private void updateDiscordChannel(MainEntryConfig event, String newName) {
        // Проверяем, что бот инициализирован
        if (jda == null || !initialized) {
            LOGGER.warn(getMessage("mod.typelogger.discord.channel.notready"));
            return;
        }

        // Проверяем, что имя не пустое
        if (newName == null || newName.isEmpty()) {
            LOGGER.debug(getMessage("mod.typelogger.discord.channel.emptyname"));
            return;
        }

        try {
            String channelId = MainConfigHelper.getChannelIdField(event);
            Guild guild = null;

            // Получаем гильдию
            VoiceChannel channel = jda.getVoiceChannelById(channelId);
            if (channel != null) {
                guild = channel.getGuild();
            }

            guild = channel.getGuild();
            if (guild == null) {
                return;
            }

            // Логируем информацию о правах
            var selfMember = guild.getSelfMember();
            LOGGER.debug("Бот {} на сервере {}", selfMember.getEffectiveName(), guild.getName());
            LOGGER.debug("Права бота: {}", selfMember.getPermissions());
            LOGGER.debug("Есть ли MANAGE_CHANNEL: {}", selfMember.hasPermission(channel, Permission.MANAGE_CHANNEL));

            if (!selfMember.hasPermission(channel, Permission.MANAGE_CHANNEL)) {
                LOGGER.debug("БОТ НЕ ИМЕЕТ ПРАВО MANAGE_CHANNEL!");
                LOGGER.debug("Выдайте боту право 'Управление каналами' в настройках сервера Discord.");
                LOGGER.debug("ID канала: {}, ID гильдии: {}", channelId, guild.getId());
                return;
            }

            // Получаем голосовой канал
            VoiceChannel voiceChannel  = guild.getVoiceChannelById(channelId);
            if (voiceChannel == null) {
                LOGGER.error(getMessage("mod.typelogger.discord.channel.notfound", channelId));
                return;
            }

            // Discord ограничивает имена каналов 100 символами
            String trimmedName = newName.length() > 100 ? newName.substring(0, 100) : newName;

            // Обновляем имя канала (асинхронно)
            voiceChannel.getManager().setName(trimmedName).queue(
                    success -> LOGGER.debug(getMessage("mod.typelogger.discord.channel.updated", trimmedName)),
                    failure -> LOGGER.error(getMessage("mod.typelogger.discord.channel.updateerror", failure.getMessage()))
            );

        } catch (Exception e) {
            LOGGER.error(getMessage("mod.typelogger.discord.channel.updateerror", e.getMessage()));
        }
    }

    /**
     * Обновляет значения всех плейсхолдеров на основе текущего состояния сервера.
     * <p>
     * Заполняет карту placeholders следующими значениями:
     * <ul>
     *   <li>onlineplayer - количество онлайн игроков</li>
     *   <li>maxplayer - максимальное количество игроков</li>
     *   <li>onlinestaff - количество онлайн персонала</li>
     *   <li>maxstaff - максимальное количество персонала</li>
     *   <li>version_server - версия сервера</li>
     * </ul>
     * </p>
     */
    private void updatePlaceholders() {
        MinecraftServer server = Discord_linker.getServer();
        if (server == null) {
            LOGGER.debug("Экземпляр сервера недоступен для обновления плейсхолдеров");
            return;
        }

        try {
            // --- Основные данные об игроках ---
            int onlinePlayers = server.getPlayerCount();
            int maxPlayers = server.getMaxPlayers();


            placeholders.put("onlineplayer", String.valueOf(onlinePlayers));
            placeholders.put("maxplayer", String.valueOf(maxPlayers));

            // --- Дополнительные плейсхолдеры (нужно реализовать) ---

            // %onlinestaff% - количество онлайн персонала
            int onlineStaff = 0;
            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                if (player.hasPermissions(2)) {
                    onlineStaff++;
                }
            }
            placeholders.put("onlinestaff", String.valueOf(onlineStaff));

            // %maxstaff% - максимальное количество персонала
            int maxStaff =server.getPlayerList().getOps().getEntries().size();
            placeholders.put("maxstaff", String.valueOf(maxStaff));

            // %version_server% - версия сервера
            String version = server.getServerVersion();
            placeholders.put("version_server", version);


            LOGGER.debug("Плейсхолдеры обновлены: online={}, max={}, onlinestaff={}, maxstaff={}",
                    onlinePlayers, maxPlayers, onlineStaff, maxStaff);

        } catch (Exception e) {
            LOGGER.error("Ошибка при обновлении плейсхолдеров", e);
        }
    }

    /**
     * Возвращает список всех событий из конфигурации.
     *
     * @return список конфигураций событий
     */
    private List<MainEntryConfig> getEventsFromConfig() {
        List<MainEntryConfig> events = new java.util.ArrayList<>();

        // Добавляем все события из конфига
        events.add(MainConfig.INSTANCE.PlayerOnline);
        events.add(MainConfig.INSTANCE.StaffOnline);
        events.add(MainConfig.INSTANCE.VersionServer);

        return events;
    }

    /**
     * Принудительно запускает обновление для указанного события.
     *
     * @param event конфигурация события для обновления
     */
    public void triggerUpdate(MainEntryConfig event) {
        if (event == null || !initialized) {
            return;
        }
        scheduleOnceWithDelay(event,INITIAL_DELAY_SECONDS);
    }

    /**
     * Перезагружает сервис, отменяя все задачи и инициализируя заново.
     */
    public void reload() {
        LOGGER.info(getMessage("mod.typelogger.discord.channel.reloading"));

        // Отменяем все запланированные задачи
        for (ScheduledFuture<?> task : scheduledTasks.values()) {
            if (task != null && !task.isDone()) {
                task.cancel(false);
            }
        }
        scheduledTasks.clear();

        // Переинициализируем с текущим JDA
        if (jda != null) {
            initializeAllEvents();
            LOGGER.info(getMessage("mod.typelogger.discord.channel.reloaded"));
        }
    }

    /**
     * Останавливает сервис и освобождает ресурсы.
     */
    public void shutdown() {
        LOGGER.info(getMessage("mod.typelogger.discord.channel.shuttingdown"));

        // Отменяем все задачи
        for (ScheduledFuture<?> task : scheduledTasks.values()) {
            if (task != null && !task.isDone()) {
                task.cancel(true);
            }
        }
        scheduledTasks.clear();

        // Завершаем планировщик
        scheduler.shutdownNow();

        // Очищаем состояние
        placeholders.clear();
        initialized = false;
        jda = null;
        instance = null;

        LOGGER.info(getMessage("mod.typelogger.discord.channel.shutdown"));
    }

    /**
     * Проверяет, инициализирован ли сервис.
     *
     * @return true если сервис инициализирован, false в противном случае
     */
    public boolean isInitialized() {
        return initialized;
    }


}