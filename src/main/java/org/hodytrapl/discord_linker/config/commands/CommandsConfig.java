package org.hodytrapl.discord_linker.config.commands;

import net.neoforged.neoforge.common.ModConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;
import java.util.List;

public class CommandsConfig {
    public static final CommandsConfig INSTANCE;
    public static final ModConfigSpec SPEC;

    // глобальные настройки группы команд
    public final ModConfigSpec.ConfigValue<String> commandPrefix;
    public final ModConfigSpec.ConfigValue<String> discordManagementUserRole;
    public final ModConfigSpec.IntValue minecraftManagementUserPermissionLevel;
    public final ModConfigSpec.ConfigValue<List<? extends String>> otherBotsPrefixes;

    // команды
    public final CommandsEntryConfig TPSCommand;
    public final CommandsEntryConfig modListCommand;
    public final CommandsEntryConfig onlineListCommand;

    static {
        Pair<CommandsConfig, ModConfigSpec> pair = new ModConfigSpec.Builder()
                .configure(CommandsConfig::new);
        INSTANCE = pair.getLeft();
        SPEC = pair.getRight();
    }
    @SuppressWarnings("deprecation")
    public CommandsConfig(ModConfigSpec.Builder builder) {
        //создаем группу
        builder.comment("All command-related settings").push("commands");
        //создаем категории
        commandPrefix = builder
                .comment("Prefix used for Minecraft commands (e.g. '/' or '!')")
                .define("command_prefix", "/");

        discordManagementUserRole = builder
                .comment("Discord role ID that grants access to bot commands. " +
                        "Users with this role will receive responses from the bot.")
                .define("discord_management_user_roleID", "00000000000000000000");


        minecraftManagementUserPermissionLevel = builder
                .comment("Permission level required for management commands (0-4)")
                .defineInRange("minecraft_management_user_permission_level", 4, 0, 4);

        otherBotsPrefixes = builder
                .comment("List of command prefixes used by other bots (to avoid conflicts)")
                .defineListAllowEmpty("other_bots_prefixes", Arrays.asList("!", "."),
                        obj -> obj instanceof String && !((String) obj).isEmpty());


        TPSCommand = new CommandsEntryConfig(builder, "tps",
                "neoforge tps", "tps", false);

        modListCommand = new CommandsEntryConfig(builder, "modlist",
                "discordlinker mods", "mods", false);

        onlineListCommand = new CommandsEntryConfig(builder, "list",
                "list", "list", false);

        builder.pop(); // фиксируем конфиг commands
    }

}