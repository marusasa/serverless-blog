package ssg.serverlessblog.data_json;

/**
 * Used for receiving analytics data.
 * 
 */
public record ReqAnalytics(int visitorId, String path, String state) {}
