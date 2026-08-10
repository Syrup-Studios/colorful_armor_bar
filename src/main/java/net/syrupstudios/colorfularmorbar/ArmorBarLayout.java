package net.syrupstudios.colorfularmorbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Pure conversion from per-item armor contributions to twenty half-point slots. */
public final class ArmorBarLayout {
    public static final int MAX_POINTS = 20;

    private ArmorBarLayout() {
    }

    public record Contribution<T>(T texture, int points) {
    }

    public static <T> List<T> build(
            int armorValue,
            List<Contribution<T>> contributions,
            T fallback,
            boolean groupMatchingArmor
    ) {
        int visiblePoints = Math.max(0, Math.min(armorValue, MAX_POINTS));
        List<T> result = new ArrayList<>(Collections.nCopies(MAX_POINTS, null));
        int point = 0;

        for (Contribution<T> contribution : grouped(contributions, groupMatchingArmor)) {
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

    private static <T> List<Contribution<T>> grouped(
            List<Contribution<T>> contributions,
            boolean groupMatchingArmor
    ) {
        if (!groupMatchingArmor) {
            return contributions;
        }

        Map<T, Integer> pointsByTexture = new LinkedHashMap<>();
        for (Contribution<T> contribution : contributions) {
            if (contribution.texture() != null && contribution.points() > 0) {
                pointsByTexture.merge(contribution.texture(), contribution.points(), Integer::sum);
            }
        }

        List<Contribution<T>> grouped = new ArrayList<>(pointsByTexture.size());
        pointsByTexture.forEach((texture, points) -> grouped.add(new Contribution<>(texture, points)));
        grouped.sort((left, right) -> Integer.compare(right.points(), left.points()));
        return grouped;
    }
}
