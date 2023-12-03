package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraduels.events.custom.QueueSelectEvent;
import club.aurorapvp.aurorakits.events.custom.KitSelectEvent;
import club.aurorapvp.auroraparties.modules.duels.PendingDuel;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class PendingDuelEventListener implements Listener {
  @EventHandler
  public void onInventoryClose(InventoryCloseEvent event) {
    for (PendingDuel pendingDuel : PendingDuel.getPendingDuels()) {
      pendingDuel.onInventoryClose(event);
    }
  }

  @EventHandler
  public void onQueueSelect(QueueSelectEvent event) {
    for (PendingDuel pendingDuel : PendingDuel.getPendingDuels()) {
      pendingDuel.onQueueSelect(event);
    }
  }

  @EventHandler
  public void onKitSelect(KitSelectEvent event) {
    for (PendingDuel pendingDuel : PendingDuel.getPendingDuels()) {
      pendingDuel.onKitSelect(event);
    }
  }
}
