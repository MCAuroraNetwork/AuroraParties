package club.aurorapvp.auroraparties.events;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.events.listeners.InventoryEventListener;
import club.aurorapvp.auroraparties.events.listeners.PlayerEventListener;
import org.bukkit.Bukkit;

public class EventManager {
  public static void init() {
    Bukkit.getPluginManager().registerEvents(new InventoryEventListener(), AuroraParties.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraParties.INSTANCE);
  }
}
