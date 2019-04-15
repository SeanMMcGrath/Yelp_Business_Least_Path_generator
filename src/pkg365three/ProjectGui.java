/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

/**
 *
 * @author smm-pc
 */
public class ProjectGui extends javax.swing.JFrame {

    /**
     * @param args the command line arguments
     * @throws java.io.FileNotFoundException
     */
    static Parser c;
    static Btree b;
    static Kmeans km;
    static ArrayList<Data> d;
    static ArrayList<Vertex> v;
    static Graph graph;
    static DijkstraAlgorithm dijkstra;

    public ProjectGui() {
        initComponents();
    }

    private void initComponents() {
        frame = new JFrame("CSC365_Project");
        frame.setSize(300, 300);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setLocation(dim.width / 2 - frame.getSize().width / 2, dim.height / 2 - frame.getSize().height / 2);

        container = frame.getContentPane();
        container.setLayout(null);
        Insets insets = frame.getInsets();
        container.setBackground(Color.WHITE);

        drop = new JComboBox(graph.getVertexes().toArray());
        drop.setBounds(20 + insets.left, 80 + insets.top, 200, 20);

        searchButton = new JButton("GRAPH");
        searchButton.setBounds(20 + insets.left, 20 + insets.top, 200, 40);
        searchButton.addActionListener((ActionEvent e) -> {
            LinkedList<Vertex> result;
            try {
                selected.setText("");
                tow.setText("");
                System.out.println("selected " + graph.getVertexes().get(drop.getSelectedIndex()).getName() + "?");
                selected.setText("From: " + graph.getVertexes().get(drop.getSelectedIndex()).getName());
                result = runShortestPath(graph.getVertexes().get(drop.getSelectedIndex()), graph); //
                if (result == null) {
                    System.out.println("NO PATH FOUND");//huge error
                } else {
                    GraphDisplay gd = new GraphDisplay();
                    gd.graphing(result);
                    System.out.println(result.toString());
                    for (Vertex p : result) {
                        System.out.println(p.getName() + ",    " + p.getId());//testing path
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ProjectGui.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ProjectGui.class.getName()).log(Level.SEVERE, null, ex);
            }
            drop.setSelectedIndex(0);
        });

        selected = new JLabel("");
        selected.setBounds(insets.left, 130 + insets.top, 300, 60);
        tow = new JLabel("");
        tow.setBounds(insets.left, 200 + insets.top, 300, 60);

        container.add(selected);
        container.add(tow);
        container.add(searchButton);
        container.add(drop);
        frame.setVisible(true);
    }

    public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException, Exception {

        c = new Parser();

        File f = new File("Root_file");
        File f2 = new File("Nodes_file");
        if (!f.exists() || !f2.exists()) {
            System.out.println("CREATING AND PRELOADING BTREE :D");
            d = c.load();//then go through this array adding to btree
            b = new Btree();
            for (Data da : d) {
                b.insert(da);////
            }
            b.diskWriteRoot();
            System.out.println(b.size);
            System.out.println("SAVING BTREE TO FILE");
        } else {
            b = new Btree();
            b = b.diskReadRoot();
            d = b.obtainData();
        }
        System.out.println(d.size());
        //read and write points for algorithm
        km = new Kmeans();
        for (Data da : d) {
            km.addPoint(da);
        }
        km.clusteringMain(5, 15);

        graph = setupGraph();
        System.out.println("There are " + graph.getEdges().size() + " edges connecting " + graph.getVertexes().size() + " vertexes");
        ProjectGui p = new ProjectGui();
    }

    //private static List<Vertex> nodes;
    public static List<Vertex> setupVertexes() {
        //put edge and vertex creation//return vertecies??? or edges, just do verticies i think
        //nodes = km.get("points");
        List<Data> data = d;//use data to make vertexes

        List<Vertex> vertexList = new ArrayList<>();
        for (Data da : data) {
            Vertex temp = new Vertex(da.id, da.name, da.lattitude, da.longitude);
            vertexList.add(temp);
        }
        return vertexList;

    }//save time

    public static List<Edge> setupEdges(List<Vertex> vertexes) throws IOException, FileNotFoundException, ClassNotFoundException {
        File edgeFile = new File("Edge_file");
        List<Edge> edges;

        if (edgeFile.exists()) {
            //load edges
            System.out.println("LOADING EDGES");
            edges = loadEdges(edgeFile);
        } else {
            //make edges and serialize
            System.out.println("CREATING EDGES");
            edges = createEdges(edgeFile, vertexes);
        }
        return edges;
    }

    //graph creator?? THEN do run shortes path
    public static Graph setupGraph() throws IOException, FileNotFoundException, ClassNotFoundException {
        List<Vertex> vertexes = setupVertexes();
        List<Edge> edges = setupEdges(vertexes);
        Graph graph = new Graph(vertexes, edges);

        int numOfDisjoint = graph.findDisjointSets();
        System.out.println(numOfDisjoint);
        if (numOfDisjoint > 1) {//meaning there are disjoint sets present, so need connections
            if (graph.validateDisjointSets()) {
                List<Edge> newEdges = graph.connectSets();
                System.out.println("VALIDATED");
                for (Edge e : newEdges) {
                    edges.add(e);//add these new connecting edges
                }
                writeEdges(new File("Edge_file"), edges);//write all edges together now
                graph = new Graph(vertexes, edges);//make new graph after connections made
                System.out.println(graph.findDisjointSets());//if this is not 1 then there is an error somewhere//0 means big error of no connection???//more than 1 means there are still unconnected sets
            } else {
                System.out.println("huh");//big problem
            }
        } else {
            System.out.println("No disjoint sets pressent anymore");//should happen only if the prog has been run b4 to preload
        }

        return graph;
    }

    public static LinkedList<Vertex> runShortestPath(Vertex from, Graph graph) throws IOException, FileNotFoundException, ClassNotFoundException {

        dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(from);

        ArrayList<Kmeans.temp> centroids = km.get("centroids");
        LinkedList<Vertex> result;
        Vertex to;
        //go through each cluster starting at closest -> farthest
        //if null goto next closest cluster center
        //if all are null then fuck me right?
        ArrayList<Kmeans.temp> used = new ArrayList<>();

        double currentDist;
        Kmeans.temp currentSpot;
        for (int i = 0; i < centroids.size(); i++) {
            to = null;
            currentSpot = null;
            currentDist = -1;
            for (Kmeans.temp centroid : centroids) {//find closest centroid not in used    //then find vertex for that centroid use for algorithm
                double dist = getDistance(from.getLat(), from.getLon(), centroid.d.lattitude, centroid.d.longitude);
                if (!used.contains(centroid)) {//not already tried, and closer than last one///possilbe null in current spot?
                    if (currentSpot == null) {
                        currentSpot = centroid;
                        currentDist = dist;
                    }
                    if (dist < currentDist) {
                        currentSpot = centroid;
                        currentDist = dist;
                    }
                }
            }
            used.add(currentSpot);//found closest
            //find vertex that is related to closest
            if (currentSpot != null) {
                for (Vertex temp : graph.getVertexes()) {
                    if (temp.getId().equals(currentSpot.d.id) && temp.getName().equals(currentSpot.d.name)) {
                        //  System.out.println("cluster workin");
                        to = temp;//find the vertex that is the cluster center//|\\
                        break;
                    }
                }
            } else {
                System.out.println("should not happen...");
            }
            if (to == null) {
                System.out.println("not working...");
            }
            tow.setText("To: " + to.getName());

            //found closest now try algorithm
            System.out.println("FROM " + from.getName() + " TO " + to.getName());
            result = dijkstra.getPath(to);

            if (result != null) {
                return result;//path exists we done
            }//else {redo} zzz
        }
        //do shortest path algorithm using from and to
        return null;
    }

    public static List<Edge> loadEdges(File f) throws FileNotFoundException, IOException, ClassNotFoundException {
        List<Edge> edges = new ArrayList<>();
        FileInputStream fi = new FileInputStream(f);
        ObjectInputStream oi = new ObjectInputStream(fi);

        int edgeNum = oi.readInt();//how many edges to ready
        for (int i = 0; i < edgeNum; i++) {//4*number of points = 40000 is original num
            Edge temp = (Edge) oi.readObject();
            edges.add(temp);
        }

        oi.close();
        fi.close();

        return edges;
    }

    public static List<Edge> createEdges(File f, List<Vertex> vs) throws IOException {//create and write to file
        List<Edge> edges = new ArrayList<>();
        int count = 0;
        for (Vertex vertex : vs) {
            Vertex[] closest = new Vertex[5];
            closest[4] = vertex;//so no repeats 5th is itself
            closest[0] = getClosest(vs, vertex);
            closest[1] = getClosestMinus(vs, vertex, closest);
            closest[2] = getClosestMinus(vs, vertex, closest);
            closest[3] = getClosestMinus(vs, vertex, closest);
            //use closest[] to make 4 edges and write them
            for (int i = 0; i < closest.length - 1; i++) {//after writing, add it edges
                Edge temp = new Edge(vertex, closest[i], getDistance(vertex.getLat(), vertex.getLon(), closest[i].getLat(), closest[i].getLon()));
                edges.add(temp);
            }
            System.out.println(count++);
        }
        ArrayList<Edge> newEdges = new ArrayList<>();
        for (Edge e : edges) {
            boolean exists = false;
            for (Edge e2 : edges) {
                if (e.getSource().equals(e2.getDestination()) && e.getDestination().equals(e2.getSource())) {
                    exists = true;
                }
            }
            if (!exists) {
                Edge edge = new Edge(e.getDestination(), e.getSource(), getDistance(e.getDestination().getLat(), e.getDestination().getLon(), e.getSource().getLat(), e.getSource().getLon()));
                newEdges.add(edge);
            }
        }
        for (Edge e : newEdges) {
            edges.add(e);
        }
        writeEdges(f, edges);
        System.out.println("num of edges: " + edges.size());
        return edges;
    }

    public static Vertex getClosest(List<Vertex> ps, Vertex p) {//finds closest point to p///that doesnt already exist as an edge
        Vertex closest = null;
        double closeness = -1;
        for (Vertex current : ps) {
            if (!current.getName().equals(p.getName()) && !current.getId().equals(p.getId())) {
                double temp = getDistance(p.getLat(), p.getLon(), current.getLat(), current.getLon());
                if (temp != 0.0) {
                    if (closest == null) {
                        closest = current;
                        closeness = temp;
                    } else {
                        if (temp < closeness) {
                            closest = current;
                            closeness = temp;
                        }
                    }
                }
            }
        }
        return closest;
    }

    public static Vertex getClosestMinus(List<Vertex> ps, Vertex p, Vertex[] closest) {//finds closest point to p that is not in closest[]
        Vertex currentClosest = null;
        double closeness = -1;
        for (Vertex current : ps) {
            double temp = getDistance(p.getLat(), p.getLon(), current.getLat(), current.getLon());
            if (temp != 0.0) {
                if (currentClosest == null && !current.getId().equals(p.getId())) {
                    currentClosest = current;
                    closeness = temp;
                } else {
                    if (temp < closeness) {
                        boolean duplicate = false;
                        for (int i = 0; i < closest.length; i++) {
                            if (closest[i] != null) {
                                if (closest[i].equals(current)) {
                                    duplicate = true;
                                }
                            }
                        }
                        if (!duplicate) {
                            currentClosest = current;
                            closeness = temp;
                        }
                    }

                }
            }
        }
        return currentClosest;
    }

    /**
     *
     * @param f
     * @param edgelist
     * @throws FileNotFoundException
     * @throws IOException
     */
    public static void writeEdges(File f, List<Edge> edgelist) throws FileNotFoundException, IOException {

        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream o = new ObjectOutputStream(fos);

        o.writeInt(edgelist.size());//used for reading, so we know how many edges need to be read
        for (Edge e : edgelist) {
            // Write objects to file
            o.writeObject(e);
        }
        o.close();
        fos.close();

    }

//    private static int getSimilarity(Vertex x, Vertex y) {//euclidean for weights
//        double euclidean = Math.sqrt(Math.pow(x.getLat() - y.getLat(), 2) + Math.pow(x.getLon() - y.getLon(), 2));
//        System.out.println("EU : " + euclidean);
//        System.out.println("MODED EU:");
//        return (int)euclidean;
//    }
    static double getDistance(double startLat, double startLong, double endLat, double endLong) {//haversine
        final int EARTH_RADIUS = 6371;

        double dLat = Math.toRadians((endLat - startLat));
        double dLong = Math.toRadians((endLong - startLong));

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = haversin(dLat) + Math.cos(startLat) * Math.cos(endLat) * haversin(dLong);
        double end = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * end; // <-- d
    }

    static double haversin(double val) {
        return Math.pow(Math.sin(val / 2), 2);
    }
    private static javax.swing.JFrame frame;
    private static Container container;
    private static JComboBox drop;
    private static javax.swing.JButton searchButton;
    private static javax.swing.JLabel selected;
    private static javax.swing.JLabel tow;
}

//to-do
//1. make drop down in alphabetical order//last priotiry
