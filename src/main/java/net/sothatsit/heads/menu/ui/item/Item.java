package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import net.sothatsit.heads.volatilecode.ItemNBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;

public final class Item {

    private final Material type;
    private final short data;
    private final int amount;

    private final String name;
    private final String[] lore;

    private final boolean enchanted;

    private Item(Material type) {
        this(type, (short) 0);
    }

    private Item(Material type, byte data) {
        this(type, data, 1, null, null, false);
    }

    private Item(Material type, short data) {
        this(type, data, 1, null, null, false);
    }

    private Item(Material type, byte data, int amount, String name, String[] lore, boolean enchanted) {
        this(type, (short) data, amount, name, lore, enchanted);

        Checks.ensureWithinRange(data, 0, 15, "data");
    }

    private Item(Material type, short durability, int amount, String name, String[] lore, boolean enchanted) {
        Checks.ensureNonNull(type, "type");
        Checks.ensureTrue(durability >= 0, "durability must be greater than 0");
        Checks.ensureTrue(amount > 0, "amount must be greater than 0");

        if(lore != null) {
            Checks.ensureArrayNonNull(lore, "lore");
        }

        this.type = type;
        this.data = durability;
        this.amount = amount;
        this.name = name;
        this.lore = (lore == null || lore.length == 0 ? null : lore);
        this.enchanted = enchanted;
    }

    public Item data(int data) {
        return new Item(type, (byte) data, amount, name, lore, enchanted);
    }

    public Item durability(int durability) {
        Checks.ensureWithinRange(durability, 0, Short.MAX_VALUE, "durability");

        return new Item(type, (short) durability, amount, name, lore, enchanted);
    }

    public Item amount(int amount) {
        return new Item(type, data, amount, name, lore, enchanted);
    }

    public Item name(String name) {
        return new Item(type, data, amount, name, lore, enchanted);
    }

    public Item lore(String... lore) {
        return new Item(type, data, amount, name, lore, enchanted);
    }

    public Item enchanted(boolean enchanted) {
        return new Item(type, data, amount, name, lore, enchanted);
    }

    public Button buildButton(Placeholder... placeholders) {
        return new Button(build(placeholders));
    }

    public Button buildButton(Callable<MenuResponse> callable, Placeholder... placeholders) {
        return new Button(build(placeholders), callable);
    }

    public ItemStack build(Placeholder... placeholders) {
        return build(null, placeholders);
    }

    public ItemStack build(Function<String, Boolean> loreFilter, Placeholder... placeholders) {
        Checks.ensureNonNull(placeholders, "placeholders");

        ItemStack item = new ItemStack(type, amount, data);

        ItemMeta meta = item.getItemMeta();

        if(meta == null)
            return item;

        if(name != null) {
            String displayName = ChatColor.translateAlternateColorCodes('&', name);

            displayName = Placeholder.applyAll(displayName, placeholders);

            meta.setDisplayName(displayName);
        }

        if(lore != null) {
            String[] itemLore = Placeholder.colourAll(lore);

            itemLore = Placeholder.filterAndApplyAll(itemLore, loreFilter, placeholders);

            meta.setLore(Arrays.asList(itemLore));
        }

        item.setItemMeta(meta);

        if(enchanted) {
            item = ItemNBT.addGlow(item);
        }

        return item;
    }

    public void save(ConfigurationSection section, String key) {
        section.set(key, null);
        save(section.createSection(key));
    }

    public void save(ConfigurationSection section) {
        section.set("type", getTypeName(type));

        if(data != 0) {
            section.set("data", data);
        }

        if(amount != 1) {
            section.set("amount", amount);
        }

        if(name != null) {
            section.set("name", name);
        }

        if(lore != null) {
            section.set("lore", Arrays.asList(lore));
        }

        if(enchanted) {
            section.set("enchanted", true);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Item))
            return false;

        Item other = (Item) obj;

