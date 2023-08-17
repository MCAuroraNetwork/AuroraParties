package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraduels.events.custom.DuelKitSelectEvent;
import club.aurorapvp.auroraduels.events.custom.QueueSelectEvent;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.DuelKitGui;
import java.util.HashSet;
import java.util.Set;

import club.aurorapvp.auroraparties.AuroraParties;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PendingDuel implements Listener {
  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player partyLeader;
  private final Duel duel;

  public PendingDuel(Player partyLeader, Duel duel) {
    this.partyLeader = partyLeader;
    this.duel = duel;

    AuroraDuels.getQueueGUI().open(partyLeader);

    PENDING_DUELS.add(this);
  }

  @EventHandler
  public void onQueueSelect(QueueSelectEvent event) {
    if (partyLeader != event.getPlayer()) {
      return;
    }

    duel.setQueue(event.getQueue());

    if (AuroraParties.isAuroraKitsInstalled()) {
      DuelKitGui.getGui(event.getPlayer()).open();
      return;
    }

    this.enterQueue();
  }

  @EventHandler
  public void onKitSelect(DuelKitSelectEvent event) {
    if (partyLeader != event.getPlayer()) {
      return;
    }

    duel.setKit(event.getKit());

    this.enterQueue();
  }

  public void enterQueue() {
    duel.getQueue().enterDuelQueue(duel);
    PENDING_DUELS.remove(this);
  }
}
