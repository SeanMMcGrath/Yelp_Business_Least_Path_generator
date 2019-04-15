/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author smm-pc
 */
public class Graph {

    private final List<Vertex> vertexes;
    private final List<Edge> edges;
    private ArrayList<List<Vertex>> disjointSets;

    public Graph(List<Vertex> vertexes, List<Edge> edges) {
        this.vertexes = vertexes;
        this.edges = edges;
    }

    public List<Vertex> getVertexes() {
        return vertexes;
    }

    public List<Edge> getEdges() {
        return edges;
    }

    public ArrayList<List<Vertex>> getDisjointSets() {
        return disjointSets;
    }

    public int findDisjointSets() {//returns the num of disjoint sets
        disjointSets = new ArrayList<>();

        if (edges == null) {
            throw new RuntimeException("Should not happen");
        } else {
            ArrayList<Vertex> alreadyFound = new ArrayList<>();
            for (Edge e : edges) {
                List<Vertex> set = getSet(e, alreadyFound);
                if (set != null) {

                    //System.out.println("new set");
                    for (Vertex c : set) {
                        int count = 0;
                        if (alreadyFound.contains(c)) {
                            if (count > 0) {//if the set is already found, any vertex in it will already be contained, so v>0 shouldnt happen
                                System.out.println("PROBLEM-------------------------------------------");
                            } else {
                                System.out.println("test");
                            }
                            // break;//break if this set is already a set we have found
                        } else {
                            alreadyFound.add(c);
                        }
                        count++;
                    }
                    disjointSets.add(set);
                } //  System.out.println("already found..."); //else { found already }  //move on to next possible set
            }
        }
        //System.out.println("DISJOINT SET NUM: " + disjointSets.size());//if == 0 we good

        ArrayList<List<Vertex>> duplicates = new ArrayList<>();
        for (List<Vertex> list : disjointSets) {
            //oneWay = false;
            //bothWays = false;
            for (List<Vertex> list2 : disjointSets) {
                if (list.size() == list2.size() && (!duplicates.contains(list) || !duplicates.contains(list2))) {//cant be equal, or alredy found
                    if (disjointSets.indexOf(list) == disjointSets.indexOf(list2)) {//completely equal, ignore
                        //
                    } else {

                        if (list.containsAll(list2) && list2.containsAll(list) && (!duplicates.contains(list) || !duplicates.contains(list2))) {
                            duplicates.add(list);
                            System.out.println("DUPLICATE----------------");
                        }
                    }
                } else {
                    //cant be equal check subset
                    if (list.containsAll(list2) || list2.containsAll(list)) {
                        System.out.println("expected subset error exists");
                        if (list.containsAll(list2)) {
                            duplicates.add(list2);
                        } else if (list2.containsAll(list)) {
                            duplicates.add(list);
                        }
                        if (list.containsAll(list2) && list2.containsAll(list)) {
                            System.out.println("DUPLICATE AND SUBSSET??? NO?");
                        }
                    }

                }
            }
//            System.out.println("DOES ONE RUN KILL IT?" + duplicates.size());

        }
        for (List<Vertex> dupli : duplicates) {
            System.out.println("dupli size: " + duplicates.size());
            if (disjointSets.contains(dupli)) {
                disjointSets.remove(dupli);
                //break;
            } else {
                System.out.println("HOHdfsfsdfsd");
            }
            System.out.println("disjount size post removal: " + disjointSets.size());
        }
        System.out.println("DISJOINT SET NUM POST REDUCTION: " + disjointSets.size());
        return disjointSets.size();
    }

    private List<Vertex> getNeighbors(Set<Vertex> nodes) {//gets all neighbors for each vertex in nodes
        List<Vertex> neighbors = new ArrayList<>();
        for (Vertex vertex : nodes) {
            for (Edge edge : edges) {
                if (edge.getSource().equals(vertex)) {//&& !neighbors.contains(edge.getDestination())) {
                    neighbors.add(edge.getDestination());
                    //      System.out.println("source of " + edge.getSource()+" neighbor, " + edge.getDestination().getName() + " added");
                } else if (edge.getDestination().equals(vertex)) {
                    //   System.out.println("possible one way from " + edge.getSource());
                }
            }
        }
        return neighbors;
    }

