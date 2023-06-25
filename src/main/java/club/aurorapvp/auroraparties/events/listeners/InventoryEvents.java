package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.PartyGUI;
import club.aurorapvp.auroraparties.modules.duels.TeamDuelGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryEvents implements Listener {
  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    PartyGUI.inventoryClicked(event);

    if (AuroraParties.isAuroraDuelsInstalled()) {
      TeamDuelGUI.onInventoryClick(event);
    }
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    if (AuroraParties.isAuroraDuelsInstalled()) {
      TeamDuelGUI.itemDropped(event);
    }
  }
}
