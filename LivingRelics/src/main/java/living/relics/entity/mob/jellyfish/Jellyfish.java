package living.relics.entity.mob.jellyfish;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.BbModelLoader;

import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;

import living.relics.entity.ModEntities;

import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import net.minecraft.resources.Identifier;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.MoverType;

import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.level.Level;

import net.minecraft.world.phys.Vec3;

// ============================================================
// JELLYFISH
// ============================================================

public class Jellyfish
                extends PathfinderMob
                implements AnimatedEntity {

        private static final Model MODEL = BbModelLoader.load(
                        Identifier.parse("living_relics:jellyfish"));

        public static final EntityType<Jellyfish> TYPE = ModEntities.register(
                        "jellyfish",

                        EntityType.Builder.of(
                                        Jellyfish::new,
                                        MobCategory.WATER_CREATURE)

                                        .sized(1.0F, 0.375F)

                                        .clientTrackingRange(8)

                                        .updateInterval(3));

        private final LivingEntityHolder<Jellyfish> holder;

        public Jellyfish(
                        EntityType<? extends Jellyfish> entityType,
                        Level level) {

                super(entityType, level);

                this.holder = new LivingEntityHolder<>(
                                this,
                                MODEL);

                EntityAttachment.ofTicking(
                                this.holder,
                                this);
        }

        @Override
        public boolean canBreatheUnderwater() {
                return true;
        }

        @Override
        protected void registerGoals() {
                super.registerGoals();
                goalSelector.addGoal(
                                1,
                                JellyfishAI.createHuntGoal(this));
                goalSelector.addGoal(
                                4,
                                JellyfishAI.createSwimGoal(this));
        }

        @Override
        protected void travelInWater(
                        Vec3 input,
                        double baseGravity,
                        boolean isFalling,
                        double oldY) {

                JellyfishAI.handleWaterMovement(this);
        }

        @Override
        public AnimatedEntityHolder getHolder() {
                return holder;
        }

        @Override
        public void tick() {

                super.tick();

                JellyfishAI.tick(this);
        }

        private static void registerAttributes() {

                FabricDefaultAttributeRegistry.register(
                                TYPE,

                                Jellyfish.createMobAttributes()
                                                .add(
                                                                Attributes.MAX_HEALTH,
                                                                10.0D)
                                                .add(
                                                                Attributes.MOVEMENT_SPEED,
                                                                0.25D)
                                                .add(
                                                                Attributes.WATER_MOVEMENT_EFFICIENCY,
                                                                1.0D));
        }

        @Override
        public boolean requiresCustomPersistence() {
                return true;
        }

        public static void initialize() {

                registerAttributes();

                PolymerEntityUtils.registerType(TYPE);

                System.out.println(
                                "[Living Relics] Registered entity: " +
                                                "living_relics:jellyfish");

                System.out.println(
                                "[Living Relics] BIL model: " +
                                                "living_relics:jellyfish.bbmodel");
        }
}