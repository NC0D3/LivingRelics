package living.relics.entity.mob.jellyfish;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.fish.AbstractFish;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

// ============================================================
// JELLYFISH AI
// ============================================================
//
// Cerebro completo de la Jellyfish.
//
// Este archivo controla:
//
//  1. Configuración de comportamiento
//  2. Configuración de animaciones
//  3. Goals
//  4. Movimiento
//  5. Estados de animación
//  6. Ataques
//  7. Acciones sincronizadas con animaciones
//
// Jellyfish.java solamente registra la entidad y conecta
// esta clase con Minecraft.
//
// ============================================================

public final class JellyfishAI {

    // ========================================================
    // 1. BEHAVIOUR CONFIGURATION
    // ========================================================

    // --------------------------------------------------------
    // HUNT
    // --------------------------------------------------------

    /**
     * Radio horizontal de detección.
     */
    public static final double HUNT_RADIUS = 1.0D;

    /**
     * Radio vertical de detección.
     */
    public static final double HUNT_VERTICAL_RANGE = 3.0D;

    /**
     * El pez debe encontrarse debajo de la Jellyfish.
     */
    public static final double MINIMUM_TARGET_HEIGHT = 0.15D;

    private static final double ATTACK_CONE_ANGLE = 30.0D;
    private static final double ATTACK_CONE_MIN_RADIUS = 0.35D;
    /**
     * Distancia máxima para realizar un ataque.
     */
    public static final double ATTACK_DISTANCE = 3.0D;

    /**
     * Probabilidad de acertar.
     */
    public static final float HIT_CHANCE = 0.60F;

    /**
     * Probabilidad de matar inmediatamente al pez
     * después de un golpe exitoso.
     */
    public static final float EFFECTIVE_HUNT_CHANCE = 0.20F;

    /**
     * Daño del ataque.
     */
    public static final float ATTACK_DAMAGE = 2.0F;

    /**
     * Tiempo entre ataques.
     */
    public static final int ATTACK_DELAY = 20;

    // --------------------------------------------------------
    // PULSE MOVEMENT
    // --------------------------------------------------------

    /**
     * Tiempo mínimo entre pulsaciones.
     */
    public static final int MIN_PULSE_DELAY = 100;

    /**
     * Tiempo máximo entre pulsaciones.
     */
    public static final int MAX_PULSE_DELAY = 180;

    /**
     * Duración de una pulsación.
     */
    public static final int PULSE_DURATION = 10;
    /**
     * Fuerza de la pulsación.
     */
    public static final double PULSE_HORIZONTAL_FORCE = 0.08D;
    /**
     * Inclinación vertical máxima.
     */
    public static final double MAX_PULSE_ANGLE = 40.0D;

    // ========================================================
    // 2. ANIMATION CONFIGURATION
    // ========================================================

    // --------------------------------------------------------
    // ANIMATION NAMES
    // --------------------------------------------------------
    public static final String ANIM_IDLE = "idle";
    public static final String ANIM_SWIM = "swim";
    public static final String ANIM_ATTACK = "attack";

    // --------------------------------------------------------
    // ANIMATION PRIORITIES
    // --------------------------------------------------------
    //
    // Mientras mayor sea el número, mayor prioridad.
    //
    // idle = comportamiento base
    // swim = movimiento
    // attack = acción importante
    //
    public static final int PRIORITY_IDLE = 0;
    public static final int PRIORITY_SWIM = 1;
    public static final int PRIORITY_ATTACK = 2;

    // ========================================================
    // 3. GOAL REGISTRATION
    // ========================================================
    //
    // Este método es llamado desde Jellyfish.java porque
    // goalSelector pertenece a Mob y no puede accederse
    // directamente desde esta clase.
    //

    // ========================================================
    // 4. ANIMATION CONTROL
    // ========================================================

    // Reproduce idle si ninguna animación importante está ocupando el modelo.
    private static void playIdle(
            Jellyfish jellyfish) {

        if (isAttackPlaying(jellyfish)) {
            return;
        }

        if (!jellyfish.getHolder()
                .getAnimator()
                .isPlaying(ANIM_IDLE)) {

            jellyfish.getHolder()
                    .getAnimator()
                    .playAnimation(
                            ANIM_IDLE,
                            PRIORITY_IDLE,
                            true);
        }
    }

