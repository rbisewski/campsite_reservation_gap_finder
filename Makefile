# Java class runner
JAVA = java

# JVM Bytecoder
JC = javac

# Classpath of where the java jars are located
CLASSPATH = -classpath .:/usr/share/java/junit4.jar

#
# Makefile build targets
#

all: app

app:
	@echo "${JC} CampsiteGapFinder.java"
	@${JC} CampsiteGapFinder.java

run: app
	@echo "Running CampsiteGapFinder with default test data..."
	@${JAVA} CampsiteGapFinder test-case.json

tests:
	@echo "${JC} CampsiteGapFinderTests.java ${CLASSPATH}"
	@${JC} CampsiteGapFinderTests.java ${CLASSPATH}

run_tests: tests
	@echo "${JAVA} ${CLASSPATH} CampsiteGapFinderTests"
	@${JAVA} ${CLASSPATH} CampsiteGapFinderTests

clean: 
	@echo "Cleaning up *.class files"
	@rm -f *.class
	@rm -f misc/*.class
	@rm -f com/campspot/*.class
	@rm -f org/json/*.class
