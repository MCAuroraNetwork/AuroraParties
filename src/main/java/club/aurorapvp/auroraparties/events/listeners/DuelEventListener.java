package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraduels.events.custom.DuelRequestAcceptEvent;
import club.aurorapvp.auroraparties.modules.duels.PartyDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DuelEventListener implements Listener {

  @EventHandler
  public static void onDuelQueue(DuelRequestAcceptEvent event) {
    if (event.getDuel() instanceof PartyDuel) {
      return;
    }

    for (Player player : event.getDuel().getPlayers()) {
      if (Party.inParty(player)) {
        Party party = Party.getParty(player);

        assert party != null;

        if (party.isLeader(player)) {
          party.disband();
        } else {
          party.leave(player);
        }
      }
    }
  }
}
