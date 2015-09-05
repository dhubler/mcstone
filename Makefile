YANGC2DIR = $(abspath ../yang-c2)
API_VER = 0.1
SRC = $(shell find src \( -name '*.java' -a -not -name '*Test.java' \) -type f)
CLASSPATH = classes:lib/minecraft_server.1.8.jar:$(YANGC2DIR)/drivers/java/lib/yangc2-$(API_VER).jar
GO_ARCH = linux_amd64
TEST_RUNNER = org.junit.runner.JUnitCore
TESTS = \
	org.conf2.mcstone.StoneGameTest \
	org.conf2.mcstone.StoneBrowserTest

TEST_SRC = \
	$(shell find src -name '*Test.java')

TEST_JARS = \
	$(wildcard lib/hamcrest-core-*.jar) \
	$(wildcard lib/hamcrest-library-*.jar) \
	$(wildcard lib/junit-*.jar)

EMPTY :=
SPACE := $(EMPTY) $(EMPTY)
TEST_CP = classes:$(subst $(SPACE),:,$(TEST_JARS)):$(CLASSPATH)

all : compile jar

.PHONY: compile jar
compile :
	javac -Xlint:unchecked -d classes -cp $(CLASSPATH) $(SRC)

jar :
	jar -cf mcstone.jar -C classes .
	jar -uf mcstone.jar -C src org/conf2/mcstone/mcstone-lite.yang

test :
	test -d test-classes || mkdir test-classes
	javac -d test-classes -cp $(TEST_CP) $(TEST_SRC)
	LD_LIBRARY_PATH=$(YANGC2DIR)/pkg/$(GO_ARCH)_shared \
	  CLASSPATH=src:test-classes:$(TEST_CP) \
	  java $(TEST_RUNNER) $(TESTS)
