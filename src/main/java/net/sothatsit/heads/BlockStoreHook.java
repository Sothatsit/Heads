package net.sothatsit.heads;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
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

        if(item == null || item.getType() != Material.SKULL_ITEM)
            return;

        SkullMeta meta = (SkullMeta) item.getItemMeta();

        if(!meta.hasOwner() || !meta.getOwner().equals("SpigotHeadPlugin") || !meta.hasDisplayName())
            return;

        BlockStoreApi.setBlockMeta(e.getBlock(), Heads.getInstance(), "name", meta.getDisplayName());
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onBlockBreak(BlockBreakEvent e) {
        Block block = e.getBlock();

        if(block.getType() != Material.SKULL)
            return;

        String name = (String) BlockStoreApi.getBlockMeta(block, Heads.getInstance(), "name");

        if(name == null)
            return;

        e.setCancelled(true);

        BlockBreakEvent event = new BlockBreakEvent(block, e.getPlayer());

        for (RegisteredListener listener : BlockBreakEvent.getHandlerList().getRegisteredListeners()) {
            if(listener.getListener() instanceof BlockStoreHook)
                continue;

            try {
                listener.callEvent(event);
            } catch (EventException exception) {
                Heads.warning("There was an exception calling BlockBreakEvent for " + listener.getPlugin().getName());
                exception.printStackTrace();
            }
        }

        if (!event.isCancelled()) {
            World world = new World(block.getWorld());
            BlockPosition pos = new BlockPosition(block.getX(), block.getY(), block.getZ());
            TileEntitySkull tile = world.getTileEntity(pos).asSkullEntity();

            block.setType(Material.AIR);

            if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {
                Location dropLocation = block.getLocation().add(0.5, 0.5, 0.5);

                block.getWorld().dropItemNaturally(dropLocation, ItemNBT.createHead(tile.getGameProfile(), name));
            }
        }
    }
    
}
