/* 
 * @file            Campsite.java
 *
 * @class           Campsite
 *
 * @author          Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description     Stores a campsite with a given id and name.
 */

package com.campspot;

public class Campsite
{
    // Variable to hold the id and name of the campsite.
    int id;
    String name;

    // Grab the id of a given campsite.
    public int getId() {
        return id;
    }

    // Grab the name of a given campsite.
    public String getName() {
        return name;
    }

    // Set the id of a given campsite.
    public void setId(int newId) {
        id = newId;
    }

    // Set the name of a given campsite.
    public void setName(String newName) {
        name = newName;
    }

    // Constructor for the Campsite object.
    public Campsite(int i, String n) {
        id   = i;
        name = n;
    }
}