    // Reproduce la animación de natación.
    private static void playSwim(
            Jellyfish jellyfish) {

        if (isAttackPlaying(jellyfish)) {
            return;
        }

        if (!jellyfish.getHolder()
                .getAnimator()
                .isPlaying(ANIM_SWIM)) {

            jellyfish.getHolder()
                    .getAnimator()
                    .playAnimation(
                            ANIM_SWIM,
                            PRIORITY_SWIM,
                            false,
                            frame -> {
                                if (frame == 30) {
                                    performSwimPulse(jellyfish);
                                }
                            },
                            player -> {
                            });
        }
    }

    // Inicia el ataque.
    private static void playAttack(
            Jellyfish jellyfish,
            AbstractFish fish) {

        jellyfish.getHolder()
                .getAnimator()
                .playAnimation(
                        ANIM_ATTACK,
                        PRIORITY_ATTACK,
                        false,
                        frame -> {
                            if (frame == 35) {
                                hurtFish(jellyfish, fish);
                            }
                        },

                        player -> {
                        });
    }

    // Comprueba si la animación de ataque está activa.
    private static boolean isAttackPlaying(
            Jellyfish jellyfish) {

        return jellyfish.getHolder()
                .getAnimator()
                .isPlaying(ANIM_ATTACK);
    }

    // ========================================================
    // 5. ANIMATION / BEHAVIOUR COMPONENTS
    // ========================================================

    // ========================================================
    // ATTACK COMPONENT
    // ========================================================

    /**
     * Ejecuta el daño real del ataque.
     *
     * IMPORTANTE:
     *
     * Este método NO debe llamarse cuando comienza la animación.
     *
     * La idea es llamarlo desde el frame de impacto de la
     * animación "attack".
     *
     * Por ahora lo dejamos como método independiente para
     * que después podamos conectar el frame exacto de BIL.
     */
    private static void hurtFish(Jellyfish jellyfish, AbstractFish fish) {

        if (!(jellyfish.level() instanceof ServerLevel serverLevel)) {

            return;
        }

        if (fish == null || !fish.isAlive()) {
            return;
        }

        // ----------------------------------------------------
        // DISTANCE CHECK
        // ----------------------------------------------------
        if (!isFishInsideAttackCone(jellyfish, fish)) {
            return;
        }
        // ----------------------------------------------------
        // HIT ROLL
        // ----------------------------------------------------

        float hitRoll = jellyfish.getRandom().nextFloat();

        if (hitRoll > HIT_CHANCE) {

            return;
        }

        // ----------------------------------------------------
        // DAMAGE
        // ----------------------------------------------------

        boolean damaged = fish.hurtServer(
                serverLevel,

                jellyfish.damageSources()
                        .mobAttack(jellyfish),

                ATTACK_DAMAGE);

        if (!damaged) {
            return;
        }

        // ----------------------------------------------------
        // EFFECTIVE HUNT
        // ----------------------------------------------------

        float killRoll = jellyfish.getRandom().nextFloat();

        if (killRoll <= EFFECTIVE_HUNT_CHANCE
                && fish.isAlive()) {
            fish.kill(serverLevel);
        }
    }

    private static void performSwimPulse(Jellyfish jellyfish) {
        // ========================================================
        // RANDOM HORIZONTAL DIRECTION
        // ========================================================

        double angle = jellyfish.getRandom().nextDouble()
                * Math.PI
                * 2.0D;

        double x = Math.cos(angle);

        double z = Math.sin(angle);

        // ========================================================
        // RANDOM VERTICAL ANGLE
        // ========================================================

        double pitchDegrees = (jellyfish.getRandom().nextDouble() * 2.0D - 1.0D)
                * MAX_PULSE_ANGLE;

        double pitchRadians = Math.toRadians(pitchDegrees);

        double horizontalScale = Math.cos(pitchRadians);

        double y = Math.sin(pitchRadians);

        x *= horizontalScale;
        z *= horizontalScale;

        // ========================================================
        // APPLY IMPULSE
        // ========================================================

        double directionX = x * PULSE_HORIZONTAL_FORCE;

        double directionY = y * PULSE_HORIZONTAL_FORCE;

        double directionZ = z * PULSE_HORIZONTAL_FORCE;

        jellyfish.setDeltaMovement(
                jellyfish.getDeltaMovement().add(
                        directionX,
                        directionY,
                        directionZ));
    }

    // ========================================================
    // 6. HUNT GOAL
    // ========================================================
    public static Goal createHuntGoal(
            Jellyfish jellyfish) {

        return new JellyfishHuntGoal(jellyfish);
    }

