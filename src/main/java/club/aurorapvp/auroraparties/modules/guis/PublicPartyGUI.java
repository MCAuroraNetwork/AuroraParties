package club.aurorapvp.auroraparties.modules.guis;

import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class PublicPartyGUI extends PartyGUI {

  private final Inventory inventory;

  public PublicPartyGUI(Party party, ItemStack leaderSkull) {
    super(party);

    inventory = Bukkit.createInventory(
        this,
        54,
        MiniMessage.miniMessage()
            .deserialize("<gradient:#FFAA00:#FF55FF><bold>" + party.getName()));

    inventory.setItem(10, leaderSkull);
  }

  @Override
  public void addMember(int index, ItemStack memberSkull) {
    inventory.setItem(index, memberSkull);
  }

  @Override
  public void removeMember(int slot, ItemStack removedSkull, ItemStack lastSkull) {
    InventoryUtil.moveItem(inventory, lastSkull, slot);

    inventory.remove(removedSkull);
  }

  @Override
  public void onInventoryClick(InventoryClickEvent event) {
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
