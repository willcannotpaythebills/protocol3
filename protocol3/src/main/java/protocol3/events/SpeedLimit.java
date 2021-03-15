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
        private static long lastCheck = -1;

	public static int totalKicks = 0;

	// Speedlimit monitor
	public static void scheduleSlTask()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, () -> {

			if (lastCheck < 0) {
				lastCheck = System.currentTimeMillis();
				return;
			}

			long now = System.currentTimeMillis();
			double duration = (now - lastCheck) / 1000.0;
			lastCheck = now;

			double tps = LagProcessor.getTPS();

			double allowed;
			if (tps >= 19) allowed = 48;		// 19-20
			else if (tps >= 18) allowed = 45;	// 18-19
			else if (tps >= 17) allowed = 42;	// 17-18
			else if (tps >= 16) allowed = 39;	// 16-17
			else if (tps >= 15) allowed = 36;	// 15-16
			else allowed = 33;					// < 15

			Bukkit.getOnlinePlayers().stream().filter(player -> !player.isOp()).forEach(player -> {
						// updated teleported player position
						if (tped.contains(player.getUniqueId())) {
							tped.remove(player.getUniqueId());
							locs.put(player.getUniqueId(), player.getLocation().clone());
							return;
						}

						// set previous location if it doesn't exist and bail
						Location previous_location = locs.get(player.getUniqueId());
						if (previous_location == null) {
							locs.put(player.getUniqueId(), player.getLocation().clone());
							return;
						}
						Location new_location = player.getLocation().clone();
						if (new_location.equals(previous_location)) {
							return;
						}
						new_location.setY(previous_location.getY()); // only consider movement in X/Z

						if (previous_location.getWorld() != new_location.getWorld())
						{
							locs.remove(player.getUniqueId());
							return;
						}

						Vector v = new_location.subtract(previous_location).toVector();
						double speed = v.length() / duration;
						if (speed > allowed)
						{
							ServerMeta.kickWithDelay(player,
									Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
							totalKicks++;
							return;
						}

						locs.put(player.getUniqueId(), player.getLocation().clone());
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
