package protocol3.backend;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.entity.Player;

import protocol3.backend.PlayerMeta.MuteType;

public class AntiSpam
{
	private static HashMap<UUID, Long> lastChatTimes = new HashMap<UUID, Long>();
	public static HashMap<UUID, List<String>> lastChatMessages = new HashMap<UUID, List<String>>();
	
	public static HashMap<UUID, Integer> violationLevels = new HashMap<UUID, Integer>();
	
	public static boolean doSendMessage(String message, Player player) {
		
		String username = player.getName();
		UUID uuid = player.getUniqueId();
		
		if(Config.getValue("spam.enable").equals("true")) {
			
			if(player.isOp()) return true;
			
			boolean censored = false;
			boolean alreadyPunished = false;
			
			if(lastChatTimes.containsKey(uuid)) {
				if(lastChatTimes.get(uuid) + Integer.parseInt(Config.getValue("spam.wait_time")) > System.currentTimeMillis()) {
					
					censored = true;
					alreadyPunished = true;
					
					if(violationLevels.containsKey(uuid)) {
						violationLevels.put(uuid, violationLevels.get(uuid) + 1);
					}
					else {
						violationLevels.put(uuid, 1);
					}
				}
			}
			
			List<String> allTimeMessages = new ArrayList<String>();
		
			if(lastChatMessages.containsKey(uuid)) {
				// case: chat is suspected spam
				allTimeMessages = lastChatMessages.get(uuid);
				
				for(String s : allTimeMessages) {
					if(similarity(s, message) * 100 > Integer.parseInt(Config.getValue("spam.max_similarity"))) {
					
						censored = true;
					
						if(!alreadyPunished) {
							if(violationLevels.containsKey(uuid)) {
								violationLevels.put(uuid, violationLevels.get(uuid) + 1);
							}
							else {
								violationLevels.put(uuid, 1);
							}
						}
						
						break;
					}
				}
			}
			
			allTimeMessages.add(message);
			
			lastChatTimes.put(uuid, System.currentTimeMillis());
			lastChatMessages.put(uuid, allTimeMessages);
			
			if(violationLevels.containsKey(uuid)) {
				if(violationLevels.get(uuid) >= Integer.parseInt(Config.getValue("spam.minimum_vl"))) {
					if(PlayerMeta.getMuteType(player) == MuteType.NONE) {
						PlayerMeta.setMuteType(player, MuteType.TEMPORARY);
					}
				}
			}
			
			return !censored;
		}
		
		return true;
	}
	
	private static double similarity(String s1, String s2) {
	    String longer = s1, shorter = s2;
	    if (s1.length() < s2.length()) { // longer should always have greater length
	      longer = s2; shorter = s1;
	    }
	    int longerLength = longer.length();
	    if (longerLength == 0) { return 1.0; /* both strings are zero length */ }
	    /* // If you have Apache Commons Text, you can use it to calculate the edit distance:
	    LevenshteinDistance levenshteinDistance = new LevenshteinDistance();
	    return (longerLength - levenshteinDistance.apply(longer, shorter)) / (double) longerLength; */
	    return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

	}

	private static int editDistance(String s1, String s2) {
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();

		int[] costs = new int[s2.length() + 1];
		for (int i = 0; i <= s1.length(); i++) {
			int lastValue = i;
			for (int j = 0; j <= s2.length(); j++) {
				if (i == 0)
					costs[j] = j;
				else {
					if (j > 0) {
						int newValue = costs[j - 1];
						if (s1.charAt(i - 1) != s2.charAt(j - 1))
							newValue = Math.min(Math.min(newValue, lastValue),
									costs[j]) + 1;
						costs[j - 1] = lastValue;
						lastValue = newValue;
					}
				}
			}
			if (i > 0)
				costs[s2.length()] = lastValue;
		}
		return costs[s2.length()];
	}
	
}
