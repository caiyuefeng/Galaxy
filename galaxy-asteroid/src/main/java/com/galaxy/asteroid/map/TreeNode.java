package com.galaxy.asteroid.map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2019/3/13 8:35
 **/
public class TreeNode<K, V> {

    /**
     * 父节点
     */
    public TreeNode<K, V> parent;
    /**
     * 左孩子
     */
    public TreeNode<K, V> left;
    /**
     * 右孩子
     */
    public TreeNode<K, V> right;
    /**
     * 键
     */
    public K key;
    /**
     * 值
     */
    public V value;
    /**
     * 红黑树标志
     */
    public boolean red;


}
