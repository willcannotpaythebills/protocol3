package protocol3.backend;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.chat.TextComponent;

public class ProxyFilter
{
	private static HashMap<String, Double> _verifiedIps = new HashMap<String, Double>();
	private static List<UUID> _notifiedAdmins = new ArrayList<UUID>();
	private static int _currentTier = 0;
	
	private static long lastCheck = 0;
	private static int checkNumbers = 0;
	

	public static boolean doBlock(Player p, String ip) {
		
		if(Config.getValue("filter.enabled").equals("false")) return false;
		if(Config.getValue("filter.email").equals("example@example.com")) return false;
		if(_currentTier == 0) return false;
		
		int blockValue;
		
		if(_currentTier < 2) {
			blockValue = Integer.parseInt(Config.getValue("filter.tier_one"));
		}
		else {
			blockValue = Integer.parseInt(Config.getValue("filter.tier_two"));
		}
		
		if(_currentTier == 3) {
			if(!p.hasPlayedBefore()) {
				notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to being a new player.");
				return true;
			}
		}
		
		if(_currentTier == 4) {
			if(Bukkit.getOnlinePlayers().size() >= Integer.parseInt(Config.getValue("filter.tier_four"))) {
				notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to the server being full.");
				return true;
			}
		}
		
		if(_verifiedIps.containsKey(ip)) {
			if(_verifiedIps.get(ip) >= blockValue) {
				double value = _verifiedIps.get(ip);
				notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to a high score. ("+value+")");
				return true;
			}
			else {
				return false;
			}
		}
		
	    try {
	    	// rate limit
	    	if(System.currentTimeMillis() - lastCheck <= 4250) {
	    		return true;
	    	}
	    	checkNumbers++;
	        URL url = new URL("http://check.getipintel.net/check.php?ip="+ip+"&contact="+Config.getValue("filter.email")+"&flags=b");
	        lastCheck = System.currentTimeMillis();
	        InputStream is = url.openStream();
	        BufferedReader br = new BufferedReader(new InputStreamReader(is));
	        String content = br.readLine().trim();
	        double value = Double.parseDouble(content);
	        value = value * 100;
	        _verifiedIps.put(ip, value);
	        if(value >= blockValue) {
	        	notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to a high score. ("+value+")");
	        	return true;
	        }
	        else if(value <= blockValue && !(value < 0)) {
	        	return false;
	        }
	        else {
	        	notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to an error with the anti-proxy service. ("+value+")");
	        	_verifiedIps.remove(ip,value);
	        	return true;
	        }
	        
	    }
	    catch(Exception e) 
	    {
	    	notifyAdmins("§cA player ("+p.getName()+"@"+ip+") was disconnected due to an error, which has been printed to console.");
	    	_verifiedIps.put(ip,0D);
	    	System.out.println(e.getMessage());
	    	return false;
	    }
	}
	
	private static void notifyAdmins(String notification) {
		for(Player p : Bukkit.getOnlinePlayers()) {
    		if(p.isOp()) {
    			p.spigot().sendMessage(new TextComponent(notification));
    		}
    	}
	}
	
	public static void whitelist(String ip) {
		_verifiedIps.put(ip, 0.0);
	}
	
	public static void loadWhitelist() {
		try {
			final String plugin_work_path = "plugins/protocol3/";
			File ip_whitelist = new File(plugin_work_path + "whitelist.txt");
		
			if(ip_whitelist.exists()) {
				Files.readAllLines(ip_whitelist.toPath()).forEach(val -> {
					whitelist(val);
					System.out.println("[protocol3] Whitelisted IP "+val);
				});
			}
		}
		catch(IOException e) {
			System.out.println("[protocol3] Failed to load IP whitelist.");
		}
		
	}
	
	public static boolean enabled() {
		return !(Config.getValue("filter.enabled").equals("false") || 
				Config.getValue("filter.email").equals("example@example.com"));
	}
	
	public static boolean toggleNotifyAdmin(Player p) {
		if(!_notifiedAdmins.contains(p.getUniqueId())) {
			_notifiedAdmins.add(p.getUniqueId());
			return true;
		}
		else {
			_notifiedAdmins.remove(p.getUniqueId());
			return false;
		}
	}
	
	public static void setTier(int tier) {
		_currentTier = tier;
		_verifiedIps.clear();
	}
	
	public static int getTier() {
		return _currentTier;
	}
	
	public static int getTotalScans() {
		return checkNumbers;
	}
	
}
