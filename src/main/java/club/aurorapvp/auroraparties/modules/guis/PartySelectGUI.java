package club.aurorapvp.auroraparties.modules.guis;

import club.aurorapvp.auroraparties.AuroraParties;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.modules.party.Party.Status;
import club.aurorapvp.auroraparties.modules.party.PartyJoinRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

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
import club.aurorapvp.auroraduels.modules.DuelRequest;
import club.aurorapvp.auroraduels.modules.duels.Duel;
import club.aurorapvp.auroraparties.modules.duels.PartyDuel;
import club.aurorapvp.aurorawarps.modules.WorldSpawn;
import org.bukkit.Location;
import net.kyori.adventure.text.Component;

public class PartySelectGUI implements Listener {

  private static final Map<UUID, Integer> PLAYER_PAGES = new HashMap<>();
  private static final List<Page> pages = new ArrayList<>();

  public static void init() {
    reload();
  }

  public static Inventory getCurrentPage(Player player) {
    int currentPage = PLAYER_PAGES.getOrDefault(player.getUniqueId(), 0);
    if (currentPage >= pages.size()) {
      currentPage = 0;
      PLAYER_PAGES.put(player.getUniqueId(), 0);
    }
    return pages.get(currentPage).getInventory();
  }

  public static void nextPage(Player player) {
    int currentPage = player != null ? PLAYER_PAGES.getOrDefault(player.getUniqueId(), 0) : 0;
    int nextPage = pages.isEmpty() ? 0 : (currentPage + 1) % pages.size();
    if (player != null) {
      PLAYER_PAGES.put(player.getUniqueId(), nextPage);
      player.openInventory(pages.get(nextPage).getInventory());
    }
  }

  public static void previousPage(Player player) {
    int currentPage = player != null ? PLAYER_PAGES.getOrDefault(player.getUniqueId(), 0) : 0;
    int prevPage = pages.isEmpty() ? 0 : (currentPage - 1 + pages.size()) % pages.size();
    if (player != null) {
      PLAYER_PAGES.put(player.getUniqueId(), prevPage);
      player.openInventory(pages.get(prevPage).getInventory());
    }
  }

  public static void reload() {
    Set<Party> partySet = new HashSet<>(Party.getParties());
    partySet.removeIf(party -> party.getStatus().equals(Status.PRIVATE));

    List<Party> partyList = new ArrayList<>(partySet);
    partyList.sort((p1, p2) -> {
      boolean p1Full = p1.getSize() >= p1.getMaxSize();
      boolean p2Full = p2.getSize() >= p2.getMaxSize();
      if (!p1Full && p2Full) return -1;
      if (p1Full && !p2Full) return 1;
      return 0;
    });

    int totalParties = partyList.size();
    int currentPages = (int) Math.ceil(totalParties / 45.0);

    pages.clear();
    for (int i = 0; i < currentPages; i++) {
      int start = i * 45;
      int end = Math.min(start + 45, totalParties);
      List<Party> pageParties = partyList.subList(start, end);
      pages.add(new Page(pageParties.toArray(new Party[0]), null));
    }
  }

  @EventHandler
  public static void onInventoryClicked(InventoryClickEvent event) {
    if (!(event.getInventory().getHolder() instanceof Page page)) {
      return;
    }
    event.setCancelled(true);
    Player player = (Player) event.getWhoClicked();
    int slot = event.getSlot();

    if (slot == 45) {
      previousPage(player);
    } else if (slot == 53) {
      nextPage(player);
    } else if (slot >= 0 && slot < 45) {
      Party selectedParty = page.slotToParty.get(slot);
      if (selectedParty != null) {
        if (Party.isPartyLeader(player)) {
          Party playerParty = Party.getParty(player);
          if (playerParty == selectedParty) {
            player.sendMessage(AuroraParties.getInstance().getLang().getComponent("cannot-duel-own-party"));
            return;
          }
          Location loc = Duel.getDuelLobby();
          if (loc == null) {
            WorldSpawn worldSpawn = WorldSpawn.getWorldSpawn(player.getWorld());
            if (worldSpawn != null) {
              loc = worldSpawn.getLocation();
            }
            if (loc == null) {
              loc = WorldSpawn.getGlobalSpawn();
            }
            if (loc == null) {
              loc = Bukkit.getWorlds().getFirst().getSpawnLocation();
            }
          }

          Set<Player> team1 = playerParty.getMembers();

          team1.add(playerParty.getLeader());

          Set<Player> team2 = selectedParty.getMembers();

          team2.add(selectedParty.getLeader());

          new DuelRequest(player, new PartyDuel(team1, team2, loc, false));
          player.sendMessage(AuroraParties.getInstance().getLang().formatComponent("duel-request-sent", selectedParty.getLeader().getName()));
        } else if (Party.inParty(player)) {
          player.sendMessage(AuroraParties.getInstance().getLang().getComponent("already-in-party"));
        } else {
          if (selectedParty.getSize() >= selectedParty.getMaxSize()) {
            player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-full"));
            return;
          }
          if (selectedParty.getStatus().equals(Status.ANYONE_CAN_JOIN)) {
            selectedParty.addMember(player);
          } else if (selectedParty.getStatus().equals(Status.INVITE_ONLY)) {
            new PartyJoinRequest(player, selectedParty);
          }
        }
      }
    }
  }

  public static void open(Player player) {
    reload();
    pages.forEach(page -> page.setViewer(player));
    player.openInventory(getCurrentPage(player));
  }

  public static class Page implements InventoryHolder {
    private final Inventory inventory;
    private final Map<Integer, Party> slotToParty = new HashMap<>();
    private Player viewer;

    private Page(Party[] parties, Player viewer) {
      this.viewer = viewer;
      inventory = Bukkit.createInventory(this, 54, AuroraParties.getInstance().getLang().getComponent("parties-gui"));
      updateItems(parties);
      addNavigationButtons();
    }

    private void updateItems(Party[] parties) {
      slotToParty.clear();
      inventory.clear();
      for (int slot = 0; slot < Math.min(parties.length, 45); slot++) {
        Party party = parties[slot];
        ItemStack displayItem = party.getDisplayItem().clone();
        ItemMeta meta = displayItem.getItemMeta();
        List<Component> lore = new ArrayList<>(meta.lore() != null ? meta.lore() : new ArrayList<>());

        if (viewer != null && Party.getParty(viewer) != party) {
          if (Party.isPartyLeader(viewer)) {
            lore.add(AuroraParties.getInstance().getLang().getComponent("click-to-duel"));
          } else {
            lore.add(AuroraParties.getInstance().getLang().getComponent("click-to-join"));
          }
        }

        meta.lore(lore);
        displayItem.setItemMeta(meta);
        inventory.setItem(slot, displayItem);
        slotToParty.put(slot, party);
      }
    }

    public void setViewer(Player viewer) {
      this.viewer = viewer;
      updateItems(slotToParty.values().toArray(new Party[0]));
    }

    private void addNavigationButtons() {
      ItemStack previousPage = new ItemStack(Material.PAPER);
      ItemMeta prevMeta = previousPage.getItemMeta();
      prevMeta.displayName(AuroraParties.getInstance().getLang().getComponent("previous-page"));
      previousPage.setItemMeta(prevMeta);
      inventory.setItem(45, previousPage);

      ItemStack nextPage = new ItemStack(Material.PAPER);
      ItemMeta nextMeta = nextPage.getItemMeta();
      nextMeta.displayName(AuroraParties.getInstance().getLang().getComponent("next-page"));
      nextPage.setItemMeta(nextMeta);
      inventory.setItem(53, nextPage);
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}