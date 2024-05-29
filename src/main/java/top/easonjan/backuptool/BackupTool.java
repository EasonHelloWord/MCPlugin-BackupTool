package top.easonjan.backuptool;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;

public final class BackupTool extends JavaPlugin implements TabCompleter {

    private FileConfiguration messagesConfig;
    private File messagesFile;
    private BukkitTask backupTask;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        createMessagesFile();
        messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
        scheduleBackupTask();
        getCommand("backuptool").setTabCompleter(this);
        getCommand("bt").setTabCompleter(this);
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
                sendMessage(sender, "no_permission");
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("backuptool") || command.getName().equalsIgnoreCase("bt")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("backup")) {
                    if (sender.hasPermission("backuptool.backup")) {
                        executeBackup(sender);
                    } else {
                        sendMessage(sender, "no_permission");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (sender.hasPermission("backuptool.admin")) {
                        reloadConfigurations();
                        sendMessage(sender, "reload_complete");
                    } else {
                        sendMessage(sender, "no_permission");
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("version")) {
                    sendMessage(sender, "plugin_version", getDescription().getVersion());
                    return true;
                } else if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(ChatColor.GREEN + getMessage("help_command_title"));
                    sender.sendMessage(ChatColor.GOLD + "/backup" + ChatColor.WHITE + " - " + getMessage("backup_command_description"));
                    sender.sendMessage(ChatColor.GOLD + "/backuptool backup" + ChatColor.WHITE + " - " + getMessage("backup_command_description"));
                    sender.sendMessage(ChatColor.GOLD + "/backuptool reload" + ChatColor.WHITE + " - " + getMessage("reload_command_description"));
                    sender.sendMessage(ChatColor.GOLD + "/backuptool version" + ChatColor.WHITE + " - " + getMessage("version_command_description"));
                    sender.sendMessage(ChatColor.GOLD + "/backuptool help" + ChatColor.WHITE + " - " + getMessage("help_command_description"));
                    return true;
                }
            }
            sendMessage(sender, "usage_command");
            return true;
        }
        return false;
    }

    private void scheduleBackupTask() {
        int interval = getConfig().getInt("backup.interval", 30) * 60 * 20; // Convert to ticks (20 ticks per second)
        backupTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!Bukkit.getOnlinePlayers().isEmpty()) {
                    Bukkit.broadcastMessage(getMessage("auto_backup_started"));
                    executeBackup(Bukkit.getConsoleSender());
                    Bukkit.broadcastMessage(getMessage("auto_backup_complete"));
                }
            }
        }.runTaskTimerAsynchronously(this, interval, interval);
    }

    private void executeBackup(CommandSender sender) {
        String scriptPath = getConfig().getString("backup.script");
        sendMessage(sender, "backup_start");
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    Process process = Runtime.getRuntime().exec(scriptPath);
                    int exitCode = process.waitFor();
                    if (exitCode == 0) {
                        sendMessage(sender, "backup_complete");
                    } else {
                        sendMessage(sender, "backup_error");
                    }
                } catch (IOException | InterruptedException e) {
                    getLogger().log(Level.SEVERE, "Backup script execution failed", e);
                    sendMessage(sender, "backup_error");
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

    private void sendMessage(CommandSender sender, String key, String... args) {
        String message = getMessage(key);
        if (args.length > 0) {
            for (int i = 0; i < args.length; i++) {
                message = message.replace("{" + i + "}", args[i]);
            }
        }
        sender.sendMessage(message);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("backuptool") || command.getName().equalsIgnoreCase("bt")) {
            if (args.length == 1) {
                return Arrays.asList("backup", "reload", "version", "help");
            }
        }
        return new ArrayList<>();
    }
}
