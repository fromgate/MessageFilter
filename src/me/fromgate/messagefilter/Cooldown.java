package me.fromgate.messagefilter;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class Cooldown {
	
	
	public static boolean isCooldown (Player player, String id, long delay){
		long time = System.currentTimeMillis();
		long playerTime = player.hasMetadata("msgfltr-time-"+id) ? player.getMetadata("msgfltr-time-"+id).get(0).asLong() : 0;
		if (playerTime<=time)
			player.setMetadata("msgfltr-time-"+id, new FixedMetadataValue (MessageFilter.getPlugin(), time+delay));
		return playerTime>time;
	}
	
}
