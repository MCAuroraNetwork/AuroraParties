package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.bukkit.entity.Player;

public class PendingTeamDuel extends PendingDuel {
  private final Player[] team1;
  private final Player[] team2;

  public PendingTeamDuel(Player partyLeader, Player[] team1, Player[] team2) {
    super(partyLeader,
        Stream.concat(Arrays.stream(team1), Arrays.stream(team2)).collect(Collectors.toSet()));

    this.team1 = team1;
    this.team2 = team2;
  }

  @Override
  public void startDuel() {
    queue.enterDuelQueue(new TeamDuel(team1, team2, queue, kit, false));
  }

  // TODO lol
  @Override
  public void sendRequest() {

  }

  @Override
  public void accept(Player player) {

  }

  @Override
  public void deny() {

  }
}