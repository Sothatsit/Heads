package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;

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

    }

    public void updateInContainer() {
        updateElement();

        if(container == null)
            return;

        container.addElement(this);
        container.updateInContainer();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("bounds", bounds).toString();
    }

}
