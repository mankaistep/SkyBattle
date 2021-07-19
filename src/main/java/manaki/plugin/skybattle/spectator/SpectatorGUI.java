package manaki.plugin.skybattle.spectator;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.manager.GameManager;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.BattleTeam;
import manaki.plugin.skybattle.util.Utils;
import mk.plugin.santory.utils.ItemStackManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class SpectatorGUI {

    public static void open(Player player) {
        var inv = Bukkit.createInventory(new Holder(), 54, "§0§lCHẾ ĐỘ KHÁN GIẢ");
        player.openInventory(inv);
        player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);

        new BukkitRunnable() {
            @Override
            public void run() {
                if (player.getOpenInventory().getTopInventory() != inv) {
                    this.cancel();
                    return;
                }
                update(inv);
            }
        }.runTaskTimerAsynchronously(SkyBattle.get(), 0, 5);
    }

    public static void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Holder)) return;
        e.setCancelled(true);
        if (e.getClickedInventory() != e.getWhoClicked().getOpenInventory().getTopInventory()) return;

        var player = (Player) e.getWhoClicked();
        int slot = e.getSlot();
        var gm = Games.getManagers().get(slot);
        if (gm == null) return;

        var state = gm.getState();
        Spectators.setSpectator(player, state);

    }

    public static void onDrag(InventoryDragEvent e) {
        if (!(e.getInventory().getHolder() instanceof Holder)) return;
        e.setCancelled(true);
    }

    private static void update(Inventory inv) {
        var list = Lists.newArrayList(Games.getManagers());
        for (int i = 0; i < list.size(); i++) {
            inv.setItem(i, getIcon(list.get(i)));
        }
    }

    private static ItemStack getIcon(GameManager gm) {
        var is = new ItemStack(Material.GREEN_CONCRETE);
        var ism = new ItemStackManager(SkyBattle.get(), is);

        var state = gm.getState();
        ism.setName("§6§lPhòng #" + state.getId());

        List<String> lore = Lists.newArrayList();
        lore.add("§eThời gian: §f" + Utils.format(state.getTime()));
        lore.add("§eNgười chơi: ");
        for (BattleTeam bt : state.getStartTeams()) {
            for (String pn : bt.getPlayers()) {
                var ps = state.getPlayerState(pn);
                var alive = ps != null && !ps.isDead();
                if (alive) lore.add("§a ● " + pn);
                else lore.add("§7 ● " + pn);
            }
        }
        ism.setLore(lore);

        return is;
    }

}

class Holder implements InventoryHolder {

    @Override
    public @NotNull Inventory getInventory() {
        return null;
    }

}
