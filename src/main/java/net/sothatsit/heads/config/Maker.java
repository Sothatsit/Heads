package net.sothatsit.heads.config;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Maker {
    
    public static void main(String[] args) {
        commands(args);
    }
    
    public static void commands(String[] args) {
        List<Head> heads = new ArrayList<Head>();
        
        String category = "Food";
        
        Scanner input = new Scanner(System.in);
        
        while (input.hasNext()) {
            String line = input.nextLine();
            
            if (line.equalsIgnoreCase("end")) {
                break;
            }
            
            line = line.substring(35);
            
            int index = line.indexOf('"');
            
            String name = line.substring(0, index);
            
            line = line.substring(index);
            
            line = line.substring(87);
            
            String texture = line.substring(0, line.indexOf('"'));
            
            heads.add(new Head(category, name, texture));
        }
        
        input.close();
        
        System.out.println();
        
        int index = 44;
        for (Head h : heads) {
            System.out.println("Head-" + index + ":");
            System.out.println("   category: '" + h.category + "'");
            System.out.println("   name: '" + h.name + "'");
            System.out.println("   texture: '" + h.texture + "'");
            
            index++;
        }
    }
    
    public static void webpage(String[] args) {
        try {
            Scanner scan = new Scanner(new URL("http://heads.freshcoal.com/heads.php?query=animal").openStream(), "UTF-8");
            scan.useDelimiter("\\A");
            
            String webpage = scan.next();
            
            MutableString string = new MutableString(webpage);
            
            Map<String, String> categoryNames = findCategoryNames(string);
            
            MutableString category = new MutableString("Animals");
            
            List<Head> heads = new ArrayList<>();
            
            Head head;
            
            whileLoop: while ((head = findNext(string, category)) != null) {
                for (Head h : heads) {
                    if (head.name.equalsIgnoreCase(h.name) && head.texture.equals(h.texture)) {
                        continue whileLoop;
                    }
                }
                
                heads.add(head);
            }
            
            int index = 1;
            for (Head h : heads) {
                if (categoryNames.containsKey(h.category)) {
                    h.category = categoryNames.get(h.category);
                }
                
                System.out.println("Head-" + index + ":");
                System.out.println("   category: '" + h.category + "'");
                System.out.println("   name: '" + h.name + "'");
                System.out.println("   texture: '" + h.texture + "'");
                
                index++;
            }
            
            scan.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        System.exit(1);
    }
    
    public static Map<String, String> findCategoryNames(MutableString string) {
        Map<String, String> map = new HashMap<>();
        
        int start = endIndexOf(string.string, "<div class=\"center1\">");
        
        if (start >= 0) {
            while (true) {
                String from = findString(string, "<a href=\"#", "Unable to find from start", "\"", "Unable to find from end");
                
                if (from == null) {
                    break;
                }
                
                String to = findString(string, "\">", "Unable to find to start", "</a>", "Unable to find to end");
                
                if (to == null) {
                    break;
                }
                
                map.put(from, to);
            }
        }
        
        return map;
    }
    
    public static Head findNext(MutableString string, MutableString category) {
        int categoryStartIndex = string.string.indexOf("<div id=\"");
        int nextStartIndex = endIndexOf(string.string, "display:{Name:\"");
        
        if (categoryStartIndex >= 0 && categoryStartIndex < nextStartIndex) {
            String categ = findString(string, "<div id=\"", "Unable to find category start", "\"", "Unable to find category end");
            
            if (categ != null) {
                category.string = categ;
            }
        }
        
        String name = findString(string, "display:{Name:\"", "Unable to find name start", "\"", "Unable to find name end");
        
        if (name == null) {
            return null;
        }
        
        String texture = findString(string, "Properties:{textures:[{Value:\"", "Unable to find texture start", "\"", "Unable to find texture end");
        
        if (texture == null) {
            return null;
        }
        
        return new Head(category.string, name, texture);
    }
    
    public static String findString(MutableString search, String start, String cantFindStartMessage, String end, String cantFindEndMessage) {
        String str = search.string;
        
        int startIndex = endIndexOf(str, start);
        
        if (startIndex < 0) {
            System.out.println(cantFindStartMessage);
            return null;
        }
        
        str = str.substring(startIndex);
        
        int endIndex = str.indexOf(end);
        
        if (endIndex < 0) {
            System.out.println(cantFindEndMessage);
            return null;
        }
        
        search.string = str.substring(endIndex);
        
        return str.substring(0, endIndex);
    }
    
    public static int endIndexOf(String str, String find) {
        int index = str.indexOf(find);
        
        if (index < 0) {
            return -1;
        }
        
        return index + find.length();
    }
    
    static class MutableString {
        public String string;
        
        public MutableString(String string) {
            this.string = string;
        }
    }
    
    static class Head {
        
        public String category;
        public String name;
        public String texture;
        
        public Head(String category, String name, String texture) {
            this.category = category;
            this.name = name;
            this.texture = texture;
        }
    }
}
