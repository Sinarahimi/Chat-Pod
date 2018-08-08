package com.fanap.podchat.mainmodel;

public class NosqlSearchMetadataCriteria {
    // is :  can be used for string and number
    private String field;
    private String is;
    private String has;
    private String gt;
    private String gte;
    private String lt;
    private String lte;
    private NosqlSearchMetadataCriteria[] and;
    private NosqlSearchMetadataCriteria[] or;
    private NosqlSearchMetadataCriteria[] not;
}
