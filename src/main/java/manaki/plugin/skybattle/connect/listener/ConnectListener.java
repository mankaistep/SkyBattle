package manaki.plugin.skybattle.connect.listener;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.connect.request.util.Requests;
import manaki.plugin.skybattle.connect.team.Team;
import manaki.plugin.skybattle.connect.team.player.TeamPlayer;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.team.BattleTeam;
import manaki.plugin.skybattle.util.Tasks;
import manaki.plugin.skybattle.util.Utils;
import mk.plugin.santory.item.Item;
import mk.plugin.santory.item.ItemType;
import mk.plugin.santory.item.Items;
import mk.plugin.santory.skin.Skins;
import mk.plugin.santory.traveler.Travelers;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class ConnectListener implements @NotNull PluginMessageListener {

    @Override
    public void onPluginMessageReceived(@NotNull String channel, @NotNull Player player, @NotNull byte[] msg) {
        if (!channel.equals(SkyBattle.CHANNEL)) return;

        var in = new DataInputStream(new ByteArrayInputStream(msg));
        String type = null;
        String data = null;
        try {
            type = in.readUTF();
            data = in.readUTF();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Start request
        if (type.equalsIgnoreCase("skybattle-start")) {
            var sr = Requests.parseStart(data);

            var datamap = Utils.read(sr.getData());
            boolean isRanked = Boolean.parseBoolean(datamap.get("ranked"));

            // Check equip after 1s
            Tasks.sync(() -> {
                // Get equip
                for (Team t : sr.getTeams()) {
                    for (TeamPlayer tp : t.getPlayers()) {
                        var p = Bukkit.getPlayer(tp.getName());
                        if (p == null) continue;

                        // Clear data
                        clearData(p);

                        // Set equip
                        for (String idata : tp.getItems()) {
                            var item = Item.parse(idata);
                            item.getData().setExp(0);
                            item.getData().setLevel(0);
                            var is = Items.build(p, item);
                            equip(p, is, item);
                        }

                        // Update state
                        Travelers.updateState(p);

                        // Heal
                        p.setHealthScale(20);
                        p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());

                        // Message
                        p.sendMessage("§c§lGhi §f§l/thoat §c§lđể trở lại máy chủ chính");
                    }
                }
            }, 20);

            // Title
            long wait = 20000L;
            long start = System.currentTimeMillis();
            Tasks.async(() -> {
                long s = (wait - (System.currentTimeMillis() - start)) / 1000;
                // Message
                for (Team t : sr.getTeams()) {
                    for (TeamPlayer tp : t.getPlayers()) {
                        var p = Bukkit.getPlayer(tp.getName());
                        if (p == null) continue;
                        p.sendTitle("§c§l" + s, "§aChuẩn bị vào game...", 0, 120, 0);
                        p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                    }
                }
            }, 0, 20, wait);

            // Start game after
            Tasks.sync(() -> {
                // Convert to skybattle object
                List<BattleTeam> teams = Lists.newArrayList();
                for (Team t : sr.getTeams()) {
                    var bTeam = new BattleTeam(null, t.getPlayers().stream().map(TeamPlayer::getName).collect(Collectors.toList()));
                    teams.add(bTeam);
                }
                // Message
                for (Team t : sr.getTeams()) {
                    for (TeamPlayer tp : t.getPlayers()) {
                        var p = Bukkit.getPlayer(tp.getName());
                        if (p == null) continue;
                        p.sendTitle("", "§cĐang thiết lập...", 0, 120, 0);
                    }
                }
                // Start
                Tasks.async(() -> {
                    Games.start(sr.getBattleId(), teams, true, isRanked);
                });

            }, Long.valueOf(wait).intValue() / 50);
        }

    }

    public static void clearData(Player p) {
        p.getInventory().clear();
        var t = Travelers.get(p);
        t.getData().setExp(0);
        t.getData().getArtifacts().clear();
        Travelers.save(p.getName());
        Travelers.updateState(p);
    }


    private static void equip(Player p, ItemStack is, Item item) {
        var it = item.getModel().getType();
        if (it == ItemType.SKIN) {
            var skin = Skins.read(is);
            switch (skin.getType()) {
                case HEAD:
                    p.getInventory().setHelmet(is);
                    break;
                case OFFHAND:
                    p.getInventory().setItemInOffHand(is);
                    break;
            }
        }
    }

}
