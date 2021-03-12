package protocol3.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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

	// Speedlimit monitor
	public static void scheduleSlTask()
	{
		Bukkit.getScheduler().scheduleSyncRepeatingTask(Main.instance, new Runnable()
		{

			@Override
			public void run()
			{

				double tps = LagProcessor.getTPS();

				double allowed;
				if (tps >= 15.0)
				{
					allowed = Integer.parseInt(Config.getValue("speedlimit.tier_one"));
				} else
				{
					allowed = Integer.parseInt(Config.getValue("speedlimit.tier_two"));
				}

				// This is the most accurate speed calculation from my testing.
				double slower = 1.0 - tps / 20.0;
				double adjallowed = Math.round(allowed + slower * 4.0 * allowed);

				for (Player p : Bukkit.getOnlinePlayers())
				{
					// Exempt ops from this check
					if (p.isOp())
						continue;

					if (locs.get(p.getUniqueId()) != null && !tped.contains(p.getUniqueId()))
					{
						Location prevloc = locs.get(p.getUniqueId()).clone();
						Location newloc = p.getLocation().clone();
						Vector v = newloc.subtract(prevloc).toVector();
						if (v.clone().normalize().getY() < -0.95)
						{
							locs.remove(p.getUniqueId());
							continue;
						}
						double distance = v.length();
						if (distance > adjallowed)
						{
							ServerMeta.kickWithDelay(p, Double.parseDouble(Config.getValue("speedlimit.rc_delay")));
							continue;
						}
					}
					locs.put(p.getUniqueId(), p.getLocation().clone());
					tped.remove(p.getUniqueId());
				}
			}
		}, 0L, 20L);
	}

	@EventHandler
	public void onTeleport(PlayerTeleportEvent e)
	{
		tped.add(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e)
	{
		tped.add(e.getEntity().getUniqueId());
	}

	@EventHandler
	public void onLeave(PlayerQuitEvent e)
	{
		tped.remove(e.getPlayer().getUniqueId());
		locs.remove(e.getPlayer().getUniqueId());
	}
}
