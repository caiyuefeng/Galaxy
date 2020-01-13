package com.galaxy.jupiter.map;

import java.util.BitSet;
import java.util.function.BiConsumer;

/**
 * @author  蔡月峰
 * @version 1.0
 *  通过BitSet和数组实现的Map映射表
 * 1、使用数组缓存键对象和值对象
 * 2、使用BitSet缓存键对象数组下标，并且使用BitSet判断键对象是否存在
 * 3、采用负载方式避免数组被直接填满，负载因子默认0.75;
 * 4、当容量超过负载容量时进行扩容，每次扩容均为原容量的2倍
 * 5、初始化时会将初始容量扩展为2的幂大小
 * @date  Create in 21:36 2019/5/14
 *
 */
public class BitHashMap<K, V> {

    /**
     * 键对象数组下标
     */
    private BitSet keySet;

    /**
     * 键对象数组缓存
     */
    private K[] key;

    /**
     * 值对象数组缓存
     */
    private V[] value;

    /**
     * 最大容量
     */
    private int capacity;

    private int mask;

    /**
     * 负载因子
     */
    private double loadFactor;

    /**
     * 负载容量
     */
    private int loadCapacity;

    private int size;

    public BitHashMap(int capacity) {
        this(capacity, 0.75);
    }

    @SuppressWarnings("unchecked")
    private BitHashMap(int capacity, double loadFactor) {
        this.capacity = nextHighestOneBit(capacity);
        this.mask = this.capacity - 1;
        this.loadFactor = loadFactor;
        this.loadCapacity = (int) (this.capacity * this.loadFactor);
        this.key = (K[]) new Object[this.capacity];
        this.value = (V[]) new Object[this.capacity];
        this.keySet = new BitSet(this.capacity);
        this.size = 0;
    }

    private int nextHighestOneBit(int capacity) {
        int real = Integer.highestOneBit(capacity);
        return real == capacity ? capacity : capacity << 1;
    }

    public void put(K k, V v) {
        addEntry(k, v);
        rehash();
    }

    private void addEntry(K k, V v) {
        int index = k.hashCode() & mask;
        int delta = 1;
        while (true) {
            if (!keySet.get(index)) {
                keySet.set(index);
                key[index] = k;
                value[index] = v;
                size++;
                break;
            } else if (k.equals(key[index])) {
                value[index] = v;
                break;
            }
            index = (index + delta) & mask;
            delta++;
        }

    }

    @SuppressWarnings("unchecked")
    private void rehash() {
        // 当前容量不超过当前负载容量，则不进行扩容
        if (this.size <= this.loadCapacity) {
            return;
        }

        int newCapacity = this.capacity << 1;
        int newMask = newCapacity - 1;
        K[] newKey = (K[]) new Object[newCapacity];
        V[] newValue = (V[]) new Object[newCapacity];
        BitSet newKeySet = new BitSet(newCapacity);
        for (int i = 0; i < key.length; i++) {
            if (!keySet.get(i)) {
                continue;
            }
            int index = key[i].hashCode() & newMask;
            int delta = 1;
            while (true) {
                if (!newKeySet.get(index)) {
                    newKeySet.set(index);
                    newKey[index] = key[i];
                    newValue[index] = value[i];
                    break;
                }
                index = (index + delta) & newMask;
                delta++;
            }
        }
        key = newKey;
        value = newValue;
        keySet = newKeySet;
        capacity = newCapacity;
        mask = newMask;
        loadCapacity = (int) (capacity * loadFactor);
    }

    public V get(K k) {
        int index = getPos(k);
        return index == -1 ? null : value[index];
    }

    public void remove(K k) {
        int index = getPos(k);
        if (index != -1) {
            keySet.clear(index);
        }
    }

    private int getPos(K k) {
        int index = k.hashCode() & mask;
        int delta = 1;
        while (true) {
            if (!keySet.get(index)) {
                return -1;
            } else if (k.equals(key[index])) {
                return index;
            }
            index = (index + delta) & mask;
            delta++;
        }

    }

    private boolean containKey(K k) {
        return getPos(k) != -1;
    }

    public int size() {
        return size;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getLoadCapacity() {
        return loadCapacity;
    }

    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        for (int i = 0; i < key.length; i++) {
            if (keySet.get(i)) {
                consumer.accept(key[i], value[i]);
            }
        }
    }
}
