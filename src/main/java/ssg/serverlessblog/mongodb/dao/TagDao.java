package ssg.serverlessblog.mongodb.dao;

import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Sorts;
import com.mongodb.client.model.Updates;
import com.mongodb.client.result.InsertOneResult;

import ssg.serverlessblog.documentref.TagDoc;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.interfaces.TagDaoInt;
import ssg.serverlessblog.mongodb.util.MongoDbUtil;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the PageComponentDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class TagDao implements TagDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(PageComponentDaoInt.class);
	
	private MongoCollection<Document> tags = null;
	private MongoCollection<Document> collection() throws Exception {
		if(tags == null) {
			tags = MongoDbUtil.getDbObj().getCollection(TagDoc.collection);
		}
		return tags;
	}
	
	@Override
	public String createTag(final Map<String, Object> data) throws Exception {
		var doc = new Document();
		doc.putAll(data);		
		InsertOneResult result = collection().insertOne(doc);
		return ((BsonObjectId) result.getInsertedId()).getValue().toHexString();
	}
	
	@Override
	public void updateTag(String tagId, Map<String, Object> data) throws Exception {
		
		Document query = new Document().append("_id",  new ObjectId(tagId)	);
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		collection().updateOne(query, updates);
	}
	
	@Override
	public List<CloudDocument> getTags() throws Exception {
		
		final List<CloudDocument> result = new ArrayList<>();
		
		MongoCursor<Document> cursor = collection().find()
                .sort(Sorts.ascending(TagDoc.field_name)).iterator();
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		
		return result;		
	}
	@Override
	public Optional<CloudDocument> getTag(String tagId) throws Exception {
		Document doc = collection().find(eq("_id", new ObjectId(tagId))).first();
		Optional<CloudDocument> result = Optional.empty();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		return result;	
	}
	
	@Override
	public void deleteTag(String tagId) throws Exception {
		Bson query = eq("_id", new ObjectId(tagId));		
		collection().deleteOne(query);
	}
	
	
}
