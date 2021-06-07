package protocol3.backend;

import java.net.*;
import java.nio.charset.Charset;
import java.io.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;
import protocol3.Main;

public class Utilities {
	public static String calculateTime(double seconds) {
		long hours = (long) (seconds / 3600);
		long hoursRem = (long) (seconds % 3600);
		long minutes = hoursRem / 60;

		String hoursString = "";
		String minutesString = "";

		if (hours == 1) {
			hoursString = hours + " hour";
		} else {
			hoursString = hours + " hours";
		}

		if (minutes == 1) {
			minutesString = minutes + " minute";
		} else if (minutes == 0) {
			minutesString = "";
		} else {
			minutesString = minutes + " minutes";
		}

		if (minutesString.isEmpty() && hoursString.equals("0 hours")) return "None";

		if (minutes == 0) {
			return hoursString;
		} else {
			return hoursString + ", " + minutesString;
		}
	}

	public static boolean restarting = false;

	public static void restart() {
		restart(false, "§6Server is restarting");
	}
	
	public static void restart(boolean doDelay) {
		restart(doDelay, "§6Server is restarting");
	}

	public static void restart(boolean doDelay, String message) {
		if (restarting) {
			return;
		} else {
			restarting = true;
		}
		new Thread(() -> {
			try {
				if (doDelay) {
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6minutes."));
					TimeUnit.MINUTES.sleep(4);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6minute."));
					TimeUnit.SECONDS.sleep(30);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l30 §r§6seconds."));
					TimeUnit.SECONDS.sleep(15);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l15 §r§6seconds."));
					TimeUnit.SECONDS.sleep(5);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l10 §r§6seconds."));
					TimeUnit.SECONDS.sleep(5);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l5 §r§6seconds."));
					TimeUnit.SECONDS.sleep(1);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l4 §r§6seconds."));
					TimeUnit.SECONDS.sleep(1);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l3 §r§6seconds."));
					TimeUnit.SECONDS.sleep(1);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l2 §r§6seconds."));
					TimeUnit.SECONDS.sleep(1);
					Bukkit.getServer().spigot()
							.broadcast(new TextComponent("§6Server restarting in §6§l1 §r§6second."));
				TimeUnit.SECONDS.sleep(1);
				}
			} catch (Exception e) {
			}
			Bukkit.getServer().spigot().broadcast(new TextComponent("§6Server is restarting."));
			Bukkit.getScheduler().runTask(Main.instance, new Runnable() 
			{
				  @Override
				public void run() {
					  for(Player p : Bukkit.getOnlinePlayers()) {
						  p.kickPlayer(message);
					  }
				  }
	        });
			try { TimeUnit.SECONDS.sleep(2); } catch(Exception e) { }
			Bukkit.shutdown();
		}).start();
	}

	public static boolean validIP (String ip) {
	    try {
	        if ( ip == null || ip.isEmpty() ) {
	            return false;
	        }

	        String[] parts = ip.split( "\\." );
	        if ( parts.length != 4 ) {
	            return false;
	        }

	        for ( String s : parts ) {
	            int i = Integer.parseInt( s );
	            if ( (i < 0) || (i > 255) ) {
	                return false;
	            }
	        }
	        if (ip.endsWith(".") ) {
	            return false;
	        }

	        return true;
	    } catch (NumberFormatException nfe) {
	        return false;
	    }
	}

	public static int getRandomNumber(int min, int max) {
		return (int) ((Math.random() * (max - min)) + min);
	}

	public static World getWorldByDimension(World.Environment thisEnv) {

		for (World thisWorld: Bukkit.getServer().getWorlds()) {
			if (thisWorld.getEnvironment().equals(thisEnv)) return thisWorld;
		}
		return null;
	}

	static double max_x = 420; static double max_z = 420;
	static double min_x = -420; static double min_z = -420;

	public static ArrayList<Material> BannedSpawnFloors = new ArrayList<>(); static {
		BannedSpawnFloors.addAll(Arrays.asList(
				Material.CAVE_AIR, Material.VOID_AIR, Material.WALL_TORCH,
				Material.WATER, Material.LAVA, Material.FIRE));
	}
	
	public static int distanceBetweenPoints(int x1, int x2, int y1, int y2) {
        return (int)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

	public static Location getRandomSpawn(World thisWorld, Location newSpawnLocation) {

		boolean valid_spawn_location = false;

		// get random x, z coords and check them top-down from y256 for validity
		while (!valid_spawn_location) {

			// get random x, z coords within range and refer to the *center* of blocks
			double tryLocation_x = Math.rint(getRandomNumber((int)min_x, (int)max_x)) + 0.5;
			double tryLocation_z = Math.rint(getRandomNumber((int)min_z, (int)max_z)) + 0.5;

			int y = 257;
			while (y > 1) {

				Location headLoc = new Location(thisWorld, tryLocation_x, y, tryLocation_z);
				Location legsLoc = new Location(thisWorld, tryLocation_x, (double)y-1, tryLocation_z);
				Location floorLoc = new Location(thisWorld, tryLocation_x, (double)y-2, tryLocation_z);

				Block headBlock = headLoc.getBlock();
				Block legsBlock = legsLoc.getBlock();
				Block floorBlock = floorLoc.getBlock();

				y--;

				if (!headBlock.getType().equals(Material.AIR) || !legsBlock.getType().equals(Material.AIR)) {
					continue;

				} else if (!floorBlock.getType().equals(Material.AIR)) {

					// potential valid spawn, check for unwanted spawn surfaces
					if (!BannedSpawnFloors.contains(floorBlock.getType())) {
						valid_spawn_location = true;

						newSpawnLocation.setWorld(thisWorld);
						newSpawnLocation.setX(tryLocation_x);
						newSpawnLocation.setY(y);
						newSpawnLocation.setZ(tryLocation_z);

					}
					break;
				}
			}
		}
		return newSpawnLocation;
	}
}
