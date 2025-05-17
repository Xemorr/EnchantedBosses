package me.xemor.enchantedbosses.spawning;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import me.xemor.enchantedbosses.EnchantedBosses;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class EggHandler implements Listener {

    @JsonIgnore
    private Map<ItemStack, SpawnEgg> itemToEgg = new HashMap<>();
    @JsonIgnore
    private Map<String, SpawnEgg> nameToEgg = new HashMap<>();

    public EggHandler() {
        EnchantedBosses.getInstance().getServer().getPluginManager().registerEvents(this, EnchantedBosses.getInstance());
    }

    @JsonAnySetter
    public void addEgg(String key, SpawnEgg value) {
        itemToEgg.put(value.getItemStack(), value);
        nameToEgg.put(key, value);
    }

    @EventHandler
    public void onRightClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        ItemStack item = e.getItem();
        ItemStack oneItem;
        if (item == null) {
            return;
        }
        if (e.getItem().getAmount() > 1) {
            oneItem = item.clone();
            oneItem.setAmount(1);
        }
        else {
            oneItem = item;
        }
        SpawnEgg spawnEgg = itemToEgg.get(oneItem);
        if (spawnEgg != null) {
            e.setCancelled(true);
            if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
                return;
            }
            boolean success = spawnEgg.spawn(player.getLocation().getChunk());
            if (success) {
                item.setAmount(item.getAmount() - 1);
            }
            else {
                EnchantedBosses.getBukkitAudiences().player(player).sendMessage(
                        MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getLanguageConfig().getBossSpawnFail(), Placeholder.parsed("player", player.getDisplayName()))
                );
            }
        }
    }

    public void clearRegisteredEggs() {
        itemToEgg.clear();
        nameToEgg.clear();
    }

    public Collection<Map.Entry<String, SpawnEgg>> getEggs() {
        return nameToEgg.entrySet();
    }

    public SpawnEgg getEgg(String name) {
        return nameToEgg.get(name);
    }
}
