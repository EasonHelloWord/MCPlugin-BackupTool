package top.easonjan.backuptool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public final class BackupTool extends JavaPlugin {

    private FileConfiguration messagesConfig;
    private File messagesFile;
    private BukkitTask backupTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesFile();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        scheduleBackupTask();
        getLogger().info("BackupTool enabled!");
    }

    @Override
    public void onDisable() {
        if (backupTask != null) {
            backupTask.cancel();
        }
        getLogger().info("BackupTool disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("backup")) {
            if (sender.hasPermission("backuptool.backup")) {
                executeBackup(sender);
            } else {
                sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("backuptool") || command.getName().equalsIgnoreCase("bt")) {
            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("backuptool.admin")) {
                    reloadConfigurations();
                    sender.sendMessage(getMessage("reload_complete"));
                } else {
                    sender.sendMessage(ChatColor.RED + "You do not have permission to use this command.");
                }
                return true;
            }
        }
        return false;
    }

    private void scheduleBackupTask() {
        int interval = getConfig().getInt("backup.interval", 30) * 60 * 20; // Convert to ticks (20 ticks per second)
        backupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    executeBackup(Bukkit.getConsoleSender());
                }
            }
        }.runTaskTimerAsynchronously(this, interval, interval);
    }

    private void executeBackup(CommandSender sender) {
        String scriptPath = getConfig().getString("backup.script");
        sender.sendMessage(getMessage("backup_start"));
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(scriptPath);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        sender.sendMessage(getMessage("backup_complete"));
                    } else {
                        sender.sendMessage(getMessage("backup_error"));
                    }
                } catch (IOException | InterruptedException e) {
                    getLogger().log(Level.SEVERE, "Backup script execution failed", e);
                    sender.sendMessage(getMessage("backup_error"));
                }
            }
        }.runTaskAsynchronously(this);
    }

    private void createMessagesFile() {
        messagesFile = new File(getDataFolder(), "messages.yml");
        if (!messagesFile.exists()) {
            saveResource("messages.yml", false);
        }
    }

    private String getMessage(String key) {
        String message = messagesConfig.getString("messages." + key, key);
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private void reloadConfigurations() {
        reloadConfig();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        if (backupTask != null) {
            backupTask.cancel();
        }
        scheduleBackupTask();
    }
}
