/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg365three;


/*
 *
 * @author smm-pc
 */

/**
 *
 * @author smm-pc
 */

public class Data implements  java.io.Serializable {
    
    // **************************************************
    // Feilds
    // **************************************************
    String id, name, city, state;
    double lattitude, longitude;
    
    // **************************************************
    // Constructor
    // **************************************************
    /*
     *initialize variables
     */

    /**
     *
     * @param id - business ID
     * @param name
     * @param city
     * @param state
     * @param lattitude
     * @param longitude
     */

    public Data(String id, String name, String city, String state, double lattitude, double longitude) {
        this.name = name;
        this.city = city;
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.id = id;
        this.state = state;
    }
}
