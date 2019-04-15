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
public class Edge implements java.io.Serializable {

    private final double weight;
    private final Vertex source;
    private final Vertex destination;

    public Edge( Vertex source, Vertex destination, double weight) {
        this.source = source;
        this.destination = destination;
        this.weight = weight;
    }

    public Vertex getDestination() {
        return destination;
    }

    public Vertex getSource() {
        return source;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return source.getName() + " " + destination.getName();
    }

}
