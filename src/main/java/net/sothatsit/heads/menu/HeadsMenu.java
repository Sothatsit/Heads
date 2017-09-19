package net.sothatsit.heads.menu;

import net.md_5.bungee.api.ChatColor;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.menu.ui.*;
import net.sothatsit.heads.menu.ui.element.Container;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.menu.ui.item.Button;
import net.sothatsit.heads.menu.ui.element.PagedBox;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Item;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Function;

public class HeadsMenu extends Container {

    public static final Template DEFAULT_TEMPLATE = new Template(PagedBox.DEFAULT_TEMPLATE);

    private Template template;

    private final Function<CacheHead, MenuResponse> onHeadSelect;
    private final PagedBox headsPagedBox;

    public HeadsMenu(Bounds bounds, Function<CacheHead, MenuResponse> onHeadSelect) {
        this(null, bounds, onHeadSelect);
    }

    public HeadsMenu(Container container, Bounds bounds, Function<CacheHead, MenuResponse> onHeadSelect) {
        super(container, bounds);

        Checks.ensureNonNull(onHeadSelect, "onHeadSelect");
        Checks.ensureTrue(bounds.height >= 3, "bounds height must be at least 3");

        this.onHeadSelect = SafeCall.nonNullFunction("onHeadSelect", onHeadSelect);

        ItemStack backItem = Item.create(Material.REDSTONE_BLOCK).name(ChatColor.RED + "Back to Categories").build();
        Button backButton = new Button(backItem, this::onBack);

        ItemStack searchItem = Item.create(Material.COMPASS).name(ChatColor.GRAY + "Search Heads").build();
        Button searchButton = new Button(searchItem, this::onSearch);

        this.headsPagedBox = new PagedBox(this, bounds, backButton, searchButton);

        this.template = DEFAULT_TEMPLATE;
        this.template.init(this);

        initHeads(Heads.getCache().getHeads());
    }

    public MenuResponse onBack() {
        Bukkit.broadcastMessage("back");

        return MenuResponse.NONE;
    }

    public MenuResponse onSearch() {
        Bukkit.broadcastMessage("search");

        return MenuResponse.NONE;
    }

    @Override
    public MenuItem[] getItems() {
        clear();

        addElement(headsPagedBox);

        return super.getItems();
    }

    private void initHeads(List<CacheHead> heads) {
        MenuItem[] headItems = new MenuItem[heads.size()];

        for(int index = 0; index < heads.size(); index++) {
            CacheHead head = heads.get(index);

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

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("template", template)
                .entry("onHeadSelect", onHeadSelect)
                .entry("headsPagedBox", headsPagedBox).toString();
    }

    public static final class Template {

        private final PagedBox.Template headsTemplate;

        public Template(PagedBox.Template headsTemplate) {
            Checks.ensureNonNull(headsTemplate, "headsTemplate");

            this.headsTemplate = headsTemplate;
        }

        private void init(HeadsMenu headsMenu) {
            headsMenu.headsPagedBox.setTemplate(headsTemplate);
        }

        @Override
        public String toString() {
            return Stringify.builder()
                    .entry("headsTemplate", headsTemplate).toString();
        }

    }

    public static void openHeadsMenu(Player player) {
        InventoryMenu inventory = new InventoryMenu(player, "Heads", 6);

        HeadsMenu menu = new HeadsMenu(inventory, inventory.bounds, head -> {
            player.sendMessage(head.getName());
            return MenuResponse.NONE;
        });

        inventory.addElement(menu);
        inventory.open();
    }

}
