package com.galaxy.asteroid.calculate;

import com.galaxy.asteroid.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @Author: 蔡月峰
 * @Version： 1.0
 * @Description: PageRank算法
 * @Date : Create in 13:56 2019/4/14
 * @Modified By:
 */
public class PageRank {

    /**
     * 最大迭代次数
     */
    private static final int MAX_ITERATOR_NUM = 100;

    /**
     * 影响因子
     */
    private static final double FACTOR = 0.85;

    private static class Node {
        /**
         * 当前节点PR值
         */
        private double pr;

        /**
         * 入度集合
         */
        private Set<Integer> inDegree = new HashSet<>();

        /**
         * 出度集合
         */
        private Set<Integer> outDegree = new HashSet<>();
    }


    private static class Graph {

        private Map<Integer, Node> nodeCollector = new HashMap<>();

        private void addEdge(int fid, int sid) {
            Node fNode = nodeCollector.get(fid);
            if (fNode == null) {
                fNode = new Node();
            }
            Node sNode = nodeCollector.get(sid);
            if (sNode == null) {
                sNode = new Node();
            }
            fNode.outDegree.add(sid);
            sNode.inDegree.add(fid);
            nodeCollector.put(fid, fNode);
            nodeCollector.put(sid, sNode);
        }
    }

    private static Map<Integer, Double> pageRank(Graph graph) {

        System.out.println("本次处理:"+graph.nodeCollector.size());
        // 初始化图
        System.out.println("初始化图...");

        double initPr = 1.0 / graph.nodeCollector.size();
        for (Map.Entry<Integer, Node> entry : graph.nodeCollector.entrySet()) {
            Node node = entry.getValue();
            // 初始化PR值
            node.pr = initPr;

            // 如果出度为空，则将该页面指向所有页面
            if (node.outDegree.isEmpty()) {
                for (Map.Entry<Integer, Node> temp : graph.nodeCollector.entrySet()) {
                    if (temp.getKey().equals(entry.getKey())) {
                        continue;
                    }
                    Node update = temp.getValue();
                    update.inDegree.add(entry.getKey());
                    node.outDegree.add(temp.getKey());
                    graph.nodeCollector.put(temp.getKey(), update);
                }
            }
            graph.nodeCollector.put(entry.getKey(), node);
        }
        System.out.println("初始化完成");
        double randomPr = (1 - FACTOR) / graph.nodeCollector.size();

        Graph currentGraph = graph;
        for (int i = 0; i < MAX_ITERATOR_NUM; i++) {
            Graph newGraph = new Graph();
            double change = 0.0;
            System.out.println("开始第"+(i+1)+"次迭代");
            for (Map.Entry<Integer, Node> entry : currentGraph.nodeCollector.entrySet()) {
                Node oldNode = entry.getValue();
                Node newNode = new Node();
                newNode.outDegree.addAll(oldNode.outDegree);
                newNode.inDegree.addAll(oldNode.inDegree);
                double pr = 0.0;
                // 计算当前节点PR值
                for (Integer inNodeId : oldNode.inDegree) {
                    Node inNode = currentGraph.nodeCollector.get(inNodeId);
                    pr += FACTOR * (inNode.pr / inNode.outDegree.size());
                }
                pr += randomPr;
                newNode.pr = pr;
                // 将结果加入新图
                newGraph.nodeCollector.put(entry.getKey(), newNode);
                change += Math.abs(pr - currentGraph.nodeCollector.get(entry.getKey()).pr);
            }
            currentGraph = newGraph;
            System.out.println("当前迭代第" + (i + 1) + "次完成");
            if (change < 0.00001) {
                System.out.println("已经达到期望精度:" + change + "\t迭代退出");
                break;
            }
        }

        Map<Integer, Double> result = new HashMap<>();
        for (Map.Entry<Integer, Node> entry : currentGraph.nodeCollector.entrySet()) {
            result.put(entry.getKey(), entry.getValue().pr);
        }
        return result;
    }


    public static void main(String[] args) throws IOException {
        Graph graph = new Graph();

        List<String> lines = FileUtils.listLines(new File("D:\\WorkSpace\\Galaxy\\Galaxy\\asteroid\\input\\Cit-HepTh.txt"));

        for (String line : lines) {
            String[] values = line.split("\t");
            graph.addEdge(Integer.parseInt(values[0]), Integer.parseInt(values[1]));
        }
        lines.clear();
//        graph.addEdge(1, 2);
//        graph.addEdge(1, 3);
//        graph.addEdge(1, 4);
//        graph.addEdge(2, 4);
//        graph.addEdge(3, 5);
//        graph.addEdge(4, 5);
//        graph.addEdge(2, 5);
//        graph.addEdge(5, 1);

        Map<Integer, Double> result = pageRank(graph);
        int maxId = 0;
        double maxScore = 0.0;
        for (Map.Entry<Integer, Double> entry : result.entrySet()) {
            if (maxScore < entry.getValue()) {
                maxId = entry.getKey();
                maxScore = entry.getKey();
            }
        }
        System.out.println("节点:" + maxId + "\tPR值:" + maxScore);

    }

}

