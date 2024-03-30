package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraduels.modules.Request;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.duels.PartyDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("partyduel")
@CommandPermission("auroraparties.command.partyduel")
public class PartyDuelCommands extends BaseCommand {

  @Default
  @CommandCompletion("@players")
  @Syntax("<player>")
  @Description("Sends a duel request to another party")
  @SuppressWarnings("unused")
  public void onNewRequest(Player player, String playerName) {
    if (!Party.inParty(player)) {
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("not-in-party"));
      return;
    }

    Party party = Party.getParty(player);

    assert party != null;
    if (!party.isLeader(player)) {
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("no-permission"));
      return;
    }

    Player requested = Bukkit.getPlayer(playerName);

    if (requested == null) {
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("unknown-opponent"));
      return;
    }

    Party opponentParty = Party.getParty(requested);

    if (opponentParty == null) {
      player.sendMessage(AuroraParties.getInstance().getLang().getComponent("unknown-party"));
      return;
    }

    Location loc = Duel.getDuelLobby();

    if (loc == null) {
      WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(player.getWorld());

      if (worldSpawn != null) {
        loc = worldSpawn.getLocation();
      }

      if (loc == null) {
        loc = WorldSpawn.getGlobalSpawn();
      }

      if (loc == null) {
        loc = Bukkit.getWorlds().get(0).getSpawnLocation();
      }
    }

    new Request(player, new PartyDuel(party.getMembers(), opponentParty.getMembers(), loc, false));
  }
}
