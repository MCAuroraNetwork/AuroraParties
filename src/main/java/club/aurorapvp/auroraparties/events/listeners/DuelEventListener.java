package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraduels.events.custom.DuelRequestAcceptEvent;
import club.aurorapvp.auroraparties.modules.duels.PartyDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DuelEventListener implements Listener {

  @EventHandler
  public static void onDuelQueue(DuelRequestAcceptEvent event) {
    if (event.getDuel() instanceof PartyDuel) {
      return;
    }

    if (Party.inParty(event.getPlayer())) {
      Party party = Party.getParty(event.getPlayer());

      assert party != null;

      if (party.isLeader(event.getPlayer())) {
        party.disband();
      } else {
        party.leave(event.getPlayer());
      }
    }
  }
}
