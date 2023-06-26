package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.modules.duels.DuelKitGui;
import club.aurorapvp.auroraduels.modules.queues.Queue;
import club.aurorapvp.auroraduels.modules.queues.QueueGUI;
import club.aurorapvp.auroraduels.modules.requests.Request;
import club.aurorapvp.aurorakits.modules.kits.Kit;
import java.util.Set;
import org.bukkit.entity.Player;

// TODO this API abuse, of my own API, lol
public abstract class PendingDuel extends Request {
  private final Player partyLeader;
  protected Queue queue;
  protected Kit kit;

  public PendingDuel(Player partyLeader, Set<Player> players) {
    super(partyLeader, players);
    this.partyLeader = partyLeader;

    QueueGUI.open(partyLeader);
  }

  public abstract void startDuel();

  public void setQueue(Queue queue) {
    this.queue = queue;

    if (queue.isKitSet()) {
      this.startDuel();
    }

    this.startDuel();

    new DuelKitGui(partyLeader);
  }

  public void setKit(Kit kit) {
    this.kit = kit;

    this.startDuel();
  }
}
