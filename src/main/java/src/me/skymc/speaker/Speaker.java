package me.skymc.speaker;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.inventivetalent.bossbar.BossBar;
import org.inventivetalent.bossbar.BossBarAPI;
import org.inventivetalent.bossbar.BossBarAPI.Color;
import org.inventivetalent.bossbar.BossBarAPI.Property;
import org.inventivetalent.bossbar.BossBarAPI.Style;

import lombok.Getter;
import me.skymc.speaker.enums.SpeakerType;
import me.skymc.taboolib.display.TitleUtils;
import me.skymc.taboolib.inventory.InventoryUtil;
import me.skymc.taboolib.inventory.ItemUtils;
import me.skymc.taboolib.string.Language;
import net.md_5.bungee.api.chat.TextComponent;

/**
 * @author sky
 * @since 2018��2��4�� ����1:53:47
 */
public class Speaker extends JavaPlugin implements Listener {
	
	@Getter
	private static Plugin inst;
	
	@Getter
	private static Language language;
	
	@Override
	public void onLoad() {
		saveDefaultConfig();
		inst = this;
	}
	
	@Override
	public void onEnable() {
		Bukkit.getPluginManager().registerEvents(this, this);
		
		// �����ļ�
		language = new Language(this);
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		reloadConfig();
		sender.sendMessage("reloado ok!");
		return true;
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void command(PlayerCommandPreprocessEvent e) {
		if (e.isCancelled()) {
			return;
		}
		// ��������
		SpeakerType type;
		String[] args = e.getMessage().split(" ");
		
		// �������
		if (e.getMessage().startsWith(getConfig().getString("Settings.SpeakerTitleCommand"))) {
			type = SpeakerType.TITLE;
		}
		else if (e.getMessage().startsWith(getConfig().getString("Settings.SpeakerBarCommand"))) {
			type = SpeakerType.BOSSBAR;
		}
		else {
			return;
		}
		
		// ȡ���¼�
		e.setCancelled(true);
		
		// ��鳤��
		if (args.length == 1) {
			language.send(e.getPlayer(), "EMPTY_COMMAND");
			return;
		}
		
		// �ϳ��ı�
		StringBuilder sb = new StringBuilder();
		for (int i = 1 ; i < args.length ; i++) {
			sb.append(args[i].replace("&", "��") + " ");
		}
		
		// ����ı�����
		if (sb.substring(0, sb.length() - 1).length() > getConfig().getInt("Settings.TextLength")) {
			language.send(e.getPlayer(), "TEXT_LIMIT");
			return;
		}
		
		// �۳���Ʒ
		if (!InventoryUtil.hasItem(e.getPlayer(), ItemUtils.getCacheItem(getConfig().getString(type.name() + ".Item")), 1, true)) {
			language.send(e.getPlayer(), "EMPTY_ITEM");
			return;
		}
		
		// ������Ϣ
		if (type == SpeakerType.TITLE) {
			// ѭ�����
			for (Player player : Bukkit.getOnlinePlayers()) {
				// ���ű���
				TitleUtils.sendTitle(player, 
						getConfig().getString("TITLE.Title")
							.replace("&", "��")
							.replace("$player", e.getPlayer().getName())
							.replace("$message", sb.substring(0, sb.length() - 1)), 
						10, 
						getConfig().getInt("TITLE.Stay") * 20, 
						10, 
						getConfig().getString("TITLE.Subtitle")
							.replace("&", "��")
							.replace("$player", e.getPlayer().getName())
							.replace("$message", sb.substring(0, sb.length() - 1)), 
						10, 
						getConfig().getInt("TITLE.Stay") * 20, 
						10);
			}
		}
		else if (type == SpeakerType.BOSSBAR) {
			// ѭ�����
			for (Player player : Bukkit.getOnlinePlayers()) {
				BossBarAPI.addBar(
						player,
						new TextComponent(getConfig().getString("BOSSBAR.Bar")
								.replace("&", "��")
								.replace("$player", e.getPlayer().getName())
								.replace("$message", sb.substring(0, sb.length() - 1))), 
						
						Color.valueOf(getConfig().getString("BOSSBAR.Color")), 
						Style.NOTCHED_20, 
						1.0f, 
						getConfig().getInt("BOSSBAR.Stay") * 20 / 2,
						2);
			}
		}
	}

}
