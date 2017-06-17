package net.sothatsit.heads;

import net.sothatsit.heads.config.cache.CacheConfig;
import net.sothatsit.heads.config.cache.CachedHead;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

public class Shrugs {

    private static final String databaseURL = "http://minecraft-heads.com/csv/UUID-Head-DB_v1.0.csv";
    private static final Map<String, String> categoryNameMap = new HashMap<>();

    static {
        categoryNameMap.put("alphabet", "Alphabet");
        categoryNameMap.put("animals", "Animals");
        categoryNameMap.put("blocks", "Blocks");
        categoryNameMap.put("decoration", "Interior");
        categoryNameMap.put("food & drinks", "Food");
        categoryNameMap.put("food-drinks", "Food");
        categoryNameMap.put("humanoid", "Monsters");
        categoryNameMap.put("humans", "Humans");
        categoryNameMap.put("minecraft-blocks", "Blocks");
        categoryNameMap.put("miscellaneous", "Misc");
        categoryNameMap.put("monsters", "Monsters");
        categoryNameMap.put("plants", "Food");
    }

    private static Set<String> getIgnoreURLS() {
        Set<String> ignoreUrls = new HashSet<>();

        Heads.getCacheConfig().getHeads().values().forEach(
                list -> list.stream()
                        .map(CachedHead::getTextureURL)
                        .forEach(ignoreUrls::add)
        );

        return ignoreUrls;
    }

    public static List<String> readDBLines() throws IOException {
        URL url = new URL(databaseURL);
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));

        List<String> lines = new ArrayList<>();

        String line;
        while((line = reader.readLine()) != null) {
            lines.add(line);
        }

        reader.close();

        return lines;
    }

    public static void shrug() throws IOException {
        Set<String> ignoreUrls = getIgnoreURLS();
        List<String> lines = readDBLines();

        Map<String, Set<CachedHead>> heads = new HashMap<>();

        // Parse the heads from the database
        for(int index = 0; index < lines.size(); ++index) {
            String[] columns = lines.get(index).split(",");

            String category = categoryNameMap.getOrDefault(columns[0].toLowerCase(), columns[0]);
            String name = columns[2].replace("\"", "");
            String texture = columns[3];
            String[] tags;

            if(columns.length >= 6) {
                tags = columns[5].replace("\"", "").replace("|", ";").split(";");
            } else {
                tags = new String[] {};
            }

            CachedHead head = new CachedHead(index + 1, category, name, texture, tags);

            if(ignoreUrls.contains(head.getTextureURL()))
                continue;

            if(!heads.containsKey(category)) {
                heads.put(category, new HashSet<>());
            }

            heads.get(category).add(head);
        }

        // Chunk the heads into 50 head files
        List<Set<CachedHead>> chunked = new ArrayList<>();
        Set<CachedHead> lastChunk = new HashSet<>();

        int totalHeads = 0;

        for(Map.Entry<String, Set<CachedHead>> entry : heads.entrySet()) {
            Heads.info("Category : " + entry.getKey() + " (" + entry.getValue().size() + " Heads)");

            totalHeads += entry.getValue().size();

            for(CachedHead head : entry.getValue()) {
                if(lastChunk.size() >= 50) {
                    chunked.add(lastChunk);
                    lastChunk = new HashSet<>();
                }

                lastChunk.add(head);
            }
        }

        Heads.info(totalHeads + " new heads.");

        if(lastChunk.size() > 0) {
            chunked.add(lastChunk);
        }

        // Write out the chunks into their own files
        int chunkNum = 15;

        for(Set<CachedHead> chunk : chunked) {
            File file = new File(Heads.getInstance().getDataFolder(), "misc-addon-" + (chunkNum++) + ".yml");

            if(!file.exists() && !file.createNewFile()) {
                throw new IOException("Could not create output yaml file " + file);
            }

            new CacheConfig(file, chunk);
        }
    }

}