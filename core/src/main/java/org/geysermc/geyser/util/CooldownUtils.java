/*
 * Copyright (c) 2019-2022 GeyserMC. http://geysermc.org
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 * @author GeyserMC
 * @link https://github.com/GeyserMC/Geyser
 */

package org.geysermc.geyser.util;

import lombok.Getter;
import org.cloudburstmc.protocol.bedrock.packet.SetTitlePacket;
import org.geysermc.geyser.session.GeyserSession;
import org.geysermc.geyser.text.ChatColor;
import org.geysermc.mcprotocollib.protocol.data.game.entity.player.GameMode;

/**
 * Manages the sending of a cooldown indicator to the Bedrock player as there is no cooldown indicator in Bedrock.
 * Much of the work here is from the wonderful folks from <a href="https://github.com/ViaVersion/ViaRewind">ViaRewind</a>
 */
public class CooldownUtils {
    /**
     * The title indicator only has a bounded set of possible outputs (one per number of dark-grey
     * segments). Precomputing them avoids rebuilding a {@link StringBuilder} and {@link String}
     * on every tick while the cooldown is active.
     */
    private static final String[] TITLES = buildTitles();

    /**
     * The integrated-pack cooldown value is bounded as well: the number of "crs"/"htb" segments
     * is derived from {@code Math.round(cooldown * 16)}, which is 0-16 for a cooldown in [0, 1).
     */
    private static final String[] CROSSHAIR_VALUES = buildValues("crs");
    private static final String[] HOTBAR_VALUES = buildValues("htb");

    private static String[] buildTitles() {
        String[] titles = new String[11];
        for (int darkGrey = 0; darkGrey <= 10; darkGrey++) {
            StringBuilder builder = new StringBuilder(ChatColor.DARK_GRAY);
            for (int i = 0; i < darkGrey; i++) {
                builder.append("˙");
            }
            builder.append(ChatColor.GRAY);
            for (int i = darkGrey; i < 10; i++) {
                builder.append("˙");
            }
            titles[darkGrey] = builder.toString();
        }
        return titles;
    }

    private static String[] buildValues(String prefix) {
        String[] values = new String[17];
        for (int i = 0; i < values.length; i++) {
            values[i] = prefix + ":" + i;
        }
        return values;
    }

    /**
     * Sets the last hit time for use when ticking the attack cooldown
     */
    public static void setCooldownHitTime(GeyserSession session) {
        if (session.getPreferencesCache().getCooldownPreference() == CooldownType.DISABLED) {
            return;
        }
        double attackSpeed = session.getAttackSpeed();
        if (attackSpeed == 0.0 || attackSpeed > 20) {
            return; // 0.0 usually happens on login and causes issues with visuals; anything above 20 means a plugin like OldCombatMechanics is being used
        }

        session.setLastHitTime(System.currentTimeMillis());
    }

    public static void tickCooldown(GeyserSession session) {
        if (session.getGameMode().equals(GameMode.SPECTATOR)) return; // No attack indicator in spectator

        CooldownType sessionPreference = session.getPreferencesCache().getCooldownPreference();
        if (sessionPreference == CooldownType.DISABLED) return;

        double attackSpeed = session.getAttackSpeed();
        if (attackSpeed == 0.0 || attackSpeed > 20) {
            clearCooldown(session); // Let's clear in the off chance there is something already displayed
            return; // 0.0 usually happens on login and causes issues with visuals; anything above 20 means a plugin like OldCombatMechanics is being used
        }

        long time = System.currentTimeMillis() - session.getLastHitTime();
        double tickrateMultiplier = Math.max(session.getMillisecondsPerTick() / 50, 1.0);
        double cooldown = MathUtils.restrain(((double) time) * attackSpeed / (tickrateMultiplier * 1000.0), 1.0);

        if (cooldown < 1.0) {
            sendCooldown(session, sessionPreference, cooldown);
        } else if (session.isNeedAttackCooldownClear()) {
            clearCooldown(session);
        }
    }

