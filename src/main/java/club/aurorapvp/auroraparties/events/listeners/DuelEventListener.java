package club.aurorapvp.auroraparties.events.listeners;

import club.aurorapvp.auroraduels.events.custom.DuelEndEvent;
import club.aurorapvp.auroraduels.events.custom.DuelRequestAcceptEvent;
import club.aurorapvp.auroraduels.events.custom.DuelRequestSendEvent;
import club.aurorapvp.auroraduels.events.custom.QueueJoinEvent;
import club.aurorapvp.auroraparties.modules.duels.PartyDuel;
import club.aurorapvp.auroraparties.modules.party.Party;
import club.aurorapvp.auroraparties.modules.party.PartyInvite;
import club.aurorapvp.auroraparties.modules.party.PartyJoinRequest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class DuelEventListener implements Listener {

  @EventHandler
  public static void onDuelAccept(DuelRequestAcceptEvent event) {
    if (event.getDuel() instanceof PartyDuel) {
      return;
    }

    for (Player player : event.getDuel().getPlayers()) {
      if (Party.inParty(player)) {
        Party party = Party.getParty(player);

        assert party != null;

        party.leave(player);
      }
    }
  }

  @EventHandler
  public static void onDuelRequestSend(DuelRequestSendEvent event) {
    if (event.getRequest().getDuel() instanceof PartyDuel) {
      return;
    }

    if (Party.inParty(event.getPlayer())) {
      Party party = Party.getParty(event.getPlayer());

      assert party != null;

      party.leave(event.getPlayer());
    }
  }

  @EventHandler
  public static void onDuelQueue(QueueJoinEvent event) {
    if (event.getDuel() instanceof PartyDuel) {
      return;
    }

    for (Player player : event.getDuel().getPlayers()) {
      if (PartyInvite.hasPendingInvite(player)) {
        for (PartyInvite invite : PartyInvite.getInvites(player)) {
          invite.deny();
        }
      }
    }

    for (Player player : event.getDuel().getPlayers()) {
      if (PartyJoinRequest.hasRequest(player)) {
        for (PartyJoinRequest request : PartyJoinRequest.getRequests(player)) {
          request.deny();
        }
      }
    }

    for (Player player : event.getDuel().getPlayers()) {
      if (Party.inParty(player)) {
        Party party = Party.getParty(player);

        assert party != null;

        if (party.isInDuel()) {
          continue;
        }

        party.leave(player);
      }
    }
  }

  @EventHandler
  public static void onDuelEnd(DuelEndEvent event) {
    for (Player player : event.getDuel().getPlayers()) {
      if (Party.inParty(player)) {
        Party party = Party.getParty(player);

        assert party != null;

        party.setInDuel(false);
      }
    }
  }
}
