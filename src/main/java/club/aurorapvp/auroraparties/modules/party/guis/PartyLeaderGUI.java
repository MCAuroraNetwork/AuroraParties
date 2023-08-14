package club.aurorapvp.auroraparties.modules.party.guis;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

public class PartyLeaderGUI extends PartyGUI {
  private final Inventory inventory;

  public PartyLeaderGUI(Party party, ItemStack leaderSkull) {
    super(party);

    inventory = Bukkit.createInventory(
        this,
        54,
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>" + party.getName()));

    inventory.setItem(10, leaderSkull);

    ItemStack disbandParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = disbandParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    disbandParty.setItemMeta(meta);

    inventory.setItem(53, disbandParty);

    ItemStack manageParty = new ItemStack(Material.ENDER_PEARL);
    meta = manageParty.getItemMeta();
    meta.displayName(Lang.getComponent("manage-party"));
    manageParty.setItemMeta(meta);

    inventory.setItem(45, manageParty);

    ItemStack teamDuel = new ItemStack(Material.NETHERITE_SWORD);
    meta = teamDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("create-team-duel"));
    teamDuel.setItemMeta(meta);

    inventory.setItem(49, teamDuel);

    ItemStack partyDuel = new ItemStack(Material.DIAMOND_AXE);
    meta = partyDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-random-team-duel"));
    partyDuel.setItemMeta(meta);

    inventory.setItem(47, partyDuel);

    ItemStack ffaDuel = new ItemStack(Material.DIAMOND_SWORD);
    meta = ffaDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-ffa-duel"));
    ffaDuel.setItemMeta(meta);

    inventory.setItem(51, ffaDuel);
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
