package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroracombat.modules.Rating;
import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TeamDuelGUI {
  private final Inventory inventory;
  private final Party party;
  private int index = 0;

  public TeamDuelGUI(Party party, ItemStack leaderSkull) {
    this.party = party;

    ItemStack blueDivider = new ItemStack(Material.BLUE_STAINED_GLASS_PANE);
    ItemMeta meta = blueDivider.getItemMeta();
    meta.displayName(Component.text(""));
    blueDivider.setItemMeta(meta);

    ItemStack blackDivider = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
    meta = blackDivider.getItemMeta();
    meta.displayName(Component.text(""));
    blackDivider.setItemMeta(meta);

    ItemStack redDivider = new ItemStack(Material.RED_STAINED_GLASS_PANE);
    meta = redDivider.getItemMeta();
    meta.displayName(Component.text(""));
    redDivider.setItemMeta(meta);

    inventory =
        Bukkit.createInventory(null, 54,
            MiniMessage.miniMessage()
                .deserialize("<gradient:#FFAA00:#FF55FF><bold>" + "New Team Duel"));

    for (int row = 0; row < 6; row++) {
      int blueSlot = row * 9 + 3;
      int blackSlot = row * 9 + 4;
      int redSlot = row * 9 + 5;

      inventory.setItem(blueSlot, blueDivider);
      inventory.setItem(blackSlot, blackDivider);
      inventory.setItem(redSlot, redDivider);
    }

    ItemStack startDuel = new ItemStack(Material.GREEN_WOOL);
    meta = startDuel.getItemMeta();
    meta.displayName(Lang.getComponent("start-team-duel"));
    startDuel.setItemMeta(meta);

    ItemStack exit = new ItemStack(Material.RED_WOOL);
    meta = exit.getItemMeta();
    meta.displayName(Lang.getComponent("exit"));
    exit.setItemMeta(meta);

    inventory.setItem(49, startDuel);
    inventory.setItem(40, exit);

    if (AuroraParties.isAuroraCombatInstalled()) {
      meta = leaderSkull.getItemMeta();
      meta.lore(List.of(
          Component.text(
              "ELO: " + Rating.getRating(party.getLeader(), "default_duels").getPoints())));
      leaderSkull.setItemMeta(meta);
    }

    inventory.setItem(0, leaderSkull);
  }

  public static void onInventoryClick(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!Party.inParty(p)) {
      return;
    }

    Party party = Party.getParty(p);

    assert party != null;
    if (!party.isLeader(p)) {
      return;
    }

    Inventory topInventory = event.getView().getTopInventory();

    if (!(party.getGUI().getTeamDuelGUI().getInventory() == topInventory)) {
      return;
    }

    ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE ||
        clickedItem.getType() == Material.BLUE_STAINED_GLASS_PANE ||
        clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
      event.setCancelled(true);
      return;
    } else if (clickedItem.getType() == Material.GREEN_WOOL) {
      event.setCancelled(true);

      party.getGUI().getTeamDuelGUI().startTeamDuel();
      return;
    } else if (clickedItem.getType() == Material.RED_WOOL) {
      party.getGUI().open(p);
      event.setCancelled(true);
      return;
    }

    ItemStack cursorItem = event.getCursor();
    int rawSlot = event.getRawSlot();

    int topInventorySize = topInventory.getSize();

    if (cursorItem != null && rawSlot >= topInventorySize) {
      event.setCancelled(true);
    }
  }

  public static void itemDropped(PlayerDropItemEvent event) {
    ItemStack droppedItem = event.getItemDrop().getItemStack();
    Player p = event.getPlayer();
    Inventory topInventory = p.getOpenInventory().getTopInventory();

    if (!Party.inParty(p)) {
      return;
    }

    Party party = Party.getParty(p);

    assert party != null;
    if (!party.isLeader(p)) {
      return;
    }

    if (!(party.getGUI().getTeamDuelGUI().getInventory() == topInventory)) {
      return;
    }

    event.setCancelled(true);

    HashMap<Integer, ItemStack> leftoverItems = topInventory.addItem(droppedItem);

    if (!leftoverItems.isEmpty()) {
      for (ItemStack leftoverItem : leftoverItems.values()) {
        p.getWorld().dropItem(p.getLocation(), leftoverItem);
      }
    }
  }

  public Inventory getInventory() {
    return inventory;
  }

  public void startTeamDuel() {
    Set<String> leftItemNames = new HashSet<>();
    Set<String> rightItemNames = new HashSet<>();

    for (int row = 0; row < 6; row++) {
      for (int column = 0; column < 3; column++) {
        int slot = row * 9 + column;
        ItemStack item = inventory.getItem(slot);
        String name = getStringName(item);

        if (name != null) {
          leftItemNames.add(name);
        }
      }
    }

    for (int row = 0; row < 6; row++) {
      for (int column = 6; column < 9; column++) {
        int slot = row * 9 + column;
        ItemStack item = inventory.getItem(slot);
        String name = getStringName(item);

        if (name != null) {
          rightItemNames.add(name);
        }
      }
    }

    if (leftItemNames.size() == 0 || rightItemNames.size() == 0) {
      party.getLeader().sendMessage(Lang.getComponent("not-enough-players"));
      party.getLeader().closeInventory();
      return;
    }

    Player[] team1 = new Player[leftItemNames.size()];
    Player[] team2 = new Player[rightItemNames.size()];

    int index = 0;
    for (String name : leftItemNames) {
      Player player = Bukkit.getPlayer(name);
      team1[index] = player;
      index++;
    }

    index = 0;
    for (String name : rightItemNames) {
      Player player = Bukkit.getPlayer(name);
      team2[index] = player;
      index++;
    }

    new PendingDuel(party.getLeader(), new TeamDuel(team1, team2, false));
  }

  private String getStringName(ItemStack item) {
    if (item != null) {
      ItemMeta meta = item.getItemMeta();
      Component name = meta.displayName();
      if (name != null) {
        return PlainTextComponentSerializer.plainText().serialize(name);
      }
    }
    return null;
  }

  public void addMember(Player p, ItemStack memberSkull) {
    incrementIndex();

    if (AuroraParties.isAuroraCombatInstalled()) {
      ItemMeta meta = memberSkull.getItemMeta();
      meta.lore(
          List.of(Component.text("ELO: " + Rating.getRating(p, "default_duels").getPoints())));
      memberSkull.setItemMeta(meta);
    }

    inventory.setItem(index, memberSkull);
  }

  public void removeMember(ItemStack memberSkull) {
    decrementIndex();

    int slot = InventoryUtil.findSlot(memberSkull, inventory);

    // TODO same as PartyGUI#removeMember
    inventory.clear(slot);

    ItemStack lastSkull = InventoryUtil.getLastSkull(inventory);

    if (!lastSkull.isSimilar(inventory.getItem(index))) {
      InventoryUtil.moveItem(inventory, lastSkull, slot);
    }
  }

  private void decrementIndex() {
    if (index <= 0) {
      throw new IndexOutOfBoundsException("Cannot decrement below 0");
    }

    index--;

    if (index % 9 == 2 || index % 9 == 5) {
      index -= 2;
    }
  }

  private void incrementIndex() {
    if (index >= 53) {
      throw new IndexOutOfBoundsException("Cannot increment over 53");
    }

    index++;

    if (index % 9 == 3 || index % 9 == 6) {
      index += 2;
    }
  }
}

