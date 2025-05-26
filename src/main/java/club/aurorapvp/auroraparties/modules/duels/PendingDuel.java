package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.events.custom.QueueSelectEvent;
import club.aurorapvp.auroraduels.modules.DuelRequest;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.queues.DuelQueue;
import club.aurorapvp.auroraduels.modules.queues.DuelQueueGUI;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PendingDuel {

  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player duelStarter;
  private final Duel duel;

  public PendingDuel(Player duelStarter, Duel duel) {
    this.duelStarter = duelStarter;
    this.duel = duel;

    if (DuelRequest.hasPendingRequest(duelStarter)) {
      duelStarter.sendMessage(
          AuroraParties.getInstance().getLang().getComponent("you-have-request"));
      return;
    } else if (DuelQueue.inQueue(duelStarter)) {
      duelStarter.sendMessage(AuroraParties.getInstance().getLang().getComponent("in-queue"));
      return;
    }

    new DuelQueueGUI("pending-duel").open(duelStarter);

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
        if (duelMember.equals(player)) {
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

    if (!(event.getInventory().getHolder() instanceof DuelQueueGUI queueGUI)) {
      return;
    }

    if (!queueGUI.getName().equals("pending-duel")) {
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


    if (!event.getName().equals("pending-duel")) {
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