        return other.type == type
                && other.data == data
                && other.amount == amount
                && Objects.equals(other.name, name)
                && (other.lore == null ? lore == null : Arrays.equals(other.lore, lore))
                && other.enchanted == enchanted;
    }

    @Override
    public String toString() {
        Stringify.Builder properties = Stringify.builder();
        {
            properties.entry("type", getTypeName(type));

            if(data != 0) {
                properties.entry("data", data);
            }

            if(amount != 1) {
                properties.entry("amount", amount);
            }

            if(name != null) {
                properties.entry("name", name);
            }

            if(lore != null) {
                properties.entry("lore", lore);
            }

            if(enchanted) {
                properties.entry("enchanted", true);
            }
        }
        return properties.toString();
    }

    public static Item create(Material type) {
        return new Item(type);
    }

    public static Item create(Material type, byte data) {
        return new Item(type, data);
    }

    public static Item create(Material type, short data) {
        return new Item(type, data);
    }

    public static Item create(ItemStack itemStack) {
        Item item = create(itemStack.getType())
                .durability(itemStack.getDurability())
                .amount(itemStack.getAmount());

        ItemMeta meta = itemStack.getItemMeta();

        if(meta == null)
            return item;

        if(meta.hasDisplayName()) {
            String name = meta.getDisplayName().replace(ChatColor.COLOR_CHAR, '&');

            item = item.name(name);
        }

        if(meta.hasLore()) {
            List<String> rawLore = meta.getLore();
            String[] lore = new String[rawLore.size()];

            for(int index = 0; index < lore.length; ++index) {
                lore[index] = rawLore.get(index).replace(ChatColor.COLOR_CHAR, '&');
            }

            item = item.lore(lore);
        }

        if(meta.hasEnchants()) {
            item = item.enchanted(true);
        }

        return item;
    }

    private static void updateLegacyTypes(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
        if(!section.isSet("type") || !section.isInt("type"))
            return;

        int typeId = section.getInt("type");
        Material type = getTypeById(typeId);

        if(type == null) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + ", " +
                    "unknown Material for type id " + typeId);
            return;
        }

        String typeName = type.name().toLowerCase();

        section.set("type", typeName);
        Heads.info("1.13 Prep - " + typeId + " converted to " + typeName +
                " for " + filename + " -> " + section.getCurrentPath());

        shouldSave.set(true);
    }

    public static Item load(String filename, ConfigurationSection section, AtomicBoolean shouldSave) {
        // Convert from legacy type ids to type names
        updateLegacyTypes(filename, section, shouldSave);

        if (!section.isSet("type") || !section.isString("type")) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + " in " + filename + ", " +
                    "expected a type name");
            return null;
        }

        String typeName = section.getString("type");
        Material type = Material.matchMaterial(typeName);

        if(type == null) {
            Heads.warning("Invalid type of item " + section.getCurrentPath() + ", " +
                    "unknown material for type name " + typeName);
            return null;
        }

        short data = (short) section.getInt("data", 0);

        if(data < 0) {
            Heads.warning("Invalid data of item " + section.getCurrentPath() + ", " +
                    "data must be at least 0");
            return null;
        }

        int amount = section.getInt("amount", 1);

        if(amount < 1) {
            Heads.warning("Invalid amount of item " + section.getCurrentPath() + ", " +
                    "amount must be at least 1");
            return null;
        }

        String name = section.getString("name", null);
        String[] lore = section.getStringList("lore").toArray(new String[0]);
        boolean enchanted = section.getBoolean("enchanted", false);

        return new Item(type, data, amount, name, lore, enchanted);
    }

    public static String getTypeName(Material type) {
        return type.name().toLowerCase();
    }

    public static Material getType(String typeName) {
        return Material.matchMaterial(typeName);
    }

    @SuppressWarnings("deprecation")
    public static Material getTypeById(int typeId) {
        return Material.getMaterial(typeId);
    }

}
