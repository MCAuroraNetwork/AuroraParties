package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.AuroraParties;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PartyStatusGUI implements InventoryHolder, Listener {

  private final Player player;
  private final Inventory inventory;

  public PartyStatusGUI(Player player) {
    this.player = player;
    this.inventory =
        Bukkit.createInventory(
            this, 9, AuroraParties.getInstance().getLang().getComponent("party-status-gui"));
    initializeItems();
    Bukkit.getPluginManager().registerEvents(this, AuroraParties.getInstance());
  }

  private void initializeItems() {
    ItemStack publicItem = new ItemStack(Material.CHEST);
    ItemMeta publicMeta = publicItem.getItemMeta();
    publicMeta.displayName(AuroraParties.getInstance().getLang().getComponent("public-party"));
    publicItem.setItemMeta(publicMeta);
    inventory.setItem(2, publicItem);

    ItemStack inviteOnlyItem = new ItemStack(Material.PAPER);
    ItemMeta inviteMeta = inviteOnlyItem.getItemMeta();
    inviteMeta.displayName(AuroraParties.getInstance().getLang().getComponent("invite-only-party"));
    inviteOnlyItem.setItemMeta(inviteMeta);
    inventory.setItem(4, inviteOnlyItem);

    ItemStack privateItem = new ItemStack(Material.ENDER_CHEST);
    ItemMeta privateMeta = privateItem.getItemMeta();
    privateMeta.displayName(AuroraParties.getInstance().getLang().getComponent("private-party"));
    privateItem.setItemMeta(privateMeta);
    inventory.setItem(6, privateItem);
  }

  public void open() {
    player.openInventory(inventory);
  }

  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!(event.getWhoClicked() instanceof Player clicker) || event.getInventory() != inventory) {
      return;
    }
    event.setCancelled(true);
    int slot = event.getSlot();
    Party.Status status = null;
    if (slot == 2) {
      status = Party.Status.ANYONE_CAN_JOIN;
    } else if (slot == 4) {
      status = Party.Status.INVITE_ONLY;
    } else if (slot == 6) {
      status = Party.Status.PRIVATE;
    }
    if (status != null) {
      new Party(clicker, status);
      clicker.openInventory(Party.getParty(clicker).getLeaderGUI().getInventory());
    }
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
