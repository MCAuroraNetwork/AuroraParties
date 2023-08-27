package club.aurorapvp.auroraparties.modules;

import club.aurorapvp.auroraduels.AuroraDuels;
import club.aurorapvp.auroraduels.events.custom.DuelKitSelectEvent;
import club.aurorapvp.auroraduels.events.custom.QueueSelectEvent;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.DuelKitGUI;
import club.aurorapvp.auroraparties.AuroraParties;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PendingDuel implements Listener {

  protected static final Set<PendingDuel> PENDING_DUELS = new HashSet<>();
  private final Player duelStarter;
  private final Duel duel;

  public PendingDuel(Player duelStarter, Duel duel) {
    this.duelStarter = duelStarter;
    this.duel = duel;

    AuroraDuels.getQueueGUI().open(duelStarter);

    PENDING_DUELS.add(this);

    Bukkit.getPluginManager().registerEvents(this, AuroraParties.INSTANCE);
  }

  public Duel getDuel() {
    return duel;
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

  @EventHandler
  public void onQueueSelect(QueueSelectEvent event) {
    if (duelStarter != event.getPlayer()) {
      return;
    }

    duel.setQueue(event.getQueue());

    if (AuroraParties.isAuroraKitsInstalled()) {
      DuelKitGUI.getGUI(event.getPlayer()).open();
      return;
    }

    this.enterQueue();
  }

  @EventHandler
  public void onKitSelect(DuelKitSelectEvent event) {
    if (duelStarter != event.getPlayer()) {
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
