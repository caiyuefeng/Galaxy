package com.galaxy.jupiter.db;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author  蔡月峰
 * @version 1.0
 *  点对象集合
 * @date  Create in 21:35 2019/5/14
 *
 */
public class Vertexes {

    private static enum PartType {
        /**
         * Hash分区方式
         */
        HASH("hash");
        private String type;

        PartType(String type) {
            this.type = type;
        }
    }

    private Map<PartType, VertexPartition> vertexes;

    private Map<Long, Property> newBuffer;

    public Vertexes() {
        vertexes = new HashMap<>();
        newBuffer = new HashMap<>();
    }

    /**
     * 载入数据
     */
    public void load() {

    }

    /**
     * 查找定点
     * TODO: 修改实现方式
     */
    public Vertex find(Long vid) {
        if (newBuffer.containsKey(vid)) {
            return Vertex.builder(vid, newBuffer.get(vid));
        }
        for (VertexPartition part : vertexes.values()) {
            Vertex vertex = part.find(vid);
            if (vertex != null) {
                return vertex;
            }
        }
        return null;
    }

    public void delete(Vertex vertex) {
        if (newBuffer.containsKey(vertex.getVid())) {
            newBuffer.remove(vertex.getVid());
            return;
        }
        vertexes.forEach((partType, part) -> {
            part.delete(vertex.getVid());
        });
    }

    public void forEach(Consumer<Vertex> consumer) {
        newBuffer.forEach((vid, property) -> consumer.accept(Vertex.builder(vid, property)));
        vertexes.forEach(((partType, vertexPartition) ->
                vertexPartition.forEach(consumer)));
    }

}
