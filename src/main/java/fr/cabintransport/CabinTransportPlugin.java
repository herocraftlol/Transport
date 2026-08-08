package fr.cabintransport;

import fr.cabintransport.command.TransportCommand;
import fr.cabintransport.listener.JourneyListener;
import fr.cabintransport.manager.JourneyManager;
import fr.cabintransport.manager.RouteManager;
import fr.cabintransport.util.Messages;
import org.bukkit.plugin.java.JavaPlugin;

public class CabinTransportPlugin extends JavaPlugin {

    private RouteManager routeManager;
    private JourneyManager journeyManager;
    private Messages messages;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.messages = new Messages(this);
        this.routeManager = new RouteManager(this);
        this.routeManager.load();
        this.journeyManager = new JourneyManager(this);

        TransportCommand command = new TransportCommand(this);
        getCommand("transport").setExecutor(command);
        getCommand("transport").setTabCompleter(command);

        getServer().getPluginManager().registerEvents(new JourneyListener(this), this);

        getLogger().info("CabinTransport active - " + routeManager.getRoutes().size() + " trajet(s) charge(s).");
    }

    @Override
    public void onDisable() {
        if (journeyManager != null) {
            journeyManager.shutdown();
        }
    }

    public RouteManager getRouteManager() {
        return routeManager;
    }

    public JourneyManager getJourneyManager() {
        return journeyManager;
    }

    public Messages getMessages() {
        return messages;
    }
}
