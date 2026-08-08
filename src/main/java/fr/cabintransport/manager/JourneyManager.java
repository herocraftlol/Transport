package fr.cabintransport.manager;

import fr.cabintransport.CabinTransportPlugin;
import fr.cabintransport.journey.Journey;
import fr.cabintransport.model.CabinPart;
import fr.cabintransport.model.Route;
import fr.cabintransport.util.FlightMath;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Gère le déplacement en temps réel des joueurs le long des trajets :
 * création de la cabine (siège + décor), animation trajectoire par trajectoire,
 * puis nettoyage en fin de voyage.
 */
public class JourneyManager {

    private final CabinTransportPlugin plugin;
    private final Map<UUID, Journey> activeJourneys = new HashMap<>();

    public JourneyManager(CabinTransportPlugin plugin) {
        this.plugin = plugin;
    }

    public boolean isTraveling(Player player) {
        return activeJourneys.containsKey(player.getUniqueId());
    }

    public Journey getJourney(Player player) {
        return activeJourneys.get(player.getUniqueId());
    }

    public boolean start(Player player, Route route) {
        if (isTraveling(player)) {
            return false;
        }
        if (!route.isComplete()) {
            return false;
        }
        Location start = route.getStart();
        Location end = route.getEnd();
        if (start.getWorld() == null || end.getWorld() == null
                || !start.getWorld().equals(end.getWorld())) {
            return false;
        }

        // Le siège : un ArmorStand invisible qui sert de véhicule au joueur
        ArmorStand seat = start.getWorld().spawn(start, ArmorStand.class, stand -> {
            stand.setVisible(false);
            stand.setSmall(true);
            stand.setGravity(false);
            stand.setBasePlate(false);
            stand.setArms(false);
            stand.setInvulnerable(true);
            stand.setSilent(true);
            stand.setPersistent(false);
            stand.setCollidable(false);
            stand.setMarker(false);
        });

        Journey journey = new Journey(player, route, seat);

        if (route.isCabinVisual()) {
            spawnCabinDecor(journey, start, route.getCabinBlock());
        }

        // Si le joueur est déjà dans un véhicule, on le fait descendre avant
        player.leaveVehicle();
        seat.addPassenger(player);

        activeJourneys.put(player.getUniqueId(), journey);

        player.playSound(start, route.getSoundStart(), 1.0f, 1.0f);

        int period = 1; // animation à chaque tick pour un mouvement fluide
        journey.setTask(plugin.getServer().getScheduler().runTaskTimer(plugin, () -> tick(journey), 0L, period));

        return true;
    }

    private void spawnCabinDecor(Journey journey, Location origin, Material material) {
        List<CabinPart> parts = buildCabinParts(material);
        for (CabinPart part : parts) {
            BlockDisplay display = origin.getWorld().spawn(origin, BlockDisplay.class, bd -> {
                bd.setBlock(part.material().createBlockData());
                bd.setPersistent(false);
                Transformation t = new Transformation(
                        new Vector3f(-part.scaleX() / 2f, -part.scaleY() / 2f, -part.scaleZ() / 2f),
                        new AxisAngle4f(0f, 0f, 1f, 0f),
                        new Vector3f(part.scaleX(), part.scaleY(), part.scaleZ()),
                        new AxisAngle4f(0f, 0f, 1f, 0f)
                );
                bd.setTransformation(t);
                bd.setInterpolationDuration(2);
                bd.setInterpolationDelay(0);
                bd.setTeleportDuration(2);
            });
            journey.getCabinEntities().add(display);
        }
    }

