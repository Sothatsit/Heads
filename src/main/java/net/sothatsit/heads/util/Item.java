package net.sothatsit.heads.util;

import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.volatilecode.ItemNBT;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.function.Function;

public class Item {

    private final Material type;
    private final short data;
    private final int amount;

    private final String name;
    private final String[] lore;

    private final boolean enchanted;

    private Item(Material type) {
        this(type, (short) 0, 1, null, null, false);
    }

    private Item(Material type, byte data, int amount, String name, String[] lore, boolean enchanted) {
        this(type, (short) data, amount, name, lore, enchanted);

        Checks.ensureWithinRange(data, 0, 15, "data");
    }

    private Item(Material type, short durability, int amount, String name, String[] lore, boolean enchanted) {
        Checks.ensureNonNull(type, "type");
        Checks.ensureTrue(durability >= 0, "durability must be greater than 0");
        Checks.ensureTrue(amount > 0, "amount must be greater than 0");

        this.type = type;
        this.data = durability;
        this.amount = amount;
        this.name = name;
        this.lore = (lore != null && lore.length > 0 ? lore : null);
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
        return new Item(type, data, amount, ChatColor.translateAlternateColorCodes('&', name), lore, enchanted);
    }

    public Item lore(String... lore) {
        return new Item(type, data, amount, name, Placeholder.colourAll(lore), enchanted);
    }

    public Item enchanted(boolean enchanted) {
        return new Item(type, data, amount, name, lore, enchanted);
    }

    public ItemStack build(Placeholder... placeholders) {
        return build(null, placeholders);
    }

    public ItemStack build(Function<String, Boolean> loreFilter, Placeholder... placeholders) {
        Checks.ensureNonNull(placeholders, "placeholders");

        ItemStack item = new ItemStack(type, amount, data);

        ItemMeta meta = item.getItemMeta();

        if(name != null) {
            meta.setDisplayName(Placeholder.applyAll(name, placeholders));
        }

        if(lore != null) {
            meta.setLore(Arrays.asList(Placeholder.filterAndApplyAll(lore, loreFilter, placeholders)));
        }

        item.setItemMeta(meta);

        if(enchanted) {
            item = ItemNBT.addGlow(item);
        }

        return item;
    }

    public static Item create(Material type) {
        return new Item(type);
    }

    public static Item create(int typeId) {
        Material type = Material.getMaterial(typeId);

        Checks.ensureTrue(type != null, "typeId does not match to a Material");

        return create(type);
    }

}
