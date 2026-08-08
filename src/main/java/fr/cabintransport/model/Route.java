package fr.cabintransport.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Représente un trajet configurable entre un point A et un point B.
 */
public class Route {

    private final String id;
    private String displayName;
    private Location start;
    private Location end;
    private int durationTicks = 200; // 10s par défaut
    private double arcHeight = 20.0;
    private Particle particle = Particle.CLOUD;
    private Sound soundStart = Sound.ENTITY_ENDER_DRAGON_FLAP;
    private Sound soundLoop = Sound.BLOCK_AMETHYST_BLOCK_CHIME;
    private Sound soundEnd = Sound.ENTITY_PLAYER_LEVELUP;
    private boolean cabinVisual = true;
    private Material cabinBlock = Material.OAK_PLANKS;

    public Route(String id) {
        this.id = id;
        this.displayName = id;
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Location getStart() {
        return start;
    }

    public void setStart(Location start) {
        this.start = start;
    }

    public Location getEnd() {
        return end;
    }

    public void setEnd(Location end) {
        this.end = end;
    }

    public int getDurationTicks() {
        return durationTicks;
    }

    public void setDurationSeconds(double seconds) {
        this.durationTicks = Math.max(1, (int) Math.round(seconds * 20.0));
    }

    public double getDurationSeconds() {
        return durationTicks / 20.0;
    }

    public double getArcHeight() {
        return arcHeight;
    }

    public void setArcHeight(double arcHeight) {
        this.arcHeight = arcHeight;
    }

    public Particle getParticle() {
        return particle;
    }

    public void setParticle(Particle particle) {
        this.particle = particle;
    }

    public Sound getSoundStart() {
        return soundStart;
    }

    public void setSoundStart(Sound soundStart) {
        this.soundStart = soundStart;
    }

    public Sound getSoundLoop() {
        return soundLoop;
    }

    public void setSoundLoop(Sound soundLoop) {
        this.soundLoop = soundLoop;
    }

    public Sound getSoundEnd() {
        return soundEnd;
    }

    public void setSoundEnd(Sound soundEnd) {
        this.soundEnd = soundEnd;
    }

    public boolean isCabinVisual() {
        return cabinVisual;
    }

    public void setCabinVisual(boolean cabinVisual) {
        this.cabinVisual = cabinVisual;
    }

    public Material getCabinBlock() {
        return cabinBlock;
    }

    public void setCabinBlock(Material cabinBlock) {
        this.cabinBlock = cabinBlock;
    }

    public boolean isComplete() {
        return start != null && end != null;
    }
}
