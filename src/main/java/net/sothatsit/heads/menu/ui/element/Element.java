package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;

public abstract class Element {

    public final Bounds bounds;

    public Element(Bounds bounds) {
        Checks.ensureNonNull(bounds, "bounds");

        this.bounds = bounds;
    }

    protected abstract MenuItem[] getItems();

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("bounds", bounds).toString();
    }

}
