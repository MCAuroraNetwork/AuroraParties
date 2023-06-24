package club.aurorapvp.auroraparties.modules;

import club.aurorapvp.auroraduels.modules.Queue;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.configs.Lang;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.auroraparties.util.PlayerHeadUtil;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class PartyGUI {
  private final Party party;
  private final HashMap<Player, ItemStack> memberSkulls = new HashMap<>();
  private final Inventory publicInventory;
  private final Inventory memberInventory;
  private final Inventory leaderInventory;
  private final Inventory manageTab;
  private final TeamDuelGUI teamDuelGUI;
  private int index = 10;

  public PartyGUI(Party party) {
    this.party = party;
    String name = party.getName();

    ItemStack leaderSkull = PlayerHeadUtil.getPlayerSkull(party.getLeader());
    memberSkulls.put(party.getLeader(), leaderSkull);

    publicInventory =
        Bukkit.createInventory(null, 54,
            MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>" + name));
    memberInventory =
        Bukkit.createInventory(null, 54,
            MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>" + name));
    leaderInventory =
        Bukkit.createInventory(null, 54,
            MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>" + name));
    manageTab = Bukkit.createInventory(null, 54,
        MiniMessage.miniMessage().deserialize("<gradient:#FFAA00:#FF55FF><bold>Manage Party"));

    publicInventory.setItem(10, leaderSkull);

    this.setupMemberInventory(leaderSkull);
    this.setupLeaderInventory(leaderSkull);
    this.setupManageTab(leaderSkull.clone());

    if (AuroraParties.isAuroraDuelsInstalled()) {
      this.teamDuelGUI = new TeamDuelGUI(leaderSkull);
    } else {
      this.teamDuelGUI = null;
    }
  }

  public TeamDuelGUI getTeamDuelGUI() {
    return teamDuelGUI;
  }

  public void open(Player p) {
    if (party.isLeader(p)) {
      p.openInventory(leaderInventory);
    } else if (party.isMember(p)) {
      p.openInventory(memberInventory);
    } else {
      p.openInventory(publicInventory);
    }
  }

  public void openManageTab(Player p) {
    p.openInventory(manageTab);
  }

  public void openTeamDuelGUI(Player p) {
    p.openInventory(teamDuelGUI.getInventory());
  }

  public void addMember(Player p) {
    incrementIndex();
    ItemStack memberSkull = PlayerHeadUtil.getPlayerSkull(p);

    memberSkulls.put(p, memberSkull);
    publicInventory.setItem(index, memberSkull);
    memberInventory.setItem(index, memberSkull);
    leaderInventory.setItem(index, memberSkull);
    this.addToManageTab(index, memberSkull.clone());

    if (AuroraParties.isAuroraDuelsInstalled()) {
      this.teamDuelGUI.addMember(memberSkull);
    }
  }

  public void removeMember(Player p) {
    decrementIndex();

    ItemStack memberSkull = memberSkulls.get(p);
    int slot = InventoryUtil.findSlot(memberSkull, publicInventory);

    List<Inventory> inventories = Arrays.asList(publicInventory, memberInventory, leaderInventory, manageTab);

    // TODO figure out why this is being so ANNOYING
    for (Inventory inventory : inventories) {
      inventory.clear(slot);
    }

    ItemStack lastSkull = InventoryUtil.getLastSkull(publicInventory);

    if (!lastSkull.isSimilar(publicInventory.getItem(index))) {
      for (Inventory inventory : inventories) {
        InventoryUtil.moveItem(inventory, lastSkull, slot);
      }
    }

    if (AuroraParties.isAuroraDuelsInstalled()) {
      this.teamDuelGUI.removeMember(memberSkull);
    }
  }

  private void decrementIndex() {
    if (index <= 10) {
      throw new IndexOutOfBoundsException("Cannot decrement below 10");
    }

    index--;

    if (index % 9 == 0) {
      index -= 2;
    }
  }

  private void incrementIndex() {
    if (index >= 43) {
      throw new IndexOutOfBoundsException("Cannot increment over 43");
    }

    index++;

    if (index % 9 == 8) {
      index += 2;
    }
  }

  private void setupMemberInventory(ItemStack leaderSkull) {
    memberInventory.setItem(10, leaderSkull);

    ItemStack leaveParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = leaveParty.getItemMeta();
    meta.displayName(Lang.getComponent("leave-party"));
    leaveParty.setItemMeta(meta);

    memberInventory.setItem(53, leaveParty);
  }

  private void setupLeaderInventory(ItemStack leaderSkull) {
    leaderInventory.setItem(10, leaderSkull);

    ItemStack disbandParty = new ItemStack(Material.RED_WOOL);
    ItemMeta meta = disbandParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    disbandParty.setItemMeta(meta);

    leaderInventory.setItem(53, disbandParty);

    ItemStack manageParty = new ItemStack(Material.ENDER_PEARL);
    meta = manageParty.getItemMeta();
    meta.displayName(Lang.getComponent("manage-party"));
    manageParty.setItemMeta(meta);

    leaderInventory.setItem(45, manageParty);

    if (!AuroraParties.isAuroraDuelsInstalled()) {
      return;
    }

    ItemStack teamDuel = new ItemStack(Material.NETHERITE_SWORD);
    meta = teamDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("create-team-duel"));
    teamDuel.setItemMeta(meta);

    leaderInventory.setItem(49, teamDuel);

    ItemStack partyDuel = new ItemStack(Material.DIAMOND_AXE);
    meta = partyDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-random-team-duel"));
    partyDuel.setItemMeta(meta);

    leaderInventory.setItem(47, partyDuel);

    ItemStack ffaDuel = new ItemStack(Material.DIAMOND_SWORD);
    meta = ffaDuel.getItemMeta();
    meta.lore(List.of(Component.text("")));
    meta.displayName(Lang.getComponent("start-ffa-duel"));
    ffaDuel.setItemMeta(meta);

    leaderInventory.setItem(51, ffaDuel);
  }

  private void setupManageTab(ItemStack leaderSkull) {
    ItemMeta meta = leaderSkull.getItemMeta();
    meta.lore(List.of(MiniMessage.miniMessage()
            .deserialize("<gradient:#FFAA00:#FF55FF><italic>This is the party leader"),
        Component.text("Cannot be removed")));
    leaderSkull.setItemMeta(meta);

    manageTab.setItem(10, leaderSkull);

    ItemStack disbandParty = new ItemStack(Material.RED_WOOL);
    meta = disbandParty.getItemMeta();
    meta.displayName(Lang.getComponent("disband-party"));
    disbandParty.setItemMeta(meta);

    manageTab.setItem(53, disbandParty);

    ItemStack exit = new ItemStack(Material.ENDER_PEARL);
    meta = exit.getItemMeta();
    meta.displayName(Lang.getComponent("exit"));
    exit.setItemMeta(meta);

    manageTab.setItem(45, exit);
   }

  private void addToManageTab(int index, ItemStack memberSkull) {
    ItemMeta meta = memberSkull.getItemMeta();
    meta.lore(List.of(Component.text("Click to kick from party")));
    memberSkull.setItemMeta(meta);

    manageTab.setItem(index, memberSkull);
  }

  private boolean isGuiInventory(Inventory inventory) {
    return inventory == publicInventory || inventory == memberInventory ||
        inventory == leaderInventory || inventory == manageTab;
  }

  private boolean isManageTab(Inventory inventory) {
    return inventory == manageTab;
  }

  private void handleManageClick(ItemStack clickedSkull) {
    String playerName =
        PlainTextComponentSerializer.plainText().serialize(clickedSkull.displayName());

    Player p = Bukkit.getPlayer(playerName.substring(1, playerName.length() - 1));

    if (party.isLeader(p)) {
      return;
    }

    assert p != null;
    party.kick(p);
  }

  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!Party.inParty(p)) {
      return;
    }

    if (!Objects.requireNonNull(Party.getParty(p)).getGUI()
        .isGuiInventory(event.getInventory())) {
      return;
    }

    Party party = Party.getParty(p);
    assert party != null;
    event.setCancelled(true);

    int slot = event.getSlot();

    if (!party.isLeader(p)) {
      if (slot == 53) {
        party.leave(p);
        p.closeInventory();
      }
      return;
    }

    ItemStack clickedItem = event.getCurrentItem();

    if (slot == 53) {
      party.disband();
      p.closeInventory();
    } else if (slot == 45) {
      if (party.getGUI().isManageTab(event.getInventory())) {
        party.getGUI().open(p);
      } else {
        party.getGUI().openManageTab(p);
      }
    }  else if (party.getGUI().isManageTab(event.getInventory()) && clickedItem != null &&
        clickedItem.getType() == Material.PLAYER_HEAD) {
      party.getGUI().handleManageClick(clickedItem);
    }

    if (!AuroraParties.isAuroraDuelsInstalled() && party.getMembers().size() >= 1) {
      return;
    }

    Set<Player> players = party.getMembers();
    players.add(party.getLeader());

    if (slot == 49) {
      party.getGUI().openTeamDuelGUI(p);
    } else if (slot == 47) {
      Queue.getQueue("default").enterDuelQueue(players, null, false);
    } else if (slot == 51) {
      Queue.getQueue("default").enterFFADuelQueue(players, null, false);
    }
  }
}
