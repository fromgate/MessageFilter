package me.fromgate.messagefilter;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;


public class Util extends FGUtilCore implements CommandExecutor, Listener {

	public Util(MessageFilter plg, String language, boolean languageSave) {
		super(plg, languageSave, language, "msgfilter", "messagefilter");
		addCmd("help", "config", "hlp_thishelp","&3/msgfilter help",'b',true);
		addCmd("add", "config", "hlp_add","&3/msgfilter add <FilterId> [GroupId]",'b',true);
		addCmd("set", "config", "hlp_set","&3/msgfilter set <FilterId> type|input|output|cooldown|group <Value>",'b',true);
		addCmd("remove", "config", "hlp_remove","&3/msgfilter remove <FilterId>",'b',true);
		addCmd("list", "config", "hlp_list","&3/msgfilter list [Mask] [PageNumber]",'b',true);
		addCmd("info", "config", "hlp_info","&3/msgfilter info <FilterId>",'b',true);
        addCmd("reload", "config", "hlp_reload","&3/msgfilter reload",'b',true);

        addMSG("hlp_add", "%1% - add new rule");
        addMSG("hlp_set", "%1% - configure existing rule");
        addMSG("hlp_remove", "%1% - remove rule");
        addMSG("hlp_list", "%1% - show rule-list");
        addMSG("hlp_info", "%1% - show full info for defined rule");
        addMSG("hlp_reload", "%1% - reload rules");
        addMSG("msg_ruleadded", "Rule %1% created");
        addMSG("msg_ruleaddfail", "Failed to add new rule");
        addMSG("msg_missedparameters", "Missing required parameters!");
        addMSG("msg_rulenotexist", "Rule %1% not exist!");
        addMSG("msg_ruleexist", "Rule %1% already exist");
        addMSG("info_title", "Id: %1% Type: %2% Group: %3% Cooldown:%4%");
        addMSG("info_inputmask", "Input mask: %1%");
        addMSG("info_outputmask", "Output mask: %1%");
        addMSG("msg_rulelist", "Rules:");
        addMSG("msg_ruleremoved", "Rule removed");
        addMSG("msg_setcooldownfail", "Failed to set cooldown for rule %1%");
        addMSG("msg_setcooldown", "Cooldown %2% configured for rule %1%");
        addMSG("msg_setoutputfail", "Failed to set output mask for rule %1%");
        addMSG("msg_setoutput", "Output mask of rule %1% was set to: %2%");
        addMSG("msg_setinputfail", "Failed to set input mask for rule %1%");
        addMSG("msg_setinput", "Input mask of rule %1% was set to: %2%");
        addMSG("msg_setgroupfail", "Failed to set group for rule %1%");
        addMSG("msg_setgroup", "Group %2% defined for rule %1%");
        addMSG("msg_settypefail", "Failed to set type for rule %1%");
        addMSG("msg_settype", "Type of rules %1% was set to %2%");
        addMSG("msg_reloaded", "Rules reloaded!");
        if (languageSave) this.SaveMSG();
	}
	
	@EventHandler(priority=EventPriority.NORMAL)
	public void onJoin (PlayerJoinEvent event){
		updateMsg(event.getPlayer());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String cmdLabel, String[] args) {
		if (args.length == 0) {
			String [] helparr = {"help"};
			args = helparr;
		}
		if (!checkCmdPerm(sender, args[0])) return returnMSG (true, sender, "cmd_cmdpermerr",'c');
		if (args[0].equalsIgnoreCase("help")) return executeHelp(sender);
		else if (args[0].equalsIgnoreCase("reload")) return executeReload(sender);
		else if (args[0].equalsIgnoreCase("add")) return executeAdd(sender,args);
		else if (args[0].equalsIgnoreCase("set")) return executeSet(sender,args);
		else if (args[0].equalsIgnoreCase("remove")) return executeRemove(sender,args);
		else if (args[0].equalsIgnoreCase("list")) return executeList(sender,args);
		else if (args[0].equalsIgnoreCase("info")) return executeInfo(sender,args);
		return false;
	}

	private boolean executeInfo(CommandSender sender, String[] args) {
		if (args.length<2) return returnMSG (true, sender, "msg_missedparameters",'c');
		String id = args[1];
		if (!FilterManager.isExist(id)) return returnMSG (true, sender, "msg_rulenotexist",'c','4',id);
		String[] ln = FilterManager.getInfo(id);
		printMSG (sender, "info_title",'e','6',ln[0],ln[1],ln[2],ln[5]);
		printMSG (sender, "info_inputmask",'a','2',ln[3]);
		printMSG (sender, "info_outputmask",'a','2',ln[4]);
		return true;
	}

