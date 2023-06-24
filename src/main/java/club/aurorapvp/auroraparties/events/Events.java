package club.aurorapvp.auroraparties.events;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.events.listeners.InventoryEvents;
import org.bukkit.Bukkit;

public class Events {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new InventoryEvents(), AuroraParties.INSTANCE);
  }
}
