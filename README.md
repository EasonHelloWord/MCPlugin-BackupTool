# BackupTool Plugin

BackupTool is a Bukkit plugin designed for performing periodic backups on a Minecraft server. It can automatically execute backups based on settings in the configuration file when players are online, and provides commands for administrators to manually trigger backups.

## Features

- Automatic Backups: Automatically executes backups based on settings in the configuration file when players are online.
- Manual Backup: Administrators can manually trigger backups using commands.
- Configuration Reload: Administrators can reload the plugin's configuration file.

## Installation and Configuration

1. Download the BackupTool.jar file and place it in the plugins directory of your server.
2. Start the server to load the plugin.
3. Edit the `config.yml` file in the plugin directory to configure backup settings and the path to the backup script.
4. (Optional) Edit the `messages.yml` file to modify message texts or add new messages.
5. Reload the server or use the `/backuptool reload` command to reload the plugin configuration.

## Using Commands

- `/backup` or `/backuptool backup`: Manually triggers a backup.
- `/backuptool reload`: Reloads the plugin's configuration file.
- `/backuptool version`: Shows the plugin's version information.
- `/backuptool help`: Displays help information for the plugin.

## Permission Nodes

- `backuptool.backup`: Allows users to execute the backup command.
- `backuptool.admin`: Allows users to execute management commands of the plugin, such as reloading the configuration file.

## Notes

- The plugin uses a language file `messages.yml` to store message texts, which you can modify as needed.
- The default backup interval of the plugin is 30 minutes, which can be adjusted in the configuration file.
