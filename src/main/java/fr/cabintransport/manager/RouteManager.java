package fr.cabintransport.manager;

import fr.cabintransport.CabinTransportPlugin;
import fr.cabintransport.model.PointRef;
import fr.cabintransport.model.Route;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Charge, sauvegarde et gère l'ensemble des trajets définis dans config.yml.
 *
 * Important : le chargement ne dépend d'AUCUN monde Bukkit déjà chargé.
 * Les coordonnées sont lues telles quelles (PointRef) et ne sont résolues
 * vers un monde réel qu'au moment de l'utilisation (voir Route#getStartLocation).
 * Cela évite de perdre des trajets si leur monde se charge après le plugin.
 */
public class RouteManager {

    private final CabinTransportPlugin plugin;
    private final Map<String, Route> routes = new LinkedHashMap<>();

    public RouteManager(CabinTransportPlugin plugin) {
        this.plugin = plugin;
    }

    public void load() {
        routes.clear();
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection section = config.getConfigurationSection("routes");
        if (section == null) {
            return;
        }
        for (String id : section.getKeys(false)) {
            ConfigurationSection r = section.getConfigurationSection(id);
            if (r == null) continue;
            Route route = new Route(id);
            route.setDisplayName(r.getString("display-name", id));

            String worldName = r.getString("world", "world");

            if (r.isConfigurationSection("start")) {
                route.setStart(readPoint(r.getConfigurationSection("start"), worldName));
            }
            if (r.isConfigurationSection("end")) {
                route.setEnd(readPoint(r.getConfigurationSection("end"), worldName));
            }

            route.setDurationSeconds(r.getDouble("duration-seconds", 10.0));
            route.setArcHeight(r.getDouble("arc-height", 20.0));

            try {
                route.setParticle(Particle.valueOf(r.getString("particle", "CLOUD").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
                plugin.getLogger().warning("Particule invalide pour le trajet " + id);
            }
            try {
                route.setSoundStart(Sound.valueOf(r.getString("sound-start", "ENTITY_ENDER_DRAGON_FLAP").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
            try {
                route.setSoundLoop(Sound.valueOf(r.getString("sound-loop", "BLOCK_AMETHYST_BLOCK_CHIME").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }
            try {
                route.setSoundEnd(Sound.valueOf(r.getString("sound-end", "ENTITY_PLAYER_LEVELUP").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }

            route.setCabinVisual(r.getBoolean("cabin-visual", true));
            route.setLockCamera(r.getBoolean("lock-camera", true));
            try {
                route.setCabinBlock(Material.valueOf(r.getString("cabin-block", "OAK_PLANKS").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }

            routes.put(id.toLowerCase(), route);

            if (!route.isReady()) {
                plugin.getLogger().warning("Le trajet '" + id + "' reference un monde non charge ('"
                        + worldName + "') : il restera indisponible tant que ce monde ne sera pas charge.");
            }
        }
    }

    private PointRef readPoint(ConfigurationSection s, String worldName) {
        double x = s.getDouble("x");
        double y = s.getDouble("y");
        double z = s.getDouble("z");
        float yaw = (float) s.getDouble("yaw", 0.0);
        float pitch = (float) s.getDouble("pitch", 0.0);
        return new PointRef(worldName, x, y, z, yaw, pitch);
    }

    private void writePoint(ConfigurationSection parent, String key, PointRef point) {
        ConfigurationSection s = parent.createSection(key);
        s.set("x", point.getX());
        s.set("y", point.getY());
        s.set("z", point.getZ());
        s.set("yaw", (double) point.getYaw());
        s.set("pitch", (double) point.getPitch());
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("routes", null);
        ConfigurationSection section = config.createSection("routes");
        for (Route route : routes.values()) {
            ConfigurationSection r = section.createSection(route.getId());
            r.set("display-name", route.getDisplayName());

            String worldName = "world";
            if (route.getStart() != null) {
                worldName = route.getStart().getWorldName();
            } else if (route.getEnd() != null) {
                worldName = route.getEnd().getWorldName();
            }
            r.set("world", worldName);

            if (route.getStart() != null) writePoint(r, "start", route.getStart());
            if (route.getEnd() != null) writePoint(r, "end", route.getEnd());
            r.set("duration-seconds", route.getDurationSeconds());
            r.set("arc-height", route.getArcHeight());
            r.set("particle", route.getParticle().name());
            r.set("sound-start", route.getSoundStart().name());
            r.set("sound-loop", route.getSoundLoop().name());
            r.set("sound-end", route.getSoundEnd().name());
            r.set("cabin-visual", route.isCabinVisual());
            r.set("lock-camera", route.isLockCamera());
            r.set("cabin-block", route.getCabinBlock().name());
        }
        plugin.saveConfig();
    }

    public Route get(String id) {
        return routes.get(id.toLowerCase());
    }

    public boolean exists(String id) {
        return routes.containsKey(id.toLowerCase());
    }

    public Route createRoute(String id) {
        Route route = new Route(id.toLowerCase());
        routes.put(id.toLowerCase(), route);
        return route;
    }

    public void deleteRoute(String id) {
        routes.remove(id.toLowerCase());
    }

    public Collection<Route> getRoutes() {
        return routes.values();
    }
}
