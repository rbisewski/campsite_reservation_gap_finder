# CampsiteGapFinder - campsite gap rule app written in java

The goal of this program is to take in JSON data, parse it to a series of
arrays, and then search through these arrays to determine whether or not a
new gap has formed as a result of placing a reservation at a given campsite.

Defining exactly what a gap is can be determined by the gapSize property of
any given gap rules. In the included file, test-case.json, the default gap
sizes are 2 and 3 days.

For developers, the java code files in this project are reasonably well
documented, including a basic usage guide of the current functionality
of the individual classes. Consider reading each of them if you plan on
extending this code.

See the requirements, building and running sections of this readme for more
information about how to get this up and running.

Primarily this uses the GNU make system to handle both the running and
the building of the project, as well as the associated JUnit tests.

The project uses a self-contained JSON parsing library for the sake of
a minimalist design, as favoured by this author. See the author section at
the end of this file for further info.

# Requirements

This program was developed on the standard debian jessie docker image. In
order to access some of the needed packages, you may need to add the
following repos to /etc/apt/sources.list file:

    deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main
    deb-src http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main

With the above added, the following packages are required:

* oracle-java8-installer
* oracle-java8-set-default
* junit4
* make

Recommend examining the Makefile for more details if you are unable to get
CampsiteGapFinder to properly convert to bytecode .class files.

Feel free to contact the author if the aforementioned code does not appear
to work on your platform or for further assistance.


# Building

Start up a standard debian docker image and grab the code via git. If you
have installed all of the above packages correctly, then the following
commands can be used:

Enter the following command to do a quick build of CampsiteGapFinder:

    make app

The command to both build and run CampsiteGapFinder is:

    make run

JUnit tests can be built and ran using the following command:

    make run_tests

To clear away any class files:

    make clean

In the event that the above do not work, edit the Makefile to adjust the
respective java path requirements to the proper location, typically on
Debian it is located here:

/usr/share/java/


# Running the gap finder and the unit tests

To do a search for gaps present in given JSON input:

    java CampsiteGapFinder test-case.json

To run unit tests to determine the effects of any code changes:

    make run_tests

If you have already built the JUnit tests as per the `make run_tests` command
above, yet want to conduct another quick run:

    java -classpath .:/usr/share/java/junit4.jar CampsiteGapFinderTests

Otherwise the `make run_tests` command is the recommended method of executing
the JUnit tests, since it will both rebuild and run the tests instantly,
allowing the developer to determine if any new code changes have caused any
unit tests to fail.

# Author

The self-contained library used for reading the JSON input data was created
by the folks at JSON.org. Specifically, the files located in the org/json/
directory. Consider contacting them to learn more about the uses of JSON:

* Website -> www.json.org

All of the other code was written by Robert Bisewski at Ibis Cybernetics.
For more information, contact:

* Website -> www.ibiscybernetics.com

* Email -> contact@ibiscybernetics.com
