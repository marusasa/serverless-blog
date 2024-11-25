package ssg.serverlessblog.gae.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.core.ApiFuture;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.AggregateQuery;
import com.google.cloud.firestore.AggregateQuerySnapshot;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.Query.Direction;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteResult;
import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.api.GenerationConfig;
import com.google.cloud.vertexai.api.HarmCategory;
import com.google.cloud.vertexai.api.SafetySetting;
import com.google.cloud.vertexai.generativeai.ContentMaker;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseStream;

import ssg.serverlessblog.data_json.Article;
import ssg.serverlessblog.documentref.ArticleDoc;
import ssg.serverlessblog.documentref.ArticleLikeDoc;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.gae.util.FirestoreDbUtil;
import ssg.serverlessblog.interfaces.ArticleDaoInt;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.AppConst;
import ssg.serverlessblog.util.CloudDocument;

/**
 * Data Access Object.
 * Google Cloud implementation of the ArticleDao.
 * 
 * It connects with Google Cloud Datastore (Firestore native mode).
 */
public class ArticleDao implements ArticleDaoInt {

	private static final Logger logger = LoggerFactory.getLogger(ArticleDao.class);
	
	private CollectionReference articles = null;
	private CollectionReference collection() throws IOException {
		if(articles == null) {
			articles = FirestoreDbUtil.getFirestoreDbObj().collection(ArticleDoc.collection);
		}
		return articles;
	}
	
	private CollectionReference articles_likes = null;
	private CollectionReference collectionLikes() throws IOException {
		if(articles_likes == null) {
			articles_likes = FirestoreDbUtil.getFirestoreDbObj().collection(ArticleLikeDoc.collection);
		}
		return articles_likes;
	}
		
	
	
	@Override
	public long incrementArticleLike(final String articleId) throws Exception {
		DocumentReference docRef = collectionLikes().document(articleId);			
		ApiFuture<DocumentSnapshot> future = docRef.get();
		DocumentSnapshot document = future.get();
		long count = 0L;
		if(!document.exists()) {				
			//create new record.
			count = 1L;
			final Map<String, Object> data = new HashMap<>();
			data.put(ArticleLikeDoc.field_like_count,count);
			data.put(ArticleLikeDoc.field_updated_at, Timestamp.now());
			final ApiFuture<WriteResult> wr = collectionLikes().document(articleId).set(data);
			wr.get();
			docRef = collectionLikes().document(articleId);
			future = docRef.get();
			document = future.get();
		}else {
			//Update record. Increment value.
			count = document.getLong(ArticleLikeDoc.field_like_count);
			count++;
			final Map<String, Object> updates = new HashMap<>();
			updates.put(ArticleLikeDoc.field_like_count,count);
			updates.put(ArticleLikeDoc.field_updated_at, Timestamp.now());
			final ApiFuture<WriteResult> writeResult = docRef.update(updates);
		    writeResult.get();
		}
		return count;		
	}



	@Override
	public boolean isArticleExists(final String articleId) throws Exception {
		boolean result = false;
		final DocumentReference docRef = collection().document(articleId);			
		final ApiFuture<DocumentSnapshot> future = docRef.get();
		final DocumentSnapshot document = future.get();
		if(document.exists()) {				
			result = true;
		}
		return result;
	}



