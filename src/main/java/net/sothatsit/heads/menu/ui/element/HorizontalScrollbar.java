package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.Item;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.List;

public class HorizontalScrollbar extends Element {

    public static final Template DEFAULT_TEMPLATE;

    static {
        Item leftItem = Item.create(Material.ARROW).name("&7Left");
        Item rightItem = Item.create(Material.ARROW).name("&7Right");
        Item noLeftItem = Item.create(Material.STAINED_GLASS_PANE).data(15).name(" ");
        Item noRightItem = Item.create(Material.STAINED_GLASS_PANE).data(15).name(" ");
        Item fillerItem = Item.create(Material.STAINED_GLASS_PANE).data(15).name(" ");

        DEFAULT_TEMPLATE = new Template(leftItem, rightItem, noLeftItem, noRightItem, fillerItem);
    }

    private Template template;

    private MenuItem[] items;
    private int index;

    public HorizontalScrollbar(Bounds bounds) {
        super(bounds);

        Checks.ensureTrue(bounds.width >= 3, "The width of bounds must be at least 3");
        Checks.ensureTrue(bounds.height == 1, "The height of bounds must be 1");

        this.items = new MenuItem[0];
        this.index = 0;

        setTemplate(DEFAULT_TEMPLATE);
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

        return MenuResponse.UPDATE;
    }

    public MenuResponse scrollRight() {
        if(!isRightScrollActive())
            return MenuResponse.NONE;

        index++;

        return MenuResponse.UPDATE;
    }

    private static int clamp(int num, int min, int max) {
        return (num < min ? min : (num > max ? max : num));
    }

    public void scrollTo(int index) {
        index = clamp(index, 0, items.length - 1);

        int visibleItems = getVisibleItems();

        if(index < this.index) {
            this.index = index;
        } else if(index >= this.index + visibleItems) {
            this.index = index - visibleItems + 1;
        }
    }

    @Override
    public MenuItem[] getItems() {
        MenuItem[] scrollbar = new MenuItem[bounds.getVolume()];

        if(isScrollActive()) {
            if(isLeftScrollActive()) {
                scrollbar[0] = template.constructScrollLeftButton(this);
            } else {
                scrollbar[0] = template.constructNoScrollLeftItem();
            }

            if(isRightScrollActive()) {
                scrollbar[bounds.width - 1] = template.constructScrollRightButton(this);
            } else {
                scrollbar[bounds.width - 1] = template.constructNoScrollRightItem();
            }

            System.arraycopy(items, index, scrollbar, 1, bounds.width - 2);
        } else {
            System.arraycopy(items, 0, scrollbar, 0, items.length);
            Arrays.fill(scrollbar, items.length, bounds.width, template.constructFillerItem());
        }

        return scrollbar;
    }

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);
    }

    public void setItems(List<MenuItem> items) {
        Checks.ensureNonNull(items, "items");

        setItems(items.toArray(new MenuItem[items.size()]));
    }

    public void setItems(MenuItem[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.index = 0;
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("index", index).toString();
    }

    public static final class Template {

        private final Item leftItem;
        private final Item rightItem;
        private final Item noLeftItem;
        private final Item noRightItem;
        private final Item fillerItem;

        public Template(Item leftItem, Item rightItem,
                        Item noLeftItem, Item noRightItem,
                        Item fillerItem) {

            Checks.ensureNonNull(leftItem, "leftItem");
            Checks.ensureNonNull(rightItem, "rightItem");
            Checks.ensureNonNull(noLeftItem, "noLeftItem");
            Checks.ensureNonNull(noRightItem, "noRightItem");
            Checks.ensureNonNull(fillerItem, "fillerItem");

            this.leftItem = leftItem;
            this.rightItem = rightItem;
            this.noLeftItem = noLeftItem;
            this.noRightItem = noRightItem;
            this.fillerItem = fillerItem;
        }

        public void init(HorizontalScrollbar scrollbar) {

        }

        public Button constructScrollLeftButton(HorizontalScrollbar scrollbar) {
            return new Button(leftItem.build(), scrollbar::scrollLeft);
        }

        public Button constructScrollRightButton(HorizontalScrollbar scrollbar) {
            return new Button(rightItem.build(), scrollbar::scrollRight);
        }

        public MenuItem constructNoScrollLeftItem() {
            return new MenuItem(noLeftItem.build());
        }

        public MenuItem constructNoScrollRightItem() {
            return new MenuItem(noRightItem.build());
        }

        public MenuItem constructFillerItem() {
            return new MenuItem(fillerItem.build());
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("leftItem", leftItem)
                    .entry("rightItem", rightItem)
                    .entry("noLeftItem", noLeftItem)
                    .entry("noRightItem", noRightItem)
                    .entry("fillerItem", fillerItem).toString();
        }

    }

}
