package me.fromgate.messagefilter;

import org.bukkit.Bukkit;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;

public class PLListener {
	
	private static boolean connected =false;
	public static boolean isConnected(){
		return connected;
	}
	

	public static void initProtocolLib(){
		try{
			if (Bukkit.getPluginManager().getPlugin("ProtocolLib")!=null){
				connected = true;
				initPacketListener();
			} 
		} catch (Throwable e){
			connected = false;
		}
		
	}
	
	
	private static String jsonToString (JSONObject source){
		String result = "";
		for (Object key : source.keySet()){
			Object value = source.get(key);
			if (value instanceof String){
				if ((key instanceof String)&&(!((String)key).equalsIgnoreCase("text"))) continue;
				result = result+(String) value;
			} else if (value instanceof JSONObject){
				result = result + jsonToString ((JSONObject) value);
			} else if (value instanceof JSONArray){
				result = result + jsonToString ((JSONArray) value);
			} 
		}
		return result;
	}
	
	private static String jsonToString (JSONArray source){
		String result = "";
            for (Object value : source) {
                if (value instanceof String){
                    result = result+(String) value;
                } else if (value instanceof JSONObject){
                    result = result + jsonToString ((JSONObject) value);
                } else if (value instanceof JSONArray){
                    result = result + jsonToString ((JSONArray) value);
                }
            }
		return result;
	}

	private static String jsonToString(String json){
		JSONObject jsonObject = (JSONObject) JSONValue.parse(json);
		if (jsonObject == null||json.isEmpty()) return json;
		JSONArray array = (JSONArray) jsonObject.get("extra");
		if (array == null||array.isEmpty()) return json;
		return jsonToString (array);
	}
	
	/* 
	 * 

	// Old message format parser (1.6)
	private static String textToString(String message){
		String text = message;
		if (text.matches("^\\{\"text\":\".*\"\\}")) {
			text = text.replaceAll("^\\{\"text\":\"", "");
			text = text.replaceAll("\"\\}$", "");
		}
		return ChatColor.stripColor(text);
	}*/
	
	public static void initPacketListener(){
		if (!connected) return;
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(MessageFilter.getPlugin(), PacketType.Play.Server.CHAT) {
					@Override
					public void onPacketSending(PacketEvent event) {
						String message = "";
						try {
							String jsonMessage = event.getPacket().getChatComponents().getValues().get(0).getJson();
							if (jsonMessage!=null) message = jsonToString(jsonMessage);
						} catch (Throwable e){
							return;
						}
						if (message.isEmpty()) return;
						String newMessage = FilterManager.processMessage(event.getPlayer(), message);
						if (message.equalsIgnoreCase(newMessage)) return;
						if (newMessage.isEmpty()) event.setCancelled(true);
						else event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromText(newMessage));
					}
				});
	}
	
	
}