	// /mfilter list [num] [mask]
	private boolean executeList(CommandSender sender, String[] args) {
		int lpp = (sender instanceof Player) ? 10 : 40;
		int pageNum = 1;
		String mask = "";
		if (args.length>=2)
			for (int i =1; i<Math.min(4, args.length); i++){
				if (MessageFilter.getUtil().isInteger(args[i])) pageNum = Integer.parseInt(args[i]);
				else mask = args[i];
				
			}
		List<String> list = FilterManager.getList(mask);
		MessageFilter.getUtil().printPage(sender, list, pageNum, "msg_rulelist", "", false, lpp);
		return true;
	}

	private boolean executeRemove(CommandSender sender, String[] args) {
		if (args.length<2) return returnMSG (true, sender, "msg_missedparameters",'c');
		String id = args[1];
		if (!FilterManager.isExist(id)) return returnMSG (true, sender, "msg_rulenotexist",'c','4',id);
		FilterManager.removeFilter(id);
		return returnMSG (true, sender, "msg_ruleremoved"); 
	}

	// /mfilter set <id> <param> <value>
	private boolean executeSet(CommandSender sender, String[] args) {
		if (args.length<3) return returnMSG (true, sender, "msg_missedparameters",'c');
		String id = args[1];
		if (!FilterManager.isExist(id)) return returnMSG (true, sender, "msg_rulenotexist",'c','4',id);
		
		String param = args[2];
		String value = "";
		if (args.length>=3)
			for (int i =3; i<args.length; i++)
				value = (i>3) ? value+" "+args[i] : args [i];
		if (param.equalsIgnoreCase("type")) return setType (sender, id, value);
		if (param.equalsIgnoreCase("group")) return setGroup(sender, id, value);
		if (param.equalsIgnoreCase("input")) return setInputMask(sender, id, value);
		if (param.equalsIgnoreCase("output")) return setOutputMask(sender, id, value);
		if (param.equalsIgnoreCase("cooldown")) return setCooldown(sender, id, value);
		return false;
	}

	
	private boolean setCooldown(CommandSender sender, String id, String value) {
		if (FilterManager.setCooldown(id, value)) return returnMSG (true, sender, "msg_setcooldown",id,value);
		return returnMSG (true, sender, "msg_setcooldownfail",'c','4',id);
	}

	private boolean setOutputMask(CommandSender sender, String id, String value) {
		if (FilterManager.setOutputMask(id, value)) return returnMSG (true, sender, "msg_setoutput",id,value);
		return returnMSG (true, sender, "msg_setoutputfail",'c','4',id);
	}

	private boolean setInputMask(CommandSender sender, String id, String value) {
		if (FilterManager.setInputMask(id, value)) return returnMSG (true, sender, "msg_setinput",id,value);
		return returnMSG (true, sender, "msg_setinputfail",'c','4',id);
	}

	private boolean setGroup(CommandSender sender, String id, String value) {
		if (FilterManager.setGroup(id, value)) return returnMSG (true, sender, "msg_setgroup",id,value);
		return returnMSG (true, sender, "msg_setgroupfail",'c','4',id);
	}

	private boolean setType(CommandSender sender, String id, String value) {
		if (FilterManager.setType(id, value)) return returnMSG (true, sender, "msg_settype",id,value);
		return returnMSG (true, sender, "msg_settype",'c','4',id);	
	}

	private boolean executeAdd(CommandSender sender, String[] args) {
		if (args.length<2) return returnMSG (true, sender, "msg_missedparameters",'c');
		String id = args[1];
		if (FilterManager.isExist(id)) return returnMSG (true, sender, "msg_ruleexist",'c','4',id);
		String fileName = args.length==2 ? "rules" : args [2]; 
		if (FilterManager.addFilter(id, fileName)) return returnMSG (true, sender, "msg_ruleadded",id);
		else return returnMSG (true, sender, "msg_ruleaddfail",id); 
	}

	private boolean executeHelp(CommandSender sender) {
		PrintHlpList(sender, 1, 100);
		return true;
	}

	private boolean executeReload(CommandSender sender){
		FilterManager.load();
		return this.returnMSG(true, sender, "msg_reloaded");
	}

}


