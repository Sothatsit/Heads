package net.sothatsit.heads.menu;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CacheConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.menu.ui.*;
import net.sothatsit.heads.menu.ui.element.Container;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.item.button.Button;
import net.sothatsit.heads.menu.ui.item.button.ButtonGroup;
import net.sothatsit.heads.menu.ui.item.button.SelectableButton;
import net.sothatsit.heads.menu.ui.element.HorizontalScrollbar;
import net.sothatsit.heads.menu.ui.element.PagedBox;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class HeadsMenu extends Container {

    public static final Template DEFAULT_TEMPLATE = new Template(HorizontalScrollbar.DEFAULT_TEMPLATE, PagedBox.DEFAULT_TEMPLATE);

    private final Function<CachedHead, MenuResponse> onHeadSelect;

    private final HorizontalScrollbar categoriesScrollbar;
    private final PagedBox headsPagedBox;

    private Template template;

    public HeadsMenu(Bounds bounds, Function<CachedHead, MenuResponse> onHeadSelect) {
        this(null, bounds, onHeadSelect);
    }

    public HeadsMenu(Container container, Bounds bounds, Function<CachedHead, MenuResponse> onHeadSelect) {
        super(container, bounds);

        Checks.ensureNonNull(onHeadSelect, "onHeadSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds height must be at least 3");

        this.onHeadSelect = SafeCall.nonNullFunction("onHeadSelect", onHeadSelect);

        this.categoriesScrollbar = new HorizontalScrollbar(this, new Bounds(Position.ZERO, bounds.width, 1));
        this.headsPagedBox = new PagedBox(this, new Bounds(0, 1, bounds.width, bounds.height - 1));

        this.template = DEFAULT_TEMPLATE;
        this.template.init(this);

        initCategories();
    }

    @Override
    public MenuItem[] getItems() {
        clear();

        setElement(categoriesScrollbar);
        setElement(headsPagedBox);

        return super.getItems();
    }

    private void initCategories() {
        CacheConfig cacheConfig = Heads.getCacheConfig();
        Map<String, List<CachedHead>> heads = cacheConfig.getHeads();

        ButtonGroup categories = new ButtonGroup();

        MenuItem[] categoryButtons = new MenuItem[heads.size()];

        int index = 0;
        for(Map.Entry<String, List<CachedHead>> entry : heads.entrySet()) {
            final List<CachedHead> categoryHeads = entry.getValue();
            final ItemStack categoryIcon = categoryHeads.get(0).getItemStack();

            categoryButtons[index] = new SelectableButton(categories, categoryIcon, () -> {
                initHeads(categoryHeads);
                return MenuResponse.NONE;
            });

            index++;
        }

        categoriesScrollbar.setItems(categoryButtons);

        categoryButtons[0].handleClick();
    }

    private void initHeads(List<CachedHead> heads) {
        MenuItem[] headItems = new MenuItem[heads.size()];

        for(int index = 0; index < heads.size(); index++) {
            final CachedHead head = heads.get(index);

            headItems[index] = new Button(head.getItemStack(), () -> onHeadSelect.apply(head));
        }

        headsPagedBox.setItems(headItems);
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);

        updateElement();
    }

    public static final class Template {

        private final HorizontalScrollbar.Template categoriesTemplate;
        private final PagedBox.Template headsTemplate;

        public Template(HorizontalScrollbar.Template categoriesTemplate, PagedBox.Template headsTemplate) {
            Checks.ensureNonNull(categoriesTemplate, "categoriesTemplate");
            Checks.ensureNonNull(headsTemplate, "headsTemplate");

            this.categoriesTemplate = categoriesTemplate;
            this.headsTemplate = headsTemplate;
        }

        private void init(HeadsMenu headsMenu) {
            headsMenu.categoriesScrollbar.setTemplate(categoriesTemplate);
            headsMenu.headsPagedBox.setTemplate(headsTemplate);
        }

    }

}
