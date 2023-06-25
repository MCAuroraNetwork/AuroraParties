package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.PartyDuel;
import java.util.Set;
import org.bukkit.entity.Player;

public class PendingPartyDuel extends PendingDuel {
  private final Set<Player> players;

  public PendingPartyDuel(Player partyLeader, Set<Player> players) {
    super(partyLeader, players);

    this.players = players;
  }

  @Override
  public void startDuel() {
    queue.enterDuelQueue(new PartyDuel(players, queue, kit, false));
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
