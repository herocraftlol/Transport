package fr.cabintransport.manager;

import fr.cabintransport.CabinTransportPlugin;
import fr.cabintransport.model.Route;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Charge, sauvegarde et gère l'ensemble des trajets définis dans config.yml.
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
            World world = Bukkit.getWorld(worldName);

            if (r.isConfigurationSection("start") && world != null) {
                route.setStart(readLocation(r.getConfigurationSection("start"), world));
            }
            if (r.isConfigurationSection("end") && world != null) {
                route.setEnd(readLocation(r.getConfigurationSection("end"), world));
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
            try {
                route.setCabinBlock(Material.valueOf(r.getString("cabin-block", "OAK_PLANKS").toUpperCase()));
            } catch (IllegalArgumentException ignored) {
            }

            routes.put(id.toLowerCase(), route);
        }
    }

    private Location readLocation(ConfigurationSection s, World world) {
        double x = s.getDouble("x");
        double y = s.getDouble("y");
        double z = s.getDouble("z");
        float yaw = (float) s.getDouble("yaw", 0.0);
        float pitch = (float) s.getDouble("pitch", 0.0);
        return new Location(world, x, y, z, yaw, pitch);
    }

    private void writeLocation(ConfigurationSection parent, String key, Location loc) {
        ConfigurationSection s = parent.createSection(key);
        s.set("x", loc.getX());
        s.set("y", loc.getY());
        s.set("z", loc.getZ());
        s.set("yaw", (double) loc.getYaw());
        s.set("pitch", (double) loc.getPitch());
    }

    public void save() {
        FileConfiguration config = plugin.getConfig();
        config.set("routes", null);
        ConfigurationSection section = config.createSection("routes");
        for (Route route : routes.values()) {
            ConfigurationSection r = section.createSection(route.getId());
            r.set("display-name", route.getDisplayName());
            String worldName = "world";
            if (route.getStart() != null && route.getStart().getWorld() != null) {
                worldName = route.getStart().getWorld().getName();
            } else if (route.getEnd() != null && route.getEnd().getWorld() != null) {
                worldName = route.getEnd().getWorld().getName();
            }
            r.set("world", worldName);
            if (route.getStart() != null) writeLocation(r, "start", route.getStart());
            if (route.getEnd() != null) writeLocation(r, "end", route.getEnd());
            r.set("duration-seconds", route.getDurationSeconds());
            r.set("arc-height", route.getArcHeight());
            r.set("particle", route.getParticle().name());
            r.set("sound-start", route.getSoundStart().name());
            r.set("sound-loop", route.getSoundLoop().name());
            r.set("sound-end", route.getSoundEnd().name());
            r.set("cabin-visual", route.isCabinVisual());
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
