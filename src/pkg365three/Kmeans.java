/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author smm-pc
 */
public class Kmeans {

    // **************************************************
    // Classes
    // **************************************************
    public class temp {

        Data d;
        int cluster;//which cluster this is in
        final private String id;
        final private String name;

        /**
         *
         * @param d - the data that this point represents
         */
        public temp(Data d) {
            this.d = d;
            id = null;
            name = null;
        }

        public temp(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

    }

    /**
     * //represents a K-means cluster of quantity K, that holds points
     */
    public class Cluster {

        ArrayList<temp> points;//points in cluster
        temp centroid;
        int id;//a number representing which cluster this is

        /**
         *
         * @param n - the number of which cluster this is for computational work
         */
        public Cluster(int n) {
            id = n;
            points = new ArrayList<>();
            centroid = null;
        }
    }
    // **************************************************
    // Fields
    // **************************************************
    int maxClusters;
    ArrayList<temp> points;
    ArrayList<temp> pointsArchive;
    ArrayList<Cluster> clusters;
    ArrayList<temp> centroids;

    // **************************************************
    // Constructors
    // **************************************************
    /**
     *
     */
    public Kmeans() {
        points = new ArrayList<>();
        clusters = new ArrayList<>();
        centroids = new ArrayList<>();
        pointsArchive = new ArrayList<>();
    }

    // **************************************************
    // Private Methods
    // **************************************************
    private double distance(temp x, temp y) {//using squared euclidean distance, smaller is closer
        // (lat1 - lat2)^2 + (lon1 - lon2)^2
        double dist = Math.pow(x.d.lattitude - y.d.lattitude, 2) + Math.pow(x.d.longitude - y.d.longitude, 2);
        return dist;
    }

    private double distance(double lat, double lon, temp y) {//using squared euclidean distance, smaller is closer////for averaging
        // (lat1 - lat2)^2 + (lon1 - lon2)^2
        double dist = Math.pow(lat - y.d.lattitude, 2) + Math.pow(lon - y.d.longitude, 2);
        return dist;
    }

    private void reCenter() {

        ArrayList<temp> oldCenters = centroids;
        ArrayList<temp> newCenters = new ArrayList<>();
        ArrayList<temp> oldPoints = points;
        ArrayList<temp> newPoints = new ArrayList<>();
        ArrayList<Cluster> oldClusters = clusters;
        ArrayList<Cluster> newClusters = new ArrayList<>();
        for (int i = 0; i < maxClusters; i++) {
            Cluster temp = new Cluster(i);
            newClusters.add(temp);
        }

        for (int i = 0; i < oldCenters.size(); i++) {
            double averageLat = 0;
            double averageLong = 0;
            for (temp p : oldClusters.get(oldCenters.get(i).cluster).points) {//go through the cluster's points that the centroid belongs to
                averageLat = averageLat + p.d.lattitude;
                averageLong = averageLong + p.d.longitude;
            }
            averageLat = averageLat + oldCenters.get(i).d.lattitude;
            averageLong = averageLong + oldCenters.get(i).d.longitude;

            averageLat = averageLat / oldClusters.get(oldCenters.get(i).cluster).points.size() + 1;
            averageLong = averageLong / oldClusters.get(oldCenters.get(i).cluster).points.size() + 1;

            ArrayList<temp> temp = new ArrayList<>();
            for (temp p : oldPoints) {
                if (p.cluster == i) {
                    temp.add(p);//collects all the points for the current cluster to find new center
                }
            }

            temp.add(oldCenters.get(i));//includes 

            temp newCenter = new temp(points.get(1).d);//to get away with not initializing, just give random starting data obj. itll go away!!!!1maybe break
            double currentDistance = 999999999;

            for (temp p : temp) {
                if (distance(averageLat, averageLong, p) < currentDistance) {
                    newCenter = p;
                    /////////instead of removing, only add 'new centers' and a filtered ver.(using .equals(d.id))(<- towwards new centers) of old points so that it wont break...///
                    currentDistance = distance(averageLat, averageLong, p);
                }
            }
            newCenter.cluster = oldCenters.get(i).cluster;
            newCenters.add(newCenter);
        }//got all centroids now!

        //build points
        for (temp p : oldCenters) {
            if (!newCenters.contains(p)) {
                newPoints.add(p);
            }
        }
        for (temp p : oldPoints) {
            if (!newCenters.contains(p)) {
                newPoints.add(p);
            }
        }
        //now rebuild newClusters
        for (temp p : newCenters) {//setCentroids
            newClusters.get(p.cluster).centroid = p;
        }
        for (temp p : newPoints) {//readd the changed points
            newClusters.get(p.cluster).points.add(p);
        }

        points = newPoints;
        centroids = newCenters;
        clusters = newClusters;

    }

    private void clustering() {

        //put all points in their closest cluster
        ArrayList<temp> newPoints = new ArrayList<>();
        ArrayList<Cluster> oldClusters = clusters;
        ArrayList<Cluster> newClusters = new ArrayList<>();
        for (int i = 0; i < maxClusters; i++) {
            Cluster temp = new Cluster(i);
            newClusters.add(temp);
        }
        ArrayList<temp> newCenters = new ArrayList<>();
        for (int i = 0; i < maxClusters; i++) {
            newCenters.add(oldClusters.get(i).centroid);
        }

        for (int i = 0; i < points.size(); i++) {

            int indexOfClosest = 0;//starts at 0 as a default, closest cluster
            double closestDistance = Integer.MAX_VALUE;//nothing SHOULD be this far ever

            for (temp centroid : newCenters) {
                if (points.get(i).d == null) {
                    System.out.println(i + " is broke...");
                }
                if (this.distance(centroid, points.get(i)) < closestDistance) {
                    indexOfClosest = centroid.cluster;//0-4
                    closestDistance = distance(centroid, points.get(i));
                }
            }
            temp p = new temp(points.get(i).d);
            p.cluster = indexOfClosest;
            newPoints.add(p);
        }
        //now add all out points to their new cluster
        for (int i = 0; i < newPoints.size(); i++) {
            newClusters.get(newPoints.get(i).cluster).points.add(newPoints.get(i));
        }
        clusters = newClusters;
        points = newPoints;
        centroids = newCenters;

        reCenter();
        System.out.println("END CLUSTERING");
    }


    // **************************************************
    // Public Methods
    // **************************************************
    /**
     *
     * @param n - how many times to cluster
     * @param k - max number of clusters defaulting to 5
     */
    public void clusteringMain(int n, int k) {
        maxClusters = k;
        points = new ArrayList<>();
        for (temp p : pointsArchive) {
            points.add(p);
        }
        //create max num of clusters
        for (int i = 0; i < maxClusters; i++) {
            Cluster c = new Cluster(i);
            clusters.add(c);
        }
        //set initial centroids randomly from points
        Random randomGenerator = new Random();
        int index;
        for (int i = 0; i < maxClusters; i++) {
            boolean redo;
            do {
                index = randomGenerator.nextInt(points.size());
                redo = false;
                temp p = points.get(index);
                if (this.centroids.contains(p)) {
                    redo = true;//highly unlikely
                } else {
                    p.cluster = i;//says its from its own cluster
                    clusters.get(i).centroid = p;
                    centroids.add(p);
                    points.remove(points.get(index));//no longer a point, now a cluster

                }
            } while (redo);
        }

        //cluster
        for (int i = 0; i < n; i++) {
            this.clustering();
        }

        int count = 0;
        for (Cluster c : clusters) {
            count = count + c.points.size();
        }

    }

    /**
     * used to fill out the points arraylist before clustering can begin
     *
     * @param d - data to be set as a point
     */
    public void addPoint(Data d) {
        //add point by point from mainclass to the point arraylist
        temp p = new temp(d);
        pointsArchive.add(p);
    }

    public ArrayList<temp> get(String what) {
        if (what.equals("centroids")) {
            return this.centroids;
        } else if (what.equals("points")) {
            return this.points;
        }
        throw new RuntimeException("Should not happen");
    }
}
