package com.quickshort.analytics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ClickAnalyticsResponse {
    private List<UrlClickStats> clicksPerUrl;
    private List<GenericStat> deviceStats;
    private List<GenericStat> osStats;
    private List<GenericStat> browserStats;
}