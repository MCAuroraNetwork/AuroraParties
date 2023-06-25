package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.configs.Lang;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CatchUnknown;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.entity.Player;

@CommandAlias("auroraparties")
public class PluginCommands extends BaseCommand {
  // TODO
  @Subcommand("reload")
  @CommandPermission("auroraparties.admin")
  @Description("Reloads all plugin data")
  @SuppressWarnings("unused")
  public void onReload() {
    long startTime = System.currentTimeMillis();



    AuroraParties.INSTANCE.getLogger().info(
        "AuroraDuels reloaded in " + (System.currentTimeMillis() - startTime) + "ms");
  }

  @CatchUnknown
  @Subcommand("help")
  @Description("Lists all available plugin commands and their function")
  @SuppressWarnings("unused")
  public void onHelp(Player p) {
    p.sendMessage(Lang.getComponent("help-command"));
  }
}