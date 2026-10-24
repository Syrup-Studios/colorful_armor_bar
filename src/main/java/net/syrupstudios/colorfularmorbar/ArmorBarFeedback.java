package net.syrupstudios.colorfularmorbar;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ArmorBarFeedback {
    private static final long PULSE_DURATION_NANOS = 350_000_000L;
    private static final EquipmentSlot[] ARMOR_SLOTS = {
            EquipmentSlot.FEET, EquipmentSlot.LEGS, EquipmentSlot.CHEST, EquipmentSlot.HEAD
    };
    private static final Item[] LAST_ITEMS = new Item[ARMOR_SLOTS.length];
    private static final int[] LAST_DAMAGE = new int[ARMOR_SLOTS.length];
    private static final long[] PULSE_STARTS = new long[ARMOR_SLOTS.length];
    private static final int[] PULSE_COLORS = new int[ARMOR_SLOTS.length];

    private static Player lastPlayer;

    private ArmorBarFeedback() {
    }

    public static void update(Player player) {
        if (player != lastPlayer) {
            lastPlayer = player;
            for (int i = 0; i < ARMOR_SLOTS.length; i++) {
                LAST_ITEMS[i] = null;
                PULSE_STARTS[i] = 0L;
            }
        }
        if (player == null) {
            return;
        }

        long now = System.nanoTime();
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            ItemStack stack = player.getItemBySlot(ARMOR_SLOTS[i]);
            if (stack.isEmpty()) {
                LAST_ITEMS[i] = null;
                PULSE_STARTS[i] = 0L;
                continue;
            }

            Item item = stack.getItem();
            int damage = stack.getDamageValue();
            if (item == LAST_ITEMS[i]) {
                if (damage > LAST_DAMAGE[i] && ColorfulArmorBarConfig.DAMAGE_PULSE.get()) {
                    PULSE_COLORS[i] = 0xFFFF4A4A;
                    PULSE_STARTS[i] = now;
                } else if (damage < LAST_DAMAGE[i] && ColorfulArmorBarConfig.REPAIR_PULSE.get()) {
                    PULSE_COLORS[i] = 0xFF48E8FF;
                    PULSE_STARTS[i] = now;
                } else if (damage != LAST_DAMAGE[i]) {
                    PULSE_STARTS[i] = 0L;
                }
            }
            LAST_ITEMS[i] = item;
            LAST_DAMAGE[i] = damage;
        }
    }

    public static void render(GuiGraphics guiGraphics, EquipmentSlot slot, boolean[] alphaMask, int x, int y, int u, int width) {
        long now = System.nanoTime();
        int index = -1;
        for (int i = 0; i < ARMOR_SLOTS.length; i++) {
            if (ARMOR_SLOTS[i] == slot) {
                index = i;
            }
        }
        if (index < 0) {
            return;
        }
        if ((PULSE_COLORS[index] == 0xFFFF4A4A && !ColorfulArmorBarConfig.DAMAGE_PULSE.get())
                || (PULSE_COLORS[index] == 0xFF48E8FF && !ColorfulArmorBarConfig.REPAIR_PULSE.get())) {
            PULSE_STARTS[index] = 0L;
            return;
        }
        long elapsed = now - PULSE_STARTS[index];
        if (PULSE_STARTS[index] == 0L || elapsed < 0L || elapsed >= PULSE_DURATION_NANOS) {
            return;
        }
        float strength = 1.0F - (float) elapsed / PULSE_DURATION_NANOS;
        int color = PULSE_COLORS[index];
        if (strength > 0.0F) {
            int alpha = Math.round(144.0F * strength);
            int argb = (alpha << 24) | (color & 0x00FFFFFF);
            for (int row = 0; row < 9; row++) {
                for (int column = 0; column < width; column++) {
                    if (alphaMask[row * 18 + u + column]) {
                        guiGraphics.fill(x + column, y + row, x + column + 1, y + row + 1, argb);
                    }
                }
            }
        }
    }
}
