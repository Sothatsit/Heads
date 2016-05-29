package net.sothatsit.heads.menu.mode;

import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.menu.CategorySelectMenu;
import net.sothatsit.heads.menu.ConfirmMenu;
import net.sothatsit.heads.menu.HeadMenu;
import net.sothatsit.heads.menu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class BaseMode extends InvMode {
    
    public BaseMode(Player player) {
        super(player, InventoryType.CATEGORY);
    }

    public BaseMode(Player player, InventoryType type, Object... args) {
        super(player, type, args);
    }
    
    @Override
    public void onClick(InventoryClickEvent e, InventoryType type) {
        e.setCancelled(true);
        
        if (e.getClickedInventory() != null && e.getClickedInventory().equals(getInventory().getInventory())) {
            switch (type) {
                case CATEGORY:
                    onCategoryClick(e);
                    break;
                case HEADS:
                    onHeadsClick(e);
                    break;
                case CONFIRM:
                    onConfirmClick(e);
                    break;
                default:
                    break;
            }
        }
    }
    
    public void onCategoryClick(InventoryClickEvent e) {
        CategorySelectMenu menu = getInventory(CategorySelectMenu.class);
        
        String category = menu.getCategory(e.getRawSlot());
        
        if (category == null || !canOpenCategory(category)) {
            return;
        }
        
        openInventory(InventoryType.HEADS, new Object[] { category, menu.getHeads(category) });
    }
    
    public abstract boolean canOpenCategory(String category);
    
    public void onHeadsClick(InventoryClickEvent e) {
        HeadMenu menu = getInventory(HeadMenu.class);
        
        int slot = e.getRawSlot();
        
        if (!menu.handleToolbar(slot)) {
            CachedHead head = menu.getHead(slot);
            
            if (head != null) {
                onHeadSelect(e, menu, head);
            }
        }
    }
    
    public abstract void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head);
    
    public void onConfirmClick(InventoryClickEvent e) {
        ConfirmMenu menu = getInventory(ConfirmMenu.class);
        
        if (menu.isConfirm(e.getRawSlot())) {
            onConfirm(e, menu, menu.getSubject());
            closeInventory();
        }
        
        if (menu.isDeny(e.getRawSlot())) {
            closeInventory();
        }
    }
    
    public abstract void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head);
    
}
