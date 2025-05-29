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
      pages.add(new Page(pageParties.toArray(new Party[0])));
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
      Party party = page.slotToParty.get(slot);
      if (party != null) {
        if (Party.inParty(player)) {
          player.sendMessage(AuroraParties.getInstance().getLang().getComponent("already-in-party"));
          return;
        }
        if (party.getSize() >= party.getMaxSize()) {
          player.sendMessage(AuroraParties.getInstance().getLang().getComponent("party-full"));
          return;
        }
        if (party.getStatus().equals(Status.ANYONE_CAN_JOIN)) {
          party.addMember(player);
        } else if (party.getStatus().equals(Status.INVITE_ONLY)) {
          new PartyJoinRequest(player, party);
        }
      }
    }
  }

  public static void open(Player player) {
    reload();
    player.openInventory(getCurrentPage(player));
  }

  public static class Page implements InventoryHolder {

    private final Inventory inventory;
    private final Map<Integer, Party> slotToParty = new HashMap<>();

    private Page(Party[] parties) {
      inventory = Bukkit.createInventory(this, 54, AuroraParties.getInstance().getLang().getComponent("parties-gui"));
      for (int slot = 0; slot < Math.min(parties.length, 45); slot++) {
        Party party = parties[slot];
        inventory.setItem(slot, party.getDisplayItem());
        slotToParty.put(slot, party);
      }
      addNavigationButtons();
    }

    private void addNavigationButtons() {
      ItemMeta meta;
      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(AuroraParties.getInstance().getLang().getComponent("previous-page"));
      previousPage.setItemMeta(meta);
      inventory.setItem(45, previousPage);

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(AuroraParties.getInstance().getLang().getComponent("next-page"));
      nextPage.setItemMeta(meta);
      inventory.setItem(53, nextPage);
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}