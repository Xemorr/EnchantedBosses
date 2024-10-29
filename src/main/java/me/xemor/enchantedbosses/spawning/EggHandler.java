package me.xemor.enchantedbosses.spawning;

import com.google.common.collect.HashMultimap;
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

public class EggHandler implements Listener {

    HashMap<ItemStack, SpawnEgg> itemToEgg = new HashMap<>();
    HashMap<String, SpawnEgg> nameToEgg = new HashMap<>();

    public EggHandler() {
        EnchantedBosses.getInstance().getServer().getPluginManager().registerEvents(this, EnchantedBosses.getInstance());
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
                        MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getBossSpawnFailMessage(), Placeholder.parsed("player", player.getDisplayName()))
                );
            }
        }
    }

    public void registerEgg(SpawnEgg spawnEgg) {
        itemToEgg.put(spawnEgg.getItemStack(), spawnEgg);
        nameToEgg.put(spawnEgg.getName(), spawnEgg);
    }

    public void clearRegisteredEggs() {
        itemToEgg.clear();
        nameToEgg.clear();
    }

    public Collection<SpawnEgg> getEggs() {
        return itemToEgg.values();
    }

    public SpawnEgg getEgg(String name) {
        return nameToEgg.get(name);
    }
}
