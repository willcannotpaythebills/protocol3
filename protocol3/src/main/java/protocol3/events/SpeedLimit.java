package protocol3.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.util.Vector;

import protocol3.Main;
import protocol3.backend.Config;
import protocol3.backend.LagProcessor;
import protocol3.backend.ServerMeta;

public class SpeedLimit implements Listener
{
	private static HashMap<UUID, Location> locs = new HashMap<UUID, Location>();
	private static List<UUID> tped = new ArrayList<UUID>();

	public static int totalKicks = 0;

	// Speedlimit monitor
	public static void scheduleSlTask()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {

			double tps = LagProcessor.getTPS();

			double allowed = (tps >= 15.0) ? Integer.parseInt(Config.getValue("speedlimit.tier_one"))
					: Integer.parseInt(Config.getValue("speedlimit.tier_two"));

			// This is the most accurate speed calculation from my testing.
			double slower = 1.0 - tps / 20.0;
			double adjallowed = Math.round(allowed + slower * 4.0 * allowed);

			Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp())
					.filter(player -> locs.get(player.getUniqueId()) != null)
					.filter(player -> !tped.contains(player.getUniqueId()))
					.filter(player -> !locs.get(player.getUniqueId()).equals(player.getLocation())).forEach(player -> {
						Location previous_location = locs.get(player.getUniqueId()).clone();
						Location new_location = player.getLocation().clone();
						new_location.setY(previous_location.getY()); // only consider movement in X/Z

						if (previous_location.getWorld() != new_location.getWorld())
						{
							locs.remove(player.getUniqueId());
							return;
						}

						Vector v = new_location.subtract(previous_location).toVector();
						if (v.length() > adjallowed)
						{
							ServerMeta.kickWithDelay(player,
									Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
							totalKicks++;
							return;
						}

						locs.put(player.getUniqueId(), player.getLocation().clone());
						tped.remove(player.getUniqueId());
					});
		}, 20L, 20L);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onDeath(PlayerRespawnEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		tped.remove(e.getPlayer().getUniqueId());
		locs.remove(e.getPlayer().getUniqueId());
	}
}
