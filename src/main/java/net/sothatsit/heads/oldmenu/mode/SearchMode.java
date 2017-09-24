package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.EconomyHook;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;

public class SearchMode extends BaseMode {

    public SearchMode(Player player, List<CacheHead> heads) {
        super(player, InventoryType.HEADS, "Search", heads);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.SEARCH.heads();
    }

    public String getHeadId(CacheHead head) {
        if(!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            return "head-no-perms";
        } else {
            return (head.hasCost() && Heads.getMainConfig().isEconomyEnabled() ? "head-cost" : "head");
        }
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        if (!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            Lang.Menu.Search.categoryPermission(head.getCategory()).send(getPlayer());
            return;
        }

        if (Heads.getMainConfig().isEconomyEnabled() && !getPlayer().hasPermission("heads.bypasscost")) {
            double cost = head.getCost();
            
            if (cost > 0) {
                if (!EconomyHook.hasBalance(getPlayer(), cost)) {
                    Lang.Menu.Search.notEnoughMoney(head.getName(), head.getCost()).send(getPlayer());
                    return;
                }
                
                if (!EconomyHook.takeBalance(getPlayer(), cost)) {
                    Lang.Menu.Search.transactionError(head.getName(), head.getCost()).send(getPlayer());
                    return;
                }
            }
        }
        
        Lang.Menu.Search.added(head.getName()).send(getPlayer());

        e.getWhoClicked().getInventory().addItem(head.getItemStack());
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        // should not be reached
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
