package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.menu.ui.Position;
import net.sothatsit.heads.util.Item;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.ButtonGroup;
import net.sothatsit.heads.menu.ui.item.SelectableButton;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class PagedBox extends Container {

    private static final Function<Integer, ItemStack> DEFAULT_UNSELECTED_ITEMS = (page) -> {
        int humanPage = page + 1;

        return Item.create(Material.PAPER).name("&7Page " + humanPage).amount(humanPage).build();
    };

    private static final Function<Integer, ItemStack> DEFAULT_SELECTED_ITEMS = (page) -> {
        int humanPage = page + 1;

        return Item.create(Material.EMPTY_MAP).name("&7Page " + humanPage).amount(humanPage).build();
    };

    public static final Template DEFAULT_TEMPLATE = new Template(HorizontalScrollbar.DEFAULT_TEMPLATE,
                                                                 DEFAULT_UNSELECTED_ITEMS, DEFAULT_SELECTED_ITEMS);

    private Template template;

    private final HorizontalScrollbar scrollbar;
    private ButtonGroup pageButtons;

    private final MenuItem leftControl;
    private final MenuItem rightControl;

    private MenuItem[] items;
    private int page;

    public PagedBox(Bounds bounds) {
        this(null, bounds, null, null);
    }

    public PagedBox(Container container, Bounds bounds) {
        this(container, bounds, null, null);
    }

    public PagedBox(Bounds bounds, MenuItem leftControl, MenuItem rightControl) {
        this(null, bounds, leftControl, rightControl);
    }

    public PagedBox(Container container, Bounds bounds, MenuItem leftControl, MenuItem rightControl) {
        super(container, bounds);

        int hasLeftControl = (leftControl != null ? 1 : 0);
        int hasRightControl = (rightControl != null ? 1 : 0);
        int requiredWidth = 3 + hasLeftControl + hasRightControl;

        Checks.ensureTrue(bounds.height >= 2, "bounds height must be at least 2");
        Checks.ensureTrue(bounds.width >= requiredWidth, "bounds width must be at least 3");

        int scrollbarY = bounds.height - 1;
        int scrollbarWidth = bounds.width - hasLeftControl - hasRightControl;
        Bounds scrollbarBounds = new Bounds(hasLeftControl, scrollbarY, scrollbarWidth, 1);

        this.scrollbar = new HorizontalScrollbar(this, scrollbarBounds);
        this.pageButtons = new ButtonGroup();

        this.leftControl = leftControl;
        this.rightControl = rightControl;

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

        updateInContainer();
    }

    @Override
    public MenuItem[] getItems() {
        clear();

        setItems(getPageBounds(), getPageContents());

        if(isScrollbarActive() || leftControl != null || rightControl != null) {
            addElement(scrollbar);
        }

        if(leftControl != null) {
            setItem(new Position(0, bounds.height - 1), leftControl);
        }

        if(rightControl != null) {
            setItem(new Position(bounds.width - 1, bounds.height - 1), rightControl);
        }

        return super.getItems();
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
        updateElement();
    }

    public void setItems(List<MenuItem> items) {
        Checks.ensureNonNull(items, "items");

        setItems(items.toArray(new MenuItem[items.size()]));
    }

    public void setItems(MenuItem[] items) {
        Checks.ensureNonNull(items, "items");

        this.items = items;
        this.page = 0;

        setupPageScrollbar();

        updateInContainer();
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
        private final Function<Integer, ItemStack> unselectedItemFn;
        private final Function<Integer, ItemStack> selectedItemFn;

        public Template(HorizontalScrollbar.Template pagesTemplate,
                        Function<Integer, ItemStack> unselectedItemFn,
                        Function<Integer, ItemStack> selectedItemFn) {

            Checks.ensureNonNull(pagesTemplate, "pagesTemplate");
            Checks.ensureNonNull(unselectedItemFn, "unselectedItemFn");
            Checks.ensureNonNull(selectedItemFn, "selectedItemFn");

            this.pagesTemplate = pagesTemplate;
            this.unselectedItemFn = SafeCall.nonNullFunction("unselectedItemFn", unselectedItemFn);
            this.selectedItemFn = SafeCall.nonNullFunction("selectedItemFn", selectedItemFn);
        }

        private void init(PagedBox pagedBox) {
            pagedBox.scrollbar.setTemplate(pagesTemplate);
        }

        private SelectableButton constructPageButton(PagedBox pagedBox, ButtonGroup group, int page) {
            return new SelectableButton(group, unselectedItemFn.apply(page), selectedItemFn.apply(page), () -> {
                pagedBox.setPage(page);
                return MenuResponse.NONE;
            });
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("pagesTemplate", pagesTemplate)
                    .entry("unselectedItemFn", unselectedItemFn)
                    .entry("selectedItems", selectedItemFn).toString();
        }

    }

}
