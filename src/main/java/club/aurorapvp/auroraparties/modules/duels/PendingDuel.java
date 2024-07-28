package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraduels.events.custom.QueueSelectEvent;
import club.aurorapvp.auroraduels.modules.Request;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.queues.Queue;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import club.aurorapvp.auroraparties.AuroraParties;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PendingDuel {

  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player duelStarter;
  private final Duel duel;

  public PendingDuel(Player duelStarter, Duel duel) {
    this.duelStarter = duelStarter;
    this.duel = duel;

    if (Request.hasPendingRequest(duelStarter)) {
      duelStarter.sendMessage(
          AuroraParties.getInstance().getLang().getComponent("you-have-request"));
      return;
    } else if (Queue.inQueue(duelStarter)) {
      duelStarter.sendMessage(AuroraParties.getInstance().getLang().getComponent("in-queue"));
      return;
    }

    AuroraDuels.getQueueGUI().open(duelStarter);

    PENDING_DUELS.add(this);
  }

  public Duel getDuel() {
    return duel;
  }

  public static Set<PendingDuel> getPendingDuels() {
    return PENDING_DUELS;
  }

  public static boolean hasPendingDuel(Player player) {
    for (PendingDuel pendingDuel : PENDING_DUELS) {
      for (Player duelMember : pendingDuel.getDuel().getPlayers()) {
        if (duelMember == player) {
          return true;
        }
      }
    }

    return false;
  }

  public void onInventoryClose(InventoryCloseEvent event) {
    if (duelStarter != event.getPlayer()) {
      return;
    }

    if (!Objects.equals(event.getInventory().getHolder(), AuroraDuels.getQueueGUI())) {
      return;
    }

    if (duel.getQueue() == null) {
      PENDING_DUELS.remove(this);
    }
  }

  public void onQueueSelect(QueueSelectEvent event) {
    if (duelStarter != event.getPlayer()) {
      return;
    }

    duel.setQueue(event.getQueue());

    if (AuroraParties.getInstance().isAuroraKitsInstalled()) {
      event.getPlayer().openInventory(KitsGUI.getGUI(event.getPlayer()).getCurrentPage());
      return;
    }

    event.getPlayer().closeInventory();
    this.enterQueue();
  }

  public void onKitSelect(KitSelectEvent event) {
    if (duelStarter != event.getPlayer()) {
      return;
    }

    duel.setKit(event.getKit());

    this.enterQueue();
  }

  public void enterQueue() {
    Party party = Party.getParty(duelStarter);

      assert party != null;
      party.setInDuel(true);

    duel.getQueue().enterDuelQueue(duel);
    PENDING_DUELS.remove(this);
  }
}
