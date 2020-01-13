package com.galaxy.jupiter.db;

import java.util.HashMap;
import java.util.Map;

/**
 * @author  蔡月峰
 * @version 1.0
 *
 * @date  Create in 21:35 2019/5/14
 *
 */
public class EdgePartition {

    private Long[] srcId;

    private Long[] dstId;

    private Property[] properties;

    private Map<Long, Integer> vidIndex;

    public EdgePartition(int capacity) {
        this.srcId = new Long[capacity];
        this.dstId = new Long[capacity];
        this.properties = new Property[capacity];
        vidIndex = new HashMap<>(capacity);
    }




}
