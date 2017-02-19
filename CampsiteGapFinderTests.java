/*
 * @file           CampsiteGapFinderTests.java
 *
 * @class          CampsiteGapFinderTests
 *
 * @author         Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description    This file contains a serious of unit tests that are
 *                 helpful for the functionality of the modules included
 *                 in this program:
 *
 *                 1) JsonInputHandler
 *                 2) SearchActionHandler
 *
 *                 Since both of the above modules are quite complex,
 *                 additional unit tests ought to be written. For now the
 *                 included tests will be marginally sufficient for the
 *                 short term as a kind of overview of the sorts of tests
 *                 that are necessary.
 *
 * @usage          Build the CampsiteGapFinderTest as a normal Java binary
 *                 and then run it as show below:
 *
 *                 java CampsiteGapFinderTest
 *
 *                 The unit tests for each of the modules needed for this
 *                 code will then run.
 */

// Other Imports
import com.campspot.*;
import java.time.LocalDate;
import org.json.*;
import org.junit.Assert.*;
import org.junit.*;
import misc.Utils;

// Class begins here.
public class CampsiteGapFinderTests {

    /**
     * Class-wide variables
     */

    // Variable will hide debug messages. Consider setting it to true if
    // the developer wants to get further messages about the events.
    static boolean debugMode = false;

    // Instance of a utility class to easily access various small
    // functions that might be needed.
    static Utils u = new Utils(debugMode);

    /**
     * Class-wide functions
     */

    @Test
    //! Test to ensure the JsonInputHandler can correctly initialize
    /*
     * @returns    none
     */
    public static void testJsonInputHandlerConstructor() {

        // Variable declaration
        String action     = "search";

        // Attempt to initialize a new JsonInput Handler, and check if
        // not null.
        Assert.assertNotNull("JsonInputHandler should initialize to " + 
          "non-null value.",
          new JsonInputHandler(action, debugMode));
    }

