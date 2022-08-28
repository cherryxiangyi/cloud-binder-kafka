package com.amway.dms.springbinder.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventInfo implements Serializable {
    String sourceApplication;
    String sourceTimestamp;
    String sourceHost;
    String sourceEventId;
    String entityType;
    String entityId;
    String eventType;
    String affiliateCode;
    String isoCountryCode;
}
