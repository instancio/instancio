package org.instancio.pojo.person;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
public class Person {
    private static final String STATIC_FINAL_FIELD = "a static final field";

    @Getter
    private static String staticField;

    private final String finalField = "a final field";

    private UUID uuid;
    private String name;
    private Address address;
    private Gender gender;
    private int age;
    private LocalDateTime lastModified;
    private Date date;
    private Pet[] pets;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
