package com.example.urlshortener.urlservice.util;

public final class UserAgentParser {

    private UserAgentParser() {
    }

    public static String detectBrowser(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("edg/")) {
            return "Edge";
        }
        if (ua.contains("chrome/")) {
            return "Chrome";
        }
        if (ua.contains("firefox/")) {
            return "Firefox";
        }
        if (ua.contains("safari/") && !ua.contains("chrome/")) {
            return "Safari";
        }
        return "Other";
    }

    public static String detectDevice(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("ipad") || ua.contains("tablet")) {
            return "Tablet";
        }
        if (ua.contains("mobile") || ua.contains("android") || ua.contains("iphone")) {
            return "Mobile";
        }
        return "Desktop";
    }

    public static String detectOperatingSystem(String userAgent) {
        if (userAgent == null || userAgent.isBlank()) {
            return "Unknown";
        }
        String ua = userAgent.toLowerCase();
        if (ua.contains("windows")) {
            return "Windows";
        }
        if (ua.contains("mac os") || ua.contains("macintosh")) {
            return "macOS";
        }
        if (ua.contains("android")) {
            return "Android";
        }
        if (ua.contains("iphone") || ua.contains("ios")) {
            return "iOS";
        }
        if (ua.contains("linux")) {
            return "Linux";
        }
        return "Other";
    }
}