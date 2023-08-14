package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PartyMemberGUI extends PartyGUI {
  private final Inventory inventory;

  public PartyMemberGUI(Party party, ItemStack leaderSkull) {
    super(party);

    inventory = Bukkit.createInventory(
        this,
        54,
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>" + party.getName()));

    inventory.setItem(10, leaderSkull);

    ItemStack leaveParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = leaveParty.getItemMeta();
    meta.displayName(Lang.getComponent("leave-party"));
    leaveParty.setItemMeta(meta);

    inventory.setItem(53, leaveParty);
  }

  @Override
  public void addMember(int index, ItemStack memberSkull) {
    inventory.setItem(index, memberSkull);
  }

  @Override
  public void removeMember(int slot, ItemStack lastSkull) {
    inventory.clear(slot);

    InventoryUtil.moveItem(inventory, lastSkull, slot);
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
