package fr.cabintransport.util;

import fr.cabintransport.CabinTransportPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Messages {

    private final CabinTransportPlugin plugin;

    public Messages(CabinTransportPlugin plugin) {
        this.plugin = plugin;
    }

    private String raw(String key) {
        return plugin.getConfig().getString("messages." + key, key);
    }

    private String prefix() {
        return raw("prefix");
    }

    public void send(CommandSender sender, String key) {
        send(sender, key, null, null);
    }

    public void send(CommandSender sender, String key, String placeholder, String value) {
        String msg = raw(key);
        if (placeholder != null) {
            msg = msg.replace(placeholder, value);
        }
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', prefix() + msg));
    }
}
