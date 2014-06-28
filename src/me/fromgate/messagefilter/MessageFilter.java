package me.fromgate.messagefilter;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class MessageFilter extends JavaPlugin {
	
	private static MessageFilter instance;
	Util u;
	private static Util util;
	
	String language;
	boolean languageSave;
	boolean checkUpdates;
	

	public static Plugin getPlugin() {
		return instance;
	}

	public static Util getUtil(){
		return util;
	}
	
	@Override
	public void onEnable(){
		reloadCfg();
		instance = this;
		u = new Util(this, language, languageSave);
		
		getCommand("msgfilter").setExecutor(u);
		getServer().getPluginManager().registerEvents(u, this);
		u.initUpdateChecker("MessageFilter", "81832", "messagefilter", checkUpdates);
		util = u;
		
		PLListener.initProtocolLib();
		if (!PLListener.isConnected()){
			getLogger().info("ProtocolLib not found!");
			return;
		}
		FilterManager.init();
		
	}
	
	public void reloadCfg(){
		this.reloadConfig();
		language = getConfig().getString("general.language","english");
		getConfig().set("general.language",language);
		languageSave = getConfig().getBoolean("general.language-save",false);
		getConfig().set("general.language-save",languageSave);
		checkUpdates = getConfig().getBoolean("general.check-updates",true);
		getConfig().set("general.check-updates",checkUpdates);
		saveConfig();
	}

	
	
	
}
