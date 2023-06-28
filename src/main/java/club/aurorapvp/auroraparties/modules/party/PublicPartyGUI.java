package club.aurorapvp.auroraparties.modules.party;

import club.aurorapvp.auroraparties.config.Lang;
import club.aurorapvp.auroraparties.enums.PartyStatus;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class PublicPartyGUI {
  private static final Map<Player, Integer> PLAYER_PAGES = new HashMap<>();
  private static Inventory[] inventories;
  private static int pages;

  public static Inventory getCurrentPage(Player p) {
    int currentPage = PLAYER_PAGES.getOrDefault(p, 0);
    return inventories[currentPage];
  }

  public static void addParty(Party party) {
    Inventory lastPage = inventories[pages - 1];

    if (lastPage.getContents()[44] != null) {
      createPage(party);
    } else {
      lastPage.addItem(party.getDisplayItem());
    }
  }

  public static void removeParty(Party party) {
    for (Inventory inventory : inventories) {
      for (ItemStack itemStack : inventory.getContents()) {

        if (itemStack == null || !(itemStack.getItemMeta() instanceof SkullMeta meta)) {
          continue;
        }

        meta = (SkullMeta) itemStack.getItemMeta();
        SkullMeta partyMeta = (SkullMeta) party.getDisplayItem().getItemMeta();

        if (meta.getOwningPlayer() == partyMeta.getOwningPlayer()) {
          inventory.remove(itemStack);
        }
      }
    }
  }

  public static void createPage(Party party) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("GUIName"));
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

    inventories[pages - 1] = inventory;
    pages++;
  }

  public static void createPage(Party[] parties) {
    Inventory inventory = Bukkit.createInventory(null, 54, Lang.getComponent("parties-gui"));

    for (Party party : parties) {
      if (party.getStatus() != PartyStatus.PRIVATE) {
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

    inventories[pages] = inventory;
    pages++;
  }

  public static void nextPage(Player p) {
    int currentPage = PLAYER_PAGES.getOrDefault(p, 0);
    if (currentPage <= pages) {
      currentPage++;
    } else {
      currentPage = 0;
    }
    PLAYER_PAGES.put(p, currentPage);
    PublicPartyGUI.open(p);
  }

  public static void previousPage(Player p) {
    int currentPage = PLAYER_PAGES.getOrDefault(p, 0);
    if (currentPage >= 1) {
      currentPage--;
    } else {
      currentPage = pages;
    }
    PLAYER_PAGES.put(p, currentPage);
    PublicPartyGUI.open(p);
  }

  public static boolean isGuiInventory(Inventory inventory) {
    for (Inventory guiInventory : inventories) {
      if (inventory.equals(guiInventory)) {
        return true;
      }
    }
    return false;
  }

  public static void reload() {
    Set<Party> partySet = Party.getParties();

    pages = 0;

    for (int i = 0; i < partySet.size(); i++) {
      if (i % 44 == 0 && i != 0) {
        pages++;
      }
    }

    inventories = new Inventory[pages + 1];

    int currentPages = pages + 1;

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

      createPage(parties);
    }
  }


  public static void inventoryClicked(InventoryClickEvent event) {
    Player p = (Player) event.getWhoClicked();

    if (!PublicPartyGUI.isGuiInventory(event.getInventory())) {
      return;
    }

    event.setCancelled(true);

    if (event.getSlot() == 45) {
      PublicPartyGUI.previousPage(p);
      return;
    } else if (event.getSlot() == 53) {
      PublicPartyGUI.nextPage(p);
      return;
    }

    final ItemStack clickedItem = event.getCurrentItem();

    if (clickedItem == null || clickedItem.getType().isAir() || !(clickedItem.getItemMeta() instanceof SkullMeta meta)) {
      return;
    }

    for (Party party : Party.getParties()) {
      meta = (SkullMeta) clickedItem.getItemMeta();
      SkullMeta partyMeta = (SkullMeta) party.getDisplayItem().getItemMeta();

      if (meta.getOwningPlayer() == partyMeta.getOwningPlayer()) {
        if (party.getStatus() == PartyStatus.ANYONE_CAN_JOIN) {
          party.addMember(p);
        } else if (party.getStatus() == PartyStatus.INVITE_ONLY) {
          new PartyJoinRequest(p, party);
        }
      }
    }
  }

  public static void open(Player p) {
    reload();

    p.openInventory(PublicPartyGUI.getCurrentPage(p));
  }
}
