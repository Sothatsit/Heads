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

public class RenameMode extends BaseMode {
    
    private String name = null;
    
    public RenameMode(Player player) {
        super(player);
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        
        Lang.Menu.Rename.open().send(getPlayer(), new Placeholder("%newname%", name));
    }
    
    @Override
    public Menu getMenu(InventoryType type) {
        return Menus.RENAME.fromType(type);
    }
    
    @Override
    public void onHeadSelect(InventoryClickEvent e, HeadMenu menu, CachedHead head) {
        openInventory(InventoryType.CONFIRM, new Object[] { head, Arrays.create(new Placeholder("%newname%", name)) });
    }
    
    @Override
    public void onConfirm(InventoryClickEvent e, ConfirmMenu menu, CachedHead head) {
        Placeholder[] placeholders = Arrays.append(head.getPlaceholders(), new Placeholder("%newname%", name));
        Lang.Menu.Rename.renamed().send(e.getWhoClicked(), placeholders);
        
        head.setName(name);
        Heads.getCacheConfig().save();
    }
    
    @Override
    public boolean canOpenCategory(String category) {
        return true;
    }
    
}
