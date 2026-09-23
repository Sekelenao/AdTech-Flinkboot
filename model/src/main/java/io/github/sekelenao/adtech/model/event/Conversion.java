package io.github.sekelenao.adtech.model.event;

import java.io.Serializable;

/**
 * Represents a user purchase or conversion event.
 */
public class Conversion implements Serializable {

    private static final long serialVersionUID = 1L;

    public long timestamp;
    public String conversionId;
    public String advertiserId;
    public String userId;
    public long orderAmount;
}
