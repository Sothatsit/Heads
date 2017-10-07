package net.sothatsit.heads.menu;

import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.ui.element.Element;
import org.bukkit.ChatColor;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.menu.ui.*;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.menu.ui.element.PagedBox;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.menu.ui.Item;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class HeadsMenu extends Element {

    public static final Template DEFAULT_TEMPLATE;

    static {
        Item closeItem = Item.create(Material.REDSTONE_BLOCK).name("&cClose Menu");
        Item backItem = Item.create(Material.REDSTONE_BLOCK).name("&cBack to Categories");
        Item searchItem = Item.create(Material.COMPASS).name("&7Search Heads");
        Item headItem = Item.create(Material.SKULL_ITEM).data(3).name("&7%category%").lore("&6%heads% &eheads");

        DEFAULT_TEMPLATE = new Template(
                PagedBox.DEFAULT_TEMPLATE, PagedBox.DEFAULT_TEMPLATE,
                closeItem, backItem, searchItem, headItem,
                "Categories", "%category%");
    }

    private Template template;

    private final CacheFile cache;
    private final InventoryMenu inventoryMenu;
    private final Function<CacheHead, MenuResponse> onHeadSelect;

    private final PagedBox categoriesPagedBox;
    private final PagedBox headsPagedBox;

    private String selectedCategory = null;

    public HeadsMenu(CacheFile cache,
                     InventoryMenu inventoryMenu, Bounds bounds,
                     Function<CacheHead, MenuResponse> onHeadSelect) {
        super(bounds);

        Checks.ensureNonNull(cache, "cache");
        Checks.ensureNonNull(inventoryMenu, "inventoryMenu");
        Checks.ensureNonNull(onHeadSelect, "onHeadSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds must have a height of at least 3");

        this.cache = cache;
        this.inventoryMenu = inventoryMenu;
        this.onHeadSelect = SafeCall.nonNullFunction("onHeadSelect", onHeadSelect);

        this.categoriesPagedBox = new PagedBox(bounds);
        this.headsPagedBox = new PagedBox(bounds);

        setTemplate(DEFAULT_TEMPLATE);
        updateCategoriesMenu();
    }

    public boolean onCategoriesScreen() {
        return selectedCategory == null;
    }

    public MenuResponse onClose() {
        return MenuResponse.CLOSE;
    }

    public MenuResponse onBack() {
        this.selectedCategory = null;
        updateCategoriesMenu();

        return MenuResponse.UPDATE;
    }

    public MenuResponse onSearch() {
        Bukkit.broadcastMessage("search");

        return MenuResponse.NONE;
    }

    public MenuResponse selectCategory(String category) {
        Checks.ensureNonNull(category, "category");

        this.selectedCategory = category;
        updateHeadsMenu();

        return MenuResponse.UPDATE;
    }

    @Override
    public MenuItem[] getItems() {
        if(onCategoriesScreen()) {
            return categoriesPagedBox.getItems();
        } else {
            return headsPagedBox.getItems();
        }
    }

    private void updateCategoriesMenu() {
        inventoryMenu.setTitle(template.getCategoriesTitle());

        List<String> categories = new ArrayList<>(cache.getCategories());
        MenuItem[] categoryItems = new MenuItem[categories.size() * 2 + 4];

        Collections.sort(categories);

        for(int index = 0; index < categories.size(); ++index) {
            String category = categories.get(index);

            categoryItems[index * 2] = template.constructCategoryButton(this, category, () -> selectCategory(category));
        }

        categoriesPagedBox.setItems(categoryItems);
    }

    private void updateHeadsMenu() {
        List<CacheHead> categoryHeads = cache.getCategoryHeads(selectedCategory);

        if(categoryHeads.size() == 0) {
            onBack();
            return;
        }

        inventoryMenu.setTitle(template.getCategoryTitle(selectedCategory));

        MenuItem[] headItems = new MenuItem[categoryHeads.size()];

        for(int index = 0; index < categoryHeads.size(); ++index) {
            CacheHead head = categoryHeads.get(index);

            headItems[index] = new Button(head.getItemStack(), () -> onHeadSelect.apply(head));
        }

        headsPagedBox.setItems(headItems);
    }

    public void setTemplate(Template template) {
        Checks.ensureNonNull(template, "template");

        this.template = template;
        this.template.init(this);
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("onHeadSelect", onHeadSelect)
                .entry("headsPagedBox", headsPagedBox).toString();
    }

    public static final class Template {

        private final PagedBox.Template categoriesTemplate;
        private final PagedBox.Template headsTemplate;
        private final Item closeItem;
        private final Item backItem;
        private final Item searchItem;
        private final Item categoryItem;
        private final String categoriesTitle;
        private final String categoryTitle;

        public Template(PagedBox.Template categoriesTemplate, PagedBox.Template headsTemplate,
                        Item closeItem, Item backItem, Item searchItem, Item categoryItem,
                        String categoriesTitle, String categoryTitle) {

            Checks.ensureNonNull(categoriesTemplate, "categoriesTemplate");
            Checks.ensureNonNull(headsTemplate, "headsTemplate");
            Checks.ensureNonNull(closeItem, "closeItem");
            Checks.ensureNonNull(backItem, "backItem");
            Checks.ensureNonNull(searchItem, "searchItem");
            Checks.ensureNonNull(categoryItem, "categoryItem");
            Checks.ensureNonNull(categoriesTitle, "categoriesTitle");
            Checks.ensureNonNull(categoryTitle, "categoryTitle");

            this.categoriesTemplate = categoriesTemplate;
            this.headsTemplate = headsTemplate;
            this.closeItem = closeItem;
            this.backItem = backItem;
            this.searchItem = searchItem;
            this.categoryItem = categoryItem;
            this.categoriesTitle = colour(categoriesTitle);
            this.categoryTitle = colour(categoryTitle);
        }

        private void init(HeadsMenu headsMenu) {
            Button closeButton = constructCloseButton(headsMenu);
            Button backButton = constructBackButton(headsMenu);
            Button searchButton = constructSearchButton(headsMenu);

            headsMenu.categoriesPagedBox.setTemplate(categoriesTemplate);
            headsMenu.categoriesPagedBox.setLeftControl(closeButton);
            headsMenu.categoriesPagedBox.setRightControl(searchButton);

            headsMenu.headsPagedBox.setTemplate(headsTemplate);
            headsMenu.headsPagedBox.setLeftControl(backButton);
            headsMenu.headsPagedBox.setRightControl(searchButton);
        }

        private static String colour(String uncoloured) {
            return ChatColor.translateAlternateColorCodes('&', uncoloured);
        }

        public String getCategoriesTitle() {
            return categoriesTitle;
        }

        public String getCategoryTitle(String category) {
            return categoryTitle.replace("%category%", category);
        }

        public Button constructCloseButton(HeadsMenu headsMenu) {
            return new Button(closeItem.build(), headsMenu::onClose);
        }

        public Button constructBackButton(HeadsMenu headsMenu) {
            return new Button(backItem.build(), headsMenu::onBack);
        }

        public Button constructSearchButton(HeadsMenu headsMenu) {
            return new Button(searchItem.build(), headsMenu::onSearch);
        }

        public Button constructCategoryButton(HeadsMenu headsMenu, String category, Callable<MenuResponse> onClick) {
            List<CacheHead> categoryHeads = headsMenu.cache.getCategoryHeads(category);
            CacheHead iconHead = categoryHeads.get(0);

            Placeholder categoryPlaceholder = new Placeholder("%category%", category);
            Placeholder headCountPlaceholder = new Placeholder("%heads%", categoryHeads.size());

            ItemStack icon = iconHead.addTexture(categoryItem.build(categoryPlaceholder, headCountPlaceholder));

            return new Button(icon, onClick);
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("headsTemplate", headsTemplate)
                    .entry("categoriesTitle", categoriesTitle)
                    .entry("categoryTitle", categoryTitle).toString();
        }

    }

    public static void openHeadsMenu(Player player) {
        InventoryMenu inventory = new InventoryMenu(player, "Heads", 6);

        HeadsMenu menu = new HeadsMenu(Heads.getCache(), inventory, inventory.bounds, head -> {
            player.sendMessage(head.getName());
            return MenuResponse.NONE;
        });

        inventory.addElement(menu);
        inventory.open();
    }

}
