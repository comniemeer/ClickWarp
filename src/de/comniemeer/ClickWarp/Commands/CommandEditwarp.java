package de.comniemeer.ClickWarp.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.comniemeer.ClickWarp.AutoCommand;
import de.comniemeer.ClickWarp.ClickWarp;

public class CommandEditwarp extends AutoCommand<ClickWarp> {
	
	public CommandEditwarp(ClickWarp plugin) {
		super(plugin, "editwarp", "Edit a warp: Change the item, add a lore or change the price");
	}
	
	@Override
	public boolean execute(CommandSender sender, String label, String[] args) {
		if (sender.hasPermission("clickwarp.editwarp")) {
			if (args.length == 3) {
				String str = args[0].toLowerCase();
				File file = new File("plugins/ClickWarp/Warps", str + ".yml");
				
				if (!(file.exists())) {
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.WarpNoExist).replace("{warp}", args[0]));
					return true;
				}
				
				FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
				String name = "";
				
				if (cfg.getString(str + ".name") == null) {
					name = str;
				} else {
					name = ChatColor.translateAlternateColorCodes('&', cfg.getString(str + ".name"));
				}
				
				if (args[1].equalsIgnoreCase("item")) {
					String item_ = args[2];
					if (item_.contains(":")) {
						String[] item__ = item_.split(":");
						
						if (item__.length > 2) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpWrongItemFormat));
							return true;
						}
						
						int item_id = 0;
						int item_meta = 0;
						
						for (int i = 0; i < item__.length; i++) {
							try {
								item_meta = Integer.parseInt(item__[i]);
							} catch (NumberFormatException e) {
								sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpNeedNumber));
								return true;
							}
							
							if (i + 1 < item__.length) {
								try {
									item_id = Integer.parseInt(item__[i]);
								} catch (NumberFormatException e) {
									sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpNeedNumber));
									return true;
								}
							}
						}
						
						Material item_name = Material.getMaterial(item_id);
						
						try {
							item_id = item_name.getId();
						} catch (NullPointerException npe) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpInvalidItem));
							return true;
						}
						
						cfg.set(str + ".item", item_id + ":" + item_meta);
						
						try {
							cfg.save(file);
						} catch (IOException e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.ErrorFileSaving));
							e.printStackTrace();
							return true;
						}
						
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.EditwarpItemSuccess).replace("{warp}", name).replace("{item}", String.valueOf(item_id) + ":" + String.valueOf(item_meta)));
						return true;
					} else {
						int item_id = 0;
						
						try {
							item_id = Integer.parseInt(item_);
						} catch (NumberFormatException e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpNeedNumber));
							return true;
						}
						
						Material item_name = Material.getMaterial(item_id);
						
						try {
							item_id = item_name.getId();
						} catch (NullPointerException npe) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpInvalidItem));
							return true;
						}
						
						cfg.set(str + ".item", item_id);
						
						try {
							cfg.save(file);
						} catch (IOException e) {
							sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.ErrorFileSaving));
							e.printStackTrace();
							return true;
						}
						
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.EditwarpItemSuccess).replace("{warp}", name).replace("{item}", String.valueOf(item_id)));
						return true;
					}
				} else if (args[1].equalsIgnoreCase("lore")) {
					String lore = args[2];
					
					cfg.set(str + ".lore", lore);
					
					try {
						cfg.save(file);
					} catch (IOException e) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.ErrorFileSaving));
						e.printStackTrace();
						return true;
					}
					
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.EditwarpLoreSuccess).replace("{warp}", name).replace("{lore}", ChatColor.translateAlternateColorCodes('&', lore.replace("_", " ").replace(":", "�r:"))));
					return true;
				} else if (args[1].equalsIgnoreCase("price")) {
					double price;
					
					try {
						price = Double.parseDouble(args[2]);
					} catch (NumberFormatException e) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.SetwarpNeedNumber));
						return true;
					}
					
					cfg.set(str + ".price", price);
					
					try {
						cfg.save(file);
					} catch (IOException e) {
						sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.ErrorFileSaving));
						e.printStackTrace();
						return true;
					}
					
					String pricesuccess = ChatColor.translateAlternateColorCodes('&', plugin.msg.EditwarpPriceSuccess).replace("{warp}", name).replace("{price}", String.valueOf(price));
					
					if (price == 1) {
						sender.sendMessage(pricesuccess.replace("{currency}", plugin.getConfig().getString("Economy.CurrencySingular")));									
					} else {
						sender.sendMessage(pricesuccess.replace("{currency}", plugin.getConfig().getString("Economy.CurrencyPlural")));
					}
				} else {
					sender.sendMessage("�e/editwarp <warp> item <item-ID>");
					sender.sendMessage("�e/editwarp <warp> lore <Line_1:Line_2:...>");
					sender.sendMessage("�e/editwarp <warp> price <Price>");
				}
			} else {
				sender.sendMessage("�e/editwarp <warp> item <item-ID>");
				sender.sendMessage("�e/editwarp <warp> lore <Line_1:Line_2:...>");
				sender.sendMessage("�e/editwarp <warp> price <Price>");
			}
		} else {
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.msg.NoPermission));
		}
		return true;
	}

	@Override
	public List<String> tabComplete(CommandSender sender, String label, String[] args) {
		if (sender.hasPermission("clickwarp.editwarp")) {
			List<String> warpList = new ArrayList<>();
			File warps_folder = new File("plugins/ClickWarp/Warps");
			
			if (warps_folder.isDirectory()) {
				File[] warps = warps_folder.listFiles();
				
				if (warps.length != 0) {
					for (int i = 0; i < warps.length; i++) {
						warpList.add(warps[i].getName().replace(".yml", ""));
					}
				}
			}
			
			if (args.length == 0) {
				return warpList;
			} else if (args.length == 1) {
				List<String> tabList = new ArrayList<>();
				
				for (String warp : warpList) {
					if (warp.startsWith(args[0].toLowerCase())) {
						tabList.add(warp);
					}
				}
				
				return tabList;
			}
		}
		return null;
	}
}