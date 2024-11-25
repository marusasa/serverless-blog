package ssg.serverlessblog.interfaces;

import java.io.InputStream;
import java.util.List;

import ssg.serverlessblog.data_json.ImageInfo;

public interface StorageDaoInt {
	public void addFile(String fileLoc, String fileName, InputStream content) throws Exception;
	public List<ImageInfo> getImages(String articleId) throws Exception;
}
