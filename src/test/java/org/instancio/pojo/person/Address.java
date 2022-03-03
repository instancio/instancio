package org.instancio.pojo.person;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.List;

@Getter
@Setter
@EqualsAndHashCode
public class Address {

    private String address;
    private String city;
    private String country;
    private List<Phone> phoneNumbers;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
