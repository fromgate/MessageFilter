package me.fromgate.messagefilter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
	private static boolean oldVersion16x = true;




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


	private static String getStringColor (String colorName){
		for (ChatColor c : ChatColor.values())
			if (c.name().equalsIgnoreCase(colorName)) return c.toString();
		return "";	
	}

	private static String getStringValue (String key, String value){
		if (key.equalsIgnoreCase("text")) return value;
		if (key.equalsIgnoreCase("color")) return getStringColor(value);
		return "";

	}

	private static String getBooleanValue (String key, boolean value){
		if (!value) return "";
		if (key.equalsIgnoreCase("bold")) return ChatColor.BOLD.toString();
		if (key.equalsIgnoreCase("italic")) return ChatColor.ITALIC.toString();
		if (key.equalsIgnoreCase("underlined")) return ChatColor.UNDERLINE.toString();
		if (key.equalsIgnoreCase("strikethrough")) return ChatColor.STRIKETHROUGH.toString();
		if (key.equalsIgnoreCase("obfuscated")) return ChatColor.MAGIC.toString();
		return "";
	}

	private static String jsonToString (JSONObject source){
		String result = "";
		for (Object key : source.keySet()){
			Object value = source.get(key);
			if (value instanceof String){
				if (!(key instanceof String))continue; //
				result = result+getStringValue ((String) key,(String) value);
			} else if (value instanceof Boolean){
				if (!(key instanceof String))continue; //
				result = result+getBooleanValue ((String) key,(Boolean) value);
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


	private static String textToString(String message){
		String text = message;
		if (text.matches("^\\{\"text\":\".*\"\\}")) {
			text = text.replaceAll("^\\{\"text\":\"", "");
			text = text.replaceAll("\"\\}$", "");
			oldVersion16x = true;
		} else oldVersion16x = false;
		return ChatColor.stripColor(text);
	}




	public static void initPacketListener(){
		if (!connected) return;
		ProtocolLibrary.getProtocolManager().addPacketListener(
				new PacketAdapter(MessageFilter.getPlugin(), PacketType.Play.Server.CHAT) {
					@Override
					public void onPacketSending(PacketEvent event) {

						String message = "";
						try {
							String jsonMessage = event.getPacket().getChatComponents().getValues().get(0).getJson();
							if (jsonMessage!=null&&!jsonMessage.isEmpty()) message = jsonToString(jsonMessage);
						} catch (Throwable e){
						}

						String oldMessage = "";
						if (event.getPacket().getStrings().size()>0) {
							String jsonMessage = event.getPacket().getStrings().read(0);
							if (jsonMessage!=null) oldMessage = textToString(jsonMessage);	
						}
						boolean newVersion = !message.isEmpty();

						message = message.isEmpty() ? oldMessage : message;

						if (message.isEmpty()) return;

						String resultMessage="";



						if (event.getPlayer().hasMetadata("message-filter-test")&&message.equalsIgnoreCase(MessageFilter.getUtil().getTestMessage())){
							boolean showMessage = event.getPlayer().getMetadata("message-filter-test").get(0).asBoolean();
							event.getPlayer().removeMetadata("message-filter-test", MessageFilter.getPlugin());
							resultMessage = showMessage ? MessageFilter.getUtil().getMSG("msg_test_ok",'6') :  "";
						} else {
							MessageSaver.saveMessage(event.getPlayer(), message.replace("§", "&"));
							resultMessage = FilterManager.processMessage(event.getPlayer(), message);
						}

						if (message.equalsIgnoreCase(resultMessage)) return;
						if (resultMessage.isEmpty()) event.setCancelled(true);
						else {
							if (newVersion)	event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromText(resultMessage));
							else event.getPacket().getStrings().write(0,  oldVersion16x ?  "{\"text\":\""+resultMessage+"\"}" : resultMessage);
						}

					}
				});
	}


}
