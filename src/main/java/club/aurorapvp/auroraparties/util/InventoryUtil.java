package club.aurorapvp.auroraparties.util;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryUtil {
  public static int findSlot(ItemStack itemStack, Inventory inventory) {
    int size = inventory.getSize();
    for (int i = 0; i < size; i++) {
      ItemStack currentItem = inventory.getItem(i);

      if (currentItem != null && currentItem.isSimilar(itemStack)) {
        return i;
      }
    }
    return -1;
  }

  public static ItemStack getLastSkull(Inventory inventory) {
    ItemStack lastSkull = null;

    for (int i = 0; i < inventory.getSize(); i++) {
      ItemStack item = inventory.getItem(i);
      if (item != null && item.getType() == Material.PLAYER_HEAD) {
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
