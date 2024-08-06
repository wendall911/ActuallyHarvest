package actuallyharvest.event;

import net.minecraft.world.InteractionResult;

public record ClickResult(Boolean hasValue) {

    private static final ClickResult INTERRUPT = new ClickResult(true);
    private static final ClickResult PASS = new ClickResult(null);

    public static ClickResult pass() {
        return PASS;
    }

    public static ClickResult interrupt() {
        return INTERRUPT;
    }

    public boolean isPresent() {
        return hasValue != null;
    }

    public InteractionResult getInteractionResult() {
        if (isPresent()) {
            return hasValue() ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }

        return InteractionResult.PASS;
    }

}
