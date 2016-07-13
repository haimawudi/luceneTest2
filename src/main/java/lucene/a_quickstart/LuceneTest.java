package lucene.a_quickstart;

import java.io.File;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Store;
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
import org.apache.lucene.util.Version;
import org.junit.Test;

// 测试程序
public class LuceneTest {
	@Test
	// 查找索引库
	public void searchIndex() throws Exception {
		// 建立Query对象 ---- 根据标题
		String queryStrng = "lucene大全";
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
			// 获得Document 下标
			int docID = scoreDocs[i].doc;
			Document document = indexSearcher.doc(docID);
			System.out.println("id:" + document.get("id"));
			System.out.println("title:" + document.get("title"));
			System.out.println("content:" + document.get("content"));

			// 思考： 能否将Document数据转换为Article对象
		}

		indexSearcher.close();
	}

	@Test
	// 建立索引
	public void buildIndex() throws Exception {
		// 需要建立索引目标数据
		Article article = new Article();
		article.setId(100);
		article.setTitle("lucene快速入门");
		article.setContent("lucene 是搜索引擎开发技术 ，lucene并不是一个现成的产品, 由Apache提供");

		// 将索引数据 转换 Document对象 （lucene要求）
		Document document = new Document();
		document.add(new Field("id", article.getId() + "", Store.YES,
				Field.Index.ANALYZED));
		document.add(new Field("title", article.getTitle(), Store.NO,
				Field.Index.ANALYZED));
		document.add(new Field("content", article.getContent(), Store.YES,
				Field.Index.NOT_ANALYZED));

		// 建立索引库
		// 索引目录位置
		Directory directory = FSDirectory.open(new File("index"));// 当前工程index目录
		// Directory directory = FSDirectory.open(new File(LuceneTest.class
		// .getResource("/").getFile()));// 类路径
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
}
