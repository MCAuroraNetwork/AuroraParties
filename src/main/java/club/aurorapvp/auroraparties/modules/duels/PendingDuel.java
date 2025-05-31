package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraduels.events.custom.QueueSelectionEvent;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.teamduels.FriendlyFireGUI;
import club.aurorapvp.auroraduels.modules.duels.teamduels.TeamDuel;
import club.aurorapvp.auroraduels.modules.queues.DuelQueue;
import club.aurorapvp.auroraduels.modules.queues.QueueSelectionGUI;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import club.aurorapvp.aurorakits.modules.kits.KitsGUI;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class PendingDuel implements Listener {
  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player requester;
  private final Duel duel;
  private Set<DuelQueue> selectedQueues = null;
  private boolean confirmed = false;
  private boolean entered = false;
  private boolean kitSelectionInProgress = false;

  public PendingDuel(Player requester, Duel duel) {
    this.requester = requester;
    this.duel = duel;

    if (DuelQueue.inQueue(requester)) {
      requester.sendMessage(AuroraDuels.getInstance().getLang().getComponent("in-queue"));
      return;
    }
    if (duel instanceof TeamDuel teamDuel) {
      new FriendlyFireGUI(requester, teamDuel).open();
    } else {
      new QueueSelectionGUI(requester, false, "duel").open();
    }
    PENDING_DUELS.add(this);
    Bukkit.getPluginManager().registerEvents(this, AuroraParties.getInstance());
  }

  public Duel getDuel() {
    return duel;
  }

  public static Set<PendingDuel> getPendingDuels() {
    return PENDING_DUELS;
  }

  public static boolean hasPendingDuel(Player player) {
    for (PendingDuel pendingDuel : PENDING_DUELS) {
      for (Player duelPlayer : pendingDuel.getDuel().getPlayers()) {
        if (duelPlayer.equals(player)) {
          return true;
        }
      }
    }
    return false;
  }

  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    if (requester != event.getPlayer()) {
      return;
    }

    if (event.getInventory().getHolder() instanceof FriendlyFireGUI) {
      if (duel instanceof TeamDuel) {
        Bukkit.getScheduler()
            .runTaskLater(
                AuroraParties.getInstance(),
                () -> new QueueSelectionGUI(requester, false, "duel").open(),
                1L);
      }
      return;
    }

    if (!(event.getInventory().getHolder() instanceof QueueSelectionGUI queueGUI)) {
      return;
    }

    if (!queueGUI.getContext().equals("duel")) {
      return;
    }

    if (!confirmed) {
      PENDING_DUELS.remove(this);
    }
  }

  @EventHandler
  public void onQueueSelect(QueueSelectionEvent event) {
    if (event.isCancelled()
        || requester != event.getPlayer()
        || !event.getContext().equals("duel")) {
      return;
    }

    this.selectedQueues = event.getSelectedQueues();
    confirmed = true;

    if (selectedQueues.isEmpty()) {
      requester.sendMessage(
          AuroraDuels.getInstance().getLang().getComponent("select-at-least-one"));
      PENDING_DUELS.remove(this);
      return;
    }

    if (AuroraParties.getInstance().isAuroraKitsInstalled()) {
      KitsGUI kitsGUI = KitsGUI.getGUI(requester);
      if (kitsGUI == null) {
        requester.sendMessage(
            AuroraDuels.getInstance().getLang().getComponent("error-opening-kits"));
        PENDING_DUELS.remove(this);
        return;
      }
      kitSelectionInProgress = true;
      Bukkit.getScheduler()
          .runTaskLater(
              AuroraParties.getInstance(),
              () -> requester.openInventory(kitsGUI.getChoiceGUI(true, false)),
              1L);
    } else {
      enter();
    }
  }

  @EventHandler
  public void onKitSelect(KitSelectEvent event) {
    if (requester != event.getPlayer() || !kitSelectionInProgress) {
      return;
    }

    duel.setKit(event.getKit());
    event.getPlayer().closeInventory();
    kitSelectionInProgress = false;
    enter();
  }

  public void enter() {
    if (entered) {
      return;
    }

    entered = true;
    Party party = Party.getParty(requester);
    if (party != null) {
      party.setInDuel(true);
    }
    DuelQueue selectedQueue =
        selectedQueues.toArray(new DuelQueue[0])[new Random().nextInt(selectedQueues.size())];
    duel.setQueue(selectedQueue);
    selectedQueue.enterDuelQueue(duel);
    PENDING_DUELS.remove(this);
  }
}
