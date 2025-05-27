package club.aurorapvp.auroraparties.modules.duels;

import java.util.Collection;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PartyDuel extends club.aurorapvp.auroraduels.modules.duels.teamduels.TeamDuel {

  public PartyDuel(Collection<Player> team1, Collection<Player> team2,
      Location respawnLocation, boolean ranked) {
    super(team1, team2, respawnLocation, ranked);
  }
}
