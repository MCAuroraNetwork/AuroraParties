package club.aurorapvp.auroraparties.modules.guis;

import club.aurorapvp.auroracombat.modules.Rating;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.TeamDuel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.PendingDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

public class TeamDuelGUI implements InventoryHolder, Listener {

  private static final NamespacedKey key = new NamespacedKey(AuroraParties.INSTANCE, "ui-item");
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
        Bukkit.createInventory(
            this,
            54,
            Lang.getComponent("team-duel"));

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
      meta.lore(
          List.of(
              Component.text(
                  "ELO: "
                      + Rating.getRating("default_duels")
                      .getScore(party.getLeader())
                      .getPoints())));
      leaderSkull.setItemMeta(meta);
    }

    inventory.setItem(0, leaderSkull);

    Bukkit.getPluginManager().registerEvents(this, AuroraParties.INSTANCE);
  }

  @EventHandler
  public void onInventoryClicked(InventoryClickEvent event) {
    if (!Objects.equals(event.getInventory().getHolder(), this)) {
      return;
    }

    ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir()) {
      return;
    }

    if (clickedItem.getType() == Material.BLACK_STAINED_GLASS_PANE
        || clickedItem.getType() == Material.BLUE_STAINED_GLASS_PANE
        || clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
      event.setCancelled(true);
      return;
    }

    if (event.getSlot() == 49) {
      event.setCancelled(true);

      this.startTeamDuel();
      return;
    } else if (event.getSlot() == 40) {
      Player player = (Player) event.getWhoClicked();

      player.openInventory(party.getLeaderGUI().getInventory());

      event.setCancelled(true);
      return;
    }

    ItemStack cursorItem = event.getCursor();

    if (cursorItem == null) {
      return;
    }

    PersistentDataContainer container = cursorItem.getItemMeta().getPersistentDataContainer();

    if (!Boolean.TRUE.equals(container.get(key, PersistentDataType.BOOLEAN))) {
      return;
    }

    int rawSlot = event.getRawSlot();

    if (rawSlot >= 54) {
      event.setCancelled(true);
    }
  }

  @EventHandler
  public static void onItemDropped(PlayerDropItemEvent event) {
    Inventory topInventory = event.getPlayer().getOpenInventory().getTopInventory();

    if (!(topInventory.getHolder() instanceof TeamDuelGUI)) {
      return;
    }

    ItemStack item = event.getItemDrop().getItemStack();

    PersistentDataContainer container = item.getItemMeta().getPersistentDataContainer();

    if (!Boolean.TRUE.equals(container.get(key, PersistentDataType.BOOLEAN))) {
      return;
    }

    event.getItemDrop().remove();

    topInventory.addItem(item);
  }

  @Override
  public @NotNull Inventory getInventory() {
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

    if (leftItemNames.isEmpty() || rightItemNames.isEmpty()) {
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

    Location loc = Duel.getDuelLobby();

    if (AuroraParties.isAuroraWarpsInstalled() && loc == null) {

      WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(party.getLeader().getWorld());

      if (worldSpawn != null) {
        loc = worldSpawn.getSpawn();
      }

      if (loc == null) {
        loc = WorldSpawn.getGlobalSpawn();
      }
    }

    if (loc == null) {
      loc = Bukkit.getWorlds().get(0).getSpawnLocation();
    }

    new PendingDuel(party.getLeader(), new TeamDuel(team1, team2, loc, false));
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

  public void addMember(ItemStack memberSkull) {
    incrementIndex();

    ItemStack clone = memberSkull.clone();

    if (AuroraParties.isAuroraCombatInstalled()) {
      ItemMeta meta = memberSkull.getItemMeta();
      meta.lore(
          List.of(
              Component.text(
                  "ELO: "
                      + Rating.getRating("default_duels")
                      .getScore(party.getLeader())
                      .getPoints())));

      meta.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);

      clone.setItemMeta(meta);
    }

    inventory.setItem(index, clone);
  }

  public void removeMember(ItemStack memberSkull) {
    decrementIndex();

    int slot = InventoryUtil.findSkullSlot(memberSkull, inventory);

    inventory.clear(slot);

    ItemStack lastSkull = InventoryUtil.getLastSkull(inventory);

    InventoryUtil.moveItem(inventory, lastSkull, slot);
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
