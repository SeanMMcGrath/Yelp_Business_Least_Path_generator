/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 *
 * @author smm-pc
 */
public class Btree implements Serializable {

    // **************************************************
    // Classes
    // **************************************************
    class Node implements Serializable {

        int childNum, keyNum;
        boolean leaf;
        Data[] keys;
        Long[] children;//the location of where the children are stored in file, not actual children
        long id;//nodeID * node size

        public Node(long id) {
            this.id = id;
            childNum = 0;
            keyNum = 0;
            keys = new Data[2 * K - 1];
            children = new Long[2 * K];
            leaf = true;
        }

    }

    // **************************************************
    // Fields
    // **************************************************
    String rootFile = "Root_file";
    String nodesFile = "Nodes_file";
    int K = 8;  //2K children and 2K-1 keys to stop overflow
    int nodeSize = 5000; //num doesnt matter as much as it needs to be long enough, under 4000 is too low overwrites data(!important!)
    int size; //how many nodes there are in total
    Node root = null;

    // **************************************************
    // Constructor
    // **************************************************
    
    Btree() throws Exception {
        size = 0;//is this even needed? hmm...
    }

    // **************************************************
    // Public methods
    // **************************************************
    public ArrayList<Data> obtainData() throws Exception {//only use after full tree is written/loaded to file
        ArrayList<Data> temp = new ArrayList<>();
        System.out.println("t: " + this.size);
        for (int i = 0; i <= this.size; i++) {
            Node n = this.diskRead(i);
            for (int j = 0; j < n.keyNum; j++) {
                temp.add(n.keys[j]);
            }
        }
        return temp;//yay works!
    }

    public void insert(Data d) throws Exception {
        if (root == null) {
            Node temp = new Node(size);
            temp.keyNum = 1;
            temp.keys[0] = d;
            diskWrite(temp);
            root = temp;
            size += 1;
        } else {
            Node r = root;
            if (root.keyNum == 2 * K - 1) {//Asking: do we need to split?
                size++;
                Node s = new Node(size);
                root = s;
                s.leaf = false;//no longer a leaf since splitting
                s.keyNum = 0;
                s.children[0] = r.id;
                s.childNum++;
                splitNode(s, r);
                insertNotFull(s, d);
            } else {//not slpitting
                insertNotFull(r, d);
            }
        }
    }

    //write root info to btree file as well as btree's values
    public void diskWriteRoot() throws IOException {
        RandomAccessFile raf = new RandomAccessFile(rootFile, "rw");
        raf.seek(0);
        FileChannel fc = raf.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(2*nodeSize);

        //write total number of nodes to the file
        bb.putInt(size);
        if (root.leaf) {//cant find a way to put a boolean so keep like this
            bb.putInt(1);
        } else {//1 is true, 0 is false
            bb.putInt(0);
        }
        bb.putLong(root.id);

        bb.putInt(root.keyNum);
        for (int i = 0; i < root.keyNum; i++) {

            Data instance = root.keys[i];
            //write each variable one by one
            byte[] id = instance.id.getBytes();
            bb.putInt(id.length);
            bb.put(id);
            byte[] name = instance.name.getBytes();
            bb.putInt(name.length);
            bb.put(name);
            byte[] city = instance.city.getBytes();
            bb.putInt(city.length);
            bb.put(city);
            byte[] state = instance.state.getBytes();
            bb.putInt(state.length);
            bb.put(state);
            bb.putDouble(instance.lattitude);
            bb.putDouble(instance.longitude);
        }

        bb.putInt(root.childNum);
        for (int i = 0; i < root.childNum; i++) {
            bb.putLong(root.children[i]);
        }

        bb.flip();
        fc.write(bb);
        bb.clear();
        fc.close();
        raf.close();

    }

    
    /*
    *writes to disk the node at location node.id*nodesize
    */
    public void diskWrite(Node n) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(nodesFile, "rw");
        raf.seek(n.id * nodeSize);
        FileChannel fc = raf.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(nodeSize);

        if (n.leaf) {//cant find a way to put a boolean so keep like this
            bb.putInt(1);
        } else {//1 is true, 0 is false
            bb.putInt(0);
        }
        bb.putLong(n.id);
        bb.putInt(n.keyNum);
        for (int i = 0; i < n.keyNum; i++) {

            Data instance = n.keys[i];
            //write each variable one by one
            byte[] id = instance.id.getBytes();
            bb.putInt(id.length);
            bb.put(id);
            byte[] name = instance.name.getBytes();
            bb.putInt(name.length);
            bb.put(name);
            byte[] city = instance.city.getBytes();
            bb.putInt(city.length);
            bb.put(city);
            byte[] state = instance.state.getBytes();
            bb.putInt(state.length);
            bb.put(state);
            bb.putDouble(instance.lattitude);
            bb.putDouble(instance.longitude);
        }

        bb.putInt(n.childNum);
        for (int i = 0; i < n.childNum; i++) {
            bb.putLong(n.children[i]);
        }

