package club.aurorapvp.auroraparties.modules.duels;

import club.aurorapvp.auroracombat.modules.Rating;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraduels.modules.duels.teamduels.TeamDuel;
import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.util.InventoryUtil;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;

import java.util.*;

import net.kyori.adventure.text.Component;
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

  private static final NamespacedKey key =
      new NamespacedKey(AuroraParties.getInstance(), "ui-item");
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
            this, 54, AuroraParties.getInstance().getLang().getComponent("team-duel"));

    for (int row = 0; row < 6; row++) {
      int blueSlot = row * 9 + 3;
      int blackSlot = row * 9 + 4;
      int redSlot = row * 9 + 5;
      inventory.setItem(blueSlot, blueDivider);
      inventory.setItem(blackSlot, blackDivider);
      inventory.setItem(redSlot, redDivider);
    }

    ItemStack startDuel = new ItemStack(Material.NETHERITE_SWORD);
    meta = startDuel.getItemMeta();
    meta.displayName(AuroraParties.getInstance().getLang().getComponent("start-team-duel"));
    startDuel.setItemMeta(meta);

    ItemStack exit = new ItemStack(Material.BARRIER);
    meta = exit.getItemMeta();
    meta.displayName(AuroraParties.getInstance().getLang().getComponent("exit"));
    exit.setItemMeta(meta);

    ItemStack shuffle = new ItemStack(Material.NETHER_STAR);
    meta = shuffle.getItemMeta();
    meta.displayName(AuroraParties.getInstance().getLang().getComponent("shuffle-teams"));
    shuffle.setItemMeta(meta);

    ItemStack balance = new ItemStack(Material.SPECTRAL_ARROW);
    meta = balance.getItemMeta();
    meta.displayName(AuroraParties.getInstance().getLang().getComponent("balance-teams"));
    balance.setItemMeta(meta);

    inventory.setItem(4, shuffle);
    inventory.setItem(13, balance);
    inventory.setItem(49, startDuel);
    inventory.setItem(40, exit);

    if (AuroraParties.getInstance().isAuroraCombatInstalled()) {
      meta = leaderSkull.getItemMeta();
      meta.lore(
          List.of(
              Component.text(
                  "ELO: "
                      + Rating.getRating("overall_duels")
                          .getScore(party.getLeader())
                          .getPoints())));
      leaderSkull.setItemMeta(meta);
    }

    inventory.setItem(0, leaderSkull);
    Bukkit.getPluginManager().registerEvents(this, AuroraParties.getInstance());
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

    if (clickedItem.getType().equals(Material.BLACK_STAINED_GLASS_PANE)
        || clickedItem.getType().equals(Material.BLUE_STAINED_GLASS_PANE)
        || clickedItem.getType().equals(Material.RED_STAINED_GLASS_PANE)) {
      event.setCancelled(true);
      return;
    }

    if (event.getSlot() == 4) {
      event.setCancelled(true);
      shuffleTeams();
      return;
    }

    if (event.getSlot() == 13) {
      event.setCancelled(true);
      balanceTeams();
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

    ItemMeta meta = event.getCursor().getItemMeta();
    if (meta == null) {
      return;
    }

    PersistentDataContainer container = meta.getPersistentDataContainer();
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
      party
          .getLeader()
          .sendMessage(AuroraParties.getInstance().getLang().getComponent("not-enough-players"));
      party.getLeader().closeInventory();
      return;
    }

    Set<Player> team1 = new HashSet<>();
    Set<Player> team2 = new HashSet<>();

    for (String name : leftItemNames) {
      Player player = Bukkit.getPlayer(name);
      team1.add(player);
    }
    for (String name : rightItemNames) {
      Player player = Bukkit.getPlayer(name);
      team2.add(player);
    }

    Location loc = Duel.getDuelLobby();

    loc = getRespawnPoint(loc, party);

    new PendingDuel(party.getLeader(), new TeamDuel(team1, team2, loc, false));
  }

  @NotNull
  public static Location getRespawnPoint(Location loc, Party party) {
    if (AuroraParties.getInstance().isAuroraWarpsInstalled() && loc == null) {

      WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(party.getLeader().getWorld());

      if (worldSpawn != null) {
        loc = worldSpawn.getLocation();
      }

      if (loc == null) {
        loc = WorldSpawn.getGlobalSpawn();
      }
    }

    if (loc == null) {
      loc = Bukkit.getWorlds().getFirst().getSpawnLocation();
    }
    return loc;
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

  public void addMember(ItemStack memberSkull, Player player) {
    incrementIndex();

    ItemStack clone = memberSkull.clone();

    if (AuroraParties.getInstance().isAuroraCombatInstalled()) {
      ItemMeta meta = memberSkull.getItemMeta();
      meta.lore(
          List.of(
              Component.text(
                  "ELO: " + Rating.getRating("overall_duels").getScore(player).getPoints())));

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

  private void balanceTeams() {
    List<ItemStack> skulls = new ArrayList<>();
    Map<ItemStack, Integer> eloMap = new HashMap<>();

    for (int slot = 0; slot < 54; slot++) {
      ItemStack it = inventory.getItem(slot);
      if (it != null && it.getType() == Material.PLAYER_HEAD) {
        skulls.add(it.clone());
        if (AuroraParties.getInstance().isAuroraCombatInstalled()) {
          String name = getStringName(it);
          Player player = Bukkit.getPlayer(name);
          if (player != null) {
            int elo = Rating.getRating("overall_duels").getScore(player).getPoints();
            eloMap.put(it, elo);
          }
        }
        inventory.clear(slot);
      }
    }

    if (skulls.size() < 2) return;

    if (AuroraParties.getInstance().isAuroraCombatInstalled()) {
      skulls.sort((a, b) -> Integer.compare(eloMap.getOrDefault(b, 0), eloMap.getOrDefault(a, 0)));
    }

    List<ItemStack> left = new ArrayList<>();
    List<ItemStack> right = new ArrayList<>();
    int leftElo = 0, rightElo = 0;

    for (ItemStack skull : skulls) {
      int elo = eloMap.getOrDefault(skull, 0);
      if (leftElo <= rightElo) {
        left.add(skull);
        leftElo += elo;
      } else {
        right.add(skull);
        rightElo += elo;
      }
    }

    int idx = 0;
    for (int row = 0; row < 6 && idx < left.size(); row++) {
      for (int col = 0; col < 3 && idx < left.size(); col++) {
        inventory.setItem(row * 9 + col, left.get(idx++));
      }
    }
    idx = 0;
    for (int row = 0; row < 6 && idx < right.size(); row++) {
      for (int col = 6; col < 9 && idx < right.size(); col++) {
        inventory.setItem(row * 9 + col, right.get(idx++));
      }
    }

    index =
        InventoryUtil.getLastSkull(inventory) == null
            ? -1
            : InventoryUtil.findSkullSlot(InventoryUtil.getLastSkull(inventory), inventory);
  }

  private void shuffleTeams() {
    List<ItemStack> skulls = new ArrayList<>();
    for (int slot = 0; slot < 54; slot++) {
      ItemStack it = inventory.getItem(slot);
      if (it != null && it.getType() == Material.PLAYER_HEAD) {
        skulls.add(it.clone());
        inventory.clear(slot);
      }
    }

    if (skulls.size() < 2) return;

    Collections.shuffle(skulls);

    int mid = (skulls.size() + 1) / 2;
    List<ItemStack> left = skulls.subList(0, mid);
    List<ItemStack> right = skulls.subList(mid, skulls.size());

    int idx = 0;
    for (int row = 0; row < 6 && idx < left.size(); row++) {
      for (int col = 0; col < 3 && idx < left.size(); col++) {
        inventory.setItem(row * 9 + col, left.get(idx++));
      }
    }
    idx = 0;
    for (int row = 0; row < 6 && idx < right.size(); row++) {
      for (int col = 6; col < 9 && idx < right.size(); col++) {
        inventory.setItem(row * 9 + col, right.get(idx++));
      }
    }

    index =
        InventoryUtil.getLastSkull(inventory) == null
            ? -1
            : InventoryUtil.findSkullSlot(InventoryUtil.getLastSkull(inventory), inventory);
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
