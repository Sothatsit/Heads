package net.sothatsit.heads.oldmenu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.cache.CacheHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;
import net.sothatsit.heads.util.ArrayUtils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public class CostMode extends BaseMode {
    
    private Double cost = null;
    
    public CostMode(Player player) {
        super(player);
    }
    
    public Double getCost() {
        return cost;
    }
    
    public void setCost(Double cost) {
        this.cost = cost;
        
        Lang.Menu.Cost.open().send(getPlayer(), new Placeholder("%newcost%", CacheHead.getCostString(cost)));
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.COST.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {
        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newcost%", CacheHead.getCostString(cost))));
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Placeholder costPlaceholder = new Placeholder("%newcost%", CacheHead.getCostString(cost));
        Lang.Menu.Cost.setCost().send(e.getWhoClicked(), ArrayUtils.append(head.getPlaceholders(), costPlaceholder));
        
        head.setCost(cost);
        Heads.getInstance().saveCache();
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
