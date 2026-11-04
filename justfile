
all:
    ./mvnw clean install

core:
    ./mvnw install -f instancio-core/pom.xml

skip-plugins:
    ./mvnw clean install -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip -Dmaven.javadoc.skip

core-tests:
    ./mvnw test -f instancio-tests/instancio-core-tests/pom.xml

feature-tests:
    ./mvnw verify -f instancio-tests/feature-tests/pom.xml

javadoc:
    ./mvnw javadoc:javadoc

benchmark *args:
    ./mvnw clean package -Pbenchmark -pl instancio-benchmark -am -DskipTests -Dpmd.skip -Dcpd.skip -Dcheckstyle.skip
    java -jar instancio-benchmark/target/benchmarks.jar {{args}} | tee instancio-benchmark/results-$(date -u +%FT%T.%3NZ).out

release:
    ./mvnw release:clean release:prepare
    ./mvnw release:perform
    echo "Close, Release: https://central.sonatype.com/publishing/deployments"

site:
    cd website && uv run mkdocs serve
