package club.aurorapvp.auroraparties.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerHeadUtil {
  public static ItemStack getPlayerSkull(Player p) {
    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
    SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();

    skullMeta.setOwningPlayer(p);
    skullMeta.displayName(Component.text(p.getName()));
    skull.setItemMeta(skullMeta);

    return skull;
  }
}
