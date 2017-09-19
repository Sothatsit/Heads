package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class SelectableButton extends Button {

    private final ButtonGroup group;
    private ItemStack selectedItem;
    private boolean selected;

    public SelectableButton(ItemStack unselectedItem,
                            ItemStack selectedItem,
                            Callable<MenuResponse> onClick) {
        this(null, null, unselectedItem, selectedItem, onClick);
    }

    public SelectableButton(ButtonGroup group,
                            ItemStack unselectedItem,
                            ItemStack selectedItem,
                            Callable<MenuResponse> onClick) {
        this(null, group, unselectedItem, selectedItem, onClick);
    }

    public SelectableButton(Element parent,
                            ItemStack unselectedItem,
                            ItemStack selectedItem,
                            Callable<MenuResponse> onClick) {
        this(parent, null, unselectedItem, selectedItem, onClick);
    }

    public SelectableButton(Element parent,
                            ButtonGroup group,
                            ItemStack unselectedItem,
                            ItemStack selectedItem,
                            Callable<MenuResponse> onClick) {
        super(parent, unselectedItem, onClick);

        Checks.ensureNonNull(selectedItem, "selectedItem");

        this.group = group;
        this.selectedItem = selectedItem;
        this.selected = false;

        if(group != null) {
            group.addButton(this);
        }
    }

    public void setSelectedItem(ItemStack item) {
        Checks.ensureNonNull(item, "item");

        this.selectedItem = item;

        updateItem();
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        if(this.selected == selected)
            return;

        if(selected && group != null) {
            group.unselectAll();
        }

        this.selected = selected;

        updateItem();
    }

    @Override
    public ItemStack getItem() {
        return selected ? selectedItem : super.getItem();
    }

    @Override
    public MenuResponse handleClick() {
        setSelected(true);

        return super.handleClick();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .previous(super.toString())
                .entry("selectedItem", selectedItem)
                .entry("selected", selected).toString();
    }

}
