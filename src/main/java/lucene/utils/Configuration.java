package lucene.utils;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

// 配置对象， 提供Lucene需要 索引目录 分词器
public class Configuration {
	private static Directory directory;
	private static Analyzer analyzer;
	private static Version version;

	static {
		try {
			directory = FSDirectory.open(new File("index"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		version = Version.LUCENE_36;
		analyzer = new StandardAnalyzer(version);
	}

	// 提供目录
	public static Directory getDirectory() {
		return directory;
	}

	// 提供分词器
	public static Analyzer getAnalyzer() {
		return analyzer;
	}

	// 获得版本
	public static Version getVersion() {
		return version;
	}

}
