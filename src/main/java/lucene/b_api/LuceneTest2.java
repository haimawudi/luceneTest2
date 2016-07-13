package lucene.b_api;

import java.io.File;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;

// 测试 查询索引 API
public class LuceneTest2 {
	@Test
	// TermQuery 指针对分词后内容进行检索
	public void demo3() throws Exception {
		// 每个Term对象构造，需要词条名称 和 一个词条内容
		Term term = new Term("title", "学习");// 词条不会再进行分词
		Query query = new TermQuery(term);
		executeQueryAndPrintResult(query);
	}

	@Test
	// 查询索引库，针对多个Field
	public void demo2() throws Exception {
		String queryStr = "lucene";
		// 在title和content 两个field中找
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,
				new String[] { "title", "content" }, new StandardAnalyzer(
						Version.LUCENE_36));
		Query query = queryParser.parse(queryStr);
		executeQueryAndPrintResult(query);
	}

	@Test
	// 查询索引库 (单一Field)
	public void demo1() throws Exception {
		String queryStr = "lucene";
		QueryParser queryParser = new QueryParser(Version.LUCENE_36, "content",
				new StandardAnalyzer(Version.LUCENE_36));
		// 使用QueryParser 解析查询内容 获得 Query对象
		Query query = queryParser.parse(queryStr);
		// 执行Query查询
		executeQueryAndPrintResult(query);
	}

	// 执行查询打印结果
	private void executeQueryAndPrintResult(Query query) throws Exception {
		IndexSearcher indexSearcher = new IndexSearcher(
				IndexReader.open(FSDirectory.open(new File("index"))));
		// topDocs 查询结果对象
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("结果数：" + topDocs.totalHits);
		// 获得每个结果得分信息
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (int i = 0; i < scoreDocs.length; i++) {
			System.out.println("得分：" + scoreDocs[i].score);
			// 先获得文档ID
			int docID = scoreDocs[i].doc;
			Document document = indexSearcher.doc(docID);
			System.out.println("id:" + document.get("id"));
			System.out.println("title:" + document.get("title"));
			System.out.println("content:" + document.get("content"));
		}
	}

}
