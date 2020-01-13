package com.galaxy.asteroid.calculate;

import java.util.*;

/**
 * @author 蔡月峰
 * @version 1.0
 *  红黑树
 * @date Create in 20:57 2019/4/25
 *
 */
public class RedBlackTree {

    private TreeNode root = null;

    private static class TreeNode {

        /**
         * 节点值
         */
        private int value;

        /**
         * 节点颜色 true表示黑色 false表示红色
         */
        private boolean color = false;

        /**
         * 父节点
         */
        private TreeNode parent = null;

        /**
         * 左孩子
         */
        private TreeNode leftChild = null;

        /**
         * 右孩子
         */
        private TreeNode rightChild = null;

        public TreeNode(int value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TreeNode node = (TreeNode) o;

            return value == node.value && color == node.color
                    && (parent != null ? parent.equals(node.parent) :
                    node.parent == null)
                    && (leftChild != null ? leftChild.equals(node.leftChild) :
                    node.leftChild == null)
                    && (rightChild != null ? rightChild.equals(node.rightChild) :
                    node.rightChild == null);
        }

        @Override
        public int hashCode() {
            int result = value;
            result = 31 * result + (color ? 1 : 0);
            result = 31 * result + (parent != null ? parent.hashCode() : 0);
            result = 31 * result + (leftChild != null ? leftChild.hashCode() : 0);
            result = 31 * result + (rightChild != null ? rightChild.hashCode() : 0);
            return result;
        }
    }

    /**
     * 增加节点
     */
    public void add(int value) {
        addNode(new TreeNode(value));
    }

    private void addNode(TreeNode node) {
        if (root == null) {
            node.color = true;
            root = node;
        } else if (insertNode(node, root)) {
            balanceTree(node);
        }
    }

    private boolean insertNode(TreeNode insert, TreeNode treeNode) {
        if (treeNode.value == insert.value) {
            return false;
        } else if (treeNode.value < insert.value) {
            if (treeNode.rightChild == null) {
                treeNode.rightChild = insert;
                insert.parent = treeNode;
                return true;
            }
            return insertNode(insert, treeNode.rightChild);
        } else if (treeNode.value > insert.value) {
            if (treeNode.leftChild == null) {
                treeNode.leftChild = insert;
                insert.parent = treeNode;
                return true;
            }
            return insertNode(insert, treeNode.leftChild);
        }
        return true;
    }

    /**
     * 平衡当前节点
     * 默认插入节点都是红色节点
     *
     * @param node 节点
     */
    private void balanceTree(TreeNode node) {
        // 1、当前节点为根节点
        if (node.parent == null) {
            node.color = true;
            root = node;
            return;
        }

        // 2、当前节点的父节点是根节点
        if (node.parent.parent == null) {
            return;
        }

        // 3、当前节点的父节点是黑色
        if (node.parent.color) {
            return;
        }

        TreeNode parent = node.parent;
        TreeNode grantParent = parent.parent;

        // 4、当前节点是父节点的左孩子
        if (parent.equals(grantParent.leftChild)) {
            // 4.1、如果叔父节点存在且为红
            if (grantParent.rightChild != null &&
                    !grantParent.rightChild.color) {
                parent.color = true;
                grantParent.rightChild.color = true;
                grantParent.color = false;
                // 将祖父节点作为新加入节点进行平衡
                balanceTree(grantParent);
                return;
            }

            // 4.2、如果叔父节点不存在或者为黑色

            // 4.2.1、如果当前节点是父节点的右孩子
            if (node.equals(parent.rightChild)) {
                leftRound(parent);
                balanceTree(parent);
                return;
            }
            // 4.2.2、如果当前节点是父节点的左孩子
            if (node.equals(parent.leftChild)) {
                parent.color = true;
                grantParent.color = false;
                rightRound(grantParent);
                return;
            }
        }

        // 5、父节点是祖父节点的右孩子
        if (parent.equals(grantParent.rightChild)) {

            // 5.1、如果叔父节点存在且为红色
            if (grantParent.leftChild != null &&
                    !grantParent.leftChild.color) {
                grantParent.color = false;
                grantParent.leftChild.color = true;
                parent.color = true;
                // 将祖父节点作为新加入节点进行平衡
                balanceTree(grantParent);
                return;
            }

            // 5.2、如果叔父节点不存在或者为黑色

            // 5.2.1、如果节点是父节点的左孩子
            if (node.equals(parent.leftChild)) {
                rightRound(parent);
                balanceTree(parent);
                return;
            }

            // 5.2.2、如果节点是父节点的右孩子
            if (node.equals(parent.rightChild)) {
                parent.color = true;
                grantParent.color = false;
                leftRound(grantParent);
            }
        }
    }

