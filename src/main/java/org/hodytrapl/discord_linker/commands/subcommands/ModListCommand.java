package org.hodytrapl.discord_linker.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;

public class ModListCommand  {
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("mods")
                .executes(context -> {
                    // Логика подкоманды /discordlinker mods
                    ModList modList = ModList.get();
                    Iterable<IModInfo> mods = modList.getMods();
                    StringBuilder modsList = new StringBuilder();
                    for (IModInfo mod : mods) {
                        String modName = mod.getDisplayName();
                        String version = mod.getVersion().toString();
                        modsList.append("- ").append(modName).append(" v").append(version).append("\n");
                    }
                    context.getSource().sendSuccess(
                            () -> Component.literal("Mod list:\n" + modsList.toString()+"\n"),
                            false
                    );
                    return 1;
                });
    }

}
