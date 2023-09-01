package club.aurorapvp.auroraparties.events;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.events.listeners.DuelEventListener;
import club.aurorapvp.auroraparties.events.listeners.PendingDuelEventListener;
import club.aurorapvp.auroraparties.events.listeners.PlayerEventListener;
import club.aurorapvp.auroraparties.modules.guis.PartySelectGUI;
import org.bukkit.Bukkit;

public class EventManager {

  public static void init() {
    Bukkit.getPluginManager().registerEvents(new PlayerEventListener(), AuroraParties.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new DuelEventListener(), AuroraParties.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PartySelectGUI(), AuroraParties.INSTANCE);
    Bukkit.getPluginManager().registerEvents(new PendingDuelEventListener(), AuroraParties.INSTANCE);
  }
}
