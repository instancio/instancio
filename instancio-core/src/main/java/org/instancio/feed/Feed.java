/*
 * Copyright 2022-2025 the original author or authors.
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

import org.instancio.documentation.ExperimentalApi;
import org.instancio.settings.FeedDataAccess;
import org.instancio.settings.FeedFormatType;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This interface provides support for using data from external sources
 * (such as CSV or JSON files) and enables the following use cases:
 *
 * <ol>
 *   <li>Generating values using methods defined by a feed.</li>
 *   <li>Populating values via the {@code generate()} method.</li>
 *   <li>Mapping feed data to an object using {@code applyFeed()}.</li>
 *   <li>Using a feed instance as a parameterized test argument.</li>
 * </ol>
 *
 * <p>Examples of the first three use cases are provided below. To illustrate
 * the usage, we will assume the existence of a {@code persons.csv} file
 * with the following contents (formatted for clarity):
 *
 * <pre>
 * firstName, lastName, age
 * John,      Doe,      21
 * Alice,     Smith,    34
 * Bob,       Brown,    67
 * # snip...
 * </pre>
 *
 * <h2>1. Generating values using methods defined by a feed</h2>
 *
 * <p>The simplest use case is mapping the file to a subclass
 * of {@code Feed} without declaring any methods:
 *
 * <pre>{@code
 * @Feed.Source(resource = "persons.csv")
 * interface PersonFeed extends Feed {}
 * }</pre>
 *
 * <p>This allows accessing the data using built-in methods provided
 * by the {@code Feed} interface. The return type of these methods
 * is {@link FeedSpec}. For example:
 *
 * <pre>{@code
 * PersonFeed feed = Instancio.ofFeed(PersonFeed.class);
 *
 * String firstName = feed.stringSpec("firstName").get(); // John
 * String lastName = feed.stringSpec("lastName").get();  // Doe
 * Integer age = feed.intSpec("age").get();  // 21
 * }</pre>
 *
 * <p>{@link FeedSpec} also allows retrieving a list of values:
 *
 * <pre>{@code
 * List<String> firstNamesList = feed.stringSpec("firstName").list(3); // [John, Alice, Bob]
 * }</pre>
 *
 * <p>Note that by default, feed data is provided in sequential order.
 *
 * <p>To make the use of feeds more convenient, subclasses of {@code Feed}
 * can declare methods that return the {@link FeedSpec}. Method names
 * will automatically map to the matching properties in the data source:
 *
 * <pre>{@code
 * @Feed.Source(resource = "persons.csv")
 * interface PersonFeed extends Feed {
 *     FeedSpec<String> firstName();
 *     FeedSpec<String> lastName();
 *     FeedSpec<Integer> age();
 * }
 * }</pre>
 *
 * <h2>2. Populating values via the {@code generate()} method</h2>
 *
 * <p>Feeds can also be used to generate values when creating an object.
 * For example, using the {@code PersonFeed} defined above:
 *
 * <pre>{@code
 * PersonFeed feed = Instancio.createFeed(PersonFeed.class);
 *
 * List<Person> personList = Instancio.ofList(Person.class)
 *     .size(10)
 *     .generate(field(Person::getFirstName), feed.firstName())
 *     .generate(field(Person::getLastName), feed.lastName())
 *     .generate(field(Person::getAge), feed.age().nullable())
 *     .create();
 * }</pre>
 *
 * <h2>3. Mapping feed data to an object using {@code applyFeed()}</h2>
 *
 * <p>If feed property names match field names of the target class,
 * data can be mapped automatically using the {@code applyFeed()} method:
 *
 * <pre>{@code
 * Feed personFeed = Instancio.createFeed(PersonFeed.class);
 *
 * List<Person> personList = Instancio.ofList(Person.class)
 *     .size(10)
 *     .applyFeed(all(Person.class), personFeed)
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
     * <p>This annotation can be used on a {@link Feed} interface to override
     * the default data access mode set via {@link Keys#FEED_DATA_ACCESS}.
     * By using this annotation, the feed can explicitly declare whether
     * data should be accessed sequentially or randomly.
     *
     * <p>For example, the data access mode for the following
     * {@code SampleFeed} class will be random, regardless of
     * the {@link Keys#FEED_DATA_ACCESS} setting.
     *
     * <p>Example:
     * <pre>{@code
     * @Feed.DataAccess(FeedDataAccess.RANDOM)
     * @Feed.Source(name = "data/sample.csv")
     * interface SampleFeed extends Feed {
     *     // ...
     * }
     * }
     * </pre>
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
     * Annotation for specifying the data format type for a feed.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface FormatType {

        /**
         * Specifies the data format type for this feed.
         *
         * @return the data format type
         * @since 5.0.0
         */
        @ExperimentalApi
        FeedFormatType value();
    }

    /**
     * Annotation for specifying a property to use as a tag key in a data feed.
     *
     * <p>A tag key can be used for filtering records in a data feed.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface TagKey {

        /**
         * Specifies the name of a property to use as the tag key.
         *
         * @return property that acts as a tag.
         * @see Keys#FEED_TAG_KEY
         * @since 5.0.0
         */
        String value();
    }

    /**
     * An annotation for specifying data for a {@link Feed}.
     *
     * @since 5.0.0
     */
    @ExperimentalApi
    @Target(ElementType.TYPE)
    @Retention(RetentionPolicy.RUNTIME)
    @interface Source {

        /**
         * Specifies inline data for a feed  as a string.
         *
         * @return the data for a feed as a string
         * @since 5.0.0
         */
        @ExperimentalApi
        String string() default "";

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
        String resource() default "";

        /**
         * Specifies the path to a file on the filesystem
         * containing the data for a feed.
         *
         * @return the path to a file
         * @since 5.0.0
         */
        @ExperimentalApi
        String file() default "";
    }
}
