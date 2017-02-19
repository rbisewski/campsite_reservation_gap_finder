/* 
 * @file            GapRule.java
 *
 * @class           GapRule
 *
 * @author          Robert Bisewski <contact@ibiscybernetics.com>
 *
 * @description     Holds rules for the purpose of measuring gaps between
 *                  given reserved dates at a campsite.
 */

package com.campspot;

public class GapRule
{
    // Variable to hold the number of days of the gap.
    int gapSize;

    // Grab the current gap size.
    public int getSize() {
        return gapSize;
    }

    // Alter the current gap size.
    public void setSize(int newSize) {
        gapSize = newSize;
    }

    // Constructor for the GapRule object.
    public GapRule(int gs) {
        gapSize = gs;
    }
}
