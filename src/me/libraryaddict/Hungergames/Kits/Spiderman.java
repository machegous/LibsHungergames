package me.libraryaddict.Hungergames.Kits;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.metadata.FixedMetadataValue;
import me.libraryaddict.Hungergames.Hungergames;
import me.libraryaddict.Hungergames.Managers.KitManager;
import me.libraryaddict.Hungergames.Types.HungergamesApi;

public class Spiderman implements Listener {
    private KitManager kits = HungergamesApi.getKitManager();
    private Hungergames hg = HungergamesApi.getHungergames();
    private HashMap<String, ArrayList<Long>> cooldown = new HashMap<String, ArrayList<Long>>();

    @EventHandler
    public void onProjectileLaunch(ProjectileLaunchEvent event) {
        if (event.getEntityType() == EntityType.SNOWBALL) {
            if (event.getEntity().getShooter() != null && event.getEntity().getShooter() instanceof Player) {
                Player p = (Player) event.getEntity().getShooter();
                if (!kits.hasAbility(p, "Spiderman"))
                    return;
                ArrayList<Long> cooldowns;
                if (cooldown.containsKey(p.getName()))
                    cooldowns = cooldown.get(p.getName());
                else {
                    cooldowns = new ArrayList<Long>();
                    cooldown.put(p.getName(), cooldowns);
                }
                if (cooldowns.size() == 3) {
                    if (cooldowns.get(2) >= System.currentTimeMillis()) {
                        event.setCancelled(true);
                        p.sendMessage(ChatColor.BLUE + "Your web shooters havn't refilled yet! Wait "
                                + ((cooldowns.get(2) - System.currentTimeMillis()) / 1000) + " seconds!");
                        return;
                    }
                    cooldowns.remove(2);
                }
                event.getEntity().setMetadata("Spiderball", new FixedMetadataValue(hg, "Spiderball"));
                cooldowns.add(System.currentTimeMillis() + 30000);
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity().hasMetadata("Spiderball")) {
            Location loc = event.getEntity().getLocation();
            int x = new Random().nextInt(2) - 1;
            int z = new Random().nextInt(2) - 1;
            for (int y = 0; y < 2; y++) {
                Block b = loc.clone().add(x, y, z).getBlock();
                if (b.getType() == Material.AIR)
                    b.setType(Material.WEB);
            }
            event.getEntity().remove();
        }
    }
}