package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import java.util.List;
import java.util.Objects;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PartyManageGUI extends PartyGUI {
  private final Inventory inventory;

  public PartyManageGUI(Party party, ItemStack leaderSkull) {
    super(party);

    inventory = Bukkit.createInventory(
        this,
        54,
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>Manage Party"));

    ItemMeta meta = leaderSkull.getItemMeta();
    meta.lore(
        List.of(
            MiniMessage.miniMessage()
                .deserialize("<gradient:#FFAA00:#FF55FF><italic>This is the party leader"),
            Component.text("Cannot be removed")));
    leaderSkull.setItemMeta(meta);

    inventory.setItem(10, leaderSkull);

    ItemStack disbandParty = new ItemStack(Material.RED_WOOL);
    meta = disbandParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    disbandParty.setItemMeta(meta);

    inventory.setItem(53, disbandParty);

    ItemStack exit = new ItemStack(Material.ENDER_PEARL);
    meta = exit.getItemMeta();
    meta.displayName(Lang.getComponent("exit"));
    exit.setItemMeta(meta);

    inventory.setItem(45, exit);

    ItemStack status = new ItemStack(Material.REDSTONE);
    meta = status.getItemMeta();
    meta.displayName(Lang.getComponent("status"));
    meta.lore(List.of(Component.text("Private")));
    status.setItemMeta(meta);

    inventory.setItem(49, status);
  }

  @Override
  public void addMember(int index, ItemStack memberSkull) {
    ItemMeta meta = memberSkull.getItemMeta();
    meta.lore(List.of(Component.text("Click to kick from party")));

    ItemStack clone = memberSkull.clone();

    clone.setItemMeta(meta);

    inventory.setItem(index, clone);
  }

  @Override
  public void removeMember(int slot, ItemStack lastSkull) {
    inventory.clear(slot);

    InventoryUtil.moveItem(inventory, lastSkull, slot);
  }

  @Override
  @EventHandler
  public void onInventoryClick(InventoryClickEvent event) {
    if (!Objects.equals(event.getInventory().getHolder(), this)) {
      return;
    }

    event.setCancelled(true);

    ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null) {
      return;
    }

    if (event.getSlot() == 49) {
      switch (party.getStatus()) {
        case PRIVATE -> {
          party.setStatus(Party.Status.ANYONE_CAN_JOIN);
          ItemMeta meta = clickedItem.getItemMeta();
          meta.lore(List.of(Component.text("Anyone can Join")));
          clickedItem.setItemMeta(meta);
        }
        case ANYONE_CAN_JOIN -> {
          party.setStatus(Party.Status.INVITE_ONLY);
          ItemMeta meta = clickedItem.getItemMeta();
          meta.lore(List.of(Component.text("Invite Only")));
          clickedItem.setItemMeta(meta);
        }
        case INVITE_ONLY -> {
          party.setStatus(Party.Status.PRIVATE);
          ItemMeta meta = clickedItem.getItemMeta();
          meta.lore(List.of(Component.text("Private")));
          clickedItem.setItemMeta(meta);
        }
      }
    }

    if (clickedItem.getType() != Material.PLAYER_HEAD) {
      return;
    }

    String playerName =
        PlainTextComponentSerializer.plainText().serialize(clickedItem.displayName());

    Player player = Bukkit.getPlayer(playerName.substring(1, playerName.length() - 1));

    if (party.isLeader(player)) {
      return;
    }

    assert player != null;
    party.kick(player);
  }

  @Override
  public @NotNull Inventory getInventory() {
    return inventory;
  }
}
