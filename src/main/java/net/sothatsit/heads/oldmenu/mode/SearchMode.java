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

import java.util.List;

public class SearchMode extends BaseMode {

    public SearchMode(Player player, List<CachedHead> heads) {
        super(player, InventoryType.HEADS, "Search", heads);
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.SEARCH.heads();
    }

    public String getHeadId(CachedHead head) {
        if(!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            return "head-no-perms";
        } else {
            return (head.hasCost() && Heads.getMainConfig().isEconomyEnabled() ? "head-cost" : "head");
        }
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        if (!getPlayer().hasPermission("heads.category." + head.getCategory().toLowerCase().replace(' ', '_'))) {
            Lang.Menu.Search.categoryPermission().send(getPlayer(), new Placeholder("%category%", head.getCategory()));
            return;
        }

        if (Heads.getMainConfig().isEconomyEnabled() && !getPlayer().hasPermission("heads.bypasscost")) {
            double cost = head.getCost();
            
            if (cost > 0) {
                if (!EconomyHook.hasBalance(getPlayer(), cost)) {
                    Lang.Menu.Search.notEnoughMoney().send(getPlayer(), head.getPlaceholders());
                    return;
                }
                
                if (!EconomyHook.takeBalance(getPlayer(), cost)) {
                    Lang.Menu.Search.transactionError().send(getPlayer(), head.getPlaceholders());
                    return;
                }
            }
        }
        
        Lang.Menu.Search.added().send(getPlayer(), head.getPlaceholders());
        
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
        return true;
    }
    
}
