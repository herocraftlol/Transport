package fr.cabintransport.listener;

import fr.cabintransport.CabinTransportPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class JourneyListener implements Listener {

    private final CabinTransportPlugin plugin;

    public JourneyListener(CabinTransportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (plugin.getJourneyManager().isTraveling(event.getPlayer())) {
            plugin.getJourneyManager().cancel(event.getPlayer());
        }
    }

    /** Le joueur est invulnérable pendant le trajet (chute, vide, suffocation dans le décor...). */
    @EventHandler(priority = EventPriority.HIGH)
    public void onDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (plugin.getJourneyManager().isTraveling(player)) {
            event.setCancelled(true);
        }
    }
}
