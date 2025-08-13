package club.aurorapvp.auroraparties.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.Objects;

public class InventoryUtil {

  public static int findSkullSlot(ItemStack itemStack, Inventory inventory) {
    if (!(itemStack.getItemMeta() instanceof SkullMeta meta)) {
      return -1;
    }

    int i = -1;
    for (ItemStack currentItem : inventory.getContents()) {
      i++;

      if (currentItem == null) {
        continue;
      }

      if (!(currentItem.getItemMeta() instanceof SkullMeta currentMeta)) {
        continue;
      }

      if (Objects.equals(meta.getOwningPlayer(), currentMeta.getOwningPlayer())) {
        return i;
      }
    }

    return -1;
  }

  public static ItemStack getLastSkull(Inventory inventory) {
    ItemStack lastSkull = null;

    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);
      if (item != null && item.getType().equals(Material.PLAYER_HEAD)) {
        lastSkull = item;
      }
    }

    return lastSkull;
  }

  public static void moveItem(Inventory inventory, ItemStack skull, int slot) {
    inventory.remove(skull);
    inventory.setItem(slot, skull);
  }
}
