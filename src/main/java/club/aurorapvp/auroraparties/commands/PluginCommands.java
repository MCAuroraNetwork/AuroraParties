package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Config;
import club.aurorapvp.auroraparties.config.Lang;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("auroraparties")
@CommandPermission("auroraparties.command.auroraparties")
public class PluginCommands extends BaseCommand {

  @Subcommand("reload")
  @CommandPermission("auroraparties.command.auroraparties.reload")
  @Description("Reloads all plugin data")
  @SuppressWarnings("unused")
  public void onReload() {
    long startTime = System.currentTimeMillis();

    AuroraParties.getInstance().reloadConfig();
    AuroraParties.getInstance().getLang().reload();

    AuroraParties.getInstance()
        .getLogger()
        .info("AuroraParties reloaded in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @CatchUnknown
  @Subcommand("help")
  @CommandPermission("auroraparties.command.auroraparties.help")
  @Description("Lists all available plugin commands and their function")
  @SuppressWarnings("unused")
  public void onHelp(Player player) {
    player.sendMessage(AuroraParties.getInstance().getLang().getComponent("help-command"));
  }
}