    @Test
    //! Test to ensure the JsonInputHandler has a Search object
    /*
     * @returns    none
     */
    public static void testGetSearchObject() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Attempt to grap a uninitialized Search object, which should be
        // null. Anything else suggests an error.
        Assert.assertNull("uninitialized getSearchObject() should be null",
          jih.getSearchObject());
    }

    @Test
    //! Test to ensure the JsonInputHandler has a gap rules list
    /*
     * @returns    none
     */
    public static void testGetGapRulesArray() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Attempt to grap a uninitialized gap rules array, which should be
        // empty. Anything else suggests an error.
        Assert.assertTrue("uninitialized getGapRulesArray() should be empty",
          jih.getGapRulesArray().isEmpty());
    }

    @Test
    //! Test to ensure the JsonInputHandler has a campsite list
    /*
     * @returns    none
     */
    public static void testGetCampsitesArray() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Attempt to grap a uninitialized gap rules array, which should be
        // empty. Anything else suggests an error.
        Assert.assertTrue("uninitialized getCampsitesArray() should be empty",
          jih.getCampsitesArray().isEmpty());
    }

    @Test
    //! Test to ensure the JsonInputHandler has a reservations array
    /*
     * @returns    none
     */
    public static void testGetReservationsArray() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Attempt to grap a uninitialized gap rules array, which should be
        // empty. Anything else suggests an error.
        Assert.assertTrue("uninitialized getGapRulesArray() should be empty",
          jih.getReservationsArray().isEmpty());
    }

    @Test
    //! Test to ensure JsonInputHandler can read JSON input data correctly
    /*
     * @returns    none
     */
    public static void testJsonToObjects() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Various chunks of JSON strings to be used during testing.
        JSONObject jsonObj1  = new JSONObject("{}");
        JSONObject jsonObj2  = new JSONObject("{\"search\":{}}");
        JSONObject jsonObj3  = new JSONObject("{\"search\":{}," +
                                               "\"campsites\":[]," +
                                               "\"gapRules\":[]," +
                                               "\"reservations\":[]}");

        JSONObject jsonObj4  = new JSONObject("{\"search\":{" + 
                                          "\"startDate\": \"2017-02-14\"," +
                                          "\"endDate\": \"2017-02-19\"}," +
                                               "\"campsites\":[]," +
                                               "\"gapRules\":[]," +
                                               "\"reservations\":[]}");

        // Attempt to feed blank input into jih.JsonToObjects(), then
        // should produce a boolean value of false.
        Assert.assertFalse("blank JSON input in JsonToObjects should be " +
          "false", jih.JsonToObjects(jsonObj1));

        // Attempt to feed bad input into jih.JsonToObjects() with only
        // a husk of a "search" action been given. This should return a
        // boolean value of false.
        Assert.assertFalse("piecemeal JSON input in JsonToObjects should " +
          "be false", jih.JsonToObjects(jsonObj2));

        // Attempt to feed empty arrays into jih.JsonToObjects() with only
        // a husk of a "search" action been given. This should return a
        // boolean value of false.
        Assert.assertFalse("partial JSON arrays in JsonToObjects should " +
          "be false", jih.JsonToObjects(jsonObj3));

        // Finally attempt to test with a complete Search object set of
        // attributes. This ought to return true.
        Assert.assertTrue("complete search object in JsonToObjects should " +
          "be true", jih.JsonToObjects(jsonObj4));
    }

    @Test
    //! Test to ensure the SearchActionHandler can correctly initialize
    /*
     * @returns    none
     */
    public static void testSearchActionHandlerConstructor() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Sample JSON input
        JSONObject jsonObj = new JSONObject("{\"search\":{" + 
                                        "\"startDate\": \"2017-02-14\"," +
                                        "\"endDate\": \"2017-02-19\"}," +
                                             "\"campsites\":[]," +
                                             "\"gapRules\":[]," +
                                             "\"reservations\":[]}");

        // Use the above sample JSON input to call JsonToObject() and
        // populate the JsonInputHandler correctly (abet with empty arrays).
        Assert.assertTrue("populate the JsonInputHandler with emptiness, " + 
          "but this should still return true", jih.JsonToObjects(jsonObj));

        // Attempt to create an instance of SearchActionHandler with the
        // empty, yet now valid, JsonInputHandler object. This should be
        // a non-null value.
        Assert.assertNotNull("initializing a SearchActionHandler actually " + 
          "calling JsonToObject(), this should return a value that is *not* " +
          "null", new SearchActionHandler(jih, debugMode));
    }

    @Test
    //! Test to ensure the searchForGaps() function works
    /*
     * @returns    none
     */
    public static void testSearchForGaps() {

        // Variable declaration
        String action        = "search";
        JsonInputHandler jih = new JsonInputHandler(action, debugMode);

        // Sample JSON input
        JSONObject jsonObj = new JSONObject("{\"search\":{" + 
                                        "\"startDate\": \"2017-02-14\"," +
                                        "\"endDate\": \"2017-02-19\"}," +
                                             "\"campsites\":[]," +
                                             "\"gapRules\":[]," +
                                             "\"reservations\":[]}");

        // Use the above sample JSON input to call JsonToObject() and
        // populate the JsonInputHandler correctly (abet with empty arrays).
        Assert.assertTrue("populate the JsonInputHandler with emptiness, " +
          "but this should still return true", jih.JsonToObjects(jsonObj));

        // Attempt to create an instance of SearchActionHandler with the
        // empty, yet now valid, JsonInputHandler object. This should be
        // a non-null value.
        SearchActionHandler sah = new SearchActionHandler(jih, debugMode);
        Assert.assertNotNull("initializing a SearchActionHandler actually " +
          "calling JsonToObject(), this should return a *not* null value",
          sah);

        // Attempt to call searchForGaps(), but since no campsite objects
        // were defined, it should return true and print a message telling
        // the developer that no campsites are present in the array.
        Assert.assertTrue("searching for gaps in an empty campsite array " +
          "ought to return true and print ", sah.searchForGaps());
    }

    //! Program Main for testing
    /*
     * @returns    none
     */
    public static void main(String[] args) {

        // Let the developer know this program is running the JUnit tests,
        // just in case it's not obviously clear...
        u.printf("--------------------------------------------");
        u.printf("CampsiteGapFinderTests are now running...");
        u.printf("--------------------------------------------");

        // Run a quick test of the JsonInputHandler constructor.
        testJsonInputHandlerConstructor();

        // Attempt to access a Search object.
        testGetSearchObject();

        // Attempt to access a gap rules array.
        testGetGapRulesArray();

        // Attempt to access a campsites array.
        testGetCampsitesArray();

        // Attempt to access a reservations array.
        testGetReservationsArray();

        // Run a lengthy test of the SearchActionHandler constructor.
        testSearchActionHandlerConstructor();

        // Attempt to call searchForGaps() to see if any issues occur.
        testSearchForGaps();

        // If the test program got this far, tell the developer all of the
        // tests appear to be a success.
        u.printf("--------------------------------------------");
        u.printf("JUnit tests for CampsiteGapFinder passed with flying " +
                 "colours.\n");
    }
}
