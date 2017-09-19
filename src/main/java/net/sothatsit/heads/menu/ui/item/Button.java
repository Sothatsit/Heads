package net.sothatsit.heads.menu.ui.item;

import net.sothatsit.heads.menu.ui.element.Element;
import net.sothatsit.heads.menu.ui.MenuResponse;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.SafeCall;
import net.sothatsit.heads.util.SafeCall.SafeCallable;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.Callable;

public class Button extends MenuItem {

    private final SafeCallable<MenuResponse> onClick;

    public Button(ItemStack item, Callable<MenuResponse> onClick) {
        this(null, item, onClick);
    }

    public Button(Element parent, ItemStack item, Callable<MenuResponse> onClick) {
        super(parent, item);

        Checks.ensureNonNull(onClick, "onClick");

        this.onClick = SafeCall.nonNullCallable("onClick", onClick);
    }

    @Override
    public MenuResponse handleClick() {
        return onClick.call();
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .previous(super.toString())
                .entry("onClick", onClick).toString();
    }

}
