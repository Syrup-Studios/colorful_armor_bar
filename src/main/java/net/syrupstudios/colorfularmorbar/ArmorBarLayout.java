package net.syrupstudios.colorfularmorbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Pure conversion from per-item armor contributions to twenty half-point slots. */
public final class ArmorBarLayout {
    public static final int MAX_POINTS = 20;

    private ArmorBarLayout() {
    }

    public record Contribution<T>(T texture, int points) {
    }

    public static <T> List<T> build(int armorValue, List<Contribution<T>> contributions, T fallback) {
        int visiblePoints = Math.max(0, Math.min(armorValue, MAX_POINTS));
        List<T> result = new ArrayList<>(Collections.nCopies(MAX_POINTS, null));
        int point = 0;

        for (Contribution<T> contribution : contributions) {
            if (contribution.texture() == null || contribution.points() <= 0) {
                continue;
            }
            for (int i = 0; i < contribution.points() && point < visiblePoints; i++) {
                result.set(point++, contribution.texture());
            }
        }

        while (point < visiblePoints) {
            result.set(point++, fallback);
        }
        return result;
    }
}
