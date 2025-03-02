package dev.zoid.warpracticecore.events;

import dev.zoid.warpracticecore.utils.TpaUtil;
import dev.zoid.warpracticecore.utils.TierUtil;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Projectile;
import org.bukkit.projectiles.ProjectileSource;

public class DeathEvent implements Listener {
    private final TierUtil tierUtil;

    public DeathEvent(TierUtil tierUtil) {
        this.tierUtil = tierUtil;
    }

    @EventHandler
    public void deathEvent(PlayerDeathEvent e) {
        Player p = e.getEntity();
        TpaUtil.setDeathLocation(p);
        EntityDamageEvent cause = p.getLastDamageCause();
        String deathMessage = "";

        if (cause instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent entityEvent = (EntityDamageByEntityEvent) cause;
            if (entityEvent.getDamager() instanceof Player) {
                Player killer = (Player) entityEvent.getDamager();
                deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was slain by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
            } else if (entityEvent.getDamager() instanceof Projectile) {
                Projectile proj = (Projectile) entityEvent.getDamager();
                ProjectileSource shooter = proj.getShooter();
                if (shooter instanceof Player) {
                    Player killer = (Player) shooter;
                    deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was shot by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                }
            } else if (entityEvent.getDamager() instanceof TNTPrimed) {
                TNTPrimed tnt = (TNTPrimed) entityEvent.getDamager();
                if (tnt.getSource() instanceof Player) {
                    Player killer = (Player) tnt.getSource();
                    deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was blown up by " + killer.getName() + " [" + tierUtil.getTier(killer.getName()) + "<gray>]";
                }
            }
        } else if (cause != null && cause.getCause() == EntityDamageEvent.DamageCause.VOID) {
            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] fell into the void";
        } else if (cause != null && cause.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] was blown up";
        } else if (cause != null && cause.getCause() == EntityDamageEvent.DamageCause.FALL) {
            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] fell to their death";
        } else {
            deathMessage = "<gray>💀 " + p.getName() + " [" + tierUtil.getTier(p.getName()) + "<gray>] died";
        }

        e.deathMessage(MiniMessage.miniMessage().deserialize(deathMessage));
    }
}