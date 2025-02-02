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
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.client.result.UpdateResult;

import ssg.serverlessblog.documentref.PageComponentDoc;
import ssg.serverlessblog.interfaces.PageComponentDaoInt;
import ssg.serverlessblog.mongodb.util.MongoDbUtil;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the PageComponentDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class PageComponentDao implements PageComponentDaoInt{

	private static final Logger logger = LoggerFactory.getLogger(PageComponentDaoInt.class);
		
	private MongoCollection<Document> pageComponents = null;
	private MongoCollection<Document> collection() throws Exception {
		if(pageComponents == null) {
			pageComponents = MongoDbUtil.getDbObj().getCollection(PageComponentDoc.collection);
		}
		return pageComponents;
	}
	
	@Override
	public boolean deletePageComponent( String pageComponentId) throws Exception {
		Bson query = eq("_id", new ObjectId(pageComponentId));		
		DeleteResult result = collection().deleteOne(query);		
		return result.getDeletedCount() > 0?true:false;
	}

	@Override
	public Optional<CloudDocument> getPageComponent(String pageComponentId) throws Exception {
		Document doc = collection().find(eq("_id", new ObjectId(pageComponentId))).first();
		Optional<CloudDocument> result = Optional.empty();
		
		if(doc != null) {
			result = Optional.of(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		return result;		
	}
	
	@Override
	public List<CloudDocument> getPageComponents() throws Exception {
		final List<CloudDocument> result = new ArrayList<>();
		
		MongoCursor<Document> cursor = collection().find()
                .sort(Sorts.ascending(PageComponentDoc.field_view_order)).iterator();
		while(cursor.hasNext()) {
			var doc = cursor.next();
			result.add(new CloudDocument(doc.getObjectId("_id").toHexString(),doc));
		}
		
		return result;
	}

	@Override
	public boolean updatePageComponent(String pageComponentId, Map<String, Object> data) throws Exception {
		
		Document query = new Document().append("_id",  new ObjectId(pageComponentId)	);
		List<Bson> list = MongoDbUtil.convertToBsonArray(data);
		Bson updates = Updates.combine(list);
		UpdateResult result = collection().updateOne(query, updates);
		
		return result.getModifiedCount()>0?true:false;
	}

	@Override
	public String createPageComponent(Map<String, Object> data) throws Exception {
		var doc = new Document();
		doc.putAll(data);
		
		InsertOneResult result = collection().insertOne(doc);
		
		return ((BsonObjectId) result.getInsertedId()).getValue().toHexString();		
	}

}
