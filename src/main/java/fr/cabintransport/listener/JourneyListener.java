package fr.cabintransport.listener;

import fr.cabintransport.CabinTransportPlugin;
import fr.cabintransport.journey.Journey;
import fr.cabintransport.manager.JourneyManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;

public class JourneyListener implements Listener {

    private final CabinTransportPlugin plugin;

    public JourneyListener(CabinTransportPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onVehicleExit(VehicleExitEvent event) {
        JourneyManager manager = plugin.getJourneyManager();
        if (!(event.getExited() instanceof Player player)) return;

        Journey journey = manager.getJourneyBySeat(event.getVehicle().getEntityId());
        if (journey == null) return;

        // Si le joueur descend manuellement (touche shift) avant la fin, on annule proprement.
        if (!journey.isNaturalEnd()) {
            manager.cancel(player);
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        JourneyManager manager = plugin.getJourneyManager();
        if (manager.isTraveling(event.getPlayer())) {
            manager.cancel(event.getPlayer());
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
