package io.github.sekelenao.adtech.model.event;

import java.io.Serializable;

/**
 * Represents an ad click event billed using the CPC model.
 */
public class AdClick implements Serializable {

    private static final long serialVersionUID = 1L;

    public long timestamp;
    public String clickId;
    public String impressionId;
    public String campaignId;
    public String advertiserId;
    public String userId;
    public long cost;
}
