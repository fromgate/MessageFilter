package me.fromgate.messagefilter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;


public class Filter {
	Type type;
	String file;
	String messageMask;
	String replaceMask;  // if empty = remove
	String cooldownTime; // if empty - no cooldown Пока игнорируем
	
	public enum Type {
		REGEX,
		CONTAINS,
		EQUAL,
		START,
		END;
		
		public static Type getByName(String name){
			for (Type t : Type.values()){
				if (t.name().equalsIgnoreCase(name)) return t;
			}
			return Type.EQUAL;
		}
		
		public static boolean isValid (String name){
			for (Type t : Type.values()){
				if (t.name().equalsIgnoreCase(name)) return true;
			}
			return false;
		}
	}
	
	public Filter (Type type, String fileName, String messageMask, String replaceMask, String cooldownTime){
		this.type = type;
		this.messageMask = messageMask;
		this.replaceMask = replaceMask;
		this.cooldownTime = cooldownTime;
		this.file = fileName;
	}
	
	public Filter (Type type, String messageMask, String replaceMask, String cooldownTime){
		this.type = type;
		this.messageMask = messageMask;
		this.replaceMask = replaceMask;
		this.cooldownTime = cooldownTime;
		this.file = "rules";
	}
	
	public Filter(String fileName) {
		this (Type.EQUAL, fileName, "default message", "", "");
	}

	public boolean filter(String message){
		switch (type){
		case CONTAINS:
			return message.toLowerCase().contains(this.messageMask.toLowerCase());
		case END:
			return message.toLowerCase().endsWith(messageMask.toLowerCase());
		case EQUAL:
			return message.equalsIgnoreCase(messageMask);
		case REGEX:
			return message.matches(messageMask);
		case START:
			return message.toLowerCase().startsWith(messageMask.toLowerCase());
		}
		return false;
	}
	
	public String processMessage(Player player, String id, String message) {
		if (this.cooldownTime.isEmpty()) return processMessage (message);
		if (Cooldown.isCooldown(player, id, MessageFilter.getUtil().parseTime(cooldownTime))) 
			return processMessage (message);
		return message;
	}
	
	public String processMessage(String message) {
		if (!filter(message)) return message;
		
		if (replaceMask.isEmpty()) return "";
		return ChatColor.translateAlternateColorCodes('&', processPlaceholders (message));
	}
	
	
	public String processPlaceholders (String message){
		if (message.isEmpty()) return message;
		String [] ln = message.split(" ");
		String newMessage = this.replaceMask;
		for (int i = 0; i<ln.length ; i++)
			newMessage = newMessage.replace("%word"+Integer.toString(1+i)+"%", ln[i]);
		return newMessage;
	}
	
	@Override
	public String toString(){
		return "["+this.file+"] "+this.type+" / "+(this.messageMask.isEmpty() ? "N/A" : this.messageMask)+
				(this.cooldownTime.isEmpty() ? "" : " ("+this.cooldownTime+")");
	}
	
	public  String[] getInfo(){
		String[] ln= new String [6];
		ln[1]=type.name();
		ln[2]=file;
		ln[3]=messageMask;
		ln[4]=replaceMask.isEmpty() ? "N/A" : replaceMask;
		ln[5]=cooldownTime.isEmpty() ? "N/A" : cooldownTime;
		return ln;
		
	}
			
			
	
	
	
}