    private static class JellyfishHuntGoal
            extends Goal {

        private final Jellyfish jellyfish;

        private AbstractFish target;

        private int attackTimer;

        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public JellyfishHuntGoal(
                Jellyfish jellyfish) {

            this.jellyfish = jellyfish;

            setFlags(
                    EnumSet.of(
                            Goal.Flag.LOOK));
        }

        // ====================================================
        // CAN USE
        // ====================================================

        @Override
        public boolean canUse() {

            target = findFish();

            return target != null
                    && isFishInsideAttackCone(jellyfish, target);
        }

        // ====================================================
        // CAN CONTINUE
        // ====================================================

        @Override
        public boolean canContinueToUse() {

            if (target == null) {
                return false;
            }

            if (!target.isAlive()) {
                return false;
            }

            if (!jellyfish.isAlive()) {
                return false;
            }

            return isFishInsideAttackCone(jellyfish, target);
        }

        // ====================================================
        // START
        // ====================================================

        @Override
        public void start() {

            attackTimer = ATTACK_DELAY;

            jellyfish.setTarget(target);
        }

        // ====================================================
        // STOP
        // ====================================================

        @Override
        public void stop() {

            target = null;

            attackTimer = 0;

            jellyfish.setTarget(null);
        }

        // ====================================================
        // TICK
        // ====================================================

        @Override
        public void tick() {

            if (target == null) {
                return;
            }

            // ------------------------------------------------
            // TIMER
            // ------------------------------------------------

            if (attackTimer > 0) {
                attackTimer--;
            }

            // ------------------------------------------------
            // ATTACK
            // ------------------------------------------------

            if (attackTimer <= 0 && isFishInsideAttackCone(jellyfish, target)) {
                if (target == null) {
                    return;
                }

                if (!target.isAlive()) {
                    return;
                }

                playAttack(jellyfish, target);

                attackTimer = ATTACK_DELAY;
            }
        }

        // ====================================================
        // FIND FISH
        // ====================================================

        private AbstractFish findFish() {

            List<AbstractFish> fishList = jellyfish.level()
                    .getEntitiesOfClass(
                            AbstractFish.class,

                            jellyfish
                                    .getBoundingBox()
                                    .inflate(
                                            HUNT_RADIUS,
                                            HUNT_VERTICAL_RANGE,
                                            HUNT_RADIUS),

                            fish -> {

                                if (!fish.isAlive()) {
                                    return false;
                                }

                                return fish.getY() < jellyfish.getY()
                                        - MINIMUM_TARGET_HEIGHT;
                            });

            if (fishList.isEmpty()) {
                return null;
            }

            AbstractFish closest = null;

            double closestDistance = Double.MAX_VALUE;

            for (AbstractFish fish : fishList) {

                double distance = jellyfish.distanceToSqr(fish);

                if (distance < closestDistance) {

                    closestDistance = distance;

                    closest = fish;
                }
            }

            return closest;
        }
    }

    // ========================================================
    // 7. PULSE SWIMMING GOAL
    // ========================================================
    public static Goal createSwimGoal(
            Jellyfish jellyfish) {

        return new JellyfishPulseSwimGoal(jellyfish);
    }

    private static class JellyfishPulseSwimGoal extends Goal {

        private final Jellyfish jellyfish;

        /**
         * Tiempo restante antes de permitir otro swim.
         */
        private int pulseCooldown;

        /**
         * Indica que actualmente estamos ejecutando
         * una animación de swim.
         */
        private boolean swimming;

        // ====================================================
        // CONSTRUCTOR
        // ====================================================

        public JellyfishPulseSwimGoal(
                Jellyfish jellyfish) {

            this.jellyfish = jellyfish;

            setFlags(
                    EnumSet.of(
                            Goal.Flag.MOVE));

            scheduleNextPulse();
        }

        // ====================================================
        // CAN USE
        // ====================================================

        @Override
        public boolean canUse() {

            return jellyfish.isAlive()
                    && jellyfish.isInWater();
        }

        // ====================================================
        // CAN CONTINUE
        // ====================================================

        @Override
        public boolean canContinueToUse() {

            return jellyfish.isAlive()
                    && jellyfish.isInWater();
        }

        // ====================================================
        // START
        // ====================================================

        @Override
        public void start() {

            swimming = false;
        }

        // ====================================================
        // STOP
        // ====================================================

        @Override
        public void stop() {

            swimming = false;
        }

        // ====================================================
        // TICK
        // ====================================================

