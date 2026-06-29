package org.hodytrapl.discord_linker.commands.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.neoforgespi.language.IModInfo;
import org.hodytrapl.discord_linker.utils.config.CommandsConfigHelper;

import static org.hodytrapl.discord_linker.LanguageManager.getMessage;


/**
 * Обработчик команды для отображения списка установленных модификаций.
 * <p>
 * Эта команда выводит список всех модов, загруженных на сервере, с их версиями.
 * Вывод команды форматируется в виде маркированного списка для удобства чтения.
 * </p>
 *
 * @see ModListCommand#register()
 */
public class ModListCommand  {

    /**
     * Регистрирует подкоманду {@code /mods} для отображения списка установленных модификаций.
     * <p>
     * Команда доступна только пользователям с уровнем разрешений,
     * заданным в {@link CommandsConfigHelper#getMinecraftManagementUserPermissionLevel()}.
     * </p>
     * <p>
     * Пример использования: {@code /discordlinker mods}
     * </p>
     *
     * @return объект {@link LiteralArgumentBuilder}, содержащий конфигурацию подкоманды
     */
    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("mods")
                .requires(source -> source.hasPermission(CommandsConfigHelper.getMinecraftManagementUserPermissionLevel()))
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
                            () -> Component.literal(getMessage("mod.typeminecraft.commands.subcommands.modlistcommand.modlist")+"\n" + modsList.toString()+"\n"),
                            false
                    );
                    return 1;
                });
    }

}
