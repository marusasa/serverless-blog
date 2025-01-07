package ssg.serverlessblog.data_json;

import java.util.List;

public record ReqTag(String name, List<String> articleIds, String tagId, String description) {

}
