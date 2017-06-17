package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class RemoveMode extends BaseMode {
    
    public RemoveMode(Player player) {
        super(player);
        
        Lang.Menu.Remove.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.REMOVE.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        openInventory(InventoryType.CONFIRM, new Object[] { head });
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        Heads.getCacheConfig().remove(head);
        
        Lang.Menu.Remove.removed().send(e.getWhoClicked(), head.getPlaceholders());
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
