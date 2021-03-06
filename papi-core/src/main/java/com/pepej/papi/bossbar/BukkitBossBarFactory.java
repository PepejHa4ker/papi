
package com.pepej.papi.bossbar;
import com.pepej.papi.text.Text;
import org.bukkit.Server;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

import static org.bukkit.boss.BarColor.*;
import static org.bukkit.boss.BarStyle.*;

/**
 * Implementation of {@link BossBarFactory} using Bukkit.
 */
public class BukkitBossBarFactory implements BossBarFactory {
    private final Server server;

    public BukkitBossBarFactory(Server server) {
        this.server = server;
    }

    @NonNull
    @Override
    public BossBar newBossBar() {
        return new BukkitBossBar(this.server.createBossBar("null", convertColor(BossBarColor.defaultColor()), convertStyle(BossBarStyle.defaultStyle())));
    }

    private static class BukkitBossBar implements BossBar {
        private final org.bukkit.boss.BossBar bar;

        BukkitBossBar(org.bukkit.boss.BossBar bar) {
            this.bar = bar;
        }

        @NonNull
        @Override
        public String title() {
            return this.bar.getTitle();
        }

        @NonNull
        @Override
        public BossBar title(@NonNull String title) {
            this.bar.setTitle(Text.colorize(title));
            return this;
        }

        @Override
        public double progress() {
            return this.bar.getProgress();
        }

        @NonNull
        @Override
        public BossBar progress(double progress) {
            this.bar.setProgress(progress);
            return this;
        }

        @NonNull
        @Override
        public BossBarColor color() {
            return convertColor(this.bar.getColor());
        }

        @NonNull
        @Override
        public BossBar color(@NonNull BossBarColor color) {
            this.bar.setColor(convertColor(color));
            return this;
        }

        @NonNull
        @Override
        public BossBarStyle style() {
            return convertStyle(this.bar.getStyle());
        }

        @NonNull
        @Override
        public BossBar style(@NonNull BossBarStyle style) {
            this.bar.setStyle(convertStyle(style));
            return this;
        }

        @Override
        public boolean visible() {
            return this.bar.isVisible();
        }

        @NonNull
        @Override
        public BossBar visible(boolean visible) {
            this.bar.setVisible(visible);
            return this;
        }

        @NonNull
        @Override
        public List<Player> players() {
            return this.bar.getPlayers();
        }

        @Override
        public void addPlayer(@NonNull Player player) {
            this.bar.addPlayer(player);
        }

        @Override
        public void removePlayer(@NonNull Player player) {
            this.bar.removePlayer(player);
        }

        @Override
        public void removeAll() {
            this.bar.removeAll();
        }

        @Override
        public void close() {
            removeAll();
        }
    }

    private static BossBarStyle convertStyle(BarStyle style) {
        switch (style) {
            case SOLID:
                return BossBarStyle.SOLID;
            case SEGMENTED_6:
                return BossBarStyle.SEGMENTED_6;
            case SEGMENTED_10:
                return BossBarStyle.SEGMENTED_10;
            case SEGMENTED_12:
                return BossBarStyle.SEGMENTED_12;
            case SEGMENTED_20:
                return BossBarStyle.SEGMENTED_20;
            default:
                return BossBarStyle.defaultStyle();
        }
    }

    private static BarStyle convertStyle(BossBarStyle style) {
        switch (style) {
            case SOLID:
                return SOLID;
            case SEGMENTED_6:
                return SEGMENTED_6;
            case SEGMENTED_10:
                return SEGMENTED_10;
            case SEGMENTED_12:
                return SEGMENTED_12;
            case SEGMENTED_20:
                return SEGMENTED_20;
            default:
                return convertStyle(BossBarStyle.defaultStyle());
        }
    }

    private static BossBarColor convertColor(BarColor color) {
        switch (color) {
            case PINK:
                return BossBarColor.PINK;
            case BLUE:
                return BossBarColor.BLUE;
            case RED:
                return BossBarColor.RED;
            case GREEN:
                return BossBarColor.GREEN;
            case YELLOW:
                return BossBarColor.YELLOW;
            case PURPLE:
                return BossBarColor.PURPLE;
            case WHITE:
                return BossBarColor.WHITE;
            default:
                return BossBarColor.defaultColor();
        }
    }

    private static BarColor convertColor(BossBarColor color) {
        switch (color) {
            case PINK:
                return PINK;
            case BLUE:
                return BLUE;
            case RED:
                return RED;
            case GREEN:
                return GREEN;
            case YELLOW:
                return YELLOW;
            case PURPLE:
                return PURPLE;
            case WHITE:
                return WHITE;
            default:
                return convertColor(BossBarColor.defaultColor());
        }
    }
}