    public static void sendCooldown(GeyserSession session, CooldownType sessionPreference, double cooldown) {
        if (session.integratedPackActive()) {
            boolean crosshair = sessionPreference == CooldownType.CROSSHAIR;
            long segments = Math.round(cooldown * 16);
            String value;
            if (segments >= 0 && segments < HOTBAR_VALUES.length) {
                value = (crosshair ? CROSSHAIR_VALUES : HOTBAR_VALUES)[(int) segments];
            } else {
                // Out-of-range cooldown (public API) - preserve the original behaviour exactly.
                value = (crosshair ? "crs" : "htb") + ":" + segments;
            }
            session.sendJsonUIData("cooldown", value);
        } else {
            SetTitlePacket titlePacket;

            // They must be incorrect here for cooldown sake
            if (!session.getWorldCache().isTitleTimesIncorrect()) {
                // Set the times to stay a bit with no fade in nor out
                titlePacket = new SetTitlePacket();
                titlePacket.setType(SetTitlePacket.Type.TIMES);
                titlePacket.setStayTime(1000);
                titlePacket.setText("");
                titlePacket.setXuid("");
                titlePacket.setPlatformOnlineId("");
                session.sendUpstreamPacket(titlePacket);

                session.getWorldCache().markTitleTimesAsIncorrect();
            }

            // Actionbars don't need an empty title
            if (sessionPreference == CooldownType.CROSSHAIR) {
                // Needs to be sent or no subtitle packet is recognized by the client
                titlePacket = new SetTitlePacket();
                titlePacket.setType(SetTitlePacket.Type.TITLE);
                titlePacket.setText(" ");
                titlePacket.setXuid("");
                titlePacket.setPlatformOnlineId("");
                session.sendUpstreamPacket(titlePacket);
            }

            titlePacket = new SetTitlePacket();
            if (sessionPreference == CooldownType.HOTBAR) {
                titlePacket.setType(SetTitlePacket.Type.ACTIONBAR);
            } else {
                titlePacket.setType(SetTitlePacket.Type.SUBTITLE);
            }
            titlePacket.setText(CooldownUtils.getTitle(cooldown));
            titlePacket.setXuid("");
            titlePacket.setPlatformOnlineId("");
            session.sendUpstreamPacket(titlePacket);
        }

        session.setNeedAttackCooldownClear(true);
    }

    public static void clearCooldown(GeyserSession session) {
        if (session.integratedPackActive()) {
            session.sendJsonUIData("cooldown", "non");
        } else {
            SetTitlePacket removeTitlePacket = new SetTitlePacket();
            removeTitlePacket.setType(SetTitlePacket.Type.CLEAR);
            removeTitlePacket.setText(" ");
            removeTitlePacket.setXuid("");
            removeTitlePacket.setPlatformOnlineId("");
            session.sendUpstreamPacket(removeTitlePacket);
        }

        session.setNeedAttackCooldownClear(false);
    }

    public static String getTitle(double cooldown) {
        int darkGrey = (int) Math.floor(10d * cooldown);
        if (darkGrey >= 0 && darkGrey < TITLES.length) {
            return TITLES[darkGrey];
        }
        // Out-of-range cooldown (public API) - preserve the original behaviour exactly.
        int grey = 10 - darkGrey;
        StringBuilder builder = new StringBuilder(ChatColor.DARK_GRAY);
        while (darkGrey > 0) {
            builder.append("˙");
            darkGrey--;
        }
        builder.append(ChatColor.GRAY);
        while (grey > 0) {
            builder.append("˙");
            grey--;
        }
        return builder.toString();
    }

    @Getter
    public enum CooldownType {
        CROSSHAIR,
        HOTBAR,
        DISABLED;

        public static final CooldownType[] VALUES = values();
    }
}
