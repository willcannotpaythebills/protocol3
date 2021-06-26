package protocol3.events;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.backend.Config;
import protocol3.commands.Admin;

public class LagPrevention implements Listener {
	public static int currentWithers = 0;

	@EventHandler
	public void onEntitySpawn(EntitySpawnEvent e) {
		
		if(e.getEntity().getType().equals(EntityType.ARMOR_STAND)) {
			for(Player p : Bukkit.getOnlinePlayers()) {
				if(Admin.LagMachineNotifs.contains(p.getUniqueId())) {
					Location loc = e.getLocation();
					String coords = loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
					p.spigot().sendMessage(new TextComponent("§cLaggy Entity - Armor Stand - [" + coords + "]"));
				}
			}
		}
	}
}
