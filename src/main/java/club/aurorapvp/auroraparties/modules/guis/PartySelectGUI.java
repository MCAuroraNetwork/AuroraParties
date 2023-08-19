package club.aurorapvp.auroraparties.modules.guis;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.modules.party.PartyJoinRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

public class PartySelectGUI {
  private static final Map<Player, Integer> PLAYER_PAGES = new HashMap<>();
  private static Page[] pages;
  private static int index;

  public static Inventory getCurrentPage(Player player) {
    int currentPage = PLAYER_PAGES.getOrDefault(player, 0);
    return pages[currentPage].getInventory();
  }

  public static void addParty(Party party) {
    Page lastPage = pages[index - 1];

    if (lastPage.getInventory().getContents()[44] != null) {
      pages[index] = new Page(party);
      index++;
    } else {
      lastPage.getInventory().addItem(party.getDisplayItem());
    }
  }

  public static void removeParty(Party party) {
    for (Page page : pages) {
      for (ItemStack itemStack : page.getInventory().getContents()) {

        if (itemStack == null || !(itemStack.getItemMeta() instanceof SkullMeta meta)) {
          continue;
        }

        meta = (SkullMeta) itemStack.getItemMeta();
        SkullMeta partyMeta = (SkullMeta) party.getDisplayItem().getItemMeta();

        if (meta.getOwningPlayer() == partyMeta.getOwningPlayer()) {
          page.getInventory().remove(itemStack);
        }
      }
    }
  }

  public static void nextPage(Player player) {
    int currentPage = PLAYER_PAGES.getOrDefault(player, 0);

    if (currentPage < index - 1) {
      currentPage++;
    } else {
      currentPage = 0;
    }

    PLAYER_PAGES.put(player, currentPage);
    PartySelectGUI.open(player);
  }

  public static void previousPage(Player player) {
    int currentPage = PLAYER_PAGES.getOrDefault(player, 0);

    if (currentPage > 1) {
      currentPage--;
    } else {
      currentPage = index - 1;
    }

    PLAYER_PAGES.put(player, currentPage);
    PartySelectGUI.open(player);
  }

  public static void reload() {
    Set<Party> partySet = Party.getParties();

    index = 0;

    for (int i = 0; i < partySet.size(); i++) {
      if (i % 44 == 0 && i != 0) {
        index++;
      }
    }

    pages = new Page[index + 1];

    int currentPages = index + 1;

    for (int i = 0; i < currentPages; i++) {
      int numParties = partySet.size() - i * 45;
      Party[] parties = new Party[Math.max(numParties, 0)];

      Iterator<Party> iterator = partySet.iterator();

      int counter = 0;

      while (iterator.hasNext()) {
        if (counter < i * 45) {
          iterator.next();
          counter++;
          continue;
        }

        if (counter - i * 45 >= parties.length) {
          break;
        }

        parties[counter - i * 45] = iterator.next();
        counter++;
      }

      pages[index] = new Page(parties);
      index++;
    }
  }

  public static void onInventoryClicked(InventoryClickEvent event) {
    Player player = (Player) event.getWhoClicked();

    if (!(event.getInventory() instanceof Page)) {
      return;
    }

    event.setCancelled(true);

    if (event.getSlot() == 45) {
      PartySelectGUI.previousPage(player);
      return;
    } else if (event.getSlot() == 53) {
      PartySelectGUI.nextPage(player);
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || !(clickedItem.getItemMeta() instanceof SkullMeta meta)) {
      return;
    }

    for (Party party : Party.getParties()) {
      meta = (SkullMeta) clickedItem.getItemMeta();
      SkullMeta partyMeta = (SkullMeta) party.getDisplayItem().getItemMeta();

      if (meta.getOwningPlayer() == partyMeta.getOwningPlayer()) {
        if (party.getStatus() == Party.Status.ANYONE_CAN_JOIN) {
          party.addMember(player);
        } else if (party.getStatus() == Party.Status.INVITE_ONLY) {
          new PartyJoinRequest(player, party);
        }
      }
    }
  }

  public static void open(Player player) {
    reload();

    player.openInventory(PartySelectGUI.getCurrentPage(player));
  }

  public static class Page implements InventoryHolder {
    private final Inventory inventory;

    private Page(Party[] parties) {
      inventory = Bukkit.createInventory(null, 54, Lang.getComponent("parties-gui"));

      for (Party party : parties) {
        if (party.getStatus() != Party.Status.PRIVATE) {
          inventory.addItem(party.getDisplayItem());
        }
      }

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(Lang.getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(Lang.getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      inventory.setItem(45, previousPage);
      inventory.setItem(53, nextPage);
    }

    private Page(Party party) {
      inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
      inventory.addItem(party.getDisplayItem());

      ItemMeta meta;

      ItemStack nextPage = new ItemStack(Material.PAPER);
      meta = nextPage.getItemMeta();
      meta.displayName(Lang.getComponent("next-page"));
      nextPage.setItemMeta(meta);

      ItemStack previousPage = new ItemStack(Material.PAPER);
      meta = previousPage.getItemMeta();
      meta.displayName(Lang.getComponent("previous-page"));
      previousPage.setItemMeta(meta);

      inventory.setItem(45, nextPage);
      inventory.setItem(53, nextPage);
    }

    @Override
    public @NotNull Inventory getInventory() {
      return inventory;
    }
  }
}
