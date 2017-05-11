/*
 * @file           SearchActionHandler.java
 *
 * @class          SearchActionHandler
 *
 * @author         Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description    Handles an event where the user is searching for
 *                 gaps in a given list of reservations.
 *
 * @usage          First, this class requires a "JsonInputHandler" object to
 *                 function. It is recommended the developer read that file
 *                 before attempting to create new instances of this class.
 *
 *                 Second, this assumes the code now has a "JsonInputHandler"
 *                 that both works and converts the input successfully.
 *                 A new SearchActionHandler may now be created by feeding
 *                 it and a boolean stating whether or not the developer would
 *                 like to print debug messages.
 *
 *                 SearchActionHandler sah
 *                   = new SearchActionHandler(jih, debugMode);
 *
 *                 Third, if the above object initialized correctly and is
 *                 not null, searching for gaps as per the defined rules can
 *                 be accomplished like so:
 *
 *                 sah.searchForGaps();
 *
 *                 Doing the above will search for any gaps that match the
 *                 rules, printing the results to stdout.
 */

// Imports
import com.campspot.*;
import java.time.LocalDate;
import java.util.*;
import org.json.*;
import misc.Utils;

public class SearchActionHandler {

    /**
     * Class-wide variables
     */

    // Instance of a utility class to easily access various small
    // functions that might be needed.
    Utils u = null;

    // Holds the data extracted from JSON objects.
    JsonInputHandler jih = null;

    /**
     * Class-wide functions
     */

    //! Constructor for the SearchActionHandler
    /*
     * @param      JSONInputHandler       object containing the parsed data input
     * @param      bool                   whether or not debug msg are printed
     *
     * @returns    SearchActionHandler    ref to newly created object
     */
    public SearchActionHandler(JsonInputHandler j, boolean dm) {
        jih = j;
        u   = new Utils(dm);
    }

