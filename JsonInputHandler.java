/*
 * @file           JsonInputHandler.java
 *
 * @class          JsonInputHandler
 *
 * @author         Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description    This class takes JSON objects and extracts them,
 *                 storing the contents into arrays of gap rules, campsites,
 *                 and campsite reservations.
 *
 * @usage          First, generate a new JSONObject via a call to the
 *                 initializer:
 *
 *                 JSONObject abc = new JSONObject(fileContents);
 *
 *                 Second, create a new JsonInputHandler, with the
 *                 requested action and a boolean stating whether the
 *                 developer wants debug messages enabled.
 *
 *                 JsonInputHandler jih = new JsonInputHandler(action,
 *                                                             debugMode);
 *
 *                 Third, feed in the previously created JSONObject as a
 *                 parameter to the primary converter function.
 *
 *                 jih.JsonToObjects(jsonObj);
 *
 *                 Having done all of the above, the JSON file data should
 *                 now be stored in the arrays of this class, which can
 *                 be accessed thusly...
 *
 *                 jih.getSearchObject();        // Search object
 *                 jih.getGapRulesArray();       // List of gap rules
 *                 jih.getCampsitesArray();      // List of campsite
 *                 jih.getReservationsArray();   // List of reservations
 *
 *                 Alternatively, the JsonInputHandler itself can be feed
 *                 as a parameter into other classes, like the included
 *                 module "SearchActionHandler", for the sake of developer
 *                 convenience.
 */

// Imports
import com.campspot.*;
import java.time.LocalDate;
import java.util.*;
import org.json.*;
import misc.Utils;

public class JsonInputHandler {

    /**
     * Class-wide variables
     */

    // Instance of a utility class to easily access various small
    // functions that might be needed.
    Utils u = null;

    // Holds the action this handler is looking for.
    String action = "";

    // Lists that hold any data extracted from JSON input.
    Search searchObj                    = null;
    ArrayList<GapRule> gapRulesArray    = new ArrayList<GapRule>();
    ArrayList<Campsite> campsites       = new ArrayList<Campsite>();
    ArrayList<Reservation> reservations = new ArrayList<Reservation>();

    /**
     * Class-wide functions
     */

    //! Constructor for the JsonInputHandler
    /*
     * @param      String              action being requested
     * @param      bool                whether or not debug msg are printed
     *
     * @returns    JsonInputHandler    ref to newly created object
     */
    public JsonInputHandler(String a, boolean dm) {
        action = a;
        u      = new Utils(dm);
    }

    //! Returns a ref to the internal Search object.
    /*
     * @returns    Search   the Search object populated by the handler.
     */
    public Search getSearchObject() {
        return searchObj;
    }

    //! Returns a ref to the internal list of gap rules.
    /*
     * @returns    ArrayList<GapRule>   list of gap rules.
     */
    public ArrayList<GapRule> getGapRulesArray() {
        return gapRulesArray;
    }

    //! Returns a ref to the internal list of campsites.
    /*
     * @returns    ArrayList<Campsite>   list of gap rules.
     */
    public ArrayList<Campsite> getCampsitesArray() {
        return campsites;
    }

    //! Returns a ref to the internal list of reservations.
    /*
     * @returns    ArrayList<Reservation>   list of reservations.
     */
    public ArrayList<Reservation> getReservationsArray() {
        return reservations;
    }

