package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

public class MenuItem {

    protected final Element parent;
    private ItemStack item;

    public MenuItem(ItemStack item) {
        this(null, item);
    }

    public MenuItem(Element parent, ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.parent = parent;
        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.item = item;

        updateItem();
    }

    public MenuResponse handleClick() {
        return MenuResponse.NONE;
    }

    public void updateItem() {
        if(parent == null)
            return;

        parent.updateInContainer();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("item", item).toString();
    }

}
