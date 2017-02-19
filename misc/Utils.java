/*
 * @file           Utils.java
 *
 * @class          Utils
 *
 * @author         Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description    Various small functions needed by the campsite JSON
 *                 program and its associated classes.
 */

package misc;

import java.time.LocalDate;
import java.util.*;

public class Utils {

    // Debug mode
    boolean debugMode = false;

    //! Print additional verbose output for the purposes of finding bugs.
    /*
     * @param     String    message to print to stdout
     *
     * @returns   none
     */
    public void debugPrint(String msg) {

        // If debug mode is off, do nothing.
        if (debugMode == false) {
            return;
        }

        // Input validation, make sure this got a valid length message.
        if (msg.length() < 1) {
            return;
        }

        // Otherwise print the debug message to stdout.
        System.out.println(msg);
    } 

    //! Print a given message to stdout, a-la C++ style.
    /*
     * @param     string    message to print to stdout
     *
     * @returns   none
     */
    public void printf(String msg) {

        // Input validation, make sure this got a valid length message.
        if (msg.length() < 1) {
            return;
        }

        // Otherwise print the message to stdout.
        System.out.println(msg);
    }

    //! Return from the program main, a la C++ style.
    /*
     * @param     int    exit value
     *
     * @returns   none
     */
    public void returns(int exitValue) {
        System.exit(exitValue);
    }

    //! Sorts an array of LocalDates from earliest to latest.
    /*
     * @param     ArrayList<LocalDate>    unsorted list
     *
     * @returns   ArrayList<LocalDate>    fully sorted list
     */
    public ArrayList<LocalDate> sortLocalDatesList(List<LocalDate> al) {

        // Input validation.
        if (al == null) {
            debugPrint("sortLocalDatesList() --> invalid input");
            return null;
        }

        // Variable to hold the sorted results.
        ArrayList<LocalDate> sortedArray = new ArrayList<LocalDate>();

        // Safety check, if the given array list is empty, so ahead and
        // just return a blank array list as the job is complete.
        if (al.isEmpty()) {
            return sortedArray;
        }

        // Continue until all of the elements in "al" have been sorted.
        while (al.isEmpty() == false) {

            // Variable to hold the earliest date still present in the array.
            LocalDate earliest = al.get(0);
            int earlyIndex = 0;

            // For every local date in the list...
            for (int i = 0; i < al.size(); i++) {

                // Grab the current element at index i. 
                LocalDate cur = al.get(i);

                // If the current element is earlier than the current value
                // stored in "earliest", then make this value the new earliest.
                if (cur.isBefore(earliest)) {
                    earliest = cur;
                    earlyIndex = i;
                }
            }

            // Remove the index at "earlyIndex" in the old "al" list.
            al.remove(earlyIndex);

            // Add the "earliest" local date to the sorted array 
            sortedArray.add(earliest);
        }

        // Finally, return the new result array.
        return sortedArray;
    }

    //! Converts strings of the form "2016-06-07" to LocalDate objects.
    /*
     * @param     String       textual representation of yyyy-MM-dd
     *
     * @returns   LocalDate    given date in a LocalDate format
     */
    public LocalDate stringToLocalDate(String dateAsString) {

        // Input validation
        if (dateAsString == null || dateAsString.length() < 1) {
            debugPrint("stringToLocalDate() --> invalid input");
            return null;
        }

        // Variables to hold the year, month, and day as ints.
        int year  = 0;
        int month = 0;
        int day   = 0;

        // Attempt to split the string using '-' as a delimiter.
        String[] yearMonthDayParts = dateAsString.split("-");

        // Sanity check, make sure this go 3 strings and that they are of
        // the proper lengths.
        if (yearMonthDayParts.length != 3
          || yearMonthDayParts[0].length() != 4
          || yearMonthDayParts[1].length() != 2
          || yearMonthDayParts[2].length() != 2) {

            // If debug, tell the developer that the 
            debugPrint("stringToLocalDate() --> improper format, please " +
                       "use yyyy-MM-dd");
            return null;
        }

        // Dump the split strings to ints.
        year  = Integer.parseUnsignedInt(yearMonthDayParts[0]); 
        month = Integer.parseUnsignedInt(yearMonthDayParts[1]);
        day   = Integer.parseUnsignedInt(yearMonthDayParts[2]);

        // Use the extracted ints to create a LocalDate object.
        LocalDate result = LocalDate.of(year, month, day);

        // Sanity check, make sure the above isn't null.
        if (result == null) {
            debugPrint("stringToLocalDate() --> unable to convert " +
                       "extracted ints to LocalDate");
            return null;
        }

        // Having got this far, go ahead and return the conversion result.
        return result;
    }

    // Constructor for the Utils class.
    public Utils(boolean dm) {
        debugMode = dm;
    }
}
