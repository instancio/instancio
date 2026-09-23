
all:
    mvn clean install

core:
    mvn install -f instancio-core/pom.xml

skip-plugins:
    mvn clean install -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip -Djavadoc.skip -Dmaven.javadoc.skip

core-tests:
    mvn test -f instancio-tests/instancio-core-tests/pom.xml

feature-tests:
    mvn verify -f instancio-tests/feature-tests/pom.xml

javadoc:
    mvn javadoc:javadoc

benchmark *args:
    mvn clean package -Pbenchmark -pl instancio-benchmark -am -DskipTests -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip
    java -jar instancio-benchmark/target/benchmarks.jar {{args}} | tee instancio-benchmark/results-$(date -u +%FT%T.%3NZ).out

release:
    mvn release:clean release:prepare
    mvn release:perform
    echo "Close, Release: https://central.sonatype.com/publishing/deployments"

pip-install-mkdocs:
    pip install mkdocs
    pip install mkdocs-autolinks-plugin
    pip install mkdocs-macros-plugin
    pip install mkdocs-material
    pip install mkdocs-minify-html-plugin
