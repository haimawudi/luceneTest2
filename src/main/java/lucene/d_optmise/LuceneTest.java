package lucene.d_optmise;

import java.io.File;
import java.io.IOException;

import lucene.utils.Configuration;
import lucene.utils.LuceneUtils;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.LogDocMergePolicy;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

public class LuceneTest {
	// 使用内存路径 进行优化
	@Test
	public void demo3() throws Exception {
		// 一、程序启动时把索引库都加载到内存中
		Directory fsDir = FSDirectory.open(new File("index"));
		Directory ramDir = new RAMDirectory(fsDir);

		// 二、程序运行时的正常使用
		Document document = new Document();
		document.add(new Field("company", "传智播客", Store.YES, Index.ANALYZED));

		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_36, new StandardAnalyzer(Version.LUCENE_36));
		indexWriterConfig.setOpenMode(OpenMode.CREATE);
		IndexWriter ramIndexWriter = new IndexWriter(ramDir, indexWriterConfig);
		ramIndexWriter.addDocument(document);
		ramIndexWriter.close();

		// 三、程序退出前，把内存中的索引库写到文件系统中
		IndexWriter fsIndexWriter = LuceneUtils.getIndexWriter();
		fsIndexWriter.addIndexes(ramDir); // 把指定索引库中的数据合并到当前索引库中
	}

	// 设置合并因子 为3
	@Test
	public void demo2() throws Exception {
		for (int i = 1; i <= 9; i++) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_36, Configuration.getAnalyzer());
			// 设置合并因子
			LogDocMergePolicy mergePolicy = new LogDocMergePolicy();
			mergePolicy.setMergeFactor(3);
			indexWriterConfig.setMergePolicy(mergePolicy);
			
			
			IndexWriter indexWriter = new IndexWriter(
					Configuration.getDirectory(), indexWriterConfig);
			Document document = new Document();
			document.add(new Field("id", i + "", Store.YES, Index.NOT_ANALYZED));
			indexWriter.addDocument(document);
			indexWriter.close();
		}
	}

	// 创建八个对象索引
	@Test
	public void demo1() throws CorruptIndexException, IOException {

		for (int i = 1; i <= 8; i++) {
			IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
					Version.LUCENE_36, Configuration.getAnalyzer());
			IndexWriter indexWriter = new IndexWriter(
					Configuration.getDirectory(), indexWriterConfig);
			Document document = new Document();
			document.add(new Field("id", i + "", Store.YES, Index.NOT_ANALYZED));
			indexWriter.addDocument(document);
			indexWriter.close();
		}
	}
}
