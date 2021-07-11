package manaki.plugin.skybattle.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;

public class ItemStackManager {

    private final ItemStack is;
    private final ItemMeta meta;

    private final Plugin plugin;

    public ItemStackManager(ItemStack is) {
        this.is = is;
        this.meta = is.getItemMeta();
        this.plugin = SkyBattle.get();
    }

    public boolean compareSpecial(ItemStack is2) {
        if (is2 == null && is == null) return true;
        if (is2 == null) return false;
        if (is2.getType() != is.getType()) return false;
        if (is2.hasItemMeta() != is.hasItemMeta()) return false;
        if (is.hasItemMeta()) {
            if (is2.getItemMeta().hasCustomModelData() != is.getItemMeta().hasCustomModelData()) return false;
            if (is2.getItemMeta().getCustomModelData() != is.getItemMeta().getCustomModelData()) return false;
            if (!is2.getItemMeta().getDisplayName().equals(is.getItemMeta().getDisplayName())) return false;
        }
        return true;
    }

    public boolean isNull() {
        return is == null || is.getType() == Material.AIR;
    }

    public boolean hasLore() {
        if (meta == null) return false;
        return meta.hasLore();
    }

    public boolean hasDisplayName() {
        if (meta == null) return false;
        return meta.hasDisplayName();
    }

    public List<String> getLore() {
        if (meta == null) return Lists.newArrayList();
        return meta.getLore();
    }

    public String getName() {
        if (hasDisplayName()) return meta.getDisplayName();
        return is.getType().name();
    }

    public void setLore(List<String> lore) {
        meta.setLore(lore);
        is.setItemMeta(meta);
    }

    public void setName(String name) {
        meta.setDisplayName(name);
        is.setItemMeta(meta);
    }

    public void setModelData(int data) {
        meta.setCustomModelData(data);
        is.setItemMeta(meta);
    }

    // Tag

    public boolean hasTag(String key) {
        synchronized (is) {
            if (isNull()) return false;
            if (meta == null) return false;
            NamespacedKey nk = new NamespacedKey(plugin, key);
            return meta.getPersistentDataContainer().has(nk, PersistentDataType.STRING);
        }
    }

    public String getTag(String key) {
        synchronized (is) {
            if (!hasTag(key)) return null;
            NamespacedKey nk = new NamespacedKey(plugin, key);
            return meta.getPersistentDataContainer().get(nk, PersistentDataType.STRING);
        }
    }

    public Map<String, String> getTags() {
        synchronized (is) {
            Map<String, String> m = Maps.newLinkedHashMap();
            for (NamespacedKey nk : meta.getPersistentDataContainer().getKeys()) {
                String v = meta.getPersistentDataContainer().get(nk, PersistentDataType.STRING);
                if (v == null) continue;
                m.put(nk.getKey(), v);
            }
            return m;
        }
    }

    public void setTag(String key, String value) {
        synchronized (is) {
            NamespacedKey nk = new NamespacedKey(plugin, key);
            meta.getPersistentDataContainer().set(nk, PersistentDataType.STRING, value);
            is.setItemMeta(meta);
        }
    }


}
