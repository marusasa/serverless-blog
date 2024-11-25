package ssg.serverlessblog.gae.dao;

import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

import ssg.serverlessblog.data_json.ImageInfo;
import ssg.serverlessblog.documentref.SettingDoc;
import ssg.serverlessblog.interfaces.StorageDaoInt;
import ssg.serverlessblog.system.Env;
import ssg.serverlessblog.util.CloudDocument;

public class StorageDao implements StorageDaoInt {

	@Override
	public void addFile(String fileLoc, String fileName, InputStream content) throws Exception {
		Storage storage = StorageOptions.getDefaultInstance().getService();
		final Optional<CloudDocument> setting = Env.settingDao.getSetting(Env.getAccountIdToUse(null));
		final String bucketName = setting.get().getString(SettingDoc.field_gae_storage_bucket);
	    BlobId blobId = BlobId.of(bucketName, fileLoc + "/" + fileName);    
	    BlobInfo blobInfo = BlobInfo.newBuilder(blobId).build();
	    
	    storage.createFrom(blobInfo, content);

	}

	@Override
	public List<ImageInfo> getImages(String articleId) throws Exception {
		List<ImageInfo> result = new ArrayList<>();
		Storage storage = StorageOptions.getDefaultInstance().getService();
		String directoryPrefix = "ARTICLES/" + articleId + "/";
		final Optional<CloudDocument> setting = Env.settingDao.getSetting(Env.getAccountIdToUse(null));
		final String bucketName = setting.get().getString(SettingDoc.field_gae_storage_bucket);
		Page<Blob> blobs = storage.list(bucketName,
				Storage.BlobListOption.prefix(directoryPrefix), Storage.BlobListOption.currentDirectory());
		
		for (Blob blob : blobs.iterateAll()) {
			if(!blob.getName().isBlank()) {
				final var index = blob.getName().lastIndexOf("/");
				final var url = "https://storage.cloud.google.com/" + bucketName + "/" + blob.getName(); 
				final var image = new ImageInfo(blob.getName().substring(index+1),url);
				result.add(image);
			}
	    }
		
		return result;
	}

}
