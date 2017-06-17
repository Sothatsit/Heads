package net.sothatsit.heads.config.menu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import net.sothatsit.heads.Heads;

import net.sothatsit.heads.util.Item;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.HeadsMenu;
import net.sothatsit.heads.menu.ui.element.HorizontalScrollbar;
import net.sothatsit.heads.menu.ui.element.PagedBox;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

public class Menu {

    private Function<String, Boolean> FILTER_ECONOMY_LINES_OUT = line -> !line.contains("%cost%");

    private String title;
    private final Map<String, Item> items = new HashMap<>();
    private final Menu defaults;

    public Menu() {
        this(null);
    }

    public Menu(Menu defaults) {
        this.defaults = defaults;
    }

    public String getTitle(Placeholder... placeholders) {
        return title != null ? Placeholder.applyAll(title, placeholders) : "Menu";
    }

    public ItemStack getItemStack(String name, Placeholder... placeholders) {
        Item item = getItem(name);

        return item != null ? item.build(getItemLoreFilter(), placeholders) : null;
    }

    private Item getItem(String name) {
        Item item = items.get(name.toLowerCase());

        return item != null ? item : getDefaultItem(name);
    }

    private Item getDefaultItem(String name) {
        return defaults != null ? defaults.getItem(name) : null;
    }

    private Function<String, Boolean> getItemLoreFilter() {
        return Heads.getMainConfig().isEconomyEnabled() ? null : FILTER_ECONOMY_LINES_OUT;
    }

    public HeadsMenu.Template toHeadsMenuTemplate() {
        HorizontalScrollbar.Template categories = createScrollbarTemplate("categories-left", "categories-right", "categories-filler");

        HorizontalScrollbar.Template pages = createScrollbarTemplate("pages-left", "pages-right", "pages-filler");
        PagedBox.Template heads = new PagedBox.Template(pages, createPageItemFunction());

        return new HeadsMenu.Template(categories, heads);
    }

    private HorizontalScrollbar.Template createScrollbarTemplate(String leftKey, String rightKey, String fillerKey) {
        return new HorizontalScrollbar.Template(getItemStack(leftKey), getItemStack(rightKey), getItemStack(fillerKey));
    }

    private Function<Integer, ItemStack> createPageItemFunction() {
        return page -> getItem("page").amount(page + 1).build(Placeholder.page(page + 1));
    }

    public void load(ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            if (!section.isConfigurationSection(key)) {
                loadValue(section, key);
                continue;
            }
            
            Item item = loadItem(section.getConfigurationSection(key));

            if(item == null)
                continue;

            items.put(key.toLowerCase(), item);
        }
    }

    private void loadValue(ConfigurationSection section, String key) {
        if(key.equals("title")) {
            title = section.getString(key, null);
            return;
        }

        Heads.warning("Unknown use of value \"" + key + "\" in menu \"" + section.getCurrentPath() + "\"");
    }

    private Item loadItem(ConfigurationSection section) {
        if (!section.isSet("type") || !section.isInt("type")) {
            Heads.warning("Invalid \"type\" of item \"" + section.getCurrentPath() + "\", " +
                          "expected integer");
            return null;
        }

        int typeId = section.getInt("type");
        Material type = Material.getMaterial(typeId);

        if(type == null) {
            Heads.warning("Invalid \"type\" of item \"" + section.getCurrentPath() + "\", " +
                          "unknown Material for type id " + typeId);
            return null;
        }

        int data = section.getInt("data", 0);

        if(data < 0 || data > 15) {
            Heads.warning("Invalid \"data\" of item \"" + section.getCurrentPath() + "\", " +
                          "data must be between 0 and 15 inclusive");
            return null;
        }

        String name = section.getString("name", null);
        String[] lore = section.getStringList("lore").toArray(new String[0]);

        return Item.create(type).data(data).name(name).lore(lore);
    }

    public static Menu loadMenu(ConfigurationSection section) {
        return loadMenu(section, null);
    }

    public static Menu loadMenu(ConfigurationSection section, Menu defaults) {
        Menu menu = new Menu(defaults);

        menu.load(section);

        return menu;
    }

}
