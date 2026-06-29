package org.hodytrapl.discord_linker.config.commands;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Конфигурация отдельной команды для Discord Linker.
 * <p>
 * Этот класс содержит настройки для конкретной команды, включая
 * её включение/отключение, соответствующие команды в Minecraft и Discord,
 * а также права доступа.
 * </p>
 */
public class CommandsEntryConfig {
    public final ModConfigSpec.BooleanValue enabled;
    public final ModConfigSpec.ConfigValue<String> minecraftCommand;
    public final ModConfigSpec.ConfigValue<String> discordCommand;
    public final ModConfigSpec.BooleanValue managementCommand;

    /**
     * Конструктор конфигурации команды.
     *
     * @param builder построитель конфигурации NeoForge
     * @param commandName имя команды для группировки настроек
     * @param defaultMinecraftCommand команда Minecraft по умолчанию
     * @param defaultDiscordCommand команда Discord по умолчанию
     * @param defaultManagement является ли команда управленческой
     */
    public CommandsEntryConfig(ModConfigSpec.Builder builder, String commandName,
                               String defaultMinecraftCommand, String defaultDiscordCommand,
                               boolean defaultManagement) {
        //группируем по имени команды
        builder.comment("Configuration for /" + commandName + " command").push(commandName);

        enabled = builder
                .comment("Enable or disable this command")
                .define("enabled", true);

        minecraftCommand = builder
                .comment("Minecraft command name (without prefix, e.g. 'link')")
                .define("minecraft_command", defaultMinecraftCommand);

        discordCommand = builder
                .comment("Discord slash command name (e.g. 'link')")
                .define("discord_command", defaultDiscordCommand);

        managementCommand = builder
                .comment("Is this command restricted to management users?")
                .define("management_command", defaultManagement);

        builder.pop();//фиксируем
    }
}