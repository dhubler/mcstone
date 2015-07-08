JAVA_SRC = $(shell find src \( -name '*.java' -a -not -name '*Test.java' \) -type f)
CLASSPATH = classes:lib/minecraft_server.1.8.jar:lib/restconf.jar

all : compile jar

.PHONY: compile jar
compile :
	javac -Xlint:unchecked -d classes -cp $(CLASSPATH) $(JAVA_SRC)

jar :
	jar -cf mcstone.jar -C classes .
	jar -uf mcstone.jar -C src org/conf2/mcstone/mcstone.yang

