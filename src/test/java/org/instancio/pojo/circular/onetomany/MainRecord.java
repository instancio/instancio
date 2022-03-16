package org.instancio.pojo.circular.onetomany;

import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@ToString
public class MainRecord {
    private Long id;
    private List<DetailRecord> detailRecords;
}
