package com.livingrelics.engine.entity_extensions.bossbar;

import com.livingrelics.engine.entity_extensions.EntityExtension;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class EntityBossBarManager {
    private static final Map<UUID, ServerBossEvent> ACTIVE_BARS =
            new HashMap<>();
    public static void create(LivingEntity entity,EntityExtension extension) {
        ServerBossEvent bossBar =
            new ServerBossEvent(
                    entity.getUUID(),
                    Component.literal(extension.getBossBarName()).withColor(parseColor(extension.getBossBarNameColor())),
                    getBarColor(extension),
                    getOverlay(extension)
            );
        bossBar.setProgress(entity.getHealth()/entity.getMaxHealth());

        ACTIVE_BARS.put(entity.getUUID(),bossBar);

        System.out.println("[Living Relics] BossBar created for " + extension.getEntity());
    }

    public static ServerBossEvent get(UUID uuid) {
        return ACTIVE_BARS.get(uuid);
    }

    public static void remove(UUID uuid) {
        ACTIVE_BARS.remove(uuid);
    }

    private static BossEvent.BossBarColor getBarColor(
        EntityExtension extension
    ){
        switch(extension.getType()){
            case "MINIBOSS":
                return BossEvent.BossBarColor.BLUE;
            case "BOSS":
                return BossEvent.BossBarColor.RED;
            default:
                return BossEvent.BossBarColor.WHITE;
        }
    }

    public static void tick(LivingEntity entity){
        ServerBossEvent bossBar = ACTIVE_BARS.get(entity.getUUID());
        if(bossBar == null){
            return;
        }
        updatePlayers(entity);
        bossBar.setProgress(entity.getHealth()/entity.getMaxHealth());
        if(entity.isRemoved() || !entity.isAlive()){
            bossBar.removeAllPlayers();
            ACTIVE_BARS.remove(entity.getUUID());
        }
    }


    public static void updatePlayers(LivingEntity entity){
        ServerBossEvent bossBar =ACTIVE_BARS.get(entity.getUUID());
        if(bossBar == null){
            return;
        }
        for(var player : entity.level().players()){
            if(!(player instanceof net.minecraft.server.level.ServerPlayer serverPlayer)){
                continue;
            }

            double distance = player.distanceToSqr(entity);
            if(distance <= 64 * 64){
                bossBar.addPlayer(serverPlayer);
            }else{
                bossBar.removePlayer(serverPlayer);
            }
        }
    }


    private static int parseColor(String hex){
        if(hex == null){
            return 0xFFFFFF;
        }
        if(hex.startsWith("#")){
            hex = hex.substring(1);
        }
        return Integer.parseInt(
                hex,
                16
        );
    }


    private static BossEvent.BossBarOverlay getOverlay(EntityExtension extension){
        switch(extension.getBossBarStyle()){
            case "NOTCHED_6":
                return BossEvent.BossBarOverlay.NOTCHED_6;
            case "NOTCHED_10":
                return BossEvent.BossBarOverlay.NOTCHED_10;
            case "NOTCHED_12":
                return BossEvent.BossBarOverlay.NOTCHED_12;
            case "NOTCHED_20":
                return BossEvent.BossBarOverlay.NOTCHED_20;
            default:
                return BossEvent.BossBarOverlay.PROGRESS;
        }
    }
}