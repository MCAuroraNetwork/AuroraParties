package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import org.bukkit.entity.Player;

@CommandAlias("parties")
@CommandPermission("auroraparties.command.parties")
public class PartyGUICommand extends BaseCommand {

  @Default
  @Description("Opens the party menu")
  @SuppressWarnings("unused")
  public void onOpenGUI(Player player) {
    PartySelectGUI.open(player);
  }
}
