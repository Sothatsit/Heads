package net.sothatsit.heads.menu.ui;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.menu.ui.element.Container;
import net.sothatsit.heads.menu.ui.item.MenuItem;
import net.sothatsit.heads.util.Checks;
import net.sothatsit.heads.util.Stringify;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class InventoryMenu extends Container implements InventoryHolder {

    private final Inventory inventory;
    private final Player player;

    public InventoryMenu(Player player, String name, int rows) {
        super(new Bounds(Position.ZERO, 9, rows));

        Checks.ensureNonNull(player, "player");
        Checks.ensureNonNull(name, "name");

        if(name.length() > 32) {
            name = name.substring(0, 32);
        }

        this.inventory = Bukkit.createInventory(this, bounds.getVolume(), name);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean hasMenuOpen() {
        InventoryView view = player.getOpenInventory();

        if (view == null || view.getTopInventory() == null)
            return false;

        InventoryHolder holder = view.getTopInventory().getHolder();

        return holder != null && holder.equals(this);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    public void open() {
        player.openInventory(inventory);
    }

    @Override
    public void updateElement() {
        super.updateElement();

        updateMenu();
    }

    private void updateMenu() {
        MenuItem[] items = getItems();
        ItemStack[] contents = new ItemStack[items.length];

        for(int index = 0; index < contents.length; index++) {
            MenuItem item = items[index];

            if(item != null) {
                contents[index] = item.getItem();
            }
        }

        inventory.setContents(contents);
    }

    public void onClick(InventoryClickEvent event) {
        event.setCancelled(true);

        // Make sure the player's inventory is up to date after the event is cancelled
        Bukkit.getScheduler().scheduleSyncDelayedTask(Heads.getInstance(), player::updateInventory, 1);

        int slot = event.getRawSlot();

        MenuResponse response = handleClick(slot);

        switch (response) {
            case CLOSE:
                player.closeInventory();
                break;
            case NONE:
                break;
            default:
                throw new IllegalStateException("Unknown MenuResponse value " + response);
        }
    }

    @Override
    public String toString() {
        return Stringify.builder()
                .previous(super.toString())
                .entry("inventory", inventory)
                .entry("player", player).toString();
    }

}