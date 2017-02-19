/* 
 * @file            Reservations.java
 *
 * @class           Reservations
 *
 * @author          Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description     Stores when a customer reservation at campsite starts,
 *                  ends, and the id of the campsite in question.
 */

// Part of the Campspot company package.
package com.campspot;

// Imports
import java.time.LocalDate;

// Search class
public class Reservation
{
    // Variable to hold the id, start and end dates.
    Campsite campsite;
    LocalDate startDate;
    LocalDate endDate;

    // Grab the campsite id.
    public int getCampsiteId() {
         return campsite.getId();
    }

    // Grab the starting date of the reservation.
    public LocalDate getStartDate() {
        return startDate;
    }

    // Grab the ending date of the reservation.
    public LocalDate getEndDate() {
        return endDate;
    }

    // Alter the current reservation start date.
    public void setCampsiteId(Campsite newCampsite) {
         campsite = newCampsite;
    }

    // Alter the current reservation start date.
    public void setStartDate(LocalDate newStartDate) {
         startDate = newStartDate;
    }

    // Alter the current reservation end date.
    public void setEndDate(LocalDate newEndDate) {
         endDate = newEndDate;
    }

    // Constructor for the Reservation object.
    public Reservation(Campsite cs, LocalDate sd, LocalDate ed) {
        campsite  = cs;
        startDate = sd;
        endDate   = ed;
    }
}
