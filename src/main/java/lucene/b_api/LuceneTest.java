package lucene.b_api;

import java.io.File;
import java.io.IOException;

import lucene.a_quickstart.Article;
import lucene.utils.LuceneUtils;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

// API详细分析
public class LuceneTest {
	@Test
	// 使用LuceneUtils 解决 IndexWriter 并发问题
	@SuppressWarnings("unused")
	public void testLock2() {
		IndexWriter indexWriter2 = LuceneUtils.getIndexWriter();
		IndexWriter indexWriter1 = LuceneUtils.getIndexWriter();
	}

	@Test
	@SuppressWarnings("all")
	// 使用两个IndexWrtier 报错，锁使用问题
	public void testLock() throws CorruptIndexException,
			LockObtainFailedException, IOException {
		// 索引目录位置
		Directory directory = FSDirectory.open(new File("index"));// 当前工程index目录
		// 分词器
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		// 写入索引
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_36, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

		IndexWriterConfig indexWriterConfig2 = new IndexWriterConfig(
				Version.LUCENE_36, analyzer);
		IndexWriter indexWriter2 = new IndexWriter(directory,
				indexWriterConfig2);
	}

	@Test
	// 查询索引库 , 查看norms 效果
	public void testQuery() throws Exception {
		// 建立Query对象 ---- 根据标题
		String queryStrng = "检索";
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, "title",
				analyzer);
		Query query = queryParser.parse(queryStrng);

		// 根据Query查找
		Directory directory = FSDirectory.open(new File("index"));
		IndexSearcher indexSearcher = new IndexSearcher(
				IndexReader.open(directory));
		// 执行查询获得满足结果前多少条记录
		TopDocs topDocs = indexSearcher.search(query, 100);// 查询满足结果前100条数据
		System.out.println("满足结果记录条数：" + topDocs.totalHits);

		// 获得每个结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			// 打印得分
			System.out.println("得分：" + scoreDocs[i].score);
			// 获得Document 下标
			int docID = scoreDocs[i].doc;
			Document document = indexSearcher.doc(docID);
			System.out.println("id:" + document.get("id"));
			System.out.println("title:" + document.get("title"));
			System.out.println("content:" + document.get("content"));
		}

		indexSearcher.close();
	}

	@Test
	// 测试Store 和 Index
	/*
	 * Store.YES 存储 、Store.NO 不存储 Index.NO 不建立索引 Index.ANALYZED 分词建立索引
	 * Index.NOT_ANALYZED 不分词建立索引 Index.ANALYZED_NO_NORMS 分词建立索引，不存放权重信息
	 * Index.NOT_ANALYZED_NO_NORMS 不分词建立索引，不存放权重信息
	 */
	public void testIndex() throws Exception {
		// 需要建立索引目标数据
		Article article = new Article();
		article.setId(100);
		article.setTitle("学习全文检索");
		article.setContent("lucene 是搜索引擎开发技术 ，lucene并不是一个现成的产品, 由Apache提供");

		// 将索引数据 转换 Document对象 （lucene要求）
		Document document = new Document();
		document.add(new Field("id", article.getId() + "", Store.YES,
				Field.Index.NOT_ANALYZED)); // 对于id 通常不分词
		document.add(new Field("title", article.getTitle(), Store.YES,
				Field.Index.ANALYZED_NO_NORMS));
		document.add(new Field("content", article.getContent(), Store.YES,
				Field.Index.ANALYZED));

		// 建立索引库
		// 索引目录位置
		Directory directory = FSDirectory.open(new File("index"));// 当前工程index目录
		// 分词器
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
		// 写入索引
		IndexWriterConfig indexWriterConfig = new IndexWriterConfig(
				Version.LUCENE_36, analyzer);
		IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

		// 将document数据写入索引库
		indexWriter.addDocument(document);
		indexWriter.close();
	}

	@Test
	// 测试路径写法
	public void testDirectory() throws IOException {
		// 磁盘路径
		FSDirectory.open(new File("index")); // 当前工程index目录，相对路径
		FSDirectory.open(new File("d:\\index"));// 绝对路径
		// 类路径 WEB-INF/classes
		FSDirectory.open(new File(LuceneTest.class.getResource("/").getFile()));

		// 内存路径
		Directory directory = new RAMDirectory();
	}
}
