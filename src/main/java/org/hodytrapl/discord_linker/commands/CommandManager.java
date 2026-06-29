package org.hodytrapl.discord_linker.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.hodytrapl.discord_linker.commands.subcommands.ModListCommand;
import org.hodytrapl.discord_linker.commands.subcommands.ReloadCommand;
import org.hodytrapl.discord_linker.utils.config.CommandsConfigHelper;

/**
 * Центральный менеджер команд для регистрации всех команд Discord Linker.
 * <p>
 * Этот класс обрабатывает регистрацию корневой команды {@code /discordlinker}
 * и всех её подкоманд. Структура команд иерархическая, где подкоманды
 * предоставляют специфическую функциональность.
 * </p>
 *
 * @see ModListCommand
 * @see ReloadCommand
 */
public class CommandManager {
    /**
     * Регистрирует корневую команду {@code /discordlinker} и все подкоманды.
     * <p>
     * Метод связывает базовую команду со следующими подкомандами:
     * <ul>
     *   <li>{@code /discordlinker mods} — просмотр списка установленных модификаций (см. {@link ModListCommand#register()})</li>
     *   <li>{@code /discordlinker reload} — перезагрузка конфигурации мода (см. {@link ReloadCommand#register()})</li>
     * </ul>
     * </p>
     *
     * @param dispatcher диспетчер команд сервера, в котором регистрируется команда
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // Создаем корень команды, discordlinker
        LiteralArgumentBuilder<CommandSourceStack> rootCommand = Commands.literal("discordlinker");

        // Подключаем логику подкоманд
        rootCommand.then(ModListCommand.register());
        rootCommand.then(ReloadCommand.register());

        // Регистрируем всю конструкцию в игре
        dispatcher.register(rootCommand);
    }
}
