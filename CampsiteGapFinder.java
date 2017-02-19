/*
 * @file           CampsiteGapFinder.java
 *
 * @class          CampsiteGapFinder
 *
 * @author         Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description    The purpose of the program is to be given JSON input data
 *                 concerning a number of different reservations over a
 *                 period of time, and then using the supplied 'gap rule'
 *                 definitions, show the campsites names that currently have
 *                 gaps as per the start and end date of defined in "search".
 *
 * @usage          Build this file as a normal Java binary, and then run it
 *                 via commandline with the intended input file, like so:
 *
 *                 java CampsiteGapFinder test-case.json
 *
 *                 Where "test-case.json" refers to the JSON file that will
 *                 be parsed and searched thru for gaps.
 */

// Imports
import com.campspot.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.text.*;
import java.time.LocalDate;
import org.json.*;
import misc.Utils;

//! Class designed to read in JSON data concerning campsites and parse it.
public class CampsiteGapFinder
{
    /**
     * Class-wide Variables Section
     */

    // Debug mode
    static boolean debugMode = false;

    // Instance of a utility class to easily access various small
    // functions that might be needed.
    static Utils u = new Utils(debugMode);

    // A buffer to hold the byte contents of the input file.
    static byte[] byteBuffer = {};

    // String variables for the possible valid actions.
    static String validActions[] = {"search"};

    // String to hold the action requested, specifically it ought to be
    // one of the from the "validActions" array above.
    static String action = "";

    /**
     * Class Functions Sections
     */

    //! Check if a valid action was requested.
    /*
     * @param     String[]   array of strings to look thru
     *
     * @returns   bool       whether or not a valid action was given
     */
    public static boolean validActionRequested(String[] fieldnames) {

        // Input validation
        if (fieldnames == null || fieldnames.length < 1) {
            u.debugPrint("validActionRequested() --> invalid input");
            return false;
        }

        // Cycle thru the list of fieldnames to determine if a valid action
        // was requested.
        for (int i = 0; i < fieldnames.length; i++) {
            for (int j = 0; j < validActions.length; j++) {
                if (fieldnames[i].equals(validActions[j])) {
                    action = validActions[j];
                    u.debugPrint("validActionRequested() --> valid action " + 
                               "requested was... " + action);
                    return true;
                }
            }
        }

        // Otherwise no valid action was given, so return false.
        u.debugPrint("validActionRequested() --> no valid action found");
        return false;
    }

