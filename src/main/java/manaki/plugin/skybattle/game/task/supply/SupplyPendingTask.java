package manaki.plugin.skybattle.game.task.supply;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.task.a.APendingTask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.ItemStackManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

public class SupplyPendingTask extends APendingTask {

    private SupplyState ss;

    public SupplyPendingTask(GameState state, long period, String message, SupplyState ss) {
        super(state, "supplyPending", period, message, 5);
        this.ss = ss;
    }

    @Override
    public Runnable getStarter() {
        return () -> {
            var l = ss.getLocation();
            spawnFallingSupply(l);
        };
    }

    public void spawnFallingSupply(Location l) {
        var loc = l.clone().add(0, 70, 0);
        var parachute = new ItemStack(Material.SPRUCE_SAPLING);
        var meta = parachute.getItemMeta();
        meta.setCustomModelData(2);
        parachute.setItemMeta(meta);

        var s = (Shulker) loc.getWorld().spawnEntity(loc, EntityType.SHULKER);
        Games.setSpecialEntity(s);
        s.setAI(false);
        s.setGlowing(true);
        s.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
        s.setHealth(1000);

        var c = (Chicken) loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
        Games.setSpecialEntity(c);
        c.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
        c.setHealth(1000);
        c.setInvisible(true);

        var a = (ArmorStand) loc.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        Games.setSpecialEntity(a);
        a.getEquipment().setHelmet(parachute);
        a.setInvisible(true);
        a.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(1000);
        a.setHealth(1000);

        c.addPassenger(s);
        c.addPassenger(a);
        c.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, Integer.MAX_VALUE, 10, false, false, false));
        c.getLocation().setDirection(new Vector(1, 0, 0));

        new BukkitRunnable() {
            @Override
            public void run() {
                if (!c.isOnGround()) return;

                this.cancel();

                a.remove();
                s.remove();
                c.remove();

                // Spawn
                Shulker shulker = (Shulker) l.getWorld().spawnEntity(l.getBlock().getLocation().add(0.5, 0, 0.5), EntityType.SHULKER);
                shulker.setAI(false);
                shulker.setGlowing(true);
                shulker.setCustomName("§cĐánh em đi");
                shulker.setCustomNameVisible(true);

                ss.setShulker(shulker);
                Games.setSpecialEntity(shulker);

                // Set
                getState().addSupply(ss);
            }
        }.runTaskTimer(SkyBattle.get(), 0, 1);
    }


}
