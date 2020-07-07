package de.cuuky.cfw.clientadapter.board.tablist;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import de.cuuky.cfw.clientadapter.board.CustomBoard;
import de.cuuky.cfw.clientadapter.board.CustomBoardType;
import de.cuuky.cfw.player.CustomPlayer;
import de.cuuky.cfw.utils.JavaUtils;
import de.cuuky.cfw.version.BukkitVersion;
import de.cuuky.cfw.version.VersionUtils;

public class CustomTablist<T extends CustomPlayer> extends CustomBoard<T> {

	private HashMap<Player, ArrayList<String>> headerReplaces;
	private HashMap<Player, ArrayList<String>> footerReplaces;

	private String tabname;

	public CustomTablist(T player) {
		super(CustomBoardType.TABLIST, player);
	}

	@Override
	protected void onEnable() {
		this.headerReplaces = new HashMap<>();
		this.footerReplaces = new HashMap<>();
	}

	private Object[] updateList(T player, boolean header) {
		ArrayList<String> tablistLines = header ? this.getUpdateHandler().getTablistHeader(player) : this.getUpdateHandler().getTablistFooter(player), oldList = null;

		if(header) {
			oldList = headerReplaces.get(player.getPlayer());
			if(oldList == null)
				headerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		} else {
			oldList = footerReplaces.get(player.getPlayer());
			if(oldList == null)
				footerReplaces.put(player.getPlayer(), oldList = new ArrayList<>());
		}

		if(tablistLines == null)
			return new Object[] { false };

		boolean changed = false;
		for(int index = 0; index < tablistLines.size(); index++) {
			String line = tablistLines.get(index);

			if(oldList.size() < tablistLines.size()) {
				oldList.add(line);
				changed = true;
			} else if(!oldList.get(index).equals(line)) {
				oldList.set(index, line);
				changed = true;
			}
		}

		return new Object[] { changed, oldList };
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onUpdate() {
		String tablistname = getUpdateHandler().getTablistName(player);

		if(tablistname != null) {
			int maxlength = BukkitVersion.ONE_8.isHigherThan(VersionUtils.getVersion()) ? 16 : -1;
			if(maxlength > 0)
				if(tablistname.length() > maxlength)
					tablistname = this.player.getName();

			if(this.tabname == null || !this.tabname.equals(tablistname))
				player.getPlayer().setPlayerListName(this.tabname = tablistname);
		}

		Object[] headerUpdate = updateList(player, true);
		Object[] footerUpdate = updateList(player, false);

		if((boolean) headerUpdate[0] || (boolean) footerUpdate[0])
			player.getNetworkManager().sendTablist(JavaUtils.getArgsToString((ArrayList<String>) headerUpdate[1], "\n"), JavaUtils.getArgsToString((ArrayList<String>) footerUpdate[1], "\n"));
	}
}