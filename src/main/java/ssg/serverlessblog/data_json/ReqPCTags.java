package ssg.serverlessblog.data_json;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Used for updating TextBox page component.
 * 
 * Instead of using a generic 'String' JSON object, it uses a proper class.
 * This guarantees that a mal-formed request will not reach the service, which
 * also prevents mal-formed data going into the data store.
 * Only properly formatted JSON will be parsed and reaches the service.
 */
public record ReqPCTags(ObjectNode  data, long order, boolean enabled) {

}