    //! Program Main
    /*
     * @param      String[]    list of arguments
     *
     * @returns    void         exit code
     *                          0 --> program success
     *                          1 --> program failure
     */
    public static void main(String[] args) {

        // Input validation, make sure this program received arguments.
        if (args.length != 1) {

            // Otherwise print the help text and then leave the program.
            u.printf("usage: java CampsiteGapFinder filename.json\n");
            u.returns(1);
        }

        // Let the developer know this program is running in debug mode,
        // just in case it's not obviously clear...
        u.debugPrint("--------------------------------------------");
        u.debugPrint("CampsiteGapFinder is running in debug mode!");
        u.debugPrint("--------------------------------------------");

        // Convert the argument into a path variable.
        Path jsonPath = FileSystems.getDefault().getPath(".", args[0]);

        // Further input validation, make sure the given JSON file actually
        // exists at the specified location.
        if (Files.isReadable(jsonPath) == false) {
            u.printf("Error: Unable to read the following file:");
            u.printf(args[0]);
            u.returns(1);
        }

        // Dump the filename argument to a more meaningful variable name.
        String jsonInputFile = args[0];

        // Read the input JSON file into the class-wide byte buffer.
        try {
            byteBuffer = Files.readAllBytes(jsonPath);

        // Otherwise reading the json file failed, go ahead and terminate
        // the program.
        } catch (IOException e) {
            u.debugPrint("Error: Unable to copy byte data from the following file:");
            u.debugPrint(jsonInputFile);
            e.printStackTrace();
            u.returns(1);
        }

        // Convert the bytes into a large string.
        String fileContents = new String(byteBuffer);

        // Sanity check, make sure this actually recovered a string and is
        // of non-empty length.
        if (fileContents == null || fileContents.length() < 1) {
            u.printf("Error: Invalid or empty file.");
            u.returns(1);
        }

        // Initialize a new JSONObject.
        JSONObject jsonObj = new JSONObject(fileContents);

        // Sanity check, make sure this was able to initialize correctly.
        if (jsonObj == null) {
            u.printf("Error: Unable to create a valid JSONObject.");
            u.returns(1);
        }

        // If debug, print out the current contents of the JSONObject.
        u.debugPrint("\n------------------------------------------------------");
        u.debugPrint("Mapped Contents of " + jsonInputFile);
        u.debugPrint("------------------------------------------------------");
        u.debugPrint(jsonObj.toString());
        u.debugPrint("------------------------------------------------------\n");

        // Grab the field names present in the json.
        String[] fieldnames = jsonObj.getNames(jsonObj);

        // Sanity check, make sure this was able to actually grab the
        // fieldnames.
        if (fieldnames == null || fieldnames.length < 1) {
            u.printf("Error: Unable to correctly extract fieldname data.");
            u.returns(1);
        }

        // If debug, print out the current contents of the JSONObject.
        u.debugPrint("------------------------------------------------------");
        u.debugPrint("Fieldname Contents of " + jsonInputFile);
        u.debugPrint("------------------------------------------------------");
        for (int i = 0; i < fieldnames.length; i++) {
            u.debugPrint(fieldnames[i]);
        }
        u.debugPrint("------------------------------------------------------\n");

        // Check if the program was given a valid action, note that this
        // function sets a global called `action` which is used later on to
        // determine which course of action ought to be taken.
        if (validActionRequested(fieldnames) == false) {
            u.printf("Note: No valid action requested. Terminating program.");
            u.returns(1);
        }

        // Variable to hold the extracted from any input of JSON objects.
        JsonInputHandler jih = null;

        // Variable to hold whether or not a given action has succeed or
        // failed.
        boolean wasActionSuccessful = false;

        // If a "search" action was requested, perform that.
        if (action == "search") {

            // Initialize the JsonInputHandler as a "search" action.
            jih = new JsonInputHandler(action, debugMode);

            // Convert the objects present in the JSON input file into
            // arrays will be examined later.
            u.debugPrint("\nAttempting to call... jih.JsonToObjects()");
            wasActionSuccessful = jih.JsonToObjects(jsonObj);
            u.debugPrint("\nCompleting call... jih.JsonToObjects()");

            // Initialize a SearchActionHandler since this needs to search
            // thru the data gathered via the JSONInputHandler above.
            SearchActionHandler sah = new SearchActionHandler(jih, debugMode);

            // Sanity check, make sure the SearchActionHandler initialized
            // correctly.
            if (sah == null) {
                wasActionSuccessful = false;
                u.debugPrint("Error: Unable to assign memory for a " +
                             "SearchAction object.");
            }

            // If the action was succesful up to this point, go ahead
            // with attempting to search for gaps.
            u.debugPrint("\nAttempting to call... sah.searchForGaps()");
            wasActionSuccessful
              = (wasActionSuccessful == true) ? sah.searchForGaps() : false;
            u.debugPrint("\nCompleting call... sah.searchForGaps()");

        // A default fall-thru in the event the end-user somehow ends up
        // accessing a valid action that has yet to be implemented.
        } else {
            u.printf("Note: You requested the action `" + action + "` but " +
                     "while this is valid, no functionality has yet been " +
                     "implemented. Please contact the software developers.");
        }

        // If the action failed, give a brief message and return 1 to 
        if (wasActionSuccessful == false) {
           u.printf("Warning: The following action failed... " + action + "\n");
           u.returns(1);
        }

        // Having gathered the results as per the requested action, the
        // program will now end.
        u.debugPrint("The program performed the `" + action + "` action " +
                   "successfully.\n");
        u.returns(0);
    }
}
