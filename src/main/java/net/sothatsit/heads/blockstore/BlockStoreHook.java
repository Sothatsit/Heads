package net.sothatsit.heads.blockstore;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventException;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.RegisteredListener;

import net.sothatsit.blockstore.BlockStoreApi;
import net.sothatsit.heads.Heads;
import net.sothatsit.heads.volatilecode.ItemNBT;
import net.sothatsit.heads.volatilecode.reflection.nms.BlockPosition;
import net.sothatsit.heads.volatilecode.reflection.nms.TileEntitySkull;
import net.sothatsit.heads.volatilecode.reflection.nms.World;

public class BlockStoreHook implements Listener {
    
    public BlockStoreHook() {
        Bukkit.getPluginManager().registerEvents(this, Heads.getInstance());
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = false)
    public void onBlockPlace(BlockPlaceEvent e) {
        ItemStack item = e.getItemInHand();
        
        if (item.getType() == Material.SKULL_ITEM) {
            SkullMeta meta = (SkullMeta) item.getItemMeta();
            
            if (meta.getOwner().equals("SpigotHeadPlugin")) {
                BlockStoreApi.setBlockMeta(e.getBlock(), Heads.getInstance(), "name", meta.getDisplayName());
            }
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = false)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();
        
        if (block.getType() == Material.SKULL) {
            String name = (String) BlockStoreApi.getBlockMeta(block, Heads.getInstance(), "name");
            
            if (name != null) {
                e.setCancelled(true);
                
                BlockBreakEvent event = new BlockBreakEvent(block, e.getPlayer());
                
                for (RegisteredListener listener : BlockBreakEvent.getHandlerList().getRegisteredListeners()) {
                    if (!listener.getListener().equals(this)) {
                        try {
                            listener.callEvent(event);
                        } catch (EventException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
                
                if (!event.isCancelled()) {
                    World world = new World(block.getWorld());
                    BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
                    TileEntitySkull tile = world.getTileEntity(pos).asSkullEntity();
                    
                    block.setType(Material.AIR);
                    
                    if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                        block.getWorld().dropItemNaturally(block.getLocation().add(0.5, 0.5, 0.5), ItemNBT.createHead(tile.getGameProfile(), name));
                    }
                }
            }
        }
    }
    
}
