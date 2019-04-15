/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author smm-pc
 */
public class GraphDisplay extends JComponent {

    public void graphing(LinkedList<Vertex> path) {
        GraphDisplay gd = new GraphDisplay();
        gd.showInWindow(400, 400, "Graph");

        //set the nodes from the path
        for (int i = 0; i < path.size(); i++) {
            if (i == 0) {//set first to red
                gd.addNode(i, path.get(i).getLat(), path.get(i).getLon(), Color.RED);
            } else if (i == path.size() - 1) { //set last to black
                gd.addNode(i, path.get(i).getLat(), path.get(i).getLon(), Color.BLACK);
            } else {
                gd.addNode(i, path.get(i).getLat(), path.get(i).getLon());
            }
        }

        //set edges careful of interuptions
        //try {
            for (int j = 0; j < path.size() - 1; j++) {
                //gd.addConnection(j, j + 1, Color.red);
                //Thread.sleep(500);
                gd.addConnection(j, j + 1, Color.black);
            }
       // } catch (InterruptedException e) {
       // }
    }

    public GraphDisplay() {
        minX = minY = Double.POSITIVE_INFINITY;
        maxX = maxY = Double.NEGATIVE_INFINITY;
    }

    public synchronized void addNode(Object identifier, double x, double y,
            Color col) {
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        nodes.put(identifier, new Node(x, y, col));
        repaint();
    }

    public synchronized void addNode(Object identifier, double x, double y) {
        maxX = Math.max(maxX, x);
        maxY = Math.max(maxY, y);
        minX = Math.min(minX, x);
        minY = Math.min(minY, y);
        nodes.put(identifier, new Node(x, y, NODE_COLOR));
        repaint();
    }

    public synchronized void addConnection(Object start, Object end, Color c) {
        removeConnection(start, end);
        connectors.add(new Connector(start, end, c));
        repaint();
    }

    public synchronized boolean removeConnection(Object start, Object end) {
        Iterator<Connector> it = connectors.iterator();
        while (it.hasNext()) {
            Connector tmp = it.next();
            if (tmp.joins(start, end)) {
                it.remove();
                repaint();
                return true;
            }
        }
        return false;
    }

    public void addConnection(Object start, Object end) {
        GraphDisplay.this.addConnection(start, end, Color.black);
    }

    public JFrame showInWindow(int width, int height, String title) {
        JFrame f = new JFrame();
        f.add(this);
        f.setSize(width, height);
        f.setTitle(title);
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                f.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            }
        });
        //f.setExtendedState(f.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        f.setVisible(true);
        return f;
    }

    public void paint(Graphics g) {
        if (nodes.isEmpty()) {
            return;
        }

        double xscl = (getSize().width - 2 * MARGIN) / (maxX - minX);
        double yscl = (getSize().height - 2 * MARGIN) / (maxY - minY);

        g.translate(+MARGIN, +MARGIN);

        synchronized (this) {
            for (Connector e : connectors) {
                e.paint(g, xscl, yscl, minX, minY);
            }
            for (Node n : nodes.values()) {
                n.paint(g, xscl, yscl, minX, minY);
            }
        }

        g.translate(-MARGIN, -MARGIN);
    }

    protected double minX, maxX, minY, maxY;
    protected HashMap<Object, Node> nodes = new HashMap<Object, Node>();
    protected ArrayList<Connector> connectors = new ArrayList<Connector>();

    protected int MARGIN = 20;
    protected int NODE_RADIUS = 5;
    protected Color NODE_COLOR = Color.blue.brighter();

    private class Node {

        public Node(double x, double y, Color col) {
            this.x = x;
            this.y = y;
            this.col = col;
        }

        public void paint(Graphics g, double xscl, double yscl, double tx, double ty) {
            g.setColor(col);
            g.fillOval(
                    (int) ((x - tx) * xscl - NODE_RADIUS),
                    (int) ((y - ty) * yscl - NODE_RADIUS),
                    2 * NODE_RADIUS,
                    2 * NODE_RADIUS
            );
        }

        protected double x, y;
        protected Color col;

    }

    private class Connector {

        public Connector(Object start, Object end, Color col) {
            this.start = start;
            this.end = end;
            this.col = col;
        }

        public boolean joins(Object a, Object b) {
            return (start.equals(a) && end.equals(b))
                    || (start.equals(b) && end.equals(a));
        }

        public void paint(Graphics g, double xscl, double yscl, double tx, double ty) {
            Node a = nodes.get(start);
            Node b = nodes.get(end);
            g.setColor(col);
            g.drawLine(
                    (int) (xscl * (a.x - tx)),
                    (int) (yscl * (a.y - ty)),
                    (int) (xscl * (b.x - tx)),
                    (int) (yscl * (b.y - ty))
            );
        }

        protected Object start, end;
        protected Color col;
    }
}
