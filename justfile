
all:
    mvn clean install

core:
    mvn install -f instancio-core/pom.xml

skip-plugins:
    mvn clean install -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip -Danimal.sniffer.skip -Djavadoc.skip -Dmaven.javadoc.skip

core-tests:
    mvn test -f instancio-tests/instancio-core-tests/pom.xml

feature-tests:
    mvn verify -f instancio-tests/feature-tests/pom.xml

java17-tests:
    mvn test -f instancio-tests/java17-tests/pom.xml

javadoc:
    mvn javadoc:javadoc

release:
    mvn release:clean release:prepare
    mvn release:perform
    echo "Close, Release: https://central.sonatype.com/publishing/deployments"

pip-install-mkdocs:
    pip install mkdocs
    pip install mkdocs-material
    pip install mkdocs-macros-plugin
    pip install mkdocs-autolinks-plugin

