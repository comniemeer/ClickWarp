package de.comniemeer.ClickWarp.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import de.comniemeer.ClickWarp.ClickWarp;

public class InventoryListener implements Listener {
	
	private ClickWarp plugin;
	public InventoryListener(ClickWarp clickwarp) {
		plugin = clickwarp;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		if (e.getSlot() == e.getRawSlot()) {
			final Player player = (Player) e.getWhoClicked();
			
			if (plugin.InvHM.containsKey(player.getName())) {
				if (plugin.InvHM.get(player.getName()).equals("InventarWarp")) {
					e.setCancelled(true);
					player.updateInventory();
					ItemStack item = e.getCurrentItem();
					
					if (item != null && item.getType() != Material.AIR) {
						String dispname = item.getItemMeta().getDisplayName();
						String name = ChatColor.stripColor(dispname.toLowerCase());
						
						plugin.warphandler.handleWarp(player, name, dispname, false);
						this.closeInv(player);
						return;
					}
				} else if (plugin.InvHM.get(player.getName()).equals("InventarTP")) {
					e.setCancelled(true);
					player.updateInventory();
					ItemStack item = e.getCurrentItem();
					
					if (item != null && item.getType() != Material.AIR) {
						String name = ChatColor.stripColor(item.getItemMeta().getDisplayName());
						final Player p_ = Bukkit.getPlayerExact(name);
						
						if (p_ != null) {
							Boolean usedelay = plugin.getConfig().getBoolean("Delay.Teleport.EnableDelay");
							
							if (!usedelay.booleanValue()) {
								player.teleport(p_);
								player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.InvTPSuccess).replace("{player}", p_.getName()));
							} else {
								if (player.hasPermission("clickwarp.teleport.instant")) {
									player.teleport(p_);
									player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.InvTPSuccess).replace("{player}", p_.getName()));
								} else {
									Boolean usedontmove = plugin.getConfig().getBoolean("Delay.Teleport.EnableDontMove");
									int delay = plugin.getConfig().getInt("Delay.Teleport.Delay");
									
									if (usedontmove.booleanValue()) {
										plugin.warp_delay.put(player.getName(), true);
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.DelayDoNotMove).replace("{delay}", String.valueOf(delay)));
									} else {
										player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.Delay).replace("{delay}", String.valueOf(delay)));
									}
									
									plugin.delaytask = Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
										@Override
										public void run() {									
											player.teleport(p_);
											plugin.warp_delay.remove(player.getName());
											player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.InvTPSuccess).replace("{player}", p_.getName()));
										}
									}, delay * 20L);
								}
							}
						} else {
							player.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.InvTPNotOnline).replace("{player}", name));
						}
						
						this.closeInv(player);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent event) {
		plugin.InvHM.remove(event.getPlayer().getName());
	}
	
	public void closeInv(Player player) {
		player.closeInventory();
		plugin.InvHM.remove(player.getName());
	}
}