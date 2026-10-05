package living.relics.entity.mob.buggle;

import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;

// ============================================================
// BUGGLE AI
// ============================================================
//
// La animación depende de los Goals REALES que Minecraft tiene
// activos.
//
// EXCEPCIÓN:
//
//     TemptGoal
//
// TemptGoal puede permanecer activo mientras el Buggle está
// quieto frente al jugador. Por eso TemptGoal necesita además
// comprobar walkAnimation.isMoving().
//
// Los demás Goals NO utilizan esa comprobación:
//
//     FloatGoal
//     PanicGoal
//     AvoidEntityGoal
//     WaterAvoidingRandomStrollGoal
//
// Para ellos basta con que el Goal esté activo.
//
// ============================================================

public final class BuggleAI {

    // ========================================================
    // 1. ANIMATIONS
    // ========================================================

    public static final String ANIM_IDLE = "idle";
    public static final String ANIM_WALK = "walk";

    public static final int PRIORITY_IDLE = 0;
    public static final int PRIORITY_WALK = 1;

    // ========================================================
    // 2. MOVEMENT GOAL DETECTION
    // ========================================================

    private static boolean isMovementGoal(
        Object goal
    ) {
        return goal instanceof FloatGoal
            || goal instanceof PanicGoal
            || goal instanceof TemptGoal
            || goal instanceof AvoidEntityGoal
            || goal instanceof WaterAvoidingRandomStrollGoal;
    }

    // ========================================================
    // 3. CHECK REAL RUNNING GOALS
    // ========================================================

    private static boolean isMovingByAI(
        Buggle buggle
    ) {
        return buggle
            .getGoalSelector()
            .getAvailableGoals()
            .stream()
            .anyMatch(
                wrappedGoal -> {

                    // El Goal debe estar realmente ejecutándose.
                    if (
                        !wrappedGoal.isRunning()
                    ) {
                        return false;
                    }

                    Object goal =
                        wrappedGoal.getGoal();

                    // =================================================
                    // TEMPT GOAL
                    // =================================================
                    //
                    // TemptGoal puede estar activo aunque el Buggle
                    // esté completamente quieto.
                    //
                    // En ese caso NO queremos WALK.
                    //
                    // Solamente activamos WALK cuando Minecraft
                    // considera que el Buggle realmente está
                    // realizando movimiento.
                    //
                    if (
                        goal instanceof TemptGoal
                    ) {
                        return buggle.walkAnimation.isMoving();
                    }

                    // =================================================
                    // RESTO DE MOVEMENT GOALS
                    // =================================================
                    //
                    // Conservan exactamente el comportamiento
                    // anterior.
                    //
                    return isMovementGoal(
                        goal
                    );
                }
            );
    }

    // ========================================================
    // 4. PLAY WALK
    // ========================================================

    private static void playWalk(
        Buggle buggle
    ) {
        var animator =
            buggle
                .getHolder()
                .getAnimator();

        // WALK ya está reproduciéndose.
        //
        // No la reiniciamos en cada tick.
        if (
            animator.isPlaying(
                ANIM_WALK
            )
        ) {
            return;
        }

        // WALK tiene prioridad sobre IDLE.
        animator.stopAnimation(
            ANIM_IDLE
        );

        animator.playAnimation(
            ANIM_WALK,
            PRIORITY_WALK
        );
    }

    // ========================================================
    // 5. PLAY IDLE
    // ========================================================

    private static void playIdle(
        Buggle buggle
    ) {
        var animator =
            buggle
                .getHolder()
                .getAnimator();

        // Si WALK todavía está reproduciéndose,
        // dejamos que termine naturalmente.
        if (
            animator.isPlaying(
                ANIM_WALK
            )
        ) {
            return;
        }

        // IDLE ya está reproduciéndose.
        if (
            animator.isPlaying(
                ANIM_IDLE
            )
        ) {
            return;
        }

        animator.playAnimation(
            ANIM_IDLE,
            PRIORITY_IDLE
        );
    }

    // ========================================================
    // 6. MAIN AI TICK
    // ========================================================

    public static void tick(
        Buggle buggle
    ) {
        // La lógica de Goals vive en servidor.
        if (
            buggle.level().isClientSide()
        ) {
            return;
        }

        var animator =
            buggle
                .getHolder()
                .getAnimator();

        // ====================================================
        // AI ESTÁ MOVIENDO AL BUGGLE
        // ====================================================

        if (
            isMovingByAI(
                buggle
            )
        ) {
            playWalk(
                buggle
            );

            return;
        }

        // ====================================================
        // NINGÚN MOVEMENT GOAL ESTÁ ACTIVO
        // ====================================================
        //
        // Si WALK sigue reproduciéndose, NO la interrumpimos.
        //
        // Esto permite que la animación NON-LOOP termine
        // naturalmente.
        //

        if (
            animator.isPlaying(
                ANIM_WALK
            )
        ) {
            return;
        }

        // ====================================================
        // IDLE
        // ====================================================

        playIdle(
            buggle
        );
    }

    // ========================================================
    // 7. CONSTRUCTOR
    // ========================================================

    private BuggleAI() {
    }
}
