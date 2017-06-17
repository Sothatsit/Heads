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

public class CategoryCostRemoveMode extends BaseMode {

    private String costString;

    public CategoryCostRemoveMode(Player player) {
        super(player);

        this.costString = CachedHead.getCostString(Heads.getMainConfig().getDefaultHeadCost());

        Lang.Menu.CategoryCost.openRemove().send(getPlayer(), new Placeholder("%newcost%", this.costString));
    }

    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.CATEGORY_COST_REMOVE.fromType(type);
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

        Lang.Menu.CategoryCost.removeCost().send(e.getWhoClicked(), placeholders);

        Heads.getMainConfig().removeCategoryCost(head.getCategory());
    }

    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }

    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {}
    
}
