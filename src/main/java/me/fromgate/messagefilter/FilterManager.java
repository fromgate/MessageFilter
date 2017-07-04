package me.fromgate.messagefilter;

import me.fromgate.messagefilter.Filter.Type;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FilterManager {

	private static Map<String,Filter> filters;

	public static void init(){
		filters = new HashMap<String,Filter>();
		load();
		if (filters.isEmpty()){
			filters.put("test", new Filter (Filter.Type.EQUAL, "testmessage", "test-replacement", "10s"));
			save();
		}

	}

	public static void load(){
		filters.clear();
		File dir = new File (MessageFilter.getPlugin().getDataFolder()+File.separator+"rules");
		if (!dir.exists()) {
			dir.mkdirs();
			return;
		}
		
		for (File f : dir.listFiles()){
			if (!f.getName().endsWith(".yml")) continue;
			YamlConfiguration cfg = new YamlConfiguration();
			try {
				cfg.load(f);
			} catch (Exception e) {	
				e.printStackTrace();
				return;
			}
			for (String key : cfg.getKeys(false)){
				String typeStr = cfg.getString(key+".type","EQUAL");
				Filter.Type type = Filter.Type.getByName(typeStr);
				String messageMask = cfg.getString(key+".message-mask","undefined mask");
				String replaceMask = cfg.getString(key+".replace-to","");
				String cooldownTime = cfg.getString(key+".cooldown-time","");
				boolean useColors = cfg.getBoolean(key+".use-formating",false);
				Filter filter = new Filter (type, f.getName().replace(".yml", ""), messageMask, replaceMask, cooldownTime,useColors);
				filters.put(key, filter);
			}
		}
	}


	public static void save(){
		Set<String> fileNames = new HashSet<String> ();
		for (Filter filter : filters.values())
			fileNames.add(filter.file);
		File dir = new File (MessageFilter.getPlugin().getDataFolder()+File.separator+"rules");
		if (!dir.exists()) dir.mkdirs();
		
		for (File f : dir.listFiles())
			if (f.getName().endsWith(".yml")) f.delete(); 
		
		for (String fileName : fileNames){
			File f = new File (MessageFilter.getPlugin().getDataFolder()+File.separator+"rules"+File.separator+fileName+".yml");
			YamlConfiguration cfg = new YamlConfiguration();
			for (String key: filters.keySet()){
				Filter filter= filters.get(key);	
				if (!filter.file.equalsIgnoreCase(fileName)) continue;
				cfg.set(key+".type", filter.type.name());
				cfg.set(key+".message-mask", filter.messageMask);
				cfg.set(key+".replace-to", filter.replaceMask);
				cfg.set(key+".cooldown-time", filter.cooldownTime);
				cfg.set(key+".use-formating",filter.useColors);
			}
			try {
				cfg.save(f);
			}  catch (Exception e){
				e.printStackTrace();
			}
		}
	}

	public static String processMessage (Player player, String message){
		for (String key : filters.keySet()){
			Filter filter = filters.get(key);
			String newMessage= filter.processMessage(player, key, message);
			if (!newMessage.equals(message)) return newMessage;
		}
		return message;
	}

	public static boolean isExist(String id) {
		return filters.containsKey(id);
	}

	public static boolean addFilter (String id, Filter filter){
		if (filters.containsKey(id)) return false;
		filters.put(id, filter);
		save();
		return true;
	}
	public static boolean addFilter(String id, String fileName) {
		if (filters.containsKey(id)) return false;
		Filter filter = new Filter (fileName);
		filters.put(id, filter);
		save();
		return true;
	}

	public static boolean setType(String id, String value) {
		if (!Type.isValid(value)) return false;
		Type type = Type.getByName(value);
		filters.get(id).type = type;
		save();
		return true;
	}

	public static boolean setGroup(String id, String value) {
		if (value.isEmpty()) return false;
		filters.get(id).file = value;
		save();
		return true;
	}

	public static boolean setInputMask(String id, String value) {
		filters.get(id).messageMask = value.isEmpty() ? "default message" : value; 
		save();
		return true;
	}

	public static boolean setOutputMask(String id, String value) {
		filters.get(id).replaceMask = value;
		save();
		return true;
	}

	public static boolean setCooldown(String id, String value) {
		if (MessageFilter.getUtil().parseTime(value)==0) filters.get(id).cooldownTime = ""; 
		else filters.get(id).cooldownTime = value;
		save();
		return true;
	}

	public static void removeFilter(String id) {
		if (!filters.containsKey(id)) return; 
		filters.remove(id);
		save();
	}

	public static List<String> getList(String mask){
		List<String> list = new ArrayList<String>();
		for (String key : filters.keySet()){
			if (mask.isEmpty()||(!mask.isEmpty()&&key.contains(mask))) 
				list.add("&6"+key+"&a : &e"+filters.get(key).toString());
		}
		return list;
	}
	
	
	public static String[] getInfo (String id){
		if (!isExist(id)) return null;
		String[] ln = filters.get(id).getInfo();
		ln[0] = id;
		return ln;
	}

	public static boolean contains(String id) {
		return filters.containsKey(id);
	}

}
