package living.relics.EntityExtensions;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class BossBarManager {
    // ============================================================
    // ACTIVE BOSS BARS
    // ============================================================
    private static final Map<UUID, ServerBossEvent> ACTIVE_BARS = new HashMap<>();
    // ============================================================
    // REGISTERED EXTENSIONS
    // ============================================================
    private static final Map<EntityType<?>, BossBar> EXTENSIONS = new HashMap<>();

    // ============================================================
    // REGISTER
    // ============================================================
    public static void register(BossBar extension) {
        if (extension == null) {
            return;
        }
        if (extension.getEntity() == null) {
            throw new IllegalStateException("Cannot register BossBar without an entity.");
        }
        EXTENSIONS.put(extension.getEntity(), extension);
        System.out.println("[Living Relics] Registered entity extension: " + extension.getEntity());
    }

    // ============================================================
    // GET EXTENSION
    // ============================================================
    public static BossBar get(EntityType<?> entityType) {
        return EXTENSIONS.get(entityType);
    }

    // ============================================================
    // CREATE BOSS BAR
    // ============================================================
    public static void create(LivingEntity entity, BossBar extension) {
        if (entity == null) {
            return;
        }
        if (extension == null) {
            return;
        }
        if (!extension.hasBossBar()) {
            return;
        }
        /*
         * Evitar duplicar la barra.
         */
        if (ACTIVE_BARS.containsKey(entity.getUUID())) {
            return;
        }
        // ========================================================
        // NAME
        // ========================================================
        int nameColor = parseColor(extension.getBossBarNameColor());
        Component name = Component.literal(extension.getBossBarName()).withStyle(style -> style.withColor(nameColor));
        // ========================================================
        // BAR COLOR
        // ========================================================
        BossEvent.BossBarColor color = getBarColor(extension);
        // ========================================================
        // BAR STYLE
        // ========================================================
        BossEvent.BossBarOverlay overlay = getOverlay(extension);
        // ========================================================
        // CREATE
        // ========================================================
        ServerBossEvent bossBar = new ServerBossEvent(
                entity.getUUID(),
                name,
                color,
                overlay);
        // ========================================================
        // INITIAL PROGRESS
        // ========================================================
        float progress = getProgress(entity);
        bossBar.setProgress(progress);
        // ========================================================
        // REGISTER ACTIVE BAR
        // ========================================================
        ACTIVE_BARS.put(entity.getUUID(), bossBar);
        // ========================================================
        // INITIAL PLAYERS
        // ========================================================
        updatePlayers(entity, bossBar);
        System.out.println("[Living Relics] BossBar created for " + extension.getEntity());
    }
    // ============================================================
    // PROGRESS
    // ============================================================

    private static float getProgress(
            LivingEntity entity) {

        if (entity.getMaxHealth() <= 0.0F) {

            return 0.0F;

        }

        float progress = entity.getHealth()
                / entity.getMaxHealth();

        return Math.max(
                0.0F,
                Math.min(
                        1.0F,
                        progress));

    }

    // ============================================================
    // GET ACTIVE
    // ============================================================

    public static ServerBossEvent getActive(
            UUID uuid) {

        return ACTIVE_BARS.get(
                uuid);

    }

    // ============================================================
    // REMOVE
    // ============================================================

    public static void remove(
            UUID uuid) {

        ServerBossEvent bossBar = ACTIVE_BARS.remove(
                uuid);

        if (bossBar != null) {

            bossBar.removeAllPlayers();

        }

    }

    // ============================================================
    // TICK
    // ============================================================

    public static void tick(
            LivingEntity entity) {

        ServerBossEvent bossBar = ACTIVE_BARS.get(
                entity.getUUID());

        if (bossBar == null) {

            return;

        }

        // ========================================================
        // ENTITY DEAD
        // ========================================================

        if (!entity.isAlive()
                || entity.isRemoved()) {

            remove(
                    entity.getUUID());

            return;

        }

        // ========================================================
        // HEALTH
        // ========================================================

        bossBar.setProgress(
                getProgress(entity));

        // ========================================================
        // PLAYERS
        // ========================================================

        updatePlayers(
                entity,
                bossBar);

    }

    // ============================================================
    // PLAYERS
    // ============================================================

    private static void updatePlayers(
            LivingEntity entity,
            ServerBossEvent bossBar) {

        for (var player : entity.level().players()) {

            if (!(player instanceof ServerPlayer serverPlayer)) {

                continue;

            }

            double distance = player.distanceToSqr(
                    entity);

            // ====================================================
            // 64 BLOCKS
            // ====================================================

            if (distance <= 64 * 64) {

                bossBar.addPlayer(
                        serverPlayer);

            } else {

                bossBar.removePlayer(
                        serverPlayer);

            }

        }

    }

    // ============================================================
    // BAR COLOR
    // ============================================================

    private static BossEvent.BossBarColor getBarColor(
            BossBar extension) {

        return switch (extension.getType()) {

            case MINIBOSS ->
                BossEvent.BossBarColor.BLUE;

            case BOSS ->
                BossEvent.BossBarColor.RED;

            case NORMAL ->
                BossEvent.BossBarColor.WHITE;

        };

    }

    // ============================================================
    // BAR STYLE
    // ============================================================

    private static BossEvent.BossBarOverlay getOverlay(
            BossBar extension) {

        return switch (extension.getBossBarStyle()) {

            case PROGRESS ->
                BossEvent.BossBarOverlay.PROGRESS;

            case NOTCHED_6 ->
                BossEvent.BossBarOverlay.NOTCHED_6;

            case NOTCHED_10 ->
                BossEvent.BossBarOverlay.NOTCHED_10;

            case NOTCHED_12 ->
                BossEvent.BossBarOverlay.NOTCHED_12;

            case NOTCHED_20 ->
                BossEvent.BossBarOverlay.NOTCHED_20;

        };

    }

    // ============================================================
    // HEX COLOR
    // ============================================================

    private static int parseColor(
            String hex) {

        if (hex == null
                || hex.isBlank()) {

            return 0xFFFFFF;

        }

        hex = hex.trim();

        if (hex.startsWith("#")) {

            hex = hex.substring(1);

        }

        try {

            return Integer.parseInt(
                    hex,
                    16);

        } catch (NumberFormatException exception) {

            System.err.println(
                    "[Living Relics] Invalid BossBar color: "
                            + hex);

            return 0xFFFFFF;

        }

    }

    // ============================================================
    // LOOT EVENTS
    // ============================================================

    public static void registerLootEvents() {

        ServerLivingEntityEvents.AFTER_DEATH.register(
                (entity, damageSource) -> {

                    dropLoot(entity);

                });

    }

    // ============================================================
    // DROP LOOT
    // ============================================================

    private static void dropLoot(
            LivingEntity entity) {

        BossBar extension = get(entity.getType());

        if (extension == null) {

            return;

        }

        if (!extension.hasLoot()) {

            return;

        }

        for (BossBar.Drop drop : extension.getDrops()) {

            float chance = drop.getChance();

            if (entity.getRandom().nextFloat() > chance) {

                continue;

            }

            ItemStack stack = new ItemStack(drop.getItem());

            net.minecraft.world.entity.item.ItemEntity itemEntity = new net.minecraft.world.entity.item.ItemEntity(
                    entity.level(),
                    entity.getX(),
                    entity.getY() + 0.5D,
                    entity.getZ(),
                    stack);

            itemEntity.setDefaultPickUpDelay();

            entity.level().addFreshEntity(itemEntity);

        }

    }
}