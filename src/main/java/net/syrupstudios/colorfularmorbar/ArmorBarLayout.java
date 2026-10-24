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

    public record Contribution<T>(T texture, int points, Object groupKey) {
        public Contribution(T texture, int points) {
            this(texture, points, texture);
        }
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

        Map<Object, List<Contribution<T>>> byGroup = new LinkedHashMap<>();
        for (Contribution<T> contribution : contributions) {
            if (contribution.texture() != null && contribution.points() > 0) {
                byGroup.computeIfAbsent(contribution.groupKey(), key -> new ArrayList<>()).add(contribution);
            }
        }

        List<List<Contribution<T>>> groups = new ArrayList<>(byGroup.values());
        groups.sort((left, right) -> Integer.compare(
                right.stream().mapToInt(Contribution::points).sum(),
                left.stream().mapToInt(Contribution::points).sum()));
        List<Contribution<T>> grouped = new ArrayList<>(contributions.size());
        groups.forEach(grouped::addAll);
        return grouped;
    }
}
