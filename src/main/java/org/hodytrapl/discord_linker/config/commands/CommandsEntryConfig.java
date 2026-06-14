package org.hodytrapl.discord_linker.config.commands;

import net.neoforged.neoforge.common.ModConfigSpec;

public class CommandsEntryConfig {
    public final ModConfigSpec.BooleanValue enabled;
    public final ModConfigSpec.ConfigValue<String> minecraftCommand;
    public final ModConfigSpec.ConfigValue<String> discordCommand;
    public final ModConfigSpec.BooleanValue managementCommand;

    public CommandsEntryConfig(ModConfigSpec.Builder builder, String commandName,
                               String defaultMinecraftCommand, String defaultDiscordCommand,
                               boolean defaultManagement) {
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

        builder.pop();
    }
}