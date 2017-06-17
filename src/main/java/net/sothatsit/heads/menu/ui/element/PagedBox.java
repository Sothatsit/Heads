package net.sothatsit.heads.menu.ui.element;

import net.sothatsit.heads.menu.ui.Bounds;
import net.sothatsit.heads.util.Item;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.item.button.ButtonGroup;
import net.sothatsit.heads.menu.ui.item.button.SelectableButton;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class PagedBox extends Container {

    private static final Function<Integer, ItemStack> DEFAULT_PAGE_ITEM_FUNCTION = (page) -> {
        int humanPage = page + 1;

        return Item.create(Material.PAPER).name("&7Page " + humanPage).amount(humanPage).build();
    };

    public static final Template DEFAULT_TEMPLATE = new Template(HorizontalScrollbar.DEFAULT_TEMPLATE, DEFAULT_PAGE_ITEM_FUNCTION);

    private final HorizontalScrollbar scrollbar;
    private ButtonGroup pageButtons;

    private Template template;

    private MenuItem[] items;
    private int page;

    public PagedBox(Bounds bounds) {
        this(null, bounds);
    }

    public PagedBox(Container container, Bounds bounds) {
        super(container, bounds);

        Checks.ensureTrue(bounds.height >= 2, "bounds height must be at least 2");
        Checks.ensureTrue(bounds.width >= 3, "bounds width must be at least 3");

        this.scrollbar = new HorizontalScrollbar(this, new Bounds(0, bounds.height - 1, bounds.width, 1));
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

        updateElement();
    }

    @Override
    public MenuItem[] getItems() {
        clear();

        setItems(getPageBounds(), getPageContents());

        if(isScrollbarActive()) {
            setElement(scrollbar);
        }

        return super.getItems();
    }

    private MenuItem[] getPageContents() {
        int pageSize = getPageSize();

        int from = page * pageSize;
        int to = Math.min((page + 1) * pageSize, items.length);

        if(to <= from)
            return new MenuItem[0];

        MenuItem[] pageContents = new MenuItem[pageSize];

        for(int index = from; index < to; index++) {
            pageContents[index - from] = items[index];
        }

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

        updateElement();
    }

    private void setupPageScrollbar() {
        int pages = getPages();

        MenuItem[] pageItems = new MenuItem[pages];

        pageButtons = new ButtonGroup();

        for(int page = 0; page < pages; page++) {
            SelectableButton pageButton = template.constructPageButton(this, pageButtons, page);

            if(page == this.page) {
                pageButton.setSelected(true);
            }

            pageItems[page] = pageButton;
        }

        scrollbar.setItems(pageItems);
    }

    public static final class Template {

        private final HorizontalScrollbar.Template pagesTemplate;
        private final Function<Integer, ItemStack> pageItemGenerator;

        public Template(HorizontalScrollbar.Template pagesTemplate, Function<Integer, ItemStack> pageItemGenerator) {
            Checks.ensureNonNull(pagesTemplate, "pagesTemplate");
            Checks.ensureNonNull(pageItemGenerator, "pageItemGenerator");

            this.pagesTemplate = pagesTemplate;
            this.pageItemGenerator = SafeCall.nonNullFunction("pageItemGenerator", pageItemGenerator);
        }

        private void init(PagedBox pagedBox) {
            pagedBox.scrollbar.setTemplate(pagesTemplate);
        }

        private SelectableButton constructPageButton(PagedBox pagedBox, ButtonGroup group, int page) {
            return new SelectableButton(group, pageItemGenerator.apply(page), () -> {
                pagedBox.setPage(page);
                return MenuResponse.NONE;
            });
        }

    }

}
