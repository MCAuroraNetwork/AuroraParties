package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraparties.modules.duels.PendingDuel;
import club.aurorapvp.auroraparties.modules.duels.TeamDuelGUI;
import club.aurorapvp.auroraparties.modules.party.PartyGUI;
import club.aurorapvp.auroraparties.modules.party.PublicPartyGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryEventListener implements Listener {
  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    PartyGUI.inventoryClicked(event);
    TeamDuelGUI.onInventoryClick(event);
    PendingDuel.onInventoryClicked(event);
    PublicPartyGUI.onInventoryClicked(event);
  }

  @EventHandler
  public void onInventoryClosed(InventoryCloseEvent event) {
    PendingDuel.onInventoryClose(event);
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    TeamDuelGUI.itemDropped(event);
  }
}
