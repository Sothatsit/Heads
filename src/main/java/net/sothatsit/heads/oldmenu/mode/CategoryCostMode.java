package net.sothatsit.heads.oldmenu.mode;

import net.md_5.bungee.api.ChatColor;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.Menus;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.config.lang.Lang;
import net.sothatsit.heads.oldmenu.ConfirmMenu;
import net.sothatsit.heads.oldmenu.HeadMenu;
import net.sothatsit.heads.oldmenu.InventoryType;
import net.sothatsit.heads.util.Arrays;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.List;
import java.util.Map;

public class CategoryCostMode extends BaseMode {

    private String costString = null;
    private Double cost = null;

    public CategoryCostMode(Player player) {
        super(player);
    }

    public void setCost(Double cost) {
        this.costString = CachedHead.getCostString(cost);
        this.cost = cost;
        
        Lang.Menu.CategoryCost.open().send(getPlayer(), new Placeholder("%newcost%", this.costString));
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.CATEGORY_COST.fromType(type);
    }

    public CachedHead getCategoryHead(String category) {
        category = category.toLowerCase().replace(" ", "");

        for(Map.Entry<String, List<CachedHead>> entry : Heads.getCacheConfig().getHeads().entrySet()) {
            String key = entry.getKey().toLowerCase().replace(" ", "");

            if(key.equalsIgnoreCase(category)) {
                return entry.getValue().get(0);
            }
        }

        return null;
    }

    @Override
    public void onCategorySelect(String category) {
        CachedHead head = this.getCategoryHead(category);

        if(head == null) {
            this.getPlayer().sendMessage(ChatColor.RED + "Invalid category");
            return;
        }

        openInventory(InventoryType.CONFIRM, new Object[] {
                head,
                Arrays.create(new Placeholder("%newcost%", this.costString))
        });
    }

    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        Placeholder[] placeholders = Arrays.append(
                head.getPlaceholders(),
                new Placeholder("%newcost%", this.costString));

        Lang.Menu.CategoryCost.setCost().send(e.getWhoClicked(), placeholders);

        Heads.getMainConfig().setCategoryCost(head.getCategory(), this.cost);
    }

    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }

    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {}
    
}
