package net.sothatsit.heads.menu.ui.item.button;

import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.volatilecode.ItemNBT;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class SelectableButton extends Button {

    private final ButtonGroup group;
    private boolean selected;

    public SelectableButton(ItemStack item, Callable<MenuResponse> onClick) {
        this(null, null, item, onClick);
    }

    public SelectableButton(ButtonGroup group, ItemStack item, Callable<MenuResponse> onClick) {
        this(null, group, item, onClick);
    }

    public SelectableButton(Element parent, ItemStack item, Callable<MenuResponse> onClick) {
        this(parent, null, item, onClick);
    }

    public SelectableButton(Element parent, ButtonGroup group, ItemStack item, Callable<MenuResponse> onClick) {
        super(parent, item, onClick);

        this.selected = false;
        this.group = group;

        if(group != null) {
            group.addButton(this);
        }
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
        ItemStack itemStack = super.getItem();

        return selected ? ItemNBT.addGlow(itemStack) : itemStack;
    }

    @Override
    public MenuResponse handleClick() {
        setSelected(true);

        return super.handleClick();
    }

}
