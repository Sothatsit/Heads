package net.sothatsit.heads.menu.mode;

import net.sothatsit.heads.config.menu.Menu;
import net.sothatsit.heads.menu.AbstractModedInventory;
import net.sothatsit.heads.menu.InventoryType;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

public abstract class InvMode {
    
    private AbstractModedInventory inventory;
    private Player player;
    
    public InvMode(Player player, InventoryType type, Object... arguments) {
        this.player = player;
        
        openInventory(type, arguments);
    }
    
    public Player getPlayer() {
        return player;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends InvMode> T asType(Class<T> clazz) {
        return (T) this;
    }
    
    public AbstractModedInventory getInventory() {
        return inventory;
    }
    
    @SuppressWarnings("unchecked")
    public <T extends AbstractModedInventory> T getInventory(Class<T> clazz) {
        return (T) inventory;
    }
    
    public void setInventory(AbstractModedInventory inventory) {
        this.inventory = inventory;
        
        this.player.openInventory(inventory.getInventory());
    }
    
    public void openInventory(InventoryType type) {
        openInventory(type, null);
    }
    
    public void openInventory(InventoryType type, Object[] arguments) {
        if (arguments == null) {
            arguments = new Object[0];
        }
        
        setInventory(type.createMenu(this, arguments));
    }
    
    public void closeInventory() {
        player.closeInventory();
    }
    
    public abstract Menu getMenu(InventoryType type);
    
    public abstract void onClick(InventoryClickEvent e, InventoryType type);
    
}
