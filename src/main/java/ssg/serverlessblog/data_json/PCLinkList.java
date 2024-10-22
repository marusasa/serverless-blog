package ssg.serverlessblog.data_json;

import java.util.List;

/**
 * Class representing LinkList type page component.
 */
public record PCLinkList(String title, List<LinkItem> items) {}