    private List<Vertex> getSet(Edge e, ArrayList<Vertex> alreadyFound) {//returns the disjoint set connected to edge e, uses disjointedSets to first check if this is already found if so return null
        if (alreadyFound.contains(e.getSource())) {//duplicate
            return null;
        }

        List<Vertex> settledNodes = new ArrayList<>();
        Set<Vertex> unSettledNodes = new HashSet<>();
        unSettledNodes.add(e.getSource());
        while (unSettledNodes.size() > 0) {
            //System.out.println(unSettledNodes.size());
            List<Vertex> nodes = getNeighbors(unSettledNodes);
            for (Vertex used : unSettledNodes) {//tell prog they've been used now
                settledNodes.add(used);
            }
            for (Vertex used : settledNodes) {//cleaning up old nodes
                if (unSettledNodes.contains(used)) {
                    unSettledNodes.remove(used);
                }
            }
            if (unSettledNodes.size() > 0) {
                System.out.println("-------------------------------------------------");
            }
            for (Vertex vertex : nodes) {
                if (!settledNodes.contains(vertex) && !unSettledNodes.contains(vertex)) {//&& !alreadyFound.contains(vertex)) {//not been used b4, might never happen since how get neighbors works idk
                    unSettledNodes.add(vertex);//adding every newly encountered node
                    //    System.out.println("Adding from neighbors " + vertex.getName());
                } else {
                    //   System.out.println("Not adding the duplicate " + vertex.getName());
                    //System.out.println("prob cant happen? we see");//maybe can happen i think i was wrong
                }
            }
        }

        for (Vertex test : settledNodes) {
            int count = 0;
            for (Vertex testIn : settledNodes) {
                if (test.equals(testIn)) {
                    count++;
                }
            }
            if (count != 1) {
                //  System.out.println("COUNT IS WEIRD? : " + count);
            }
        }
        System.out.println("number of vertexes being inserted: " + settledNodes.size());
        return settledNodes;//should be no duplicates???
    }

    public ArrayList<Edge> connectSets() {//connects disjointSets.Size() number of disjoint sets by connecting their closest points in two way connected edges    returns all the new edges to be written in main
        //do a double for loop using disjointed sets lists, for each list n there should be 2*(n^n) new edges (2* because they are both ways) so 2*87^87???
        System.out.println("BEGGIN CONNECTING--->");
        ArrayList<Edge> newEdges = new ArrayList<>();
        for (List<Vertex> list1 : disjointSets) {//#87 originally
            for (List<Vertex> list2 : disjointSets) {//two new directed edges per second forloop
                if (!list1.equals(list2)) {
                    Vertex currentClosestO = null;
                    Vertex currentClosestI = null;
                    double distance = -1; //placeholder values
                    for (Vertex currentOuter : list1) {
                        for (Vertex currentInner : list2) {
                            double temp = getDistance(currentOuter.getLat(), currentOuter.getLon(), currentInner.getLat(), currentInner.getLon());
                            if (temp != 0.0) {
                                if (currentClosestO == null && currentClosestI == null) {
                                    currentClosestO = currentOuter;
                                    currentClosestI = currentInner;
                                    distance = temp;
                                } else if (temp < distance) {//if pair is closes than current pair(and not same)
                                    currentClosestO = currentOuter;
                                    currentClosestI = currentInner;
                                    distance = temp;
                                }
                            }
                        }
                    }
                    //create new edges, sout checking if connection already exists(shouldnt)
                    Edge oneWay = new Edge(currentClosestO,currentClosestI,distance);
                    Edge wayBack = new Edge(currentClosestI,currentClosestO, distance);
                    if (edges.contains(oneWay)||edges.contains(wayBack)) {
                        System.out.println("CONNECTION ALREADY EXISTS ERROR");
                    }
                    if(!newEdges.contains(oneWay)){
                        newEdges.add(oneWay);
                    }
                    if(!newEdges.contains(wayBack)){//will be done twice, try to fix this instead of this temp fix to save time...  maybe map of lists that have already been used???
                        newEdges.add(wayBack);
                    }
                }
            }
        }

        System.out.println("NUM OF NEW EDGES FOR CONNECTIONS: " + newEdges.size());
        return newEdges;
    }

    public boolean validateDisjointSets() {//returns true if disjoint set algorithms are working corectly
        int check = 0;
        for (List<Vertex> list : disjointSets) {
            check = check + list.size();
        }
        System.out.println("total num of vertexes throughout disjointSets: " + check);
        for (List<Vertex> list : disjointSets) {
            check = 0;
            for (Vertex vertex : list) {
                for (Vertex vertex2 : list) {//checks if there are more than one of the same vertex in a single list for debugging
                    if (vertex.equals(vertex2)) {
                        check++;
                    }
                }
            }
            if (check > 0) {
                //        System.out.println("this list is bugged: " + check);
            }
        }

        for (List<Vertex> list : disjointSets) {
            for (Vertex vertex : list) {
                int count = 0;
                for (List<Vertex> listOfLists2 : disjointSets) {
                    for (Vertex vertex2 : listOfLists2) {
                        if (vertex.equals(vertex2)) {
                            count++;
                        }
                    }
                }
                if (count == 1) {
                    //good
                    //          System.out.println("good count");
                } else {
                    //           System.out.println("badcount: " + count);
                    return false;//bad     0 == this isnt working   >1 == disjoint set structure not working since there are reused vertexes
                }
            }
        }
        return true;
    }

    static double getDistance(double startLat, double startLong, double endLat, double endLong) {//haversine
        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double end = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * end;
    }

    static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }

    private void resetDisjointSets() {//maybe useless
        disjointSets = new ArrayList<>();
    }
}