    //! Take a JSON object and populate the Search object and element arrays.
    /*
     * @param     JSONObject    map to search through for gaps
     *
     * @returns   bool          whether or not a valid action was given
     */
    public boolean JsonToObjects(JSONObject jsonObj) {

        // Input validation.
        if (jsonObj == null || jsonObj.length() < 1) {
            u.printf("JsonToObjects() --> invalid input");
            return false;
        }

        // Variable declaration
        JSONObject searchJsonRaw         = null;
        LocalDate  searchStartDate       = null;
        LocalDate  searchEndDate         = null;
        LocalDate  reservationStartDate  = null;
        LocalDate  reservationEndDate    = null;
        JSONArray  gapRulesJsonArray     = null;
        JSONArray  campsitesJsonArray    = null;
        JSONArray  reservationsJsonArray = null;

        // Grab a list of names from the json object.
        JSONArray names = jsonObj.names(); 

        // Sanity check, make sure this actually got an object.
        if (names == null) {
            u.printf("JsonToObjects() --> malformed JSONObject names");
            return false;
        }

        // If debug mode, print out a list of keys present in the array.
        u.debugPrint("--------------------------------------------------------");
        u.debugPrint("JsonToObjects() --> JSON Names Data");
        u.debugPrint("--------------------------------------------------------");
        u.debugPrint(names.toString());
        u.debugPrint("--------------------------------------------------------");

        // Grab the JSONObject associated with the "search" action.
        try {
            searchJsonRaw = (JSONObject) jsonObj.get("search");
            u.debugPrint("JsonToObjects() --> Search JSON: " + searchJsonRaw.toString());

        // Catch any JSONArray initialization errors, if they occur.
        } catch (JSONException je) {
            u.printf("Error: Unable to create JSONObject from search input.");
            je.printStackTrace();
            return false;
        }

        // If debug mode, go ahead and output the raw, unconverted start
        // and end dates.
        u.debugPrint("JsonToObjects() --> Raw start date: " +
                   searchJsonRaw.get("startDate"));
        u.debugPrint("JsonToObjects() --> Raw end date: " +
                   searchJsonRaw.get("endDate"));

        // Extract the start and end date 
        searchStartDate
          = u.stringToLocalDate("" + searchJsonRaw.get("startDate"));
        searchEndDate
          = u.stringToLocalDate("" + searchJsonRaw.get("endDate"));

        // Sanity check, make sure the the parsing didn't just throw back
        // a null value.
        if (searchStartDate == null || searchEndDate == null) {
            u.printf("Warning: Improperly parsed Date objects for the " +
                   "`search` action start / end dates.");
            return false;
        }

        // If debug mode, tell the developer what the search start and
        // end dates are.
        u.debugPrint("Search action starting date is: " +
                   searchStartDate.toString());
        u.debugPrint("Search action ending date is: " +
                   searchEndDate.toString());

        // Populate the Search object with the given dates.
        searchObj = new Search(searchStartDate, searchEndDate);
      
        // Sanity check, make sure the Search object was initialized
        // correctly and didn't end up null.
        if (searchObj == null) {
            u.printf("Error: Possible memory error in assignment of " +
                   "Search object.");
            return false;
        }
 
        // Read in the gap rules from the JSON data so that this program
        // can determine the size of the gaps to be looking for.
        try {
            gapRulesJsonArray = jsonObj.getJSONArray("gapRules");

        // Catch any JSONArray initialization errors, if they occur.
        } catch (JSONException je) {
            u.printf("Error: Unable to create JSONArray from gap rules input.");
            return false;
        }

        // Iterate through a list of gap rules and add 'em to the array of rules.
        Iterator<Object> itObj = gapRulesJsonArray.iterator();
        while (itObj.hasNext()) {

            // Assign the current element on the iterator to a variable.
            JSONObject gapElement = (JSONObject) itObj.next();

            // If the element is null or damaged, skip to the next one.
            if (gapElement == null || gapElement.get("gapSize") == null) {
                u.debugPrint("JsonToObjects() --> damaged or unusable " +
                           "element detected, skipping...");
                continue;
            }

            // If debug mode, print out the gap element info.
            u.debugPrint(gapElement.toString());

            // Since the element has been harvested out of the JSON, go
            // ahead and create a new GapRule.
            GapRule gr = new GapRule((int) gapElement.get("gapSize"));

            // Safety check, make sure this actually created a gap element.
            if (gr == null) {
                u.debugPrint("JsonToObjects() --> unable to create " + 
                             "GapRule, skipping...");
                continue;
            }

            // As the rule was created successfully, go ahead and add it to
            // an array holding all of the given gap rules.
            gapRulesArray.add(gr);
        }

        // If the gap rules array currently has zero elements, go ahead and
        // return back since there is no need to check for gaps.
        if (gapRulesArray.isEmpty()) {
            u.printf("No gaps rules were relevant between " +
                   searchStartDate.toString() + " and " +
                   searchEndDate.toString() + ".");
            return true;
        }
 
        // Grab the JSONArray associated with the campsite info.
        try {
            campsitesJsonArray = jsonObj.getJSONArray("campsites");

        // Catch any JSONArray initialization errors, if they occur.
        } catch (JSONException je) {
            u.printf("Error: Unable to create JSONArray from campsites input.");
            return false;
        }

        // Iterate through a list of campsites and add 'em to the array of rules.
        Iterator<Object> itObjCamp = campsitesJsonArray.iterator();
        while (itObjCamp.hasNext()) {

            // Assign the current element on the iterator to a variable.
            JSONObject campsiteElement = (JSONObject) itObjCamp.next();

            // If the element is null or damaged, skip to the next one.
            if (campsiteElement == null || campsiteElement.get("id") == null
              || campsiteElement.get("name") == null) {
                u.debugPrint("JsonToObjects() --> damaged or unusable " +
                           "element detected, skipping...");
                continue;
            }

            // If debug mode, print out the campsite element info.
            u.debugPrint(campsiteElement.toString());

            // Since the element has been harvested out of the JSON, go
            // ahead and create a new Campsite.
            Campsite cs = new Campsite((int) campsiteElement.get("id"),
                                       campsiteElement.get("name").toString());

            // Safety check, make sure this was actually able to initialize
            // a campsite object.
            if (cs == null) {
                u.debugPrint("JsonToObjects() --> unable to create " + 
                             "Campsite, skipping...");
                continue;
            }

            // As the rule was created successfully, go ahead and add it to
            // an array holding all of the given campsite.
            campsites.add(cs);
        }

        // If the campsite array currently has zero elements, go ahead and
        // return back since there is no need to check for elements.
        if (campsites.isEmpty()) {
            u.printf("Note: No campsites were detected. Ergo, a search " + 
                   "returns no results.");
            return true;
        }
 
        // Grab the JSONArray associated with the campsite reservations.
        try {
            reservationsJsonArray = jsonObj.getJSONArray("reservations");

        // Catch any JSONArray initialization errors, if they occur.
        } catch (JSONException je) {
            u.printf("Error: Unable to create JSONArray from reservations input.");
            return false;
        }

        // Iterate through a list of campsites and add 'em to the array of
        // reservations.
        Iterator<Object> itObjReserv = reservationsJsonArray.iterator();
        while (itObjReserv.hasNext()) {

            // Assign the current element on the iterator to a variable.
            JSONObject reservationElement = (JSONObject) itObjReserv.next();

            // If the element is null or damaged, skip to the next one.
            if (reservationElement == null
              || reservationElement.get("campsiteId") == null
              || reservationElement.get("startDate") == null
              || reservationElement.get("endDate") == null) {
                u.debugPrint("JsonToObjects() --> damaged or unusable " +
                           "element detected, skipping...");
                continue;
            }

            // If debug mode, print out the gap element info.
            u.debugPrint(reservationElement.toString());

            // Cast the reservation element start / end date strings to
            // actually java Date objects; this is done for the purpose
            // of easily comparing them later on.
            reservationStartDate
              = u.stringToLocalDate("" + reservationElement.get("startDate"));
            reservationEndDate
              = u.stringToLocalDate("" + reservationElement.get("endDate"));

            // Extract the reservation campsiteId so that it can be checked
            // against all current Campsites to ensure this is a valid
            // reservation.
            int reservationCampsiteId
              = (int) reservationElement.get("campsiteId");

            // Using the reservation's campsiteId, check to see if a valid
            // campsite for this reservation actually exists.
            Campsite cs = null;
            for (int i = 0; i < campsites.size(); i++) {

                // Grab the campsite at that index.
                cs = campsites.get(i);

                // Compare the id of the Campsite with the campsiteId of the
                // Reservation object. If this matches, then break.
                if (cs.getId() == reservationCampsiteId) {
                    break;
                }
            }

            // Sanity check, make sure the campsite is actually valid and
            // not null.
            if (cs == null) {
                u.printf("Error: The following invalid campsite id was " + 
                       "detected while parsing reservations... " +
                       cs.getId());
                return false;
            }
 
            // Since the element has been harvested out of the JSON, go
            // ahead and create a new Reservation.
            Reservation rs = new Reservation(cs,
                                             reservationStartDate,
                                             reservationEndDate);

            // Safety check, make sure initialiazed properly.
            if (rs == null) {
                u.debugPrint("JsonToObjects() --> unable to initialize " +
                             "Reservation object correctly, skipping...");
                continue;
            }

            // As the reservation was created successfully, go ahead and
            // add it to an array holding all of the given reservations.
            reservations.add(rs);
        }

        // If the reservations array currently has zero elements, go ahead and
        // return back since there is no need to check for gaps.
        if (reservations.isEmpty()) {
            u.printf("Note: No campsite reservations were detected. Ergo, " +
                   "a search returns no results.");
            return true;
        }

        // If the search was handled correctly, return true.
        return true;
    }
}
