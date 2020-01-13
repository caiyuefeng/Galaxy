package com.galaxy.jupiter.map;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author  蔡月峰
 * @version 1.0
 *
 * @date  Create in 22:24 2019/5/14
 *
 */
public class BitHashMapTest {
    @Test
    public void testPutGet() {
        BitHashMap<Integer, Integer> map = new BitHashMap<>(4);
        map.put(1, 2);
        map.put(2, 3);
        map.put(3, 4);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(4L, (long) map.get(3));
        map.put(3, 5);
        Assert.assertEquals(3, map.size());
        Assert.assertEquals(5L, (long) map.get(3));
    }

    @Test
    public void testCapacity() {
        BitHashMap<Integer, Integer> map = new BitHashMap<>(2);
        map.put(1, 2);
        Assert.assertEquals(2, map.getCapacity());
        Assert.assertEquals(1, map.getLoadCapacity());
        Assert.assertEquals(1, map.size());
        map.put(2, 3);
        Assert.assertEquals(4, map.getCapacity());
        Assert.assertEquals(3, map.getLoadCapacity());
        Assert.assertEquals(2, map.size());
        map.put(3, 3);
        Assert.assertEquals(4, map.getCapacity());
        Assert.assertEquals(3, map.getLoadCapacity());
        Assert.assertEquals(3, map.size());
        map.put(4, 3);
        Assert.assertEquals(8, map.getCapacity());
        Assert.assertEquals(6, map.getLoadCapacity());
        Assert.assertEquals(4, map.size());
    }

}