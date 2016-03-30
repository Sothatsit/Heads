package net.sothatsit.heads.menu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CacheConfig;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.menu.mode.InvMode;

public class CategorySelectMenu extends AbstractModedInventory {
    
    private Map<String, List<CachedHead>> heads;
    private List<String> categories;
    private double offset;
    
    public CategorySelectMenu(InvMode mode) {
        super(InventoryType.CATEGORY, mode);
        
        recreate();
    }
    
    @Override
    public void recreate() {
        CacheConfig cache = Heads.getCacheConfig();
        
        int numHeads = cache.getHeads().size();
        
        this.heads = new HashMap<>();
        this.categories = new ArrayList<>();
        
        ItemStack[] contents;
        
        if(numHeads > 27) {
        	int size = (int) Math.ceil(numHeads / 9d) * 9;
            
            setInventory(Bukkit.createInventory(this, size, getMenu().getName()));
            
            int lastRow = numHeads % 5;
            
            this.offset = (9d - lastRow) / 2d;
            
            contents = new ItemStack[size];
        	
        	for (Map.Entry<String, List<CachedHead>> entry : cache.getHeads().entrySet()) {
                List<CachedHead> list = new ArrayList<>();
                list.addAll(entry.getValue());
                heads.put(entry.getKey(), list);
                
                int index = categories.size();
                
                if (index >= size - 9) {
                    index += (int) Math.floor(offset);
                    
                    if (index % 9 >= 4) {
                        index += (int) Math.ceil(offset % 1);
                    }
                }
                
                CachedHead h = list.get(0);
                ItemStack head = getMenu().getItemStack("head", new Placeholder("%category%", entry.getKey()));
                contents[index] = h.applyTo(head);
                
                categories.add(entry.getKey());
            }
        } else {
        	int rows = (int) Math.ceil(numHeads / 9d);
        	
        	if(numHeads <= rows * 9 - 4) {
        		rows = rows * 2 - 1;
        	} else {
        		rows = rows * 2;
        	}
        	
        	int size = rows * 9;
        	
        	setInventory(Bukkit.createInventory(this, size, getMenu().getName()));
        	
        	contents = new ItemStack[size];
        	
        	for (Map.Entry<String, List<CachedHead>> entry : cache.getHeads().entrySet()) {
                List<CachedHead> list = new ArrayList<>();
                list.addAll(entry.getValue());
                heads.put(entry.getKey(), list);
                
                int index = categories.size() * 2;
                
                CachedHead h = list.get(0);
                ItemStack head = getMenu().getItemStack("head", new Placeholder("%category%", entry.getKey()));
                contents[index] = h.applyTo(head);
                
                categories.add(entry.getKey());
            }
        }
        
        getInventory().setContents(contents);
    }
    
    public String getCategory(int slot) {
        Inventory inv = getInventory();
        int size = inv.getSize();
        
        if(categories.size() > 27) {
        	if (slot < 0 || slot >= size || inv.getItem(slot) == null) {
                return null;
            }
            
            if (slot >= size - 9) {
                if (slot % 9 >= 4) {
                    slot -= (int) Math.ceil(offset);
                } else {
                    slot -= (int) Math.floor(offset);
                }
            }
            
            return categories.get(slot);
        } else {
        	if(slot % 2 == 1) {
        		return null;
        	}
        	
        	return categories.get(slot / 2);
        }
    }
    
    public List<CachedHead> getHeads(String category) {
        return heads.get(category);
    }
    
}
