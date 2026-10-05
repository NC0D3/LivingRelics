package living.relics.entity.mob.jellyfish;

public final class JellyfishAnims {

    private JellyfishAnims() {
    }

    public static void playIdle(Jellyfish jellyfish) {

        if (!jellyfish.getHolder()
                .getAnimator()
                .isPlaying("idle")) {

            jellyfish.getHolder()
                    .getAnimator()
                    .playAnimation(
                            "idle",
                            0,
                            true);
        }
    }

    public static void playSwim(Jellyfish jellyfish) {

        if (!jellyfish.getHolder()
                .getAnimator()
                .isPlaying("swim")) {

            jellyfish.getHolder()
                    .getAnimator()
                    .playAnimation(
                            "swim",
                            1,
                            true);
        }
    }

    public static void playAttack(Jellyfish jellyfish) {

        jellyfish.getHolder()
                .getAnimator()
                .playAnimation(
                        "attack",
                        2,
                        false);
    }

    public static void stop(
            Jellyfish jellyfish,
            String animation) {

        jellyfish.getHolder()
                .getAnimator()
                .stopAnimation(animation);
    }
}