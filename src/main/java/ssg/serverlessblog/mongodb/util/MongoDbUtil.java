package ssg.serverlessblog.mongodb.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bson.Document;
import org.bson.conversions.Bson;

import com.google.cloud.Timestamp;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Updates;

import ssg.serverlessblog.interfaces.DataUtilInt;
import ssg.serverlessblog.util.AppProperties;
import ssg.serverlessblog.util.CloudDocument;


public class MongoDbUtil implements DataUtilInt{
	
	
	private static MongoDatabase db = null; 
	
	public static MongoDatabase getDbObj() throws Exception  {
		
		if(db == null) {
			//Use this when connecting to Mongo DB running in docker from
			//non-docker run-time.
//			var uri = "mongodb://localhost:27018/";
			//Use this when this app and Mongo DB is both running in docker.
//			var uri = "mongodb://sb-mongo:27017/";
			var uri = AppProperties.getString("mongodb.uri");
			MongoClient mongoClient = MongoClients.create(uri);
			db = mongoClient.getDatabase("serverless-blog");
		}
		return db;
	}
	
	public static List<Bson> convertToBsonArray(Map<String, Object> map) {
        List<Bson> bsonArray = new ArrayList<>();

        for (String key : map.keySet()) {
            bsonArray.add(Updates.set(key, map.get(key)));
        }

        return bsonArray;
    }
	
	final static SimpleDateFormat sdfGMT =
	        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.'000Z'");
	{
		sdfGMT.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	@Override
	public String getUtcString(CloudDocument doc, String fieldId) {
		// Convert Date() to '2024-10-25T04:36:17.743Z', which is 
		//UTC timezone compatible in Javascript
		String result = "";
		if(!doc.isNull(fieldId)) {
			result = sdfGMT.format((Date)doc.get(fieldId));
		}		
		return result;
	}

}
