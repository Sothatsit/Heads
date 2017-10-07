package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.Item;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.ButtonGroup;
import net.sothatsit.heads.menu.ui.item.SelectableButton;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class PagedBox extends Element {

    public static final Template DEFAULT_TEMPLATE;

    static {
        Item unselectedPageItem = Item.create(Material.PAPER).name("&7Page %page%");
        Item selectedPageItem = Item.create(Material.EMPTY_MAP).name("&7Page %page%");

        DEFAULT_TEMPLATE = new Template(HorizontalScrollbar.DEFAULT_TEMPLATE, unselectedPageItem, selectedPageItem);
    }

    private Template template;

    private final HorizontalScrollbar scrollbar;
    private ButtonGroup pageButtons;

    private MenuItem leftControl;
    private MenuItem rightControl;
    private MenuItem[] items;
    private int page;

    public PagedBox(Bounds bounds) {
        super(bounds);

        Checks.ensureTrue(bounds.height >= 2, "bounds height must be at least 2");
        Checks.ensureTrue(bounds.width >= 5, "bounds width must be at least 3");

        Bounds scrollbarBounds = new Bounds(1, bounds.height - 1, bounds.width - 2, 1);

        this.scrollbar = new HorizontalScrollbar(scrollbarBounds);
        this.pageButtons = new ButtonGroup();

        this.items = new MenuItem[0];
        this.page = 0;

        this.template = DEFAULT_TEMPLATE;
        this.template.init(this);
    }

    public boolean isScrollbarActive() {
        return items.length > bounds.getVolume();
    }

    private Bounds getPageBounds() {
        return isScrollbarActive() ? new Bounds(bounds.position, bounds.width, bounds.height - 1) : bounds;
    }

    public int getPageSize() {
        return getPageBounds().getVolume();
    }

    public int getPages() {
        int pageSize = getPageSize();

        return (items.length + pageSize - 1) / pageSize;
    }

    private static int clamp(int num, int min, int max) {
        return (num < min ? min : (num > max ? max : num));
    }

    public void setPage(int page) {
        this.page = clamp(page, 0, getPages() - 1);

        scrollbar.scrollTo(page);
        pageButtons.select(page);
    }

    @Override
    public MenuItem[] getItems() {
        Container container = new Container(bounds);

        container.setItems(getPageBounds(), getPageContents());

        container.addElement(scrollbar);
        container.setItem(0, bounds.height - 1, leftControl);
        container.setItem(bounds.width - 1, bounds.height - 1, rightControl);

        return container.getItems();
    }

    private MenuItem[] getPageContents() {
        int pageSize = getPageSize();

        int from = page * pageSize;
        int to = Math.min((page + 1) * pageSize, items.length);

        if(to <= from)
            return new MenuItem[pageSize];

        MenuItem[] pageContents = new MenuItem[pageSize];

        System.arraycopy(items, from, pageContents, 0, to - from);

        return pageContents;
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);

        setupPageScrollbar();
    }

    public void setLeftControl(MenuItem leftControl) {
        Checks.ensureNonNull(leftControl, "leftControl");

        this.leftControl = leftControl;
    }

    public void setRightControl(MenuItem rightControl) {
        Checks.ensureNonNull(rightControl, "rightControl");

        this.rightControl = rightControl;
    }

    public void setItems(MenuItem[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.page = 0;

        setupPageScrollbar();
    }

    private void setupPageScrollbar() {
        int pages = getPages();

        MenuItem[] pageItems = new MenuItem[pages];

        pageButtons = new ButtonGroup();

        for(int page = 0; page < pages; page++) {
            SelectableButton pageButton = template.constructPageButton(this, pageButtons, page);

            pageButton.setSelected(page == this.page);

            pageItems[page] = pageButton;
        }

        scrollbar.setItems(pageItems);
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("scrollbar", scrollbar)
                .entry("pageButtons", pageButtons)
                .entry("page", page).toString();
    }

    public static final class Template {

        private final HorizontalScrollbar.Template pagesTemplate;
        private final Item unselectedItem;
        private final Item selectedItem;

        public Template(HorizontalScrollbar.Template pagesTemplate, Item unselectedItem, Item selectedItem) {
            Checks.ensureNonNull(pagesTemplate, "pagesTemplate");
            Checks.ensureNonNull(unselectedItem, "unselectedItem");
            Checks.ensureNonNull(selectedItem, "selectedItem");

            this.pagesTemplate = pagesTemplate;
            this.unselectedItem = unselectedItem;
            this.selectedItem = selectedItem;
        }

        public void init(PagedBox pagedBox) {
            pagedBox.scrollbar.setTemplate(pagesTemplate);
        }

        private ItemStack constructPageItem(Item templateItem, int page) {
            int humanPage = page + 1;
            Placeholder pagePlaceholder = new Placeholder("%page%", humanPage);

            ItemStack item = templateItem.build(pagePlaceholder);
            item.setAmount(humanPage > 60 ? (humanPage % 10 == 0 ? 10 : humanPage % 10) : humanPage);

            return item;
        }

        public ItemStack constructUnselectedPageItem(int page) {
            return constructPageItem(unselectedItem, page);
        }

        public ItemStack constructSelectedPageItem(int page) {
            return constructPageItem(selectedItem, page);
        }

        public SelectableButton constructPageButton(PagedBox pagedBox, ButtonGroup group, int page) {
            ItemStack unselectedItem = constructUnselectedPageItem(page);
            ItemStack selectedItem = constructSelectedPageItem(page);

            return new SelectableButton(group, unselectedItem, selectedItem, () -> {
                pagedBox.setPage(page);
                return MenuResponse.UPDATE;
            });
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("pagesTemplate", pagesTemplate)
                    .entry("unselectedItem", unselectedItem)
                    .entry("selectedItem", selectedItem).toString();
        }

    }

}
