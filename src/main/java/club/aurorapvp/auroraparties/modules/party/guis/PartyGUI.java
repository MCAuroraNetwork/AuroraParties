package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class PartyGUI implements InventoryHolder, Listener {
  protected final Party party;

  public PartyGUI(Party party) {
    this.party = party;
    Bukkit.getPluginManager().registerEvents(this, AuroraParties.INSTANCE);
  }

  public abstract void addMember(int index, ItemStack memberSkull);

  public abstract void removeMember(int slot, ItemStack lastSkull);

  @EventHandler
  public abstract void onInventoryClick(InventoryClickEvent event);
}
