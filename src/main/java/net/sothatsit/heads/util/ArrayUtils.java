package net.sothatsit.heads.util;

public class ArrayUtils {
    
    @SafeVarargs
    public static <T> T[] create(T... values) {
        return values;
    }
    
    @SafeVarargs
    public static <T> T[] append(T[] list1, T... list2) {
        T[] newList = java.util.Arrays.copyOf(list1, list1.length + list2.length);
        System.arraycopy(list2, 0, newList, list1.length, list2.length);
        return newList;
        
    }
    
}