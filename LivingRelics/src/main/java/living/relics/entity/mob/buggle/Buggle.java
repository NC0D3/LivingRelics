package living.relics.entity.mob.buggle;

import de.tomalbrc.bil.api.AnimatedEntity;
import de.tomalbrc.bil.api.AnimatedEntityHolder;
import de.tomalbrc.bil.core.holder.entity.living.LivingEntityHolder;
import de.tomalbrc.bil.core.model.Model;
import de.tomalbrc.bil.file.loader.BbModelLoader;
import eu.pb4.polymer.core.api.entity.PolymerEntityUtils;
import eu.pb4.polymer.virtualentity.api.attachment.EntityAttachment;
import living.relics.entity.ModEntities;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

// ============================================================
// BUGGLE
// ============================================================
//
// Small passive creature from CoolAliens.
//
// Buggle.java is responsible for:
//
// 1. Entity registration
// 2. BIL model connection
// 3. Minecraft goals
// 4. Attributes
// 5. Sounds
// 6. Persistence
//
// All behaviour related to animation is handled by BuggleAI.
//
// ============================================================

public class Buggle extends Animal implements AnimatedEntity {

// ============================================================
// MODEL
// ============================================================

private static final Model MODEL = BbModelLoader.load(
    Identifier.parse("living_relics:buggle")
);

// ============================================================
// ENTITY TYPE
// ============================================================

public static final EntityType<Buggle> TYPE = ModEntities.register(
    "buggle",
    EntityType.Builder.of(
        Buggle::new,
        MobCategory.CREATURE
    )
    .sized(0.35F, 0.25F)
    .clientTrackingRange(8)
    .updateInterval(3)
);

// ============================================================
// BIL HOLDER
// ============================================================

private final LivingEntityHolder<Buggle> holder;

// ============================================================
// CONSTRUCTOR
// ============================================================

public Buggle(
    EntityType<? extends Buggle> entityType,
    Level level
) {
    super(entityType, level);

    this.holder = new LivingEntityHolder<>(
        this,
        MODEL
    );

    EntityAttachment.ofTicking(
        this.holder,
        this
    );
}

// ============================================================
// GOALS
// ============================================================

@Override
protected void registerGoals() {
    super.registerGoals();

    // --------------------------------------------------------
    // FLOAT
    // --------------------------------------------------------

    this.goalSelector.addGoal(
        0,
        new FloatGoal(this)
    );

    // --------------------------------------------------------
    // PANIC
    // --------------------------------------------------------

    this.goalSelector.addGoal(
        2,
        new PanicGoal(
            this,
            1.0F
        )
    );

    // --------------------------------------------------------
    // TEMPT
    // --------------------------------------------------------

    /*
     * ORIGINAL COOLALIENS ITEM:
     *
     *     ModItems.THERMAL_PULP
     *
     * TEMPORARY:
     *
     *     Minecraft sugar
     */

    this.goalSelector.addGoal(
        4,
        new TemptGoal(
            this,
            0.75F,
            stack -> stack.is(Items.SUGAR),
            false
        )
    );

    // --------------------------------------------------------
    // AVOID PLAYER
    // --------------------------------------------------------

    this.goalSelector.addGoal(
        4,
        new AvoidEntityGoal<>(
            this,
            Player.class,
            4.0F,
            1.0D,
            1.0D
        )
    );

    // --------------------------------------------------------
    // FUTURE: MARSHMELLY
    // --------------------------------------------------------

    /*
     * Uncomment when Marshmelly exists.
     *
     * this.goalSelector.addGoal(
     *     4,
     *     new AvoidEntityGoal<>(
     *         this,
     *         Marshmelly.class,
     *         4.0F,
     *         1.0D,
     *         1.0D
     *     )
     * );
     */

    // --------------------------------------------------------
    // FUTURE: GIANT MELLY
    // --------------------------------------------------------

    /*
     * Uncomment when GiantMelly exists.
     *
     * this.goalSelector.addGoal(
     *     4,
     *     new AvoidEntityGoal<>(
     *         this,
     *         GiantMelly.class,
     *         4.0F,
     *         1.0D,
     *         1.0D
     *     )
     * );
     */

    // --------------------------------------------------------
    // RANDOM STROLL
    // --------------------------------------------------------
    //
    // IMPORTANT:
    //
    // This is the goal that BuggleAI uses to determine whether
    // the Buggle actually decided to perform a normal walk.
    //
    // Therefore an external push/knockback does NOT activate
    // the walk animation.
    //

    this.goalSelector.addGoal(
        5,
        new WaterAvoidingRandomStrollGoal(
            this,
            1.0F
        )
    );

    // --------------------------------------------------------
    // RANDOM LOOK
    // --------------------------------------------------------

    this.goalSelector.addGoal(
        7,
        new RandomLookAroundGoal(this)
    );
}

// ============================================================
// TICK
// ============================================================

@Override
public void tick() {
    super.tick();

    BuggleAI.tick(this);

    // --------------------------------------------------------
    // BODY ROTATION
    // --------------------------------------------------------

    this.yBodyRot = this.getYRot();
    this.yHeadRot = this.getYRot();
}

// ============================================================
// BIL HOLDER
// ============================================================

@Override
public AnimatedEntityHolder getHolder() {
    return this.holder;
}

// ============================================================
// ATTRIBUTES
// ============================================================

private static void registerAttributes() {

    FabricDefaultAttributeRegistry.register(
        TYPE,
        Buggle.createMobAttributes()
            .add(
                Attributes.MAX_HEALTH,
                2.0D
            )
            .add(
                Attributes.TEMPT_RANGE,
                12.0D
            )
            .add(
                Attributes.MOVEMENT_SPEED,
                0.2D
            )
            .add(
                Attributes.ATTACK_DAMAGE,
                1.0D
            )
            .add(
                Attributes.FOLLOW_RANGE,
                12.0D
            )
    );
}

// ============================================================
// BREEDING
// ============================================================

@Override
public boolean isFood(
    ItemStack itemStack
) {
    return false;
}

@Override
public AgeableMob getBreedOffspring(
    ServerLevel level,
    AgeableMob partner
) {
    return null;
}

// ============================================================
// SOUNDS
// ============================================================

@Override
protected void playStepSound(
    BlockPos pos,
    net.minecraft.world.level.block.state.BlockState state
) {

    this.playSound(
        SoundEvents.ARMADILLO_STEP,
        0.5F,
        1.0F
    );
}

@Override
protected SoundEvent getHurtSound(
    DamageSource source
) {
    return SoundEvents.RABBIT_HURT;
}

@Override
protected SoundEvent getDeathSound() {
    return SoundEvents.RABBIT_DEATH;
}

// ============================================================
// PERSISTENCE
// ============================================================

@Override
public boolean requiresCustomPersistence() {
    return true;
}

// ============================================================
// REGISTRATION
// ============================================================

public static void initialize() {

    registerAttributes();

    PolymerEntityUtils.registerType(
        TYPE
    );

    System.out.println(
        "[Living Relics] Registered entity: living_relics:buggle"
    );

    System.out.println(
        "[Living Relics] BIL model: living_relics:buggle.bbmodel"
    );
}

}
