package net.sothatsit.heads.menu.mode;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.menu.ConfirmMenu;
import net.sothatsit.heads.menu.HeadMenu;
import net.sothatsit.heads.menu.InventoryType;
import net.sothatsit.heads.util.Arrays;

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
        
        Lang.Menu.Cost.open().send(getPlayer(), new Placeholder("%newcost%", CachedHead.getCostString(cost)));
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.COST.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        openInventory(InventoryType.CONFIRM, new Object[] { head, Arrays.create(new Placeholder("%newcost%", CachedHead.getCostString(cost))) });
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        Placeholder[] placeholders = Arrays.append(head.getPlaceholders(), new Placeholder("%newcost%", CachedHead.getCostString(cost)));
        Lang.Menu.Cost.setCost().send(e.getWhoClicked(), placeholders);
        
        head.setCost(cost);
        Heads.getCacheConfig().save();
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
