package lucene_day2.utils;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.search.IndexSearcher;

// lucene工具类
public class LuceneUtils {
	private static IndexWriter indexWriter;
	static {
		// 索引目录位置
		try {
			// 写入索引
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Configuration.getVersion(), Configuration.getAnalyzer());
			indexWriter = new IndexWriter(Configuration.getDirectory(),
					indexWriterConfig);

			// 绑定虚拟机退出事件，关闭IndexWriter
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					try {
						indexWriter.close();
					} catch (CorruptIndexException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 提供获取IndexWriter对象
	public static IndexWriter getIndexWriter() {
		return indexWriter;
	}

	// 获得查询IndexSeacher对象
	public static IndexSearcher getIndexSearcher() throws Exception {
		return new IndexSearcher(IndexReader.open(Configuration.getDirectory()));
	}

}
