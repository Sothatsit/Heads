package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

public class MenuItem {

    private ItemStack item;

    public MenuItem(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.item = item;
    }

    public ItemStack getItem() {
        return item;
    }

    public void setItem(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.item = item;
    }

    public MenuResponse handleClick() {
        return MenuResponse.NONE;
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .entry("item", item).toString();
    }

}