    /** Construit une petite plateforme avec 4 poteaux d'angle : la "cabine". */
    private List<CabinPart> buildCabinParts(Material floorMaterial) {
        List<CabinPart> parts = new ArrayList<>();
        // plateforme
        parts.add(new CabinPart(0, -1.3, 0, 2.2f, 0.25f, 2.2f, floorMaterial));
        // poteaux d'angle
        Material post = Material.OAK_FENCE;
        double off = 0.95;
        parts.add(new CabinPart(off, -0.75, off, 0.2f, 1.1f, 0.2f, post));
        parts.add(new CabinPart(-off, -0.75, off, 0.2f, 1.1f, 0.2f, post));
        parts.add(new CabinPart(off, -0.75, -off, 0.2f, 1.1f, 0.2f, post));
        parts.add(new CabinPart(-off, -0.75, -off, 0.2f, 1.1f, 0.2f, post));
        return parts;
    }

    private void tick(Journey journey) {
        Player player = plugin.getServer().getPlayer(journey.getPlayerUuid());
        if (player == null || !player.isOnline()) {
            cleanup(journey, false);
            return;
        }

        Route route = journey.getRoute();
        double t = journey.getCurrentTick() / (double) route.getDurationTicks();

        if (t >= 1.0) {
            finish(journey, player);
            return;
        }

        Location current = FlightMath.positionAt(route.getStart(), route.getEnd(), route.getArcHeight(), t);
        journey.getSeat().teleport(current);

        // repositionne le décor de la cabine autour du siège
        List<CabinPart> parts = buildCabinParts(route.getCabinBlock());
        List<BlockDisplay> entities = journey.getCabinEntities();
        for (int i = 0; i < entities.size() && i < parts.size(); i++) {
            CabinPart part = parts.get(i);
            Vector offset = FlightMath.localToWorldOffset(part.side(), part.up(), part.forward(), current.getYaw());
            entities.get(i).teleport(current.clone().add(offset));
        }

        // particules le long du trajet
        current.getWorld().spawnParticle(route.getParticle(), current, 6, 0.4, 0.2, 0.4, 0.01);

        if (journey.getCurrentTick() % 15 == 0) {
            player.playSound(current, route.getSoundLoop(), 0.6f, 1.0f);
        }

        journey.incrementTick();
    }

    private void finish(Journey journey, Player player) {
        Route route = journey.getRoute();
        Location end = route.getEnd().clone();

        journey.setNaturalEnd(true);
        player.leaveVehicle();
        player.teleport(end);
        player.playSound(end, route.getSoundEnd(), 1.0f, 1.0f);
        end.getWorld().spawnParticle(route.getParticle(), end, 25, 0.6, 0.4, 0.6, 0.02);

        cleanup(journey, true);
    }

    /** Annule un voyage en cours (commande /transport cancel, déconnexion, sortie manuelle...). */
    public void cancel(Player player) {
        Journey journey = activeJourneys.get(player.getUniqueId());
        if (journey == null) return;
        journey.setNaturalEnd(true);
        player.leaveVehicle();
        cleanup(journey, true);
    }

    private void cleanup(Journey journey, boolean removeFromMap) {
        if (journey.getTask() != null) {
            journey.getTask().cancel();
        }
        if (journey.getSeat() != null && !journey.getSeat().isDead()) {
            journey.getSeat().remove();
        }
        for (BlockDisplay display : journey.getCabinEntities()) {
            if (!display.isDead()) {
                display.remove();
            }
        }
        if (removeFromMap) {
            activeJourneys.remove(journey.getPlayerUuid());
        }
    }

    /** Utilisé par le listener pour savoir si une sortie de véhicule est volontaire (fin normale). */
    public boolean isSeatEntity(int entityId) {
        for (Journey j : activeJourneys.values()) {
            if (j.getSeat().getEntityId() == entityId) return true;
        }
        return false;
    }

    public Journey getJourneyBySeat(int entityId) {
        for (Journey j : activeJourneys.values()) {
            if (j.getSeat().getEntityId() == entityId) return j;
        }
        return null;
    }

    public void shutdown() {
        for (Journey journey : new ArrayList<>(activeJourneys.values())) {
            Player player = plugin.getServer().getPlayer(journey.getPlayerUuid());
            if (player != null) {
                player.leaveVehicle();
            }
            cleanup(journey, false);
        }
        activeJourneys.clear();
    }
}
