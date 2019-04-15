/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

/**
 *
 * @author smm-pc
 */
public class DijkstraAlgorithm {

    private final List<Edge> edges;
    private List<Vertex> vertexes;
    private Set<Vertex> settledNodes;
    private Map<Vertex, Vertex> paths;//
    PriorityQueue<Vertex> unsettledNodes;

    public DijkstraAlgorithm(Graph graph) {
        // create a copy of the array so that we can operate on this array
        this.edges = new ArrayList<>(graph.getEdges());
        this.vertexes = new ArrayList<>(graph.getVertexes());
    }

    public void execute(Vertex source) {
        settledNodes = new HashSet<>();
        paths = new HashMap<>();
        vertexes.get(vertexes.indexOf(source)).setDistance(0);
        unsettledNodes = new PriorityQueue<>();
        unsettledNodes.add(source);
//
        while (!unsettledNodes.isEmpty()) {//
            //System.out.println(unSettledNodes.size());

            Vertex node = unsettledNodes.poll();
            settledNodes.add(node);
            findSmallestDistances(node);
            //System.out.println(unSettledNodes.size());
        }
    }

    //find closest vertex for unsettled nodes
    private void findSmallestDistances(Vertex node) {
        List<Vertex> adjacentNodes = getNeighbors(node);
        // System.out.println(adjacentNodes.size() + " many");
        for (int i = 0; i < adjacentNodes.size(); i++) {
            if (getShortestDistance(adjacentNodes.get(i)) > getShortestDistance(node) + getDistance(node, adjacentNodes.get(i))) {
                vertexes.get(vertexes.indexOf(adjacentNodes.get(i))).setDistance(getShortestDistance(node) + getDistance(node, adjacentNodes.get(i)));
                paths.put(adjacentNodes.get(i), node);
                //System.out.println("PUTTING: " + target.getName() + ", " + node.getName());
                unsettledNodes.add(adjacentNodes.get(i));
            }
        }

    }//concurent? prob

    //returns the weight between two vertexes
    private double getDistance(Vertex node, Vertex target) {
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && edge.getDestination().equals(target)) {
                return edge.getWeight();
            }
        }
        throw new RuntimeException("ERROR");//prob wont happen
    }

    //gets a list of all neighbors to a vertex that are not settled
    private List<Vertex> getNeighbors(Vertex node) {
        List<Vertex> neighbors = new ArrayList<>();
        //System.out.println("neighbors for " + node.getName());
        for (Edge edge : edges) {
            if (edge.getSource().equals(node) && !isSettled(edge.getDestination()) && !unsettledNodes.contains(edge.getDestination()) && !neighbors.contains(edge.getDestination())) {
                neighbors.add(edge.getDestination());
            }
        }
        return neighbors;
    }

    //returns true if settled
    private boolean isSettled(Vertex vertex) {//returns if node settled
        return settledNodes.contains(vertex);
    }

    ////returns max value if a distance has not been found yet, otherwise returns the distance recorded in the map
    private double getShortestDistance(Vertex destination) {
        double distance = vertexes.get(vertexes.indexOf(destination)).getDistance();
        return distance;
    }


    /*
     * This method returns the path from the source to the selected target and
     * NULL if no path exists
     */
    public LinkedList<Vertex> getPath(Vertex target) {

        LinkedList<Vertex> path = new LinkedList<>();
        Vertex step = target;

        // check if a path exists
        if (paths.get(step) == null) {
            return null;
        }
        path.add(step);
        while (paths.get(step) != null) {
            step = paths.get(step);
            path.add(step);
        }
        // Put it into the correct order
        Collections.reverse(path);
        return path;
    }
}