        bb.flip();
        fc.write(bb);
        bb.clear();
        fc.close();
        raf.close();
    }
    
    /*
    * reads the root and other btree values from file root_file.txt and after adding to a btree object, returns that object
    */
    public Btree diskReadRoot() throws Exception {
        Node temp = new Node(0);
        RandomAccessFile raf = new RandomAccessFile(rootFile, "rw");
        raf.seek(0);
        FileChannel fc = raf.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(nodeSize * 2);
        fc.read(bb);
        bb.flip();

        Btree b = new Btree();
        b.root = temp;
        b.size = bb.getInt();
        int leaf = bb.getInt();
        if (leaf == 1) {
            temp.leaf = true;
        } else {
            temp.leaf = false;
        }
        temp.id = bb.getLong();
        temp.keyNum = bb.getInt();
        for (int i = 0; i < temp.keyNum; i++) {

            int idSize = bb.getInt();
            byte[] idByte = new byte[idSize];
            bb.get(idByte);
            String id = new String(idByte);
            int nameSize = bb.getInt();
            byte[] nameByte = new byte[nameSize];
            bb.get(nameByte);
            String name = new String(nameByte);
            int citySize = bb.getInt();
            byte[] cityByte = new byte[citySize];
            bb.get(cityByte);
            String city = new String(cityByte);
            int stateSize = bb.getInt();
            byte[] stateByte = new byte[stateSize];
            bb.get(stateByte);
            String state = new String(stateByte);
            Double lattitude = bb.getDouble();
            Double longitude = bb.getDouble();

            Data d = new Data(id, name, city, state, lattitude, longitude);
            temp.keys[i] = d;
        }

        temp.childNum = bb.getInt(); //recover children
        for (int i = 0; i < temp.childNum; i++) {
            temp.children[i] = bb.getLong();
        }

        bb.clear();
        fc.close();
        raf.close();
        return b;
    }
    // **************************************************
    // Private methods
    // **************************************************
    
    private void insertNotFull(Node x, Data d) throws Exception {

        int i = x.keyNum - 1;
        if (x.leaf) {
            while (i > -1 && d.hashCode() < x.keys[i].hashCode()) {//moves the keys until a spot is found for d
                x.keys[i + 1] = x.keys[i];
                i -= 1;
            }
            i += 1;
            x.keys[i] = d;
            x.keyNum++;
            diskWrite(x);
        } else {
            while (i > -1 && d.hashCode() < x.keys[i].hashCode()) { //search for spot
                i--;
            }
            i++;
            Node temp = diskRead(x.children[i]);
            if (temp.keyNum == (2 * K - 1)) {
                splitNode(x, temp);
                if (d.hashCode() > x.keys[i].hashCode()) {
                    temp = diskRead(x.children[i + 1]);
                }
            }
            insertNotFull(temp, d);
        }
    }

    /*
    *splits a node into two and then overwrites old written data as well as creating new ones
    * @param x -
    * @param y -
    */
    private void splitNode(Node x, Node y) throws IOException {
        size++;
        Node z = new Node(size);
        z.leaf = y.leaf;
        for (int i = 0; i < K - 1; i++) { //move second half of y's keys to to first half of z's keys
            z.keys[i] = y.keys[i + K];
            z.keyNum++; //just added a key, increment numKeys
            y.keys[i + K] = null; ///this line might give some weird errors - for just keys it was y.keys[i + K] = 0
            y.keyNum--;
        }

        if (!y.leaf) {
            for (int i = 0; i < K; i++) {
                z.children[i] = y.children[i + K];
                z.childNum++;
                y.children[i + K] = null;
                y.childNum--;
            }
        }
        int index = x.keyNum - 1;
        while (index > -1 && y.keys[K - 1].hashCode() < x.keys[index].hashCode()) {
            x.keys[index + 1] = x.keys[index];
            index--;
        }
        index++;
        x.keys[index] = y.keys[K - 1];//move key to new spot
        x.keyNum++;
        y.keys[K - 1] = null;//old space set empty now so no duplicate
        y.keyNum--;//key removed so --

        int index2 = x.childNum - 1;
        while (index2 > index) {
            x.children[index2 + 1] = x.children[index2];
            index2--;
        }
        index2++;
        x.children[index2] = z.id;
        x.childNum++;
        diskWrite(x);
        diskWrite(y);
        diskWrite(z);

    }
    
    /*
    * reads from disk the node at the diskoffset x*disksize and returns this node
    * @param x - a long that represents where the node is in the file so it can be read
    */
    private Node diskRead(long x) throws Exception {

        Node temp = new Node(0);
        RandomAccessFile file = new RandomAccessFile(nodesFile, "rw");
        file.seek(x * nodeSize);//find where node is
        FileChannel fc = file.getChannel();
        ByteBuffer bb = ByteBuffer.allocate(nodeSize);
        fc.read(bb);
        bb.flip();
        int leaf = bb.getInt();
        if (leaf == 1) {//turn int rep. of the leaf value and turn back to boolean
            temp.leaf = true;
        } else {
            temp.leaf = false;
        }
        temp.id = bb.getLong();
        temp.keyNum = bb.getInt();
        for (int i = 0; i < temp.keyNum; i++) {

            int idSize = bb.getInt();
            byte[] idByte = new byte[idSize];
            bb.get(idByte);
            String id = new String(idByte);
            int nameSize = bb.getInt();
            byte[] nameByte = new byte[nameSize];
            bb.get(nameByte);
            String name = new String(nameByte);
            int citySize = bb.getInt();
            byte[] cityByte = new byte[citySize];
            bb.get(cityByte);
            String city = new String(cityByte);
            int stateSize = bb.getInt();
            byte[] stateByte = new byte[stateSize];
            bb.get(stateByte);
            String state = new String(stateByte);
            Double lattitude = bb.getDouble();
            Double longitude = bb.getDouble();

            Data d = new Data(id, name, city, state, lattitude, longitude);
            temp.keys[i] = d;
        }
        //children stuff
        temp.childNum = bb.getInt();
        for (int i = 0; i < temp.childNum; i++) {
            temp.children[i] = bb.getLong();
        }
        bb.clear();
        fc.close();
        file.close();
        return temp;
    }
}
