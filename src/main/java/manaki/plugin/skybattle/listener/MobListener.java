package manaki.plugin.skybattle.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntitySpawnEvent;

public class MobListener implements Listener {

    @EventHandler
    public void onSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof LivingEntity)) return;
        var le = (LivingEntity) e.getEntity();
        Bukkit.getScheduler().runTaskLater(SkyBattle.get(), () -> {
            if (!MythicMobs.inst().getMobManager().isActiveMob(le.getUniqueId())) le.remove();
        }, 5);
    }
}
