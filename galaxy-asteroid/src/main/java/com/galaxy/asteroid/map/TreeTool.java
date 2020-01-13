package com.galaxy.asteroid.map;

/**
 * @author : 蔡月峰
 * @version : 1.0
 *
 * @date 2019/3/13 8:41
 **/
public class TreeTool {

    /**
     * 将非标准红黑树平衡为标准红黑树
     *
     * @param root 树根节点
     * @return 平衡后根节点
     */
    public static <K, V> TreeNode<K, V> treeForm(TreeNode<K, V> root, TreeNode<K, V> insertNode) {
        // 父节点
        TreeNode<K, V> parentNode;
        // 祖父节点
        TreeNode<K, V> greatParentNode;
        // 叔叔节点
        TreeNode<K, V> uncleNode;

        while (true) {
            parentNode = insertNode.parent;
            // 当前无根节点
            if (parentNode == null) {
                insertNode.red = false;
                insertNode.left = null;
                insertNode.parent = null;
                insertNode.left = null;
                root = insertNode;
                return root;
            }

            // 当前父节点为根节点 或者为黑色节点
            if (parentNode.parent == null || !parentNode.red) {
                return root;
            }
            // 获取父节点引用
            greatParentNode = parentNode.parent;
            // 如果父节点为祖父节点的左孩子
            if (greatParentNode.left == parentNode) {
                uncleNode = greatParentNode.right;
                // 如果叔父节点为红色，则将叔父和父节点变为黑色，将祖父节点变为红色
                // 将祖父节点作为插入节点继续循环
                if (uncleNode != null && uncleNode.red) {
                    uncleNode.red = false;
                    greatParentNode.red = true;
                    parentNode.red = false;
                    insertNode = greatParentNode;
                    continue;
                }

                // 如果叔父节点为空或者黑色
                if (uncleNode == null || !uncleNode.red) {

                    // 如果插入节点是父节点的右节点
                    if (parentNode.right == insertNode) {
                        // 将父节点左旋
                        root = leftRotate(root, insertNode = parentNode);
                        // 重新设置当前父节点和祖父节点
                        parentNode = insertNode.parent;
                        greatParentNode = parentNode == null ? null : parentNode.parent;
                    }
                    // 如果插入节点是父节点的右节点
                    if (parentNode.left == insertNode) {
                        parentNode.red = false;
                        if (greatParentNode != null) {
                            greatParentNode.red = true;
                            root = rightRotate(root, greatParentNode);
                        }
                    }
                }
            }

            // 当前叔父节点


        }
    }

    /**
     * 左旋
     *
     * @param root
     * @param rotateNode
     * @param <K>
     * @param <V>
     * @return
     */
    private static <K, V> TreeNode<K, V> leftRotate(TreeNode<K, V> root, TreeNode<K, V> rotateNode) {
        // 左旋节点不为空
        if (rotateNode != null && rotateNode.right != null) {
            // 左旋节点的父节点
            TreeNode<K, V> parentNode = rotateNode.parent;

            // 左旋节点的右孩子
            TreeNode<K, V> rightNode = rotateNode.right;

            // 左旋节点的右孩子的左孩子
            TreeNode<K, V> rightLeftNode = rotateNode.right.left;

            // 将左旋节点的父节点连接到右孩子节点
            if (parentNode.parent == null) {
                rightNode.red = false;
                root = rightNode;
            } else {
                rightNode.parent = parentNode.parent;
                if (parentNode.left == rotateNode) {
                    parentNode.left = rightNode;
                } else if (parentNode.right == rotateNode) {
                    parentNode.right = rightNode;
                }
            }

            // 将左旋节点的右孩子的左孩子连接到左旋节点的右孩子
            if (rightLeftNode != null) {
                rightLeftNode.parent = rotateNode;
                rotateNode.right = rightLeftNode;
            }

            rightNode.left = parentNode;
            parentNode.parent = rightNode;
        }
        return root;
    }

    /**
     * 右旋
     *
     * @param root
     * @param rotateNode
     * @param <K>
     * @param <V>
     * @return
     */
    private static <K, V> TreeNode<K, V> rightRotate(TreeNode<K, V> root, TreeNode<K, V> rotateNode) {

        if (rotateNode != null && rotateNode.left != null) {
            // 旋转节点的父节点
            TreeNode<K, V> parentNode = rotateNode.parent;
            // 旋转节点的左孩子
            TreeNode<K, V> leftNode = rotateNode.left;
            // 旋转节点的右孩子
            TreeNode<K, V> leftRightNode = rotateNode.left.right;

            // 父节点为空，则将当前节点置为黑色
            if (parentNode == null) {
                leftNode.red = false;
                root = leftNode;
            }
            // 将旋转节点父节点的子节点连接到左孩子上
            else {
                leftNode.parent = parentNode;
                if (parentNode.left == rotateNode) {
                    parentNode.left = leftNode;
                } else if (parentNode.right == rotateNode) {
                    parentNode.right = leftNode;
                }
            }
            if (leftRightNode != null) {
                leftRightNode.parent = rotateNode;
                rotateNode.left = leftRightNode;
            }
            leftNode.right = rotateNode;
            rotateNode.parent = leftNode;
        }
        return root;
    }

}
