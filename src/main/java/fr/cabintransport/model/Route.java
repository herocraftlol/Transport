package fr.cabintransport.model;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;

/**
 * Représente un trajet configurable entre un point A et un point B.
 * Les points sont stockés en référence brute (PointRef) et résolus vers
 * un monde Bukkit uniquement à l'usage, pour ne jamais perdre un trajet
 * si son monde n'est pas encore chargé au démarrage du plugin.
 */
public class Route {

    private final String id;
    private String displayName;
    private PointRef start;
    private PointRef end;
    private int durationTicks = 200; // 10s par défaut
    private double arcHeight = 20.0;
    private Particle particle = Particle.CLOUD;
    private Sound soundStart = Sound.ENTITY_ENDER_DRAGON_FLAP;
    private Sound soundLoop = Sound.BLOCK_AMETHYST_BLOCK_CHIME;
    private Sound soundEnd = Sound.ENTITY_PLAYER_LEVELUP;
    private boolean cabinVisual = true;
    private Material cabinBlock = Material.OAK_PLANKS;
    /** Si true, la caméra du joueur est orientée dans le sens du déplacement (effet "vehicule"). */
    private boolean lockCamera = true;

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

    public PointRef getStart() {
        return start;
    }

    public void setStart(PointRef start) {
        this.start = start;
    }

    public void setStart(Location location) {
        this.start = PointRef.of(location);
    }

    public PointRef getEnd() {
        return end;
    }

    public void setEnd(PointRef end) {
        this.end = end;
    }

    public void setEnd(Location location) {
        this.end = PointRef.of(location);
    }

    /** Résout le point de départ vers une Location, ou null si le monde n'est pas chargé. */
    public Location getStartLocation() {
        return start == null ? null : start.resolve();
    }

    /** Résout le point d'arrivée vers une Location, ou null si le monde n'est pas chargé. */
    public Location getEndLocation() {
        return end == null ? null : end.resolve();
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

    public boolean isLockCamera() {
        return lockCamera;
    }

    public void setLockCamera(boolean lockCamera) {
        this.lockCamera = lockCamera;
    }

    /** true si le trajet a bien un départ et une arrivée définis (même si le monde n'est pas chargé). */
    public boolean isComplete() {
        return start != null && end != null;
    }

    /** true si le trajet est complet ET que son monde est actuellement chargé. */
    public boolean isReady() {
        return isComplete() && getStartLocation() != null && getEndLocation() != null;
    }
}
