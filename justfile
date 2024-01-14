
all:
    mvn clean install

all-no-checks:
    mvn clean install -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip

core:
    mvn install -f instancio-core/pom.xml

core-tests:
    mvn test -f instancio-tests/instancio-core-tests/pom.xml

feature-tests:
    mvn verify -f instancio-tests/feature-tests/pom.xml

java17-tests:
    mvn test -f instancio-tests/java17-tests/pom.xml

javadoc:
    mvn javadoc:javadoc

release:
    mvn release:clean release:prepare -Darguments="-Dmaven.test.skip=true -DskipITs -DskipTests"
    mvn -Prelease,sign release:perform -Darguments="-Dmaven.test.skip=true -DskipITs -DskipTests"
    echo "Close, Release: https://s01.oss.sonatype.org/"

pip-install-mkdocs:
    pip install mkdocs
    pip install mkdocs-material
    pip install mkdocs-macros-plugin
    pip install mkdocs-autolinks-plugin

