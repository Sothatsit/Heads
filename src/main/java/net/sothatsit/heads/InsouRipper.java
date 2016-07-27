package net.sothatsit.heads;

import net.sothatsit.heads.config.cache.CacheConfig;
import net.sothatsit.heads.config.cache.CachedHead;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

public class InsouRipper {

    public static void rip(Set<CachedHead> ignoreHeads, File in) throws Exception {
        Set<String> ignoreUrls = ignoreHeads.stream().map(CachedHead::getTextureURL).collect(Collectors.toSet());

        Map<String, String> categoryNameMap = new HashMap<>();

        categoryNameMap.put("decoration", "Interior");
        categoryNameMap.put("monsters", "Monsters");
        categoryNameMap.put("plants", "Food");
        categoryNameMap.put("food & drinks", "Food");
        categoryNameMap.put("minecraft-blocks", "Blocks");
        categoryNameMap.put("miscellaneous", "Misc");

        Map<String, Set<CachedHead>> heads = new HashMap<>();

        String[] lines = Files.readAllLines(in.toPath()).toArray(new String[0]);

        int idCounter = 1;

        for(int line = 0; line < lines.length; line += 4) {
            String name = lines[line];
            String category = categoryNameMap.getOrDefault(lines[line + 1].toLowerCase(), lines[line + 1]);
            String url = lines[line + 2];

            if(ignoreUrls.contains(url)) {
                continue;
            }

            String texture = Base64.getEncoder().encodeToString(("{\"textures\":{\"SKIN\":{\"url\":\"" + url + "\"}}}").getBytes());

            CachedHead head = new CachedHead(idCounter++, category, name, texture);

            Set<CachedHead> set = heads.get(category);

            if(set == null) {
                set = new HashSet<>();
                heads.put(category, set);
            }

            set.add(head);
        }

        List<Set<CachedHead>> blocked = new ArrayList<>();

        blocked.add(new HashSet<>());

        for(Map.Entry<String, Set<CachedHead>> entry : heads.entrySet()) {
            Heads.info("Category : " + entry.getKey() + " (" + entry.getValue().size() + " Heads)");

            Set<CachedHead> last = blocked.get(blocked.size() - 1);

            for(CachedHead head : entry.getValue()) {
                if(last.size() >= 50) {
                    last = new HashSet<>();
                    blocked.add(last);
                }

                last.add(head);
            }
        }

        int blockNo = 1;

        for(Set<CachedHead> block : blocked) {
            File file = new File(Heads.getInstance().getDataFolder(), "misc-addon-" + (blockNo++) + ".yml");

            if(!file.exists()) {
                file.createNewFile();
            }

            new CacheConfig(file, block);
        }
    }

}
