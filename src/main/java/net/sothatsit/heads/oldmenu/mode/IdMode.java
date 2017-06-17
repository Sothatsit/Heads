package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Menus;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class IdMode extends BaseMode {
    
    public IdMode(Player player) {
        super(player);
        
        Lang.Menu.Get.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.ID.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        Lang.Menu.Id.clicked().send(e.getWhoClicked(), head.getPlaceholders());
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        // should not be reached
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
