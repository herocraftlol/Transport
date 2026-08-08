package fr.cabintransport.journey;

import fr.cabintransport.model.Route;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Représente un voyage en cours pour un joueur donné.
 */
public class Journey {

    private final UUID playerUuid;
    private final Route route;
    private final ArmorStand seat;
    private final List<BlockDisplay> cabinEntities = new ArrayList<>();

    private BukkitTask task;
    private int currentTick = 0;

    /** true si le voyage se termine normalement (évite une double annulation via VehicleExitEvent) */
    private boolean naturalEnd = false;

    public Journey(Player player, Route route, ArmorStand seat) {
        this.playerUuid = player.getUniqueId();
        this.route = route;
        this.seat = seat;
    }

    public UUID getPlayerUuid() {
        return playerUuid;
    }

    public Route getRoute() {
        return route;
    }

    public ArmorStand getSeat() {
        return seat;
    }

    public List<BlockDisplay> getCabinEntities() {
        return cabinEntities;
    }

    public BukkitTask getTask() {
        return task;
    }

    public void setTask(BukkitTask task) {
        this.task = task;
    }

    public int getCurrentTick() {
        return currentTick;
    }

    public void incrementTick() {
        this.currentTick++;
    }

    public boolean isNaturalEnd() {
        return naturalEnd;
    }

    public void setNaturalEnd(boolean naturalEnd) {
        this.naturalEnd = naturalEnd;
    }

    public double progress() {
        return Math.min(1.0, currentTick / (double) route.getDurationTicks());
    }
}