    private void deleteNode(TreeNode node) {
        // 从当前树中寻找相等的节点
        TreeNode deleteNode = findTreeNode(node);
        if (deleteNode != null) {
            delete(deleteNode);
        }
    }

    private TreeNode findTreeNode(TreeNode deleteNode) {
        return root == null ? null : findTreeNode(deleteNode, root);
    }

    private TreeNode findTreeNode(TreeNode deleteNode, TreeNode treeNode) {
        if (deleteNode.value > treeNode.value) {
            return treeNode.rightChild != null ?
                    findTreeNode(deleteNode, treeNode.rightChild) : null;
        }
        if (deleteNode.value < treeNode.value) {
            return treeNode.leftChild != null ?
                    findTreeNode(deleteNode, treeNode.leftChild) : null;
        }
        return treeNode;
    }

    private void delete(TreeNode deleteNode) {
        // 如果删除节点是根节点，则直接将根节点置空
        if (deleteNode.equals(root)) {
            root = null;
            return;
        }
        if (deleteNode.leftChild != null && deleteNode.rightChild != null) {
            TreeNode node = findNextNode(deleteNode);
            deleteNode.value = node.value;
            deleteNode = node;
        }
        if (deleteNode.rightChild != null) {
            TreeNode parent = deleteNode.parent;
            deleteNode.rightChild.parent = parent;
            if (deleteNode.equals(parent.leftChild)) {
                parent.leftChild = deleteNode.rightChild;
            }
            if (deleteNode.equals(parent.rightChild)) {
                parent.rightChild = deleteNode.rightChild;
            }
            if(deleteNode.color){
                balanceTree(deleteNode.rightChild);
            }
        } else if (deleteNode.leftChild == null) {
            TreeNode parent = deleteNode.parent;
            if (deleteNode.equals(parent.leftChild)) {
                parent.leftChild = null;
                deleteNode = null;
            } else if (deleteNode.equals(parent.rightChild)) {
                parent.rightChild = null;
                deleteNode = null;
            }
        }


    }

    private TreeNode findNextNode(TreeNode node) {
        if (node.rightChild != null) {
            TreeNode nextNode = node.rightChild;
            while (nextNode.leftChild != null) {
                nextNode = nextNode.leftChild;
            }
            return nextNode;
        }
        TreeNode nextNode = node.parent;
        TreeNode compareNode = node;
        while (nextNode != null && compareNode.equals(node.rightChild)) {
            compareNode = nextNode;
            nextNode = nextNode.parent;
        }
        return nextNode;
    }

    private void balanceDeleteTreee(TreeNode node){

    }

