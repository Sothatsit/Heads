package net.sothatsit.heads.menu.ui;

import net.sothatsit.heads.util.Checks;

public final class Position {

    public static final Position ZERO = new Position(0, 0);

    public final int x;
    public final int y;

    public Position(int x, int y) {
        Checks.ensureTrue(x >= 0, "x must be at least 0");
        Checks.ensureTrue(y >= 0, "y must be at least 0");

        this.x = x;
        this.y = y;
    }

    public Position add(Position other) {
        return new Position(x + other.x, y + other.y);
    }

    public Position add(int x, int y) {
        return new Position(this.x + x, this.y + y);
    }

    public boolean within(Bounds bounds) {
        return bounds.inBounds(this);
    }

    public int toSerialIndex(int width) {
        return x + y * width;
    }

}
