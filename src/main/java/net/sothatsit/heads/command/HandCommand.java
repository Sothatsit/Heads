package net.sothatsit.heads.command;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.config.menu.Placeholder;
import net.sothatsit.heads.lang.Lang;
import net.sothatsit.heads.util.Callback;
import net.sothatsit.heads.volatilecode.ItemNBT;
import net.sothatsit.heads.volatilecode.reflection.Version;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class HandCommand extends AbstractCommand {
    
    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, final String[] args) {
        if (!(sender instanceof Player)) {
            Lang.Command.Errors.mustBePlayer().send(sender);
            return true;
        }
        
        if (args.length < 3) {
            Lang.Command.Errors.invalidArgs().send(sender, Placeholder.valid(Lang.Command.Hand.help().command()));
        }
        
        Player player = (Player) sender;
        
        ItemStack hand = player.getItemInHand();
        
        if (hand == null || hand.getType() != Material.SKULL_ITEM || hand.getDurability() != 3) {
            Lang.Command.Hand.notSkull().send(sender);
            return true;
        }
        
        String texture = ItemNBT.getTextureProperty(hand);
        
        if (texture == null || texture.isEmpty()) {
            Lang.Command.Hand.noTextureProperty().send(sender);
            
            if (Version.v1_8.higherThan(Version.getVersion())) {
                Lang.Command.Hand.notSupported().send(sender);
                return true;
            }
            
            SkullMeta meta = (SkullMeta) hand.getItemMeta();
            
            final String owner = meta.getOwner();
            
            if (owner == null || owner.isEmpty()) {
                Lang.Command.Hand.noNameProperty().send(sender);
                return true;
            }
            
            texture = Heads.getTextureGetter().getCachedTexture(owner);
            
            if (texture == null || texture.isEmpty()) {
                Lang.Command.Hand.fetching().send(sender);
                Heads.getTextureGetter().getTexture(owner, new Callback<String>() {
                    @Override
                    public void call(String texture) {
                        if (texture == null || texture.isEmpty()) {
                            Lang.Command.Hand.cantFind().send(sender, Placeholder.name(owner));
                            return;
                        }
                        
                        add(sender, args, texture);
                    }
                });
                return true;
            }
        }
        
        add(sender, args, texture);
        return true;
    }
    
    public void add(CommandSender sender, String[] args, String texture) {
        String category = args[1];
        
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 2; i < args.length; i++) {
            nameBuilder.append(' ');
            nameBuilder.append(args[i]);
        }
        
        String name = nameBuilder.toString().substring(1);
        
        CachedHead head = new CachedHead(-1, category, name, texture);
        
        Heads.getCacheConfig().add(head);
        
        Lang.Command.Hand.adding().send(sender, Placeholder.name(name), Placeholder.category(category));
    }
    
}
