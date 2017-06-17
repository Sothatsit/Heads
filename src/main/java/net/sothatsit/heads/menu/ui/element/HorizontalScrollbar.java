package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.util.Item;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.button.Button;
import net.sothatsit.heads.util.Checks;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class HorizontalScrollbar extends Element {

    private static final ItemStack DEFAULT_LEFT_ITEM = Item.create(Material.ARROW).name("&7Left").build();
    private static final ItemStack DEFAULT_RIGHT_ITEM = Item.create(Material.ARROW).name("&7Right").build();
    private static final ItemStack DEFAULT_FILLER_ITEM = Item.create(Material.STAINED_GLASS_PANE)
                                                                        .name(" ")
                                                                        .data(15).build();

    public static final Template DEFAULT_TEMPLATE = new Template(DEFAULT_LEFT_ITEM, DEFAULT_RIGHT_ITEM, DEFAULT_FILLER_ITEM);

    private Template template;

    private MenuItem[] items;
    private int index;

    public HorizontalScrollbar(Bounds bounds) {
        this(null, bounds);
    }

    public HorizontalScrollbar(Container container, Bounds bounds) {
        super(container, bounds);

        Checks.ensureTrue(bounds.width >= 3, "The width of bounds must be at least 3");
        Checks.ensureTrue(bounds.height != 1, "The height of bounds must be 1");

        this.items = new MenuItem[0];
        this.index = 0;

        this.template = DEFAULT_TEMPLATE;
        this.template.init(this);
    }

    public boolean isScrollActive() {
        return items.length > bounds.width;
    }

    public int getVisibleItems() {
        return isScrollActive() ? bounds.width - 2 : bounds.width;
    }

    public int getMaxScroll() {
        return isScrollActive() ? items.length - bounds.width + 2 : 0;
    }

    public boolean isLeftScrollActive() {
        return isScrollActive() && index > 0;
    }

    public boolean isRightScrollActive() {
        return isScrollActive() && index < getMaxScroll();
    }

    public MenuResponse scrollLeft() {
        if(!isLeftScrollActive())
            return MenuResponse.NONE;

        index--;

        updateElement();

        return MenuResponse.NONE;
    }

    public MenuResponse scrollRight() {
        if(!isRightScrollActive())
            return MenuResponse.NONE;

        index++;

        updateElement();

        return MenuResponse.NONE;
    }

    private static final int clamp(int num, int min, int max) {
        return (num < min ? min : (num > max ? max : num));
    }

    public void scrollTo(int index) {
        index = clamp(index, 0, items.length - 1);

        int visibleItems = getVisibleItems();

        if(index < this.index) {
            this.index = index;
        } else if(index >= this.index + visibleItems) {
            this.index = index - visibleItems + 1;
        } else {
            return;
        }

        updateElement();
    }

    public MenuItem[] getItems() {
        MenuItem[] scrollbar = new MenuItem[bounds.getVolume()];

        if(isScrollActive()) {
            if(isLeftScrollActive()) {
                scrollbar[0] = template.constructScrollLeftButton(this);
            }

            if(isRightScrollActive()) {
                scrollbar[bounds.width - 1] = template.constructScrollRightButton(this);
            }

            System.arraycopy(items, index, scrollbar, 1, bounds.width - 2);
        } else {
            System.arraycopy(items, 0, scrollbar, 0, items.length);
            Arrays.fill(items, items.length, bounds.width, template.constructFillerItem());
        }

        return scrollbar;
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);

        updateElement();
    }

    public void setItems(List<MenuItem> items) {
        Checks.ensureNonNull(items, "items");

        setItems(items.toArray(new MenuItem[items.size()]));
    }

    public void setItems(MenuItem[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.index = 0;

        updateElement();
    }

    public static final class Template {

        private final ItemStack leftItem;
        private final ItemStack rightItem;
        private final ItemStack fillerItem;

        public Template(ItemStack leftItem, ItemStack rightItem, ItemStack fillerItem) {
            Checks.ensureNonNull(leftItem, "leftItem");
            Checks.ensureNonNull(rightItem, "rightItem");
            Checks.ensureNonNull(fillerItem, "fillerItem");

            this.leftItem = leftItem;
            this.rightItem = rightItem;
            this.fillerItem = fillerItem;
        }

        private void init(HorizontalScrollbar scrollbar) {

        }

        private Button constructScrollLeftButton(HorizontalScrollbar scrollbar) {
            return new Button(scrollbar, leftItem, scrollbar::scrollLeft);
        }

        private Button constructScrollRightButton(HorizontalScrollbar scrollbar) {
            return new Button(scrollbar, rightItem, scrollbar::scrollRight);
        }

        private MenuItem constructFillerItem() {
            return new MenuItem(fillerItem);
        }

    }

}
