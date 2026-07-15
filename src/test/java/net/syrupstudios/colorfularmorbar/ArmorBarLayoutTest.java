package net.syrupstudios.colorfularmorbar;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class ArmorBarLayoutTest {
    @Test
    void preservesEquipmentOrderAndFillsMissingPoints() {
        List<String> layout = ArmorBarLayout.build(8, List.of(
                new ArmorBarLayout.Contribution<>("boots", 2),
                new ArmorBarLayout.Contribution<>("chest", 4)
        ), "fallback");

        assertEquals(List.of(
                "boots", "boots", "chest", "chest", "chest", "chest", "fallback", "fallback"
        ), layout.subList(0, 8));
        assertNull(layout.get(8));
        assertEquals(ArmorBarLayout.MAX_POINTS, layout.size());
    }

    @Test
    void clampsToTheVanillaTwentyPointBar() {
        List<String> layout = ArmorBarLayout.build(40,
                List.of(new ArmorBarLayout.Contribution<>("armor", 40)), "fallback");

        assertEquals(ArmorBarLayout.MAX_POINTS, layout.size());
        assertEquals(ArmorBarLayout.MAX_POINTS, layout.stream().filter("armor"::equals).count());
    }

    @Test
    void ignoresInvalidContributionsAndNegativeArmor() {
        List<String> empty = ArmorBarLayout.build(-1, List.of(
                new ArmorBarLayout.Contribution<>("zero", 0),
                new ArmorBarLayout.Contribution<String>(null, 5)
        ), "fallback");

        assertEquals(ArmorBarLayout.MAX_POINTS, empty.size());
        assertEquals(ArmorBarLayout.MAX_POINTS, empty.stream().filter(value -> value == null).count());
    }
}
