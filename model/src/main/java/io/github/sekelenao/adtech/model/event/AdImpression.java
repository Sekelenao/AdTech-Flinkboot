package io.github.sekelenao.adtech.model.event;

import java.io.Serializable;

/**
 * Represents an ad impression event billed using the CPM model.
 */
public class AdImpression implements Serializable {

    private static final long serialVersionUID = 1L;

    public long timestamp;
    public String impressionId;
    public String campaignId;
    public String advertiserId;
    public String userId;
    public long cost;
}
