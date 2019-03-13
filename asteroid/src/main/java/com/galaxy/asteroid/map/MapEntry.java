package com.galaxy.asteroid.map;

import java.util.Objects;

/**
 * @author : 蔡月峰
 * @version : 1.0
 * @Description:
 * @date : 2019/3/12 17:00
 **/
public class MapEntry<K, V> {
    private K key;
    private V value;

    public MapEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MapEntry<?, ?> mapEntry = (MapEntry<?, ?>) o;
        return Objects.equals(key, mapEntry.key) &&
                Objects.equals(value, mapEntry.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