        @Override
        public void tick() {

            // ==================================================
            // SWIM EN PROGRESO
            // ==================================================

            if (swimming) {

                /*
                 * Mientras la animación esté reproduciéndose,
                 * NO hacemos absolutamente nada.
                 *
                 * Esto evita que playSwim() vuelva a ejecutarse.
                 */

                if (jellyfish.getHolder()
                        .getAnimator()
                        .isPlaying(ANIM_SWIM)) {

                    return;
                }

                // ----------------------------------------------
                // LA ANIMACIÓN TERMINÓ
                // ----------------------------------------------

                swimming = false;

                /*
                 * Ahora comienza el período de idle.
                 */
                scheduleNextPulse();

                return;
            }

            // ==================================================
            // IDLE / COOLDOWN
            // ==================================================

            if (pulseCooldown > 0) {

                pulseCooldown--;

                return;
            }

            // ==================================================
            // COMENZAR UN ÚNICO SWIM
            // ==================================================

            swimming = true;

            playSwim(jellyfish);
        }

        // ====================================================
        // NEXT PULSE
        // ====================================================

        private void scheduleNextPulse() {

            pulseCooldown = MIN_PULSE_DELAY
                    + jellyfish.getRandom().nextInt(
                            MAX_PULSE_DELAY
                                    - MIN_PULSE_DELAY
                                    + 1);
        }
    }

    // ========================================================
    // 8. MOVEMENT BRIDGE
    // ========================================================
    //
    // Jellyfish.java llama esto desde travelInWater().
    //
    // De esta forma toda la lógica de movimiento permanece
    // dentro de JellyfishAI.
    //

    public static void handleWaterMovement(
            Jellyfish jellyfish) {

        // ====================================================
        // MOVEMENT
        // ====================================================

        jellyfish.move(
                net.minecraft.world.entity.MoverType.SELF,
                jellyfish.getDeltaMovement());

        // ====================================================
        // OUT OF WATER
        // ====================================================

        if (!jellyfish.isInWater()) {
            return;
        }

        // ====================================================
        // FRICTION
        // ====================================================

        Vec3 velocity = jellyfish.getDeltaMovement();

        jellyfish.setDeltaMovement(
                velocity.x * 0.90D,
                velocity.y * 0.90D,
                velocity.z * 0.90D);
    }

    // ========================================================
    // 9. GLOBAL BEHAVIOUR / ANIMATION UPDATE
    // ========================================================
    //
    // Este método será el equivalente a un "state machine
    // update" de la Jellyfish.
    //
    // Lo llamaremos desde Jellyfish.tick().
    //
    // ========================================================

    public static void tick(
            Jellyfish jellyfish) {

        // ----------------------------------------------------
        // SERVER
        // ----------------------------------------------------

        if (jellyfish.level().isClientSide()) {
            return;
        }

        // ----------------------------------------------------
        // WATER
        // ----------------------------------------------------

        if (!jellyfish.isInWater()) {
            return;
        }

        // ----------------------------------------------------
        // ATTACK HAS PRIORITY
        // ----------------------------------------------------

        if (isAttackPlaying(jellyfish)) {
            return;
        }

        // ----------------------------------------------------
        // SWIM
        // ----------------------------------------------------

        if (jellyfish.getHolder()
                .getAnimator()
                .isPlaying(ANIM_SWIM)) {

            return;
        }

        // ----------------------------------------------------
        // IDLE
        // ----------------------------------------------------

        playIdle(jellyfish);
    }

    private static boolean isFishInsideAttackCone(
            Jellyfish jellyfish,
            AbstractFish fish) {

        double dx = fish.getX() - jellyfish.getX();

        double dy = fish.getY() - jellyfish.getY();

        double dz = fish.getZ() - jellyfish.getZ();

        // ====================================================
        // DEBE ESTAR DEBAJO
        // ====================================================

        if (dy >= -MINIMUM_TARGET_HEIGHT) {
            return false;
        }

        // ====================================================
        // DISTANCIA VERTICAL
        // ====================================================

        double verticalDistance = -dy;

        if (verticalDistance > ATTACK_DISTANCE) {
            return false;
        }

        // ====================================================
        // RADIO DEL CONO
        // ====================================================

        double coneRadius = Math.max(
                ATTACK_CONE_MIN_RADIUS,

                verticalDistance
                        * Math.tan(
                                Math.toRadians(
                                        ATTACK_CONE_ANGLE)));

        // ====================================================
        // DISTANCIA HORIZONTAL
        // ====================================================

        double horizontalDistanceSquared = dx * dx + dz * dz;

        // ====================================================
        // DENTRO DEL CONO
        // ====================================================

        return horizontalDistanceSquared <= coneRadius * coneRadius;
    }
    // ========================================================
    // CONSTRUCTOR
    // ========================================================

    private JellyfishAI() {
    }
}