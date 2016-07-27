package net.sothatsit.heads.volatilecode;

import java.util.Base64;
import java.util.UUID;

import net.sothatsit.heads.Heads;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagString;
import org.bukkit.ChatColor;
import org.bukkit.Material;

import net.sothatsit.heads.config.cache.CachedHead;
import net.sothatsit.heads.volatilecode.reflection.authlib.GameProfile;
import net.sothatsit.heads.volatilecode.reflection.authlib.Property;
import net.sothatsit.heads.volatilecode.reflection.craftbukkit.CraftItemStack;
import net.sothatsit.heads.volatilecode.reflection.nms.ItemStack;
import net.sothatsit.heads.volatilecode.reflection.nms.Items;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagCompound;
import net.sothatsit.heads.volatilecode.reflection.nms.nbt.NBTTagList;

public class ItemNBT {
    
    public static String getTextureProperty(org.bukkit.inventory.ItemStack item) {
        return getTextureProperty(CraftItemStack.asNMSCopy(item));
    }
    
    public static String getTextureProperty(ItemStack item) {
        NBTTagCompound tag = item.getTag();
        
        if (tag == null || tag.getHandle() == null) {
            return null;
        }
        
        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        
        if (skullOwner == null || skullOwner.getHandle() == null) {
            return null;
        }
        
        NBTTagCompound properties = skullOwner.getCompound("Properties");
        
        if (properties == null || properties.getHandle() == null) {
            return null;
        }
        
        NBTTagList textures = properties.getList("textures", 10);
        
        if (textures == null || textures.getHandle() == null || textures.size() == 0) {
            return null;
        }
        
        return textures.get(0).getString("Value");
    }
    
    public static org.bukkit.inventory.ItemStack createHead(CachedHead head, String name) {
        ItemStack itemstack = new ItemStack(Items.getItem("SKULL"), 1, 3);
        
        NBTTagCompound tag = itemstack.getTag();
        
        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();
            
            itemstack.setTag(tag);
        }
        
        NBTTagCompound display = tag.getCompound("display");
        display.setString("Name", (name == null ? ChatColor.GRAY + head.getName() : name));

        NBTTagList lore = new NBTTagList();
        lore.add(new NBTTagString(ChatColor.DARK_GRAY + head.getCategory()));

        display.set("Lore", lore);
        tag.set("display", display);
        
        itemstack.setTag(tag);
        
        return CraftItemStack.asBukkitCopy(apply(head, itemstack));
    }
    
    public static org.bukkit.inventory.ItemStack createHead(GameProfile profile, String name) {
        ItemStack itemstack = new ItemStack(Items.getItem("SKULL"), 1, 3);
        
        NBTTagCompound tag = itemstack.getTag();
        
        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();
            
            itemstack.setTag(tag);
        }
        
        NBTTagCompound display = tag.getCompound("display");
        display.setString("Name", name);
        tag.set("display", display);
        
        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        skullOwner.setString("Id", UUID.randomUUID().toString());
        skullOwner.setString("Name", "SpigotHeadPlugin");
        
        NBTTagCompound properties = skullOwner.getCompound("Properties");
        NBTTagList textures = new NBTTagList();
        
        for (Property property : profile.getProperties().get("textures")) {
            NBTTagCompound value = new NBTTagCompound();
            value.setString("Value", property.getValue());

            if(property.hasSignature()) {
                value.setString("Signature", property.getSignature());
            }
            
            textures.add(value);
        }
        
        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        tag.set("SkullOwner", skullOwner);
        
        itemstack.setTag(tag);
        
        return CraftItemStack.asBukkitCopy(itemstack);
    }
    
    public static org.bukkit.inventory.ItemStack applyHead(CachedHead head, org.bukkit.inventory.ItemStack item) {
        if (item.getType() != Material.SKULL_ITEM || item.getDurability() != 3) {
            return item;
        }
        
        ItemStack itemstack = CraftItemStack.asNMSCopy(item);
        
        return CraftItemStack.asBukkitCopy(apply(head, itemstack));
    }
    
    public static ItemStack apply(CachedHead head, ItemStack itemstack) {
        NBTTagCompound tag = itemstack.getTag();
        
        if (tag.getHandle() == null) {
            tag = new NBTTagCompound();
            
            itemstack.setTag(tag);
        }

        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");
        skullOwner.setString("Id", UUID.randomUUID().toString());
        skullOwner.setString("Name", "SpigotHeadPlugin");

        NBTTagCompound properties = skullOwner.getCompound("Properties");
        NBTTagList textures = new NBTTagList();

        NBTTagCompound value = new NBTTagCompound();
        value.setString("Value", head.getTexture());
        value.setString("Signature", "");

        textures.add(value);

        properties.set("textures", textures);
        skullOwner.set("Properties", properties);
        tag.set("SkullOwner", skullOwner);

        NBTTagCompound headInfo = new NBTTagCompound();

        headInfo.setString("id", Integer.toString(head.getId()));
        headInfo.setString("name", head.getName());
        headInfo.setString("category", head.getCategory());
        headInfo.setString("texture", head.getTexture());
        headInfo.setString("cost", Double.toString(head.getCost()));
        headInfo.setString("permission", head.getPermission());

        tag.set("SpigotHeadPlugin", headInfo);

        itemstack.setTag(tag);
        
        return itemstack;
    }

    public static org.bukkit.inventory.ItemStack fix(org.bukkit.inventory.ItemStack itemstack) {
        ItemStack nmsItem = CraftItemStack.asNMSCopy(itemstack);

        fix(nmsItem);

        return CraftItemStack.asBukkitCopy(nmsItem);
    }

    public static void fix(ItemStack itemstack) {
        NBTTagCompound tag = itemstack.getTag();

        if (tag.getHandle() == null) {
            return;
        }

        fix(tag);

        itemstack.setTag(tag);
    }

    public static void fix(NBTTagCompound tag) {
        if(!tag.hasKey("SkullOwner")) {
            return;
        }

        NBTTagCompound skullOwner = tag.getCompound("SkullOwner");

        if(!skullOwner.hasKey("Properties")) {
            return;
        }

        NBTTagCompound properties = skullOwner.getCompound("Properties");

        if(!properties.hasKey("textures")) {
            return;
        }

        NBTTagList textures = properties.getList("textures", 10); // 10 = id for tag compound

        for(int i=0; i < textures.size(); i++) {
            NBTTagCompound compound = textures.get(i);

            if(!compound.hasKey("Signature")) {
                compound.setString("Signature", "");
            }
        }
    }
}
