package lucene_day2.b_result;

import lucene.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.FuzzyQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.MatchAllDocsQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.junit.Test;

// 查询结果排序 和 分页
public class LuceneTest {
	@Test
	// 查询5-10条、分页查询效果
	public void demo2() throws Exception{
		Query query = new MatchAllDocsQuery();
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		// 执行Query 
		TopDocs topDocs = indexSearcher.search(query, 10 );// 先查询10条
		System.out.println("结果：" + topDocs.totalHits);
		// 遍历每一个结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		
		// 循环从第5条开始
		for (int i = 4; i < scoreDocs.length; i++) {
			System.out.println("得分：" + scoreDocs[i].score);
			// 获得Document对象
			int docID = scoreDocs[i].doc;
			Document document = indexSearcher.doc(docID);

			System.out.println("id: " + document.get("id"));
			System.out.println("title :" + document.get("title"));
			System.out.println("content:" + document.get("content"));
		}
	}
	
	@Test
	// 指定sort 对象结果排序
	public void demo1() throws Exception {
		// 按照标题查询
		Query query = new FuzzyQuery(new Term("title","title11"),0.1f); // 相似度查询
		
		// 执行查询
		executeQueryAndPringResult(query);
	}

	// 执行查询 ，打印结果
	private void executeQueryAndPringResult(Query query) throws Exception {
		// 获得 IndexSearcher对象
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		// 执行Query 
		// 传入sort 改变默认排序结果	此时得分为NAN
		Sort sort = new Sort(new SortField("title", SortField.STRING ,true));
		TopDocs topDocs = indexSearcher.search(query, 100 , sort);
		System.out.println("结果：" + topDocs.totalHits);
		// 遍历每一个结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("得分：" + scoreDoc.score);
			// 获得Document对象
			int docID = scoreDoc.doc;
			Document document = indexSearcher.doc(docID);

			System.out.println("id: " + document.get("id"));
			System.out.println("title :" + document.get("title"));
			System.out.println("content:" + document.get("content"));
		}
	}
}
