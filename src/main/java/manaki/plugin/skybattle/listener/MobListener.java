package manaki.plugin.skybattle.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.config.model.battle.MobDropModel;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import me.manaki.plugin.shops.storage.ItemStorage;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Shulker;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {

    // Block vanilla spawn
    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) return;
        var le = (LivingEntity) e.getEntity();
        Bukkit.getScheduler().runTaskLater(SkyBattle.get(), () -> {
            if (!Games.isSpeacialEntity(le)) le.remove();
        }, 5);
    }

    // Drop
    @EventHandler
    public void onMythicmobKilled(MythicMobDeathEvent e) {
        var mid = e.getMob().getMobType();
        var killer = e.getKiller();
        if (!(killer instanceof Player)) return;

        var p = (Player) killer;
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var bm = Games.battleFromState(state);
        var mobm = bm.getMobModel();
        if (mobm.getDrops().containsKey(mid)) {
            // Clear default drops
            var list = mobm.getDrops().get(mid);
            var drops = e.getDrops();
            drops.clear();

            // Random
            for (var mdm : list) {
                if (!Utils.rate(mdm.getRate())) continue;
                var is = ItemStorage.get(mdm.getItemId());
                is.setAmount(mdm.getAmount().random());
                drops.add(is);
            }
        }
    }

    // Supply entity death
    @EventHandler
    public void onSupplyEntityDeath(EntityDeathEvent e) {
        var entity = e.getEntity();
        if (!(entity instanceof Shulker)) return;

        var shulker = (Shulker) entity;
        var gm = Games.managerFromWorld(shulker.getWorld());
        if (gm == null) return;

        var state = gm.getState();
        for (SupplyState ss : state.getSupplyStates()) {
            if (ss.getShulker() == shulker) {
                var drops = e.getDrops();
                drops.clear();
                drops.addAll(ss.getItems());
            }
        }
    }

}
