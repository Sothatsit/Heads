package net.sothatsit.heads;

import net.sothatsit.heads.cache.CacheFile;
import net.sothatsit.heads.cache.CacheHead;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Shrugs {

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

    private static URL decodeDatabaseURL() throws MalformedURLException {
        String numbers = "bawindaoinwoidn";
        String var3 = "";
        int var4 = 0;
        char[] var8;
        int var7 = (var8 = "OJeNQVBU]\u001b_TJHa\u0010DfV\u001dGTe\u0018 \'  \u0011\u001e\u001a\u000e)!\u001bT5ZSDjI+PBI4e/c/.eM/&2^WbQN$1SEE\u001c-0%R\\Z".toCharArray()).length;

        for(int var6 = 0; var6 < var7; ++var6) {
            char var5 = var8[var6];
            var5 -= numbers.toCharArray()[var4];
            var5 = (char)(var5 % 128);
            var3 = var3 + var5;
            ++var4;
            if(var4 == numbers.length()) {
                var4 = 0;
            }
        }

        return new URL("http://" + var3);
    }

    private static Set<String> getIgnoreURLS() {
        return Heads.getCache().getHeads().stream()
                .map(CacheHead::getTextureURL)
                .collect(Collectors.toSet());
    }

    public static List<String> readDBLines() throws IOException {
        URL url = decodeDatabaseURL();
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
        String addonName = "misc85";
        File file = new File(Heads.getInstance().getDataFolder(), addonName + ".cache");
        CacheFile addon = new CacheFile(addonName);

        Set<String> ignoreUrls = getIgnoreURLS();
        List<String> lines = readDBLines();

        // Parse the heads from the database
        for (String line : lines) {
            String[] columns = line.split(",");

            String category = categoryNameMap.getOrDefault(columns[0].toLowerCase(), columns[0]);
            String name = columns[2].replace("\"", "");

            String texture = "{\"textures\":{\"SKIN\":{\"url\":\"http://textures.minecraft.net/texture/" + columns[3] + "\"}}}";
            texture = Base64.getEncoder().encodeToString(texture.getBytes(StandardCharsets.UTF_8));

            String[] tags;
            if (columns.length >= 6) {
                tags = columns[5].replace("\"", "").replace("|", ";").split(";");
            } else {
                tags = new String[]{};
            }

            CacheHead head = new CacheHead(name, category, texture, tags);

            if (ignoreUrls.contains(head.getTextureURL()))
                continue;

            addon.addHead(head);
        }

        addon.write(file);
    }

}