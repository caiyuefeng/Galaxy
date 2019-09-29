package com.galaxy.jupiter.db;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description:
 * @Date : Create in 21:35 2019/5/14
 * @Modified By:
 */
public class Vertex {

    /**
     * 顶点唯一编码
     */
    private Long vid;

    /**
     * 属性
     */
    private Property property;

    private Vertex(Long vid, Property property) {
        this.vid = vid;
        this.property = property;
    }

    public static Vertex builder(Long vid) {
        return builder(vid, null);
    }

    public static Vertex builder(Long vid, Property property) {
        return new Vertex(vid, property);
    }

    public void setVid(Long vid) {
        this.vid = vid;
    }

    public void setProperty(Property property) {
        this.property = property;
    }

    public Long getVid() {
        return vid;
    }

    public Property getProperty() {
        return property;
    }

    @Override
    public boolean equals(Object that) {
        return that != null
                && that instanceof Vertex
                && vid.equals(((Vertex) that).vid);
    }
}
