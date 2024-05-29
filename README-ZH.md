# BackupTool 插件

[English Version](README.md)

BackupTool 是一个 Bukkit 插件，用于在 Minecraft 服务器上执行定期备份。它可以根据配置文件中的设置，在有玩家在线时自动执行备份，并提供了命令供管理员手动触发备份。

## 功能

- 自动备份：根据配置文件中的设置，当有玩家在线时自动执行备份。
- 手动备份：管理员可以使用命令手动触发备份。
- 配置重载：管理员可以重新加载插件的配置文件。

## 安装与配置

1. 下载 BackupTool.jar 文件并将其放置在服务器的插件目录中。
2. 启动服务器以加载插件。
3. 在插件目录中编辑 `config.yml` 文件以配置备份设置和备份脚本路径。
4. （可选）编辑 `messages.yml` 文件以修改消息文本或添加新的消息。
5. 重新加载服务器或使用 `/backuptool reload` 命令重新加载插件配置。

## 使用命令

- `/backup` 或 `/backuptool backup`：手动触发备份。
- `/backuptool reload`：重新加载插件的配置文件。
- `/backuptool version`：显示插件的版本信息。
- `/backuptool help`：显示插件的帮助信息。

## 权限节点

- `backuptool.backup`：允许用户执行备份命令。
- `backuptool.admin`：允许用户执行插件的管理命令，如重载配置文件。

## 注意事项

- 插件使用了语言文件 `messages.yml` 来存储消息文本，你可以根据需要进行修改。
- 插件默认的备份间隔为 30 分钟，可以在配置文件中进行调整。
