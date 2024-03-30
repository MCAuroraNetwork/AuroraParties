package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import java.util.Collection;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class PartyDuel extends TeamDuel {

  public PartyDuel(Collection<Player> team1, Collection<Player> team2,
      Location respawnLocation, boolean ranked) {
    super(team1, team2, respawnLocation, ranked);
  }
}
