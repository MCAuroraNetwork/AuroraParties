package club.aurorapvp.auroraparties.commands;

import club.aurorapvp.auroraduels.config.Lang;
import club.aurorapvp.auroraduels.modules.Request;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Split;
import co.aikar.commands.annotation.Syntax;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@CommandAlias("partyduel")
public class PartyDuelCommands extends BaseCommand {
  @Default
  @Syntax("[player]")
  @CommandCompletion("@players")
  @Description("Sends a duel request to another party")
  @SuppressWarnings("unused")
  public void onNewRequest(Player p, String playerName) {
    if (!Party.inParty(p)) {
      p.sendMessage(Lang.getComponent("not-in-party"));
      return;
    }

    Party party = Party.getParty(p);

    Player requested = Bukkit.getPlayer(playerName);

    if (requested == null) {
      p.sendMessage(Lang.getComponent("unknown-opponent"));
      return;
    }

    Party opponentParty = Party.getParty(requested);

    if (opponentParty == null) {
      p.sendMessage(Lang.getComponent("unknown-party"));
      return;
    }

    assert party != null;
    Set<Player> players = party.getMembers();
    players.add(party.getLeader());

    Player[] team1 = players.toArray(new Player[0]);

    players = opponentParty.getMembers();
    players.add(opponentParty.getLeader());

    Player[] team2 = players.toArray(new Player[0]);

    Location loc = Duel.getDuelLobby();

    if (loc == null) {
      WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(p.getWorld());

      if (worldSpawn != null) {
        loc = worldSpawn.getSpawn();
      }

      if (loc == null) {
        loc = WorldSpawn.getGlobalSpawn();
      }

      if (loc == null) {
        loc = Bukkit.getWorlds().get(0).getSpawnLocation();
      }
    }

    new Request(p, new TeamDuel(team1, team2, loc, false));
  }
}
