package manaki.plugin.skybattle.listener;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.hide.VisionHides;
import manaki.plugin.skybattle.spectator.SpectatorGUI;
import manaki.plugin.skybattle.hide.Invisibles;
import manaki.plugin.skybattle.util.Utils;
import manaki.plugin.skybattle.util.command.Command;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    // Hidden players join
    @EventHandler
    public void onHiddenPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        VisionHides.show(p);
    }

    // Hidden players quit
    @EventHandler
    public void onHiddenPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        VisionHides.show(p);
    }

    // Death drop chest
    @EventHandler
    public void onDeathDrop(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        // Set block
        var l1 = p.getLocation();
        var l2 = l1.clone().add(1, 0, 0);
        l1.getBlock().setType(Material.CHEST);
        l2.getBlock().setType(Material.CHEST);

        // Update
        l1.getBlock().getState().update();
        l2.getBlock().getState().update();

        // To one chest
        var bd1 = ((org.bukkit.block.data.type.Chest) l1.getBlock().getBlockData());
        bd1.setType(org.bukkit.block.data.type.Chest.Type.LEFT);
        var bd2 = ((org.bukkit.block.data.type.Chest) l2.getBlock().getBlockData());
        bd2.setType(org.bukkit.block.data.type.Chest.Type.RIGHT);

        l1.getBlock().setBlockData(bd1);
        l2.getBlock().setBlockData(bd2);

        List<ItemStack> drops = Lists.newArrayList();
        drops.addAll(e.getDrops());
        e.getDrops().clear();


        // Shuffle drops
        for (int i = drops.size() ; i < 54 ; i++) {
            drops.add(new ItemStack(Material.AIR));
        }
        Collections.shuffle(drops);

        var c1 = (Chest) l1.getBlock().getState();
        c1.setCustomName("§4Đồ của " + p.getName());
        c1.update();

        for (ItemStack is : drops) c1.getInventory().addItem(is);

    }

    // No fall damage
    @EventHandler(priority = EventPriority.LOWEST)
    public void onFall(EntityDamageEvent e) {
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL) e.setCancelled(true);
    }

    // Death message
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeathMessage(PlayerDeathEvent e) {
        var player = e.getEntity();
        var killer = player.getKiller();
        e.setDeathMessage(null);

        var state = Games.getCurrentGame(player);
        if (state == null) return;

        for (Player p : state.getPlayers()) {
            if (killer != null) p.sendMessage("§f" + player.getName() + " §cbị hạ bởi §e" + killer.getName());
            else p.sendMessage("§f" + player.getName() + " §cbị loại khỏi cuộc chơi");
        }
    }

    // Survival on join
    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        var p = e.getPlayer();
        p.setGameMode(GameMode.SURVIVAL);
    }

    // Velocity not in battle world
    @EventHandler
    public void onVelocity(PlayerVelocityEvent e) {
        var p = e.getPlayer();
        var gm = Games.managerFromWorld(p.getWorld());
        if (gm == null) e.setCancelled(true);
    }

    // Spectator move to another world
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();
        var pw = e.getFrom().getWorld();
        var aw = e.getTo().getWorld();
        if (pw != aw && p.getGameMode() == GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SURVIVAL);
            var gm = Games.managerFromWorld(pw);
            if (gm == null) return;

            for (BossBar bb : gm.getState().getBossbars()) {
                bb.removePlayer(p);
            }
        }
    }

    // GUI
    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        SpectatorGUI.onClick(e);
    }

    @EventHandler
    public void onInvDrag(InventoryDragEvent e) {
        SpectatorGUI.onDrag(e);
    }

    // Complete invisiable
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGH)
    public void onInvisiable(EntityPotionEffectEvent e) {
        if (e.getNewEffect() == null) return;
        if (!(e.getEntity() instanceof Player)) return;
        if (!e.getNewEffect().getType().equals(PotionEffectType.INVISIBILITY)) return;
        var p = (Player) e.getEntity();
        var time = e.getNewEffect().getDuration() * 50;

        Invisibles.hide(p.getName(), time);
        p.sendTitle("", "§aTàng hình (kể cả giáp, vũ khí) trong " + time / 1000 + " giây", 2, 25, 5);
    }

    // Player death
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var gm = Games.managerFromState(state);
        gm.playerDead(p);
    }

    // Quit game
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var gm = Games.managerFromState(state);
        gm.playerQuit(p);
    }

    // Open supply
    @EventHandler
    public void onInvOpen(PlayerInteractEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var block = e.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;

        for (SupplyState ss : state.getSupplyStates()) {
            if (Utils.isSameBlock(ss.getLocation(), block.getLocation())) ss.setOpened(true);
        }
    }

    // PvP
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        var target = (Player) e.getEntity();
        var damager = (Player) e.getDamager();

        var state = Games.getCurrentGame(target);
        if (state == null) return;
        if (state != Games.getCurrentGame(damager)) return;

        if (state.getTeam(target) == state.getTeam(damager)) {
            e.setCancelled(true);
            damager.sendMessage("§cKhông được tấn công đồng đội!");
        }
    }

    // Build (Place)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var b = e.getBlock();
        state.addBlockPlaced(b);
    }

    // Build (Break)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockBreakEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var b = e.getBlock();
        if (!b.isSolid()) return;
        if (!state.isBlockPlaced(b)) {
            e.setCancelled(true);
            p.sendMessage("§cBạn chỉ được đặt những block đã đặt ra!");
            return;
        }

        state.removeBlockPlaced(b);
    }

    // Block click
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        var b = e.getClickedBlock();
        if (b == null || b.getType() == Material.AIR) return;

        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var bm = Games.battleFromState(state);
        for (Map.Entry<Material, List<Command>> entry : bm.getBlockCommands().entrySet()) {
            var m = entry.getKey();
            if (b.getType() == m) {
                e.setCancelled(true);
                for (Command cmd : entry.getValue()) {
                    cmd.execute(SkyBattle.get(), p, Map.of("%player%", p.getName()));
                }
                break;
            }
        }
    }

    // World chat
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);

        var message = e.getMessage();
        var recipients = e.getRecipients();
        recipients.clear();

        // World chat
        if (message.startsWith("@all ") || state == null) {
            e.setFormat("§6[@all] " + p.getName() + ": §f"  + message.replace("@all ", ""));
            recipients.addAll(Games.getWorldChatRecipients(p));
        }

        // Team chat
        else {
            e.setFormat("§a[@team] " + p.getName() + ": §f"  + message);
            for (String tmname : state.getTeam(p).getPlayers()) {
                var teammate = Bukkit.getPlayer(tmname);
                if (teammate != null) recipients.add(teammate);
            }
        }
    }


}
