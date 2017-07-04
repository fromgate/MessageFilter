package me.fromgate.messagefilter;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class MessageSaver {
	
	public static void saveMessage(Player player, String message){
		if (!isMessageWillSaved(player)) return;
		String fileName = player.getMetadata("MessageFilterSaveFile").get(0).asString();
		int num = player.hasMetadata("MessageFilterSaveNum") ? player.getMetadata("MessageFilterSaveNum").get(0).asInt() : -1;
		if (num>0) player.setMetadata("MessageFilterSaveNum", new FixedMetadataValue (MessageFilter.getPlugin(), --num));
		Filter filter = new Filter (Filter.Type.EQUAL, fileName, message, message,"",true);
		//FilterManager.addFilter(generateId (ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message.replace(".", "_")))), filter);
		FilterManager.addFilter(generateId (fileName), filter);
	}
	
	private static String generateId (String fileName){
		//String id = message.isEmpty() ? "id" : message.length()>15 ? message.substring(0,15) : message;
		int i =1;
		String newId = fileName+"_1";
		while (FilterManager.contains(newId))
			newId = fileName+"_"+Integer.toString(i++);
		return newId;
	}

	public static void enableSave(Player player, String fileName, int num, long time) {
		player.setMetadata("MessageFilterSaveFile", new FixedMetadataValue(MessageFilter.getPlugin(),fileName));
		player.setMetadata("MessageFilterSaveNum", new FixedMetadataValue(MessageFilter.getPlugin(),num));
		player.setMetadata("MessageFilterSaveTime", new FixedMetadataValue(MessageFilter.getPlugin(),System.currentTimeMillis()+time));
	}
	
	public static boolean isMessageWillSaved(Player player){
		if (!player.hasMetadata("MessageFilterSaveFile")) return false;
		int num = player.hasMetadata("MessageFilterSaveNum") ? player.getMetadata("MessageFilterSaveNum").get(0).asInt() : -1;
		long time = player.hasMetadata("MessageFilterSaveTime") ? player.getMetadata("MessageFilterSaveTime").get(0).asLong(): 0;
		if (num<=0&&time<System.currentTimeMillis()) return false;
		return true;
	}
	

}
