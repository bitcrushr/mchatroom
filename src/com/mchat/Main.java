package com.mchat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin implements Listener {
	public HashMap<String, ArrayList<String>> channels = new HashMap<String, ArrayList<String>>();
	public HashMap<String, HashMap<String, String>> details = new HashMap<String, HashMap<String, String>>();

	
	@Override
	public void onEnable(){
		System.out.println("- Enabling Mchat -");
		this.getServer().getPluginManager().registerEvents(this, this);
		ArrayList<String> playz = new ArrayList<String>();
		for(Player person : Bukkit.getOnlinePlayers()){
			playz.add(person.getDisplayName());
		}
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("owner", "Server");
		details.put("default", temp);
		channels.put("default", playz);
	}
	@Override
	public void onDisable(){
		System.out.println("- Disabling MChat -");
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent event){
		Player sender = event.getPlayer();
		String channel = getChannel((Player) sender);
		for(String name : channels.get(channel)){
			Bukkit.getPlayer(name).sendMessage(lblue("[" + sender.getDisplayName() + "]") + " " + ChatColor.WHITE + event.getMessage());
		}
		System.out.println(sender.getDisplayName() + "> " + event.getMessage());
		event.setCancelled(true);
		return;
	}
	
	@EventHandler
	public boolean onQuit(PlayerQuitEvent event){
		channels.get(getChannel(event.getPlayer())).remove(event.getPlayer().getDisplayName());
		return true;
	}
	
	@EventHandler
	public boolean onPlayerJoin(PlayerJoinEvent event){
		setChannel(event.getPlayer(), getChannel(event.getPlayer()));
		return true;
	}
	
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		switch (label){
		case "clist":
			listChannels((Player) sender);
			return true;
		case "cjoin":
			if(args.length < 1){
				sender.sendMessage(lblue("Please specify a channel to join."));
				return false;
			}
			if(args.length < 2 ){
				setChannel((Player) sender, args[0]);
				return true;
			}
				setChannel((Player) sender, args[0], args[1]);
			return true;
		case "cmake":
			if(args.length < 1 ){
				sender.sendMessage(lblue("Please specify a channel to create."));
				return false;
			}
			if(args.length < 2 ){
				makeChannel((Player) sender, args[0]);
				return true;
			}
			makeChannel((Player) sender, args[0], args[1]);
			return true;
		case "cdel":
			if(args.length < 1){
				sender.sendMessage(lblue("Please specify a channel to delete."));
				return false;
			}
			delChannel((Player) sender, args[0]);
			return true;
		case "chan":
			String chan = getChannel((Player) sender);
				sender.sendMessage(blue("You are currently in \'") + lblue(chan) + blue("\'."));
			return true;
		case "cwho":
			if(args.length < 1){
				sender.sendMessage(lblue("Please specify a channel."));
				return false;
			}
			listPeopleInChannel((Player) sender, args[0]);
			return true;
		default:
			return false;
		}
	}
	
	//Commands
	
	public void listPeopleInChannel(Player player, String chan){
		if(channels.containsKey(chan)){
			player.sendMessage(blue("---Players---"));
			for(String person : channels.get(chan)){
				player.sendMessage(blue("- ") + lblue(person) + blue(" -"));
			}
			player.sendMessage(blue("------------"));
			return;
		} 
		player.sendMessage(lblue("Channel \'") + lblue(chan) + blue("\' does not exist."));
	}
	
	public void listChannels(Player player){
		player.sendMessage(blue("---Channels---"));
		Set<String> keys = channels.keySet();
		for(String i : keys){
			player.sendMessage(blue("- ") + lblue(i) + blue(" - Owner: " + lblue(details.get(i).get("owner")) + blue(" -")));
		}
		player.sendMessage(blue("--------------"));
	}
	
	public void setChannel(Player player, String channel){
		if(channels.containsKey(channel)){
			if(details.get(channel).containsKey("password")){
				player.sendMessage(lblue("Channel \'" + blue(channel) + lblue("\' is password protected.")));
				return;
			}
			String chan = getChannel(player);
			channels.get(chan).remove(player.getDisplayName());
			for(String person : channels.get(chan)){
				Bukkit.getPlayer(person).sendMessage(lblue(player.getDisplayName()) + blue(" left the channel."));
			}
			channels.get(channel).add(player.getDisplayName());
			for(String person : channels.get(channel)){
				Bukkit.getPlayer(person).sendMessage(lblue(player.getDisplayName()) + blue(" joined the channel."));
			}
			player.sendMessage(blue("You've joined \'" + lblue(channel) + blue("\'.")));
			return;
		}
		
	}
	
	public void setChannel(Player player, String channel, String password){
		if(channels.containsKey(channel)){
			if(details.get(channel).containsKey("password")){
				if(!password.equals(details.get(channel).get("password"))){
					player.sendMessage(lblue("Incorrect password for channel \'" + blue(channel) + lblue("\'.")));
					return;
				}
			}
			String chan = getChannel(player);
			channels.get(chan).remove(player.getDisplayName());
			for(String person : channels.get(chan)){
				Bukkit.getPlayer(person).sendMessage(lblue(player.getDisplayName()) + blue(" left the channel."));
			}
			channels.get(channel).add(player.getDisplayName());
			for(String person : channels.get(channel)){
				Bukkit.getPlayer(person).sendMessage(lblue(player.getDisplayName()) + blue(" joined the channel."));
			}
			player.sendMessage(blue("You've joined \'" + lblue(channel) + blue("\'.")));
			return;
		}
		
	}
	
	public String getChannel(Player player){
		for(String key : channels.keySet()){
			if(channels.get(key).contains(player.getDisplayName())){
				return key;
			}
		}
		return "default";
	}
	
	public void makeChannel(Player player, String name){
		if(channels.containsKey(name)){
			player.sendMessage(lblue("Channel \'" + name + "\' already exists."));
			return;
		}
		channels.put(name, new ArrayList<String>());
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("owner", player.getDisplayName());
		details.put(name, temp);
		player.sendMessage(blue("channel \'") + lblue(name) + blue("\' created."));
		}
	
	public void makeChannel(Player player, String name, String password){
		if(channels.containsKey(name)){
			player.sendMessage(lblue("Channel \'" + name + "\' already exists."));
			return;
		}
		channels.put(name, new ArrayList<String>());
		HashMap<String, String> temp = new HashMap<String, String>();
		temp.put("owner", player.getDisplayName());
		temp.put("password", password);
		details.put(name, temp);
		player.sendMessage(blue("channel \'") + lblue(name) + blue("\' created."));
		}
	
	public void delChannel(Player player, String name){
		if(name.equals("default")){
			player.sendMessage(lblue("You can't delete the default channel."));
			return;
		}
		if(channels.containsKey(name)){
			if(!details.get(name).get("owner").equals(player.getDisplayName())){
				player.sendMessage(lblue("You can't delete another player's channel."));
				return;
			}
			ArrayList<String> people = channels.get(name);
			channels.remove(name);
			details.remove(name);
			for(String pname : people){
				Bukkit.getPlayer(pname).sendMessage(blue("Current channel \'") + lblue(name) + blue("\' has been deleted."));
				setChannel(Bukkit.getPlayer(pname), "default");
			}
			player.sendMessage(blue("Channel \'") + lblue(name) + blue("\' was deleted."));
			return;
		}
		player.sendMessage(lblue("Channel \'") + lblue(name) + blue("\' does not exist."));
	}
	
	public String lblue(String in){
		return ChatColor.AQUA + in;
	}
	
	public String blue(String in){
		return ChatColor.BLUE + in;
	}
	
}
