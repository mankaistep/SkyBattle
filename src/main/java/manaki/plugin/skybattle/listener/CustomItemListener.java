package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.TNT;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class CustomItemListener implements Listener {

    /*
    TNT
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onPlaceTNT(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var b = e.getBlock();
        if (Games.getCurrentGame(p) == null) return;
        if (b.getType() != Material.TNT) return;

        Tasks.sync(() -> {
            if (e.isCancelled()) return;
            b.setType(Material.AIR);
            p.getWorld().spawnEntity(b.getLocation().add(0.5, 0, 0.5), EntityType.PRIMED_TNT);
        });
    }

    @EventHandler
    public void onThrowTNT(PlayerInteractEvent e) {
        var p = e.getPlayer();
        if (Games.getCurrentGame(p) == null) return;
        if (!e.getAction().name().startsWith("RIGHT")) return;

        var ishand = p.getInventory().getItemInMainHand();
        if (ishand.getType() != Material.TNT) return;

        e.setCancelled(true);
        ishand.setAmount(ishand.getAmount() - 1);
        p.updateInventory();

        var v = p.getLocation().getDirection().multiply(0.5);
        if (p.isSneaking()) v = v.multiply(1.5);

        var tnt = (TNTPrimed) p.getWorld().spawnEntity(p.getLocation().add(0, 1.7, 0).add(p.getLocation().getDirection().multiply(2)), EntityType.PRIMED_TNT);
        tnt.setVelocity(v);
        tnt.setFuseTicks(25);
    }

    /*
    FIREBALL
     */
    @EventHandler
    public void onThrowFireball(PlayerInteractEvent e) {
        var p = e.getPlayer();
        if (Games.getCurrentGame(p) == null) return;
        if (!e.getAction().name().startsWith("RIGHT")) return;

        var ishand = p.getInventory().getItemInMainHand();
        if (ishand.getType() != Material.FIRE_CHARGE) return;

        e.setCancelled(true);
        ishand.setAmount(ishand.getAmount() - 1);
        p.updateInventory();

        var v = p.getLocation().getDirection().multiply(2);
        var fireball = (Fireball )p.getWorld().spawnEntity(p.getLocation().add(0, 1.7, 0).add(p.getLocation().getDirection().multiply(2)), EntityType.FIREBALL);
        fireball.setYield(3);
        fireball.setVelocity(v);
    }

    /*
    FIREWORK
     */
    @EventHandler
    public void onFireworkLaunch(PlayerInteractEvent e) {
        var p = e.getPlayer();
        if (Games.getCurrentGame(p) == null) return;
        if (!e.getAction().name().startsWith("RIGHT")) return;

        var ishand = p.getInventory().getItemInMainHand();
        if (ishand.getType() != Material.FIREWORK_ROCKET) return;

        e.setCancelled(true);
        ishand.setAmount(ishand.getAmount() - 1);
        p.updateInventory();

        var v = p.getLocation().getDirection().multiply(7);
        p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
        Tasks.async(() -> {
            p.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, p.getLocation(), 5, 0.2, 0.2, 0.2, 0.1);
        }, 0, 1, 1000L);
        p.setVelocity(v);
    }



    /*
    END_ROD - ANG TEN
     */
    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        var p = e.getPlayer();
        if (Games.getCurrentGame(p) == null) return;
        if (!e.getAction().name().startsWith("RIGHT")) return;

        var ishand = p.getInventory().getItemInMainHand();
        if (ishand.getType() != Material.END_ROD) return;

        e.setCancelled(true);
        ishand.setAmount(ishand.getAmount() - 1);
        p.updateInventory();

        var state = Games.getCurrentGame(p);
        for (Player player : state.getPlayers()) {
            player.setGlowing(true);
            player.playSound(player.getLocation(), Sound.BLOCK_WOODEN_BUTTON_CLICK_ON, 1, 1);
            player.sendTitle("", "§aBạn đã bị lộ diện bởi Ăng ten", 5, 25, 5);
            Tasks.sync(() -> {
                player.setGlowing(false);
            }, 60);
        }
    }

    // EXPLODE WITHOUT DROP
    @EventHandler
    public void onExplode(EntityExplodeEvent e) {
        var w = e.getEntity().getWorld();
        var gm = Games.managerFromWorld(w);
        if (gm == null) return;
        List<Block> blocks = List.copyOf(e.blockList());
        e.blockList().clear();
        Tasks.sync(() -> {
            for (Block b : blocks) {
                if (b.getType() != Material.CHEST) b.setType(Material.AIR);
            }
        });
    }

}
