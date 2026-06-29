    package org.hodytrapl.discord_linker.commands.subcommands;

    import com.mojang.brigadier.builder.LiteralArgumentBuilder;
    import net.minecraft.commands.CommandSourceStack;
    import net.minecraft.commands.Commands;
    import net.minecraft.network.chat.Component;
    import org.hodytrapl.discord_linker.Discord_linker;
    import org.hodytrapl.discord_linker.utils.config.CommandsConfigHelper;

    import static org.hodytrapl.discord_linker.LanguageManager.getMessage;

    /**
     * Обработчик команды для перезагрузки интеграции с Discord ботом.
     * <p>
     * Эта команда полностью перезапускает Discord бота, применяя все изменения
     * в конфигурации бота. Полезно для применения обновлений настроек без
     * перезапуска сервера.
     * </p>
     *
     * @see ReloadCommand#register()
     */
    public class ReloadCommand {
        /**
         * Регистрирует подкоманду {@code /reload} для перезапуска Discord бота.
         * <p>
         * Команда доступна только пользователям с правами оператора (уровень 4).
         * </p>
         * <p>
         * Пример использования: {@code /discordlinker reload}
         * </p>
         * <p>
         * При выполнении команда:
         * <ul>
         *   <li>Останавливает текущий экземпляр Discord бота</li>
         *   <li>Перезагружает конфигурацию бота из файлов настроек</li>
         *   <li>Повторно инициализирует бота с новыми настройками</li>
         *   <li>Отправляет подтверждение отправителю команды</li>
         * </ul>
         * </p>
         *
         * @return объект {@link LiteralArgumentBuilder}, содержащий конфигурацию подкоманды
         */
        public static LiteralArgumentBuilder<CommandSourceStack> register() {
            return Commands.literal("reload")
                    .requires(source -> source.hasPermission(4)) // только операторы
                    .executes(context -> {
                        // Перезапускаем бота
                        Discord_linker.getBotManager().reloadBot();
                        context.getSource().sendSuccess(() -> Component.literal(getMessage("mod.typeminecraft.commands.subcommands.reloadcommand.botreloadsuccess")+"\n"), true);
                        return 1;
                    });
        }
    }