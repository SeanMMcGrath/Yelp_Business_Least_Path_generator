/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;


/**
 *
 * @author smm-pc
 */
public class Vertex implements java.io.Serializable, Comparable{

    private double distance = Double.POSITIVE_INFINITY;
    final private String id;
    final private String name;
    final private double lat;
    final private double lon;

    public Vertex(String id, String name, double lat, double lon) {
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lon = lon;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }
    
    public double getDistance(){
        return distance;
    }
    
    public void setDistance(double d) {
        distance = d;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Vertex other = (Vertex) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(Object o) {
        Vertex other = (Vertex)o;
        return Double.compare(other.getDistance(), this.getDistance());
    }
    
    @Override
    public String toString() {
        return name;
    }

}