	@Override
	public String createArticle(final String accountId,final Article article) throws Exception {
		var newId = "";
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			
			//add article to database
			final Map<String, Object> data = new HashMap<>();
			data.put(ArticleDoc.field_ref_account_id,accountDocRef);
			data.put(ArticleDoc.field_title, article.title());
			data.put(ArticleDoc.field_body, article.body());
			data.put(ArticleDoc.field_status, article.status());
			data.put(ArticleDoc.field_created_at, Timestamp.now());
			if(article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
				Timestamp now = Timestamp.now();
				data.put(ArticleDoc.field_published_at, now);
				data.put(ArticleDoc.field_published_at_millisec, now.toDate().getTime());
			}else {
				data.put(ArticleDoc.field_published_at, null);
				data.put(ArticleDoc.field_published_at_millisec, null);
			}
			data.put(ArticleDoc.field_updated_at, null);
			data.put(ArticleDoc.field_summary, "");	//initially blank
			data.put(ArticleDoc.field_summary_ai, true);
			
			final ApiFuture<DocumentReference> docRef = collection().add(data);
			newId = docRef.get().getId();
		}catch(Exception e) {
			throw e;
		}
		return newId;
	}

	@Override
	public Optional<CloudDocument> getArticleForManage(final String accountId,final String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final DocumentReference docRef = collection().document(articleId);			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			
			//make sure it belongs to this account
			final DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				result = Optional.of(new CloudDocument(document.getId(), document.getData()));
			}else {
				logger.warn("Article %s not authorized with account %s".formatted(articleId,accountId));
				return result;
			}						
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public Optional<CloudDocument> getArticle(final String articleId) 
			throws Exception {
		Optional<CloudDocument> result = Optional.empty();
		try {
			final DocumentReference docRef = collection().document(articleId);			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(document.exists()) {				
				final DocumentReference docRefLike = collectionLikes().document(articleId);			
				final ApiFuture<DocumentSnapshot> futureLike = docRefLike.get();
				final DocumentSnapshot docLike = futureLike.get();
				final Map<String,Object> data = document.getData();
				long likes = 0l;
				if(docLike.exists()) {
					likes = docLike.getLong(ArticleLikeDoc.field_like_count);
				}
				data.put(ArticleLikeDoc.field_like_count, likes);
				result = Optional.of(new CloudDocument(document.getId(), data));
			}else {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}				
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public boolean updateArticle(final String accountId, final Article article) throws Exception {
		var result = false;
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final DocumentReference docRef = collection().document(article.articleId());
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(article.articleId()));
				return result;
			}
			
			final DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(refAccount.equals(accountDocRef)) {
				//update document
				final var oldStatus = document.getString(ArticleDoc.field_status);
				final Map<String, Object> updates = new HashMap<>();
				updates.put(ArticleDoc.field_title, article.title());
				updates.put(ArticleDoc.field_body, article.body());
				updates.put(ArticleDoc.field_status, article.status());
				updates.put(ArticleDoc.field_updated_at, Timestamp.now());
				updates.put(ArticleDoc.field_summary, article.summary());
				updates.put(ArticleDoc.field_summary_ai, true);
				
				if(oldStatus.equals(AppConst.ART_STATUS_DRAFT) && article.status().equals(AppConst.ART_STATUS_PUBLISH)) {
					Timestamp now = Timestamp.now();
					updates.put(ArticleDoc.field_published_at, now);
					updates.put(ArticleDoc.field_published_at_millisec, now.toDate().getTime());
				}
				final ApiFuture<WriteResult> writeResult = docRef.update(updates);
			    writeResult.get();				
				result = true;
			}else {
				logger.warn("Article %s not authorized with account %s".formatted(article.articleId(),accountId));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}

	@Override
	public List<CloudDocument> getArticlesForManage(final String accountId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.orderBy(ArticleDoc.field_created_at, Direction.DESCENDING);
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				//make the response light
				final var data = document.getData();
				data.remove(ArticleDoc.field_body);
				data.remove(ArticleDoc.field_summary);
				result.add(new CloudDocument(document.getId(), data));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlog(final String accountId, long publishedAtMillisec, int countPerPage) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH)
					.orderBy(ArticleDoc.field_published_at_millisec, Direction.DESCENDING)
					.startAfter(publishedAtMillisec)
					.limit(countPerPage);
			
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), document.getData()));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public List<CloudDocument> getArticlesForBlogAll(final String accountId) throws Exception {
		final List<CloudDocument> result = new ArrayList<CloudDocument>();
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH)
					.orderBy(ArticleDoc.field_published_at_millisec, Direction.DESCENDING);			
			
			final ApiFuture<QuerySnapshot> future = query.get();
			final QuerySnapshot qs = future.get();
				
			for (QueryDocumentSnapshot document : qs.getDocuments()) {
				result.add(new CloudDocument(document.getId(), document.getData()));
			}
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	

	@Override
	public long getArticlesForBlogTotalCount(String accountId) throws Exception {
		long result = 0;
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final Query query = collection()
					.whereEqualTo(ArticleDoc.field_ref_account_id, accountDocRef)
					.whereEqualTo(ArticleDoc.field_status, AppConst.ART_STATUS_PUBLISH);
			AggregateQuery countQuery = query.count();
			
			ApiFuture<AggregateQuerySnapshot> future =  countQuery.get();
			result = future.get().getCount();			
		}catch(Exception e) {
			throw e;
		}
		return result;
	}



	@Override
	public boolean deleteArticle(final String accountId, final String articleId) throws Exception {
		var result = false;
		try {
			final DocumentReference accountDocRef = AccountDao.getAccountDocRef(accountId);
			final DocumentReference docRef = collection().document(articleId);
			
			final ApiFuture<DocumentSnapshot> future = docRef.get();
			final DocumentSnapshot document = future.get();
			if(!document.exists()) {
				logger.warn("Article document not found: %s".formatted(articleId));
				return result;
			}
			
			final DocumentReference refAccount = (DocumentReference)document.get(ArticleDoc.field_ref_account_id); 
			if(!refAccount.equals(accountDocRef)) {
				logger.warn("Article %s not authorized with account %s".formatted(articleId,accountId));
				return result;
			}		
			
			ApiFuture<WriteResult> writeResult = docRef.delete();
			writeResult.get();
			result = true;
		}catch(Exception e) {
			throw e;
		}
		return result;
	}
	
	@Override
	public String generateAiSummary(final String accountId, final String articleId) throws Exception {
		final var result = new StringBuilder();
		final Optional<CloudDocument> setting = Env.settingDao.getSetting(Env.getAccountIdToUse(null));
		try (VertexAI vertexAi = new VertexAI(setting.get().getString(SettingDoc.field_gae_ai_project_id), 
				setting.get().getString(SettingDoc.field_gae_ai_location));) {
			// get article
			final Optional<CloudDocument> op = getArticleForManage(accountId, articleId);
			if (op.isPresent()) {
				final CloudDocument document = op.get();
				final var text1 = document.getString(ArticleDoc.field_body);
				final var textsi_1 = 
						"""
						You are a research bot, tasked with helping college students research quicker. 
						Your job is to summarize the texts submitted to you.
						Be sure to:
						* keep your summaries under 160 characters
						* present it so people will want to read the full text
						* focus on the main points of the text
						* keep it condense and to the point
						* do not hallucinate
						""";

				final GenerationConfig generationConfig = GenerationConfig.newBuilder().setMaxOutputTokens(1024)
						.setTemperature(2F).setTopP(0.95F).build();
				final List<SafetySetting> safetySettings = Arrays.asList(
						SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HATE_SPEECH)
								.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
						SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_DANGEROUS_CONTENT)
								.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
						SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_SEXUALLY_EXPLICIT)
								.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build(),
						SafetySetting.newBuilder().setCategory(HarmCategory.HARM_CATEGORY_HARASSMENT)
								.setThreshold(SafetySetting.HarmBlockThreshold.OFF).build());
				final var systemInstruction = ContentMaker.fromMultiModalData(textsi_1);
				final GenerativeModel model = new GenerativeModel.Builder().setModelName("gemini-1.5-flash-002")
						.setVertexAi(vertexAi).setGenerationConfig(generationConfig).setSafetySettings(safetySettings)
						.setSystemInstruction(systemInstruction).build();

				
				final var content = ContentMaker.fromMultiModalData(text1);
				final ResponseStream<GenerateContentResponse> responseStream = model.generateContentStream(content);

				responseStream.stream().forEach(t -> {
					t.getCandidatesList().forEach(c -> {
						result.append(c.getContent().getParts(0).getText());
					});
				});
			}
		}
		return result.toString();
	}

}
