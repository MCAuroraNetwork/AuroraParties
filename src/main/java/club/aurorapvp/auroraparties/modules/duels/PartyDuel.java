package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PartyDuel extends TeamDuel {

  public PartyDuel(Player[] team1, Player[] team2,
      Location respawnLocation, boolean ranked) {
    super(team1, team2, respawnLocation, ranked);
  }
}