    //! Determine which gaps are present at which campsites.
    /*
     * @returns   bool    whether or not an error occurred while looking
     *                    for gaps in the reservations
     */
    public boolean searchForGaps() {

        // Safety check, make sure the class-wide "JSONInputHandler jih"
        // variable is defined.
        if (jih == null || jih.getSearchObject() == null) {
            u.printf("searchForGaps() --> invalid JSONInputHandler");
            return false;
        }

        // Grab the data from the JSON input handler
        Search searchObj               = jih.getSearchObject();
        List<GapRule> gapRulesArray    = jih.getGapRulesArray();
        List<Campsite> campsites       = jih.getCampsitesArray();
        List<Reservation> reservations = jih.getReservationsArray();

        // Sanity check, make sure the Search objects and element array
        // exist and have elements.
        if (searchObj == null || gapRulesArray == null
          || campsites == null || reservations == null) {
            u.printf("searchForGaps() --> invalid array lists in jih");
            return false;
        }

        // Variable that will store whether a gap has currently been
        // detected at a given campsite.
        boolean wasGapDetected = false;

        // Variable that will hold a list of campsites that *do not* have
        // any gaps, as per the gap rules extracted earlier via JSON.
        List<Integer> campsiteIdsWithoutGaps = new ArrayList<Integer>();

        // For each campsite...
        for (int i = 0; i < campsites.size(); i++) {

            // Since this is the next campsite, reset the wasGapDetected
            // variable.
            wasGapDetected = false;

            // Attempt to grab the current campsite from index i.
            Campsite cs = campsites.get(i);

            // Sanity check, make sure the above actually grabbed a campsite.
            if (cs == null
              || ("" + cs.getClass()).equals("class com.campspot.Campsite") == false) {

                // Since this failed, print an error and return false.
                u.printf("Error: An invalid Campsite object was detected in " +
                       "the class-wide `campsites` ArrayList!");
                return false;
            }

            // Make an array to hold the currently "reserved days" of a specific
            // campsite id.
            List<LocalDate> datesThatAreReservedAtGivenCampsite
              = new ArrayList<LocalDate>();

            // For each reservation...
            for (int j = 0; j < reservations.size(); j++) {

                // Grab the reservation at index j.
                Reservation rs = reservations.get(j);

                // Sanity check, make sure this actually got a reservation.
                if (rs == null
                  || ("" + rs.getClass()).equals("class com.campspot.Reservation") == false) {

                    // Since this failed, print an error and return false.
                    u.printf("Error: An invalid Reservation object was " + 
                           "detected in the class-wide `reservation` " +
                           "ListArray!");
                    return false;
                }

                // If the reservation is at that campsite...
                if (cs.getId() == rs.getCampsiteId()) {

                    // Grab the startDate and endDate of the reservation,
                    // they'll be the start-and-finish counters needed per
                    // the below for-loop.
                    LocalDate starting = rs.getStartDate();
                    LocalDate ending   = rs.getEndDate();

                    // For every day between the startDate and endDate that
                    // is reserved at that campsite...
                    for (LocalDate date = starting;
                         date.isEqual(ending.plusDays(1)) == false;
                         date = date.plusDays(1)) {

                        // Add it to the array of reserved days.
                        datesThatAreReservedAtGivenCampsite.add(date);
                    }
                }
            }

            // If the "reserved days" array is empty...
            if (datesThatAreReservedAtGivenCampsite.isEmpty()) {

                // Add the id of the current campsite to the
                // "campsiteIdsWithoutGaps" array.
                campsiteIdsWithoutGaps.add(cs.getId());

                // Continue on to the next campsite.
                continue;
            }

            // If debug mode, tell the developers which dates have currently
            // been reserved at a given campsite.
            u.debugPrint("------------------------------------------------");
            u.debugPrint("Reserved dates at Campsite #" + cs.getId());
            u.debugPrint("------------------------------------------------");
            for (int a = 0;
                 a < datesThatAreReservedAtGivenCampsite.size();
                 a++) {

                 // Dump the date to a String and print it.
                 u.debugPrint(datesThatAreReservedAtGivenCampsite.get(a).toString());
            }

            // Convert the Search object start and end dates into LocalDates.
            LocalDate searchStart = searchObj.getStartDate();
            LocalDate searchEnd   = searchObj.getEndDate();

            // If debug mode, tell the developer what the search start and
            // end dates were at this point.
            u.debugPrint("\n------------------------------------------------");
            u.debugPrint("Attempted Campsite Reservation:\n");
            u.debugPrint("start date: " + searchStart.toString());
            u.debugPrint("end date:   " + searchEnd.toString());
            u.debugPrint("------------------------------------------------\n");

            // For every day between Search object startDate and endDate...
            boolean wasCampsiteAlreadyBooked = false;
            for (LocalDate ldate = searchStart;
                 ldate.isEqual(searchEnd.plusDays(1)) == false;
                 ldate = ldate.plusDays(1)) {

                // Variable to hold if a given date is found.
                boolean preExistingReservation = false;

                // Check if the array contains at least one of the requested
                // reservation dates.
                if (datesThatAreReservedAtGivenCampsite.contains(ldate)) {
                    u.debugPrint("searchForGaps() --> ldate = " +
                                 ldate.toString());
                    preExistingReservation = true;
                }

                // Check if that day has already been held by a previous
                // reservation entry. If so, then break out of this loop,
                // and set the flag to indicate that this campsite was
                // already booked by some other customer.
                if (preExistingReservation) {
                    wasCampsiteAlreadyBooked = true;
                    break;
                }

                // Else add that day to the "reserved days" array.
                datesThatAreReservedAtGivenCampsite.add(ldate);
            }

            // Since at least one of these days is already taken,
            // skip this campsite and move on to next element.
            if (wasCampsiteAlreadyBooked) {
                u.debugPrint("Note: Campsite of id #" + cs.getId() + " was " +
                           "already booked.\n");
                continue;
            }

            // Having added the new reservation to the "reserved dates", 
            // sort the elements in the "reserved days" array, from earliest
            // to latest.
            datesThatAreReservedAtGivenCampsite
              = u.sortLocalDatesList(datesThatAreReservedAtGivenCampsite);

            // If debug mode, tell the developers which dates have currently
            // been reserved at a given campsite AFTER the attempted
            // reservation done via the search action.
            u.debugPrint("------------------------------------------------");
            u.debugPrint("Sorted Reserved dates at Campsite #" + cs.getId());
            u.debugPrint("------------------------------------------------");
            for (int a = 0;
                 a < datesThatAreReservedAtGivenCampsite.size();
                 a++) {

                 // Dump the date to a String and print it.
                 u.debugPrint(datesThatAreReservedAtGivenCampsite.get(a).toString());
            }

            // Finally, cycle through each of the gap rules...
            for (int l = 0; l < gapRulesArray.size(); l++) {

                // Grab the current gap rule object.
                GapRule gr  = gapRulesArray.get(l);

                // Sanity check, make sure this is a gap rule object.
                if (gr == null
                  || ("" + gr.getClass()).equals("class com.campsite.GapRule")) {
                    u.debugPrint("searchForGaps() --> Unable to read from " +
                               "class-wide array of gap rules.");
                    return false;
                }

                // Grab the current gap rule size, as an int.
                int gapSize = gr.getSize();

                // Safety check, make sure the gap size is greater than zero.
                if (gapSize < 1) {

                    // Move on to the next gap rule; no point check for gaps
                    // that are zero or lower.
                    continue;
                }

                // Grab the first date present in the
                // "datesThatAreReservedAtGivenCampsite" array.
                LocalDate first = datesThatAreReservedAtGivenCampsite.get(0);

                // Grab the index of the last element; this will be useful
                // further down later, especially for the sake of clarity.
                int lastIndex  = datesThatAreReservedAtGivenCampsite.size() - 1;

                // Safety check, this shouldn't occur, but if last is less
                // than zero break out of here.
                if (lastIndex < 0) {
                    u.debugPrint("searchForGaps() --> Odd or misaligned array " +
                               "of reserved dates was detected...");
                    return false;
                }

                // Attempt to grab the element at the last index.
                LocalDate last
                  = datesThatAreReservedAtGivenCampsite.get(lastIndex);

                // Sanity check, make sure the last element is actually
                // a valid LocalDate that isn't null.
                if (last == null) {

                    // Assume some minor memory or JSON typo, and move
                    // on to the next campsite reservations.
                    continue;
                }

                // Go from the starting day backwards, and see if any new
                // gaps were created as a result of the new reservation.
                int unreservedDayCounter = 0;

                // Cycle backwards from the searchStart until a space the
                // size of a gap rule is found (if any).
                for (LocalDate pivot = searchStart.minusDays(1);
                     pivot.isAfter(first);
                     pivot = pivot.minusDays(1)) {

                    // Having reached a reserved date, leave the for loop.
                    if (datesThatAreReservedAtGivenCampsite.contains(pivot)) {
                        break;
                    }

                    // If an unreserved day was found, increment the
                    // unreserved day counter.
                    unreservedDayCounter++;
                }

                // If a new gap was detected during a run of the above
                // "reserved days" for-loop, then go ahead and break out
                // of this "gap rules" for-loop.
                if (unreservedDayCounter == gapSize) {

                    // Set the wasGapDetected variable to true.
                    wasGapDetected = true;

                    // If debug, tell the developer what happened.
                    u.debugPrint("Note: Gap of size " + gapSize + " was " +
                               "detected in Campsite id #" + cs.getId());

                    // Break from this for-loop as a gap was detected.
                    break;
                }

                // Go from the ending day forwards, and see if any new
                // gaps were created as a result of the new reservation.
                unreservedDayCounter = 0;
                for (LocalDate pivot = searchEnd.plusDays(1);
                     pivot.isBefore(last);
                     pivot = pivot.plusDays(1)) {

                    // Having reached a reserved date, leave the for loop.
                    if (datesThatAreReservedAtGivenCampsite.contains(pivot)) {
                        break;
                    }

                    // If an unreserved day was found, increment the
                    // unreserved day counter.
                    unreservedDayCounter++;
                }

                // If a new gap was detected during a run of the above
                // "reserved days" for-loop, then go ahead and break out
                // of this "gap rules" for-loop.
                if (unreservedDayCounter == gapSize) {

                    // Set the wasGapDetected variable to true.
                    wasGapDetected = true;

                    // If debug, tell the developer what happened.
                    u.debugPrint("Note: Gap of size " + gapSize + " was " +
                               "detected in Campsite id #" + cs.getId());

                    // Break from this for-loop as a gap was detected.
                    break;
                }
            }

            // If none of the gap rules apply to the current "reserved days"
            // array, go ahead and add it.
            if (wasGapDetected == false) {
                campsiteIdsWithoutGaps.add(cs.getId());
            }
        }

        // If no campsites were found, print out a short message telling
        // the end-user about it, and then return true.
        if (campsiteIdsWithoutGaps.isEmpty()) {
            u.printf("No campsites without gaps were present.");
            return true;
        }

        // Finally, having exhaustively determined which campsites do not
        // currently have gaps, print their names to stdout.
        u.printf("------------------------------------------------------------");
        u.printf("The requested campsite reservation between...\n");
        u.printf("" + searchObj.getStartDate().toString() +
                 " and " + searchObj.getEndDate().toString());
        u.printf("\n... can be safely placed without creating new gaps at:");
        u.printf("------------------------------------------------------------");
        for (int i = 0; i < campsiteIdsWithoutGaps.size(); i++) {

            // Grab the campsite id from the current index.
            int curId = campsiteIdsWithoutGaps.get(i);

            // Cycle through the campsites array until this gets an id that
            // matches, and then grab the name.
            for (int j = 0; j < campsites.size(); j++) {
               
                // Safety check, make sure this element is actually not null.
                if (campsites.get(j) == null) {
                    u.debugPrint("searchForGaps() --> null Campsite element " +
                                 "detected, skipping...");
                    continue;
                }
 
                // Grab the given campsite info.
                int givenCampsiteIdAtIndex = campsites.get(j).getId();
                String givenCampsiteNameAtIndex = campsites.get(j).getName();

                // Further sanity check, make sure the name is something valid.
                if (givenCampsiteNameAtIndex.length() < 1) {
                    u.debugPrint("searchForGaps() --> null Campsite name " +
                                 "detected, skipping...");
                    continue;
                }

                // If the id matches and the name is something valid, go ahead
                // and print it out so that the end-user can determine which
                // campsite currently do not have gaps.
                if (curId == givenCampsiteIdAtIndex) {
                    u.printf("" + campsites.get(j).getName());
                }
            }
        }
        u.printf("------------------------------------------------------------");

        // Since everything executed correctly, return true.
        return true;
    }
}
