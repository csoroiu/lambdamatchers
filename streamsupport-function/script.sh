#!/bin/bash
VERSION=1.5.3
FILENAME=streamsupport-${VERSION}-sources.jar
wget https://repo1.maven.org/maven2/net/sourceforge/streamsupport/streamsupport/${VERSION}/${FILENAME} -N
# -O ${FILENAME}

# unpack and delete the unneeded files
jar -xf ${FILENAME} java8/util/function/ java8/util/Objects.java java8/util/package-info.java
find java8/util/function/ -iname '*.java' | xargs grep -c "^public interface" | grep ":0$" | cut -f 1 -d ':' | xargs rm
jar -xf ${FILENAME} java8/util/function/package-info.java
#fix the problems
sed -e 's/java8.lang.FunctionalInterface/java.lang.FunctionalInterface/' -i java8/util/function/package-info.java

# prepare the destination directory
rm -rf dest
mkdir -p dest/src/main/java/
mv java8 dest/src/main/java/


cp pom-streamsupport-function.xml dest/
mvn -Dversion=${VERSION} clean package -f dest/pom-streamsupport-function.xml
