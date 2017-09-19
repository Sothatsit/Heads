package net.sothatsit.heads.oldmenu.mode;

import net.md_5.bungee.api.ChatColor;
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

import java.util.List;

public class CategoryCostRemoveMode extends BaseMode {

    private String costString;

    public CategoryCostRemoveMode(Player player) {
        super(player);

        this.costString = CacheHead.getCostString(Heads.getMainConfig().getDefaultHeadCost());

        Lang.Menu.CategoryCost.openRemove().send(getPlayer(), new Placeholder("%newcost%", this.costString));
    }

    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.CATEGORY_COST_REMOVE.fromType(type);
    }

    public CacheHead getCategoryHead(String category) {
        List<CacheHead> heads = Heads.getCache().getCategoryHeads(category);

        return (heads.size() > 0 ? heads.get(0) : null);
    }

    @Override
    public void onCategorySelect(String category) {
        CacheHead head = this.getCategoryHead(category);

        if(head == null) {
            this.getPlayer().sendMessage(ChatColor.RED + "Invalid category");
            return;
        }

        openInventory(InventoryType.CONFIRM,
                head,
                ArrayUtils.create(new Placeholder("%newcost%", this.costString)));
    }

    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CacheHead head) {
        Placeholder[] placeholders = ArrayUtils.append(
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
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CacheHead head) {}
    
}
