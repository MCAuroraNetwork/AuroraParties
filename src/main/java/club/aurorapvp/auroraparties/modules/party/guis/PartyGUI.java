package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public abstract class PartyGUI implements InventoryHolder {
  protected final Party party;

  public PartyGUI(Party party) {
    this.party = party;
  }

  public abstract void addMember(int index, ItemStack memberSkull);

  public abstract void removeMember(int slot, ItemStack lastSkull);
}
