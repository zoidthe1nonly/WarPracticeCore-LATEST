package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.utils.TpaUtil;
import dev.zoid.warpracticecore.utils.TierUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.projectiles.ProjectileSource;

public class DeathEvent implements Listener {
    private final TierUtil tierUtil;
    private final JavaPlugin plugin;
    private final MiniMessage mm = MiniMessage.miniMessage();

    public DeathEvent(TierUtil tierUtil, JavaPlugin plugin) {
        this.tierUtil = tierUtil;
        this.plugin = plugin;
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent e) {
        Player p = e.getEntity();
        World eventsWorld = Bukkit.getWorld("events");

        EntityDamageEvent cause = p.getLastDamageCause();
        String deathMessage = "";
        Player killer = null;

        if (cause instanceof EntityDamageByEntityEvent entityEvent) {
            if (entityEvent.getDamager() instanceof Player damager) {
                killer = damager;
                deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was slain by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
            } else if (entityEvent.getDamager() instanceof Projectile proj) {
                ProjectileSource shooter = proj.getShooter();
                if (shooter instanceof Player damager) {
                    killer = damager;
                    deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was shot by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                }
            } else if (entityEvent.getDamager() instanceof TNTPrimed tnt) {
                if (tnt.getSource() instanceof Player damager) {
                    killer = damager;
                    if (cause.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && p.getLocation().getBlock().getType().toString().contains("RESPAWN_ANCHOR")) {
                        deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was anchored by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                    } else {
                        deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was blown up by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                    }
                }
            } else if (entityEvent.getDamager() instanceof EnderCrystal) {
                if (entityEvent.getDamager().getLastDamageCause() instanceof EntityDamageByEntityEvent crystalDamage) {
                    if (crystalDamage.getDamager() instanceof Player damager) {
                        killer = damager;
                        deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was crystalled by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                    }
                }
            }
            else if (entityEvent.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION && p.getLocation().getBlock().getType().toString().contains("RESPAWN_ANCHOR")) {
                if (entityEvent.getDamager() instanceof org.bukkit.entity.LivingEntity) {
                    org.bukkit.entity.LivingEntity damager = (org.bukkit.entity.LivingEntity) entityEvent.getDamager();
                    if (damager.getLastDamageCause() instanceof EntityDamageByEntityEvent lastDamageEvent) {
                        if (lastDamageEvent.getDamager() instanceof Player damagerPlayer) {
                            killer = damagerPlayer;
                            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was anchored by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                        }
                    }
                }
            }
        } else if (cause != null) {
            switch (cause.getCause()) {
                case VOID -> deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] fell into the void";
                case ENTITY_EXPLOSION -> deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was blown up";
                case FALL -> deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] fell to their death";
                default -> deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] died";
            }
        } else {
            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] died";
        }

        e.deathMessage(mm.deserialize(deathMessage));
        p.playSound(p.getLocation(), Sound.ENTITY_WARDEN_DEATH, 1.0f, 1.0f);

        if (killer != null) {
            killer.playSound(killer.getLocation(), Sound.ENTITY_WARDEN_ATTACK_IMPACT, 1.0f, 1.0f);
        }

        if (eventsWorld != null && p.getWorld().equals(eventsWorld)) {
            p.setGameMode(GameMode.SPECTATOR);
        } else {
            TpaUtil.setDeathLocation(p);
        }
    }
}