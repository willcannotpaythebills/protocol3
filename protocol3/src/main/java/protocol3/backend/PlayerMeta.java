package protocol3.backend;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class PlayerMeta
{

	public static List<UUID> _donatorList = new ArrayList<UUID>();
	public static List<UUID> _permanentMutes = new ArrayList<UUID>();
	public static HashMap<UUID, Double> _temporaryMutes = new HashMap<UUID, Double>();

	public static HashMap<UUID, Double> Playtimes = new HashMap<UUID, Double>();

	public static HashMap<UUID, String> _lagfagList = new HashMap<UUID, String>();

	public static List<String> DonorCodes = new ArrayList<String>();
	public static List<String> UsedDonorCodes = new ArrayList<String>();

	public static boolean MuteAll = false;

	// --- GET/SET DONATOR STATUS --- //

	public static boolean isDonator(Player p)
	{
		return _donatorList.contains(p.getUniqueId());
	}

	public static void setDonator(Player p, boolean status)
	{
		if (status)
		{
			if (!_donatorList.contains(p.getUniqueId()))
			{
				_donatorList.add(p.getUniqueId());
			}
		} else
		{
			if (_donatorList.contains(p.getUniqueId()))
			{
				_donatorList.remove(p.getUniqueId());
			}
		}
		try
		{
			saveDonators();
		} catch (IOException e)
		{
			System.out.println("[protocol3] Failed to save donators.");
		}
	}

	// --- MUTES --- //

	public static boolean isMuted(Player p)
	{
		return (_temporaryMutes.keySet().contains(p.getUniqueId()) || _permanentMutes.contains(p.getUniqueId()));
	}

	public static MuteType getMuteType(Player p)
	{
		if (isMuted(p))
		{
			if (_temporaryMutes.keySet().contains(p.getUniqueId()) && !_permanentMutes.contains(p.getUniqueId()))
			{
				return MuteType.TEMPORARY;
			} else
			{
				return MuteType.PERMANENT;
			}
		} else
		{
			return MuteType.NONE;
		}
	}

	public static void setMuteType(Player p, MuteType type)
	{
		UUID uuid = p.getUniqueId();
		String muteType = "";
		if (type.equals(MuteType.NONE))
		{
			muteType = "not";
			if (_temporaryMutes.keySet().contains(uuid))
				_temporaryMutes.remove(uuid);
			if (_permanentMutes.contains(uuid))
			{
				_permanentMutes.remove(uuid);
				try
				{
					saveMuted();
				} catch (IOException e)
				{
					System.out.println("[protocol3] Failed to save mutes.");
				}
			}
		} else if (type.equals(MuteType.TEMPORARY))
		{
			muteType = "temporarily";
			if (_permanentMutes.contains(uuid))
				_permanentMutes.remove(uuid);
			if (!_temporaryMutes.keySet().contains(uuid))
				_temporaryMutes.put(uuid, 0.0);
		} else if (type.equals(MuteType.PERMANENT))
		{
			muteType = "permanently";
			if (!_permanentMutes.contains(uuid))
				_permanentMutes.add(uuid);
			if (_temporaryMutes.keySet().contains(uuid))
				_temporaryMutes.remove(uuid);
			try
			{
				saveMuted();
			} catch (IOException e)
			{
				System.out.println("[protocol3] Failed to save mutes.");
			}
		}
		p.spigot().sendMessage(new TextComponent("§cYou are now " + muteType + " muted"));
	}

	public static void tickTempMutes(double msToAdd)
	{
		for (UUID u : _temporaryMutes.keySet())
		{
			_temporaryMutes.put(u, msToAdd / 1000);
			if (msToAdd / 1000 >= 3600)
			{
				_temporaryMutes.remove(u);
			}
		}
	}

	// -- LAGFAGS -- //
	public static void setLagfag(Player p, boolean status)
	{
		if (status)
		{
			if (!_lagfagList.containsKey(p.getUniqueId()))
			{
				_lagfagList.put(p.getUniqueId(), p.getAddress().toString().split(":")[0]);
			}
		} else
		{
			if (_lagfagList.containsKey(p.getUniqueId()))
			{
				_lagfagList.remove(p.getUniqueId());
			}
		}
		try
		{
			saveLagfags();
		} catch (IOException e)
		{
			System.out.println("[protocol3] Failed to save lagfags.");
		}
	}

	public static boolean isLagfag(Player p)
	{
		return _lagfagList.containsKey(p.getUniqueId())
				|| _lagfagList.containsValue(p.getAddress().toString().split(":")[0]);
	}

	public static void saveLagfags() throws IOException
	{
		List<String> list = new ArrayList<String>();
		for (UUID u : _lagfagList.keySet())
		{
			list.add(u.toString() + ":" + _lagfagList.get(u));
		}
		Files.write(Paths.get("plugins/protocol3/lagfag.db"), String.join("\n", list).getBytes());
	}

	public static void loadLagfags() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("plugins/protocol3/lagfag.db"));
		for (String val : lines)
		{
			_lagfagList.put(UUID.fromString(val.split(":")[0]), val.split(":")[1]);
		}
	}

	// --- SAVE/LOAD DONATORS --- //

	public static void loadDonators() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("plugins/protocol3/donator.db"));
		for (String val : lines)
		{
			_donatorList.add(UUID.fromString(val));
		}
	}

	public static void saveDonators() throws IOException
	{
		List<String> list = new ArrayList<String>();
		for (UUID u : _donatorList)
		{
			list.add(u.toString());
		}
		Files.write(Paths.get("plugins/protocol3/donator.db"), String.join("\n", list).getBytes());
		Files.write(Paths.get("plugins/protocol3/codes/used.db"), String.join("\n", UsedDonorCodes).getBytes());
	}

	// --- SAVE/LOAD MUTED --- //

	public static void loadMuted() throws IOException
	{
		List<String> lines = Files.readAllLines(Paths.get("plugins/protocol3/muted.db"));
		for (String val : lines)
		{
			_permanentMutes.add(UUID.fromString(val));
		}
	}

	public static void saveMuted() throws IOException
	{
		List<String> list = new ArrayList<String>();
		for (UUID u : _permanentMutes)
		{
			list.add(u.toString());
		}
		Files.write(Paths.get("plugins/protocol3/muted.db"), String.join("\n", list).getBytes());
	}

	// --- PLAYTIME --- //
	public static void tickPlaytime(Player p, double msToAdd)
	{
		if (Playtimes.containsKey(p.getUniqueId()))
		{
			double value = Playtimes.get(p.getUniqueId());
			value += msToAdd / 1000;
			Playtimes.put(p.getUniqueId(), value);
		} else
		{
			Playtimes.put(p.getUniqueId(), msToAdd / 1000);
		}
	}

	public static double getPlaytime(OfflinePlayer p)
	{
		if (Playtimes.containsKey(p.getUniqueId()))
		{
			return Playtimes.get(p.getUniqueId());
		} else
		{
			return 0;
		}
	}

	public static int getRank(OfflinePlayer p)
	{
		if (!Playtimes.keySet().contains(p.getUniqueId()))
		{
			return 0;
		}
		Playtimes = sortByValue(Playtimes);
		int x = 0;
		for (UUID u : Playtimes.keySet())
		{
			x++;
			if (p.getUniqueId().equals(u))
			{
				break;
			}
		}
		return x;
	}

	public static HashMap<UUID, Double> getTopFivePlayers()
	{
		Playtimes = sortByValue(Playtimes);
		HashMap<UUID, Double> toRet = new HashMap<UUID, Double>();
		int x = 0;
		for (UUID u : Playtimes.keySet())
		{
			if (x <= 4)
			{
				toRet.put(u, Playtimes.get(u));
			}
			x++;
		}
		toRet = sortByValue(toRet);
		return toRet;
	}

	private static HashMap<UUID, Double> sortByValue(HashMap<UUID, Double> hm)
	{
		// Create a list from elements of HashMap
		List<Map.Entry<UUID, Double>> list = new LinkedList<Map.Entry<UUID, Double>>(hm.entrySet());

		// Sort the list
		Collections.sort(list, new Comparator<Map.Entry<UUID, Double>>()
		{
			@Override
			public int compare(Map.Entry<UUID, Double> o1, Map.Entry<UUID, Double> o2)
			{
				return -(o1.getValue()).compareTo(o2.getValue());
			}
		});

		// put data from sorted list to hashmap
		HashMap<UUID, Double> temp = new LinkedHashMap<UUID, Double>();
		for (Map.Entry<UUID, Double> aa : list)
		{
			temp.put(aa.getKey(), aa.getValue());
		}
		return temp;
	}

	public static void writePlaytime() throws IOException
	{
		List<String> list = new ArrayList<String>();
		for (UUID u : Playtimes.keySet())
		{
			list.add(u.toString() + ":" + Math.rint(Playtimes.get(u)));
		}
		Files.write(Paths.get("plugins/protocol3/playtime.db"), String.join("\n", list).getBytes());
	}

	// --- MUTE TYPE ENUM -- //

	public enum MuteType
	{
		TEMPORARY, PERMANENT, NONE
	}
}
