package org.hodytrapl.discord_linker.utils.config;

import org.hodytrapl.discord_linker.config.commands.CommandsConfig;
import org.hodytrapl.discord_linker.config.commands.CommandsEntryConfig;
import org.hodytrapl.discord_linker.config.events.EventEntryConfig;

import java.util.List;

public class CommandsConfigHelper {

    //main
    public static String getCommandPrefix() {
        return CommandsConfig.INSTANCE.commandPrefix.get();
    }

    public static String getDiscordManagementUserRole() {
        return CommandsConfig.INSTANCE.discordManagementUserRole.get();
    }

    public static int getMinecraftManagementUserPermissionLevel() {
        return CommandsConfig.INSTANCE.minecraftManagementUserPermissionLevel.get();
    }

    public static List<? extends String> getOtherBotsPrefixes() {
        return CommandsConfig.INSTANCE.otherBotsPrefixes.get();
    }

    // ------Command entry------

    //enable
    public static boolean isEventEnabled(CommandsEntryConfig event) {
        return event.enabled.get();
    }

    //minecraft command
    public static String getEventMinecraftCommand(CommandsEntryConfig event) {
        return event.minecraftCommand.get();
    }

    //discord command
    public static String getEventDiscordCommand(CommandsEntryConfig event) {
        return event.discordCommand.get();
    }

    //management command
    public static boolean getEventManagementCommand(CommandsEntryConfig event) {
        return event.managementCommand.get();
    }


}