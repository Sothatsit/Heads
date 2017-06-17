package net.sothatsit.heads.oldmenu;

import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.config.lang.Placeholder;
import net.sothatsit.heads.oldmenu.mode.InvMode;
import net.sothatsit.heads.util.Arrays;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ConfirmMenu extends AbstractModedInventory {
    
    private CachedHead subject;
    private Placeholder[] placeholders;
    
    public ConfirmMenu(InvMode mode, CachedHead subject) {
        this(mode, subject, new Placeholder[0]);
    }
    
    public ConfirmMenu(InvMode mode, CachedHead subject, Placeholder[] placeholders) {
        super(InventoryType.CONFIRM, 45, Arrays.append(placeholders, subject.getPlaceholders()), mode);
        
        this.subject = subject;
        this.placeholders = Arrays.append(placeholders, subject.getPlaceholders());
        
        recreate();
    }
    
    @Override
    public void recreate() {
        Inventory inv = getInventory();
        Menu menu = getMenu();
        
        ItemStack[] contents = new ItemStack[inv.getSize()];
        
        contents[13] = subject.applyTo(menu.getItemStack("head", placeholders));
        contents[29] = menu.getItemStack("accept", placeholders);
        contents[33] = menu.getItemStack("deny", placeholders);
        
        inv.setContents(contents);
    }
    
    public CachedHead getSubject() {
        return subject;
    }
    
    public boolean isConfirm(int slot) {
        return slot == 29;
    }
    
    public boolean isDeny(int slot) {
        return slot == 33;
    }
}
