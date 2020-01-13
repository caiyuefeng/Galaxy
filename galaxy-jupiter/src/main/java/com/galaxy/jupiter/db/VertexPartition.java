package com.galaxy.jupiter.db;

import com.galaxy.jupiter.map.BitHashMap;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author  蔡月峰
 * @version 1.0
 *  顶点分区缓存
 * 1、该分区缓存用于对磁盘上的顶点数据按照需求聚合
 * 2、提供持久化数据到磁盘的能力
 * @date  Create in 21:36 2019/5/14
 *
 */
public class VertexPartition {

    private BitHashMap<Long, Property> vertex;

    private VertexPartition(int capacity) {
        vertex = new BitHashMap<>(capacity);
    }

    public static VertexPartition builder(Map<Long, Property> map) {
        VertexPartition partition = new VertexPartition(1000);
        map.forEach((vid, property) -> partition.vertex.put(vid, property));
        return partition;
    }

    Vertex find(Long vid) {
        return Vertex.builder(vid, vertex.get(vid));
    }

    void delete(Long vid) {
        vertex.remove(vid);
    }

    /**
     * 将缓存数据持久化到磁盘
     */
    public void save() {
    }

    public void forEach(Consumer<? super Vertex> consumer) {
        vertex.forEach((vid, property) -> consumer.accept(Vertex.builder(vid, property)));
    }


}
