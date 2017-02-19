/* 
 * @file            Search.java
 *
 * @class           Search
 *
 * @author          Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description     Stores a start and an end date that is utilized when
 *                  looking for gaps in lists of reservations.
 */

// Part of the Campspot company package.
package com.campspot;

// Imports
import java.time.LocalDate;

// Search class
public class Search
{
    // Variable to hold the start and end of the search.
    LocalDate startDate;
    LocalDate endDate;

    // Grab the starting date of the search.
    public LocalDate getStartDate() {
        return startDate;
    }

    // Grab the ending date of the search.
    public LocalDate getEndDate() {
        return endDate;
    }

    // Alter the current search start date.
    public void setStartDate(LocalDate newStartDate) {
         startDate = newStartDate;
    }

    // Alter the current search end date.
    public void setEndDate(LocalDate newEndDate) {
         endDate = newEndDate;
    }

    // Constructor for the Search object.
    public Search(LocalDate sd, LocalDate ed) {
        startDate = sd;
        endDate   = ed;
    }

    // toString method, which prints out the start and end date.
    public String toString() {
        return "[" + startDate.toString()  + ", " + endDate.toString() + "]";
    }
}
