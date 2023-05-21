package ch.epfl.javions.gui;

import org.junit.Test;

import java.util.LinkedHashMap;

public final class TestTileManager {

    @Test
    public void test() {
        final LinkedHashMap<String, String> cache = new LinkedHashMap<>(5, 1, true);

        cache.put("1", "1");
        cache.put("2", "2");
        cache.put("3", "3");
        cache.put("4", "4");
        cache.put("5", "5");
        System.out.println(cache);
        cache.get("1");
        System.out.println(cache);
        cache.put("6", "6");
        System.out.println(cache);
        cache.remove(cache.keySet().iterator().next());
        System.out.println(cache);
    }
}