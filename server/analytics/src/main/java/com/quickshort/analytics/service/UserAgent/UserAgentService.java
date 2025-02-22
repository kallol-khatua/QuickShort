package com.quickshort.analytics.service.UserAgent;

import com.quickshort.analytics.model.ClickTracking;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class UserAgentService {
    private final UserAgentAnalyzer userAgentAnalyzer;

    public UserAgentService() {
        this.userAgentAnalyzer = UserAgentAnalyzer.newBuilder().build();
    }

    public ClickTracking getClientInfo(HttpServletRequest request) {
        ClickTracking clickTracking = new ClickTracking();

        String userAgentString = request.getHeader("User-Agent");
        UserAgent userAgent = userAgentAnalyzer.parse(userAgentString);

        String operatingSystem = userAgent.getValue("OperatingSystemName");
        String browser = userAgent.getValue("AgentName");
        String deviceClass = userAgent.getValue("DeviceClass");

        clickTracking.setBrowser(browser);
        clickTracking.setOs(operatingSystem);
        clickTracking.setDevice(deviceClass);

        return clickTracking;
    }
}
