/*
 * Copyright 2022-2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.feed;

import org.instancio.InstancioApi;
import org.instancio.InstancioFeedApi;
import org.instancio.TargetSelector;
import org.instancio.documentation.ExperimentalApi;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This class provides an abstraction for mapping an external
 * data source to a collection of methods that return a {@link FeedSpec}.
 * This is an experimental feature. Currently, data sources are limited
 * to CSV files only.
 *
 * <p>Note that this interface may be extended, however it should
 * <b>not</b> be implemented.
 *
 * <h2>Example</h2>
 * <p>Given the following CSV file:
 *
 * <pre>
 * firstName, lastName, age
 * John,      Doe,      21
 * Alice,     Smith,    34
 * # snip...
 * </pre>
 *
 * <p>a feed can be defined as follows:
 *
 * <pre>{@code
 * @FeedSource(path = "persons.csv")
 * interface PersonFeed extends Feed {
 *     FeedSpec<String> firstName();
 *     FeedSpec<String> lastName();
 *     FeedSpec<Integer> age();
 * }
 * }</pre>
 *
 * <p>With this setup in place, feeds support the following use cases.
 *
 * <h2>1. Using the feed directly to get values</h2>
 *
 * <pre>{@code
 * PersonFeed feed = Instancio.createFeed(PersonFeed.class);
 *
 * // invoking feed methods will select a random record from the file:
 * feed.firstName().get(); // Alice
 * feed.lastName().get();  // Smith
 * feed.lastName().get();  // 34
 *
 * // subsequent invocations will select a new random record from the file:
 * feed.firstName().get(); // John
 * feed.lastName().get();  // Doe
 * feed.lastName().get();  // 21
 * }</pre>
 *
 * <h2>2. Using the feed with the {@code generate()} method</h2>
 *
 * <p>Assuming we have a {@code Person} POJO, we can populate it with
 * data from the feed as follows:
 *
 * <pre>{@code
 * PersonFeed feed = Instancio.createFeed(PersonFeed.class);
 *
 * List<Person> person = Instancio.ofList(Person.class)
 *     .size(10)
 *     .generate(field(Person::getFirstName), feed.firstName())
 *     .generate(field(Person::getLastName), feed.lastName())
 *     .create();
 * }</pre>
 *
 * <h2>3. Mapping feed properties to objects</h2>
 * <p>
 * This can be done using {@link InstancioApi#withFeed(TargetSelector, Feed)}
 * (see the method's Javadoc for more details):
 *
 * <pre>{@code
 * Feed personFeed = Instancio.createFeed(PersonFeed.class);
 *
 * List<Person> person = Instancio.ofList(Person.class)
 *     .size(10)
 *     .withFeed(all(Person.class), personFeed)
 *     .create();
 * }</pre>
 *
 * @since 5.0.0
 */
@ExperimentalApi
public interface Feed extends FeedSpecAccessors, FeedSpecAnnotations {

    /**
     * Specifies the data access strategy to be used by a feed.
     *
     * <p>This annotation can be used on a {@link Feed} interface to
     * override the default data access strategy set in the {@link Settings}.
     * By using this annotation, the feed can explicitly declare whether
     * data should be accessed sequentially or randomly.
     *
     * <p>Example:
     * <pre>{@code
     * @Feed.DataAccess(FeedDataAccess.RANDOM)
     * @Feed.Source(name = "data/sample.csv")
     * interface SampleFeed extends Feed {
     *     // snip...
     * }
     * }
     * </pre>
     *
     * <p>In this example, the data access strategy for {@code SampleFeed}
     * will be random, regardless of the {@link Keys#FEED_DATA_ACCESS} setting.
     *
     * @see FeedDataAccess
     * @see Settings
     * @since 5.0.0
     */
    @ExperimentalApi
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface DataAccess {

        /**
         * Specifies the data access strategy to be used by the annotated feed.
         *
         * @return the data access strategy
         * @since 5.0.0
         */
        @ExperimentalApi
        FeedDataAccess value();
    }

    /**
     * An annotation for specifying data for a {@link Feed}.
     *
     * <p>The sources of data can be (in order of precedence
     * starting from highest):
     *
     * <ul>
     *   <li>inline data specified via {@link Source#data()}</li>
     *   <li>custom {@link DataSource} specified via the
     *      {@link InstancioFeedApi#withDataSource(DataSourceProvider)}
     *   </li>
     *   <li>file specified via {@link Source#path()} or {@link Source#name()}</li>
     * </ul>
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Source {

        /**
         * Specifies inline data for a feed.
         *
         * @return the data for a feed as a string
         * @since 5.0.0
         */
        @ExperimentalApi
        String data() default "";

        /**
         * Specifies the name of a classpath resource
         * containing the data for a feed.
         *
         * <p>A given value should not have a leading slash.
         * For example, {@code "sample.csv"} will load the specified
         * file from the root of the classpath.
         *
         * @return the name of a classpath resource
         * @since 5.0.0
         */
        @ExperimentalApi
        String name() default "";

        /**
         * Specifies the path to a file on the filesystem
         * containing the data for a feed.
         *
         * @return the path to a file
         * @since 5.0.0
         */
        @ExperimentalApi
        String path() default "";

        /**
         * Specifies the name of a property to use as the tag key.
         *
         * @return property that acts as a tag.
         * @since 5.0.0
         */
        @ExperimentalApi
        String tagKey() default "";
    }
}