    /**
     * 左旋: 当前节点左旋树的根节点
     * 1、将当前节点的左孩子移至当前节点位置，
     * 2、将当前节点的左孩子的右孩子移至当前节点的左孩子位置
     * 3、将当前节点移至当前节点左孩子的右孩子节点位置
     *
     * @param node 左旋节点
     */
    private void leftRound(TreeNode node) {
        TreeNode parent = node.parent;
        TreeNode rightChild = node.rightChild;
        TreeNode rightLeftChild = rightChild.leftChild;

        // 重链接父节点-左孩子节点
        rightChild.parent = parent;
        if (parent != null) {
            if (node.equals(parent.leftChild)) {
                parent.leftChild = rightChild;
            } else if (node.equals(parent.rightChild)) {
                parent.rightChild = rightChild;
            }
        } else {
            root = rightChild;
        }

        // 重链接当前节点和左孩子的右孩子节点
        node.rightChild = rightLeftChild;
        if (rightLeftChild != null) {
            rightLeftChild.parent = node;
        }

        // 重链接当前节点和左孩子节点
        rightChild.leftChild = node;
        node.parent = rightChild;
    }

    /**
     * 右旋:当前节点是右旋树的根节点
     * 1、将当前节点左孩子放置当前节点位置
     * 2、将当前节点的右孩子的左孩子节点链接至当前节点的右孩子位置
     * 3、将当前节点链接至当前节点左孩子位置
     *
     * @param node 右旋节点
     */
    private void rightRound(TreeNode node) {
        TreeNode parent = node.parent;
        TreeNode leftChild = node.leftChild;
        TreeNode leftRightChild = leftChild.rightChild;

        // 重链接父节点和右孩子节点
        leftChild.parent = parent;
        if (parent != null) {
            if (node.equals(parent.rightChild)) {
                parent.rightChild = leftChild;
            } else if (node.equals(parent.leftChild)) {
                parent.leftChild = leftChild;
            }
        } else {
            root = leftChild;
        }

        // 重链接右孩子的左孩子节点和当前节点
        node.leftChild = leftRightChild;
        if (leftRightChild != null) {
            leftRightChild.parent = node;
        }

        // 重链接当前节点和右孩子节点
        leftChild.rightChild = node;
        node.parent = leftChild;
    }

    @Override
    public String toString() {
        List<List<TreeNode>> floorNodes = new ArrayList<>();
        List<TreeNode> rootNodes = new ArrayList<>();
        rootNodes.add(root);
        floorNodes.add(rootNodes);
        List<TreeNode> current = rootNodes;
        while (true) {
            current = getNextFloorNode(current);
            if (current.isEmpty()) {
                break;
            }
            floorNodes.add(current);
        }

        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < floorNodes.size(); i++) {
            builder.append("第").append(i + 1).append("层:");
            for (TreeNode node : floorNodes.get(i)) {
                int mark = -1;
                if (node.parent != null && node.equals(node.parent.rightChild)) {
                    mark = 1;
                }
                if (node.parent != null && node.equals(node.parent.leftChild)) {
                    mark = 0;
                }
                builder.append("{\"v\":")
                        .append(node.value)
                        .append(",\"color\":")
                        .append(node.color)
                        .append(",\"dir\":")
                        .append(mark).append("}\t");
            }
            builder.append("\n");
        }

        return builder.toString();
    }

    private List<TreeNode> getNextFloorNode(List<TreeNode> treeNodes) {
        List<TreeNode> nextFloorNodes = new ArrayList<>();
        for (TreeNode node : treeNodes) {
            if (node.leftChild != null) {
                nextFloorNodes.add(node.leftChild);
            }
            if (node.rightChild != null) {
                nextFloorNodes.add(node.rightChild);
            }
        }
        return nextFloorNodes;
    }

    public static void main(String[] args) {
        RedBlackTree tree = new RedBlackTree();
        Random random = new Random();
        long s = System.currentTimeMillis();
        for (int i = 0; i < 100000; i++) {
            tree.add(Math.abs(random.nextInt()));
        }
        System.out.println("耗时:" + (System.currentTimeMillis() - s));
        s = System.currentTimeMillis();
        Map<Integer, Integer> map = new HashMap<>();
        for (int i = 0; i < 100000; i++) {
            int v = Math.abs(random.nextInt());
            map.put(v, v);
        }
        System.out.println("耗时:" + (System.currentTimeMillis() - s));
    }

}
