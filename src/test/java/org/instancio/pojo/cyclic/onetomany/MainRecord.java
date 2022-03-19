package org.instancio.pojo.cyclic.onetomany;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class MainRecord {
    private Long id;
    private List<DetailRecord> detailRecords;
}
