package com.hilaryoi.plugin.dailyparkour;

import java.util.Calendar;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class DailyParkourPlugin extends JavaPlugin implements Listener {

	String[] parkourLines;

	int money;

	FileConfiguration config;

	@Override
	public void onEnable() {

		this.saveDefaultConfig();
		config = this.getConfig();

		money = config.getInt("money");

		parkourLines = new String[] { ChatColor.DARK_BLUE.toString() + ChatColor.BOLD.toString() + "[Click here]", "",
				"Daily money", "for parkour" };

		this.getServer().getPluginManager().registerEvents(this, this);

	}

	@Override
	public void onDisable() {

		this.saveConfig();

	}

	@EventHandler
	public void onSignClick(PlayerInteractEvent e) {

		// Bukkit.getLogger().info(e.getClickedBlock().get);

		if (!e.getClickedBlock().getWorld().getName().equals("spawnworld")) {
			return;

		}

		// is it a sign
		if (!(e.getClickedBlock().getState() instanceof Sign)) {
			return;

		}

		String[] lines = ((Sign) e.getClickedBlock().getState()).getLines();

		for (int i = 0; i < 3; i++) {

			if (!lines[i].equals(parkourLines[i])) {
				return;

			}

		}

		UUID uuid = e.getPlayer().getUniqueId();

		Calendar lastClick = Calendar.getInstance();
		lastClick.setTimeInMillis(getDate(uuid));

		Calendar currTime = Calendar.getInstance();

		// if the last clicked day was today or after today
		if (lastClick.get(Calendar.DAY_OF_YEAR) >= currTime.get(Calendar.DAY_OF_YEAR)) {

			// if the last year was 2015 and it's now 2016, allow it.
			// note: didn't use greater than or equal to because that would be
			// time travel
			if (lastClick.get(Calendar.YEAR) == currTime.get(Calendar.YEAR)) {
				e.getPlayer()
						.sendMessage(ChatColor.RED + "Sorry, you already claimed your daily parkour reward for today!");
				return;

			}

		}

		// store value
		setDate(uuid);
		Bukkit.dispatchCommand(Bukkit.getConsoleSender(), String.format("economy give %s %s", uuid, money));

	}

	public void setDate(UUID uuid) {

		config.set("player." + uuid.toString(), System.currentTimeMillis());

	}

	public long getDate(UUID uuid) {

		return config.getLong("player." + uuid.toString());

	}

}
