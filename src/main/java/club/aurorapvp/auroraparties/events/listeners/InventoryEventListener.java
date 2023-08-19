package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import club.aurorapvp.auroraparties.modules.guis.TeamDuelGUI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class InventoryEventListener implements Listener {
  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    PartySelectGUI.onInventoryClicked(event);
  }

  @EventHandler
  public void onItemDrop(PlayerDropItemEvent event) {
    TeamDuelGUI.onItemDropped(event);
  }
}
