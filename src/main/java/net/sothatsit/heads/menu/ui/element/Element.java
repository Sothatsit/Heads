package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.util.Checks;

public abstract class Element {

    protected final Container container;
    public final Bounds bounds;

    public Element(Bounds bounds) {
        this(null, bounds);
    }

    public Element(Container container, Bounds bounds) {
        Checks.ensureNonNull(bounds, "bounds");

        if(container != null) {
            Checks.ensureTrue(container.bounds.inBounds(bounds), "bounds is not within the bounds of the container");
        }

        this.container = container;
        this.bounds = bounds;
    }

    protected abstract MenuItem[] getItems();

    public void updateElement() {
        if(container == null)
            return;

        container.setElement(this);
        container.updateElement();
    }

    public void updateInventory() {
        if(container == null)
            return;

        container.updateInventory();
    }

}
