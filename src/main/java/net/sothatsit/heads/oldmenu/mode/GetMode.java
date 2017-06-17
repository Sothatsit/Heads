package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.EconomyHook;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class GetMode extends BaseMode {
    
    public GetMode(Player player) {
        super(player);
        
        Lang.Menu.Get.open().send(player);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.GET.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        if (Heads.getMainConfig().isEconomyEnabled() && !getPlayer().hasPermission("heads.bypasscost")) {
            double cost = head.getCost();
            
            if (cost > 0) {
                if (!EconomyHook.hasBalance(getPlayer(), cost)) {
                    Lang.Menu.Get.notEnoughMoney().send(getPlayer(), head.getPlaceholders());
                    return;
                }
                
                if (!EconomyHook.takeBalance(getPlayer(), cost)) {
                    Lang.Menu.Get.transactionError().send(getPlayer(), head.getPlaceholders());
                    return;
                }

                Lang.Menu.Get.purchased().send(getPlayer(), head.getPlaceholders());
            }
        }
        
        Lang.Menu.Get.added().send(getPlayer(), head.getPlaceholders());
        
        if (Heads.isHatMode()) {
            e.getWhoClicked().getInventory().setHelmet(head.getItemStack());
        } else {
            e.getWhoClicked().getInventory().addItem(head.getItemStack());
        }
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        // should not be reached
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        if (getPlayer().hasPermission("heads.category." + category.toLowerCase().replace(' ', '_'))) {
            return true;
        } else {
            Lang.Menu.Get.categoryPermission().send(getPlayer(), new Placeholder("%category%", category));
            return false;
        }
    }
    
}
