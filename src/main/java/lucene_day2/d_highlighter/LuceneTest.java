package lucene_day2.d_highlighter;

import java.io.IOException;

import lucene.utils.Configuration;
import lucene_day2.utils.LuceneUtils;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Formatter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.Scorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

// 查询内容制作高亮效果
public class LuceneTest {
	@Test
	// 查询目标内容，进行高亮显示
	public void demo2() throws Exception {
		// 创建Query
		String queryStr = "传智播客";
		QueryParser queryParser = new MultiFieldQueryParser(Version.LUCENE_36,
				new String[] { "title", "content" }, new IKAnalyzer(false));
		Query query = queryParser.parse(queryStr);

		// 第一步 ： 创建高亮器
		Formatter formatter = new SimpleHTMLFormatter("<em>", "</em>");
		Scorer scorer = new QueryScorer(query);
		Highlighter highlighter = new Highlighter(formatter, scorer);
		// 默认摘要 100 字
		highlighter.setTextFragmenter(new SimpleFragmenter(20));// 摘要20字

		// 执行Query
		IndexSearcher indexSearcher = LuceneUtils.getIndexSearcher();
		// 执行Query
		TopDocs topDocs = indexSearcher.search(query, 100);
		System.out.println("结果：" + topDocs.totalHits);
		// 遍历每一个结果
		ScoreDoc[] scoreDocs = topDocs.scoreDocs;
		for (ScoreDoc scoreDoc : scoreDocs) {
			System.out.println("得分：" + scoreDoc.score);
			// 获得Document对象
			int docID = scoreDoc.doc;
			Document document = indexSearcher.doc(docID);

			// 第二步 对结果进行高亮处理
			String text = highlighter.getBestFragment(
					 new IKAnalyzer(false), "content",
					document.get("content")); // 高亮后内容
			if (text == null) {
				if (document.get("content").length() > 20) {
					// 没有高亮摘要，也要创建摘要
					document.getField("content").setValue(
							document.get("content").substring(0, 20));
				}
			} else {
				// 存在高亮内容
				document.getField("content").setValue(text); // 将高亮后内容保存回document，不会更新索引库
			}

			System.out.println("title :" + document.get("title"));
			System.out.println("content:" + document.get("content")); // 取得就是高亮内容
		}
	}

	@Test
	// 创建索引
	public void demo1() throws CorruptIndexException, IOException {
		Document document1 = new Document();
		document1.add(new Field("title",
				"专业的Java、.Net、PHP、C/C++、网页设计、平面设计、UI设计、iOS培训机构 ", Store.YES,
				Index.ANALYZED));
		document1
				.add(new Field(
						"content",
						"史上最难就业年，怎么办？到传智免费学PS吧！   霸王餐免费学习活动—有种就来吃！    关于竞争对手恶意攻击传智播客的通告    【老学员福利】传智第二期iOS兴趣班免费报名中   【友情提示】“租房那些事”勿入租房陷阱！传智播客首家推出Hadoop高薪就业课程 自带笔记本、写日记、拍视频可以享受优惠价 万人疯抢免费Java,.Net,PHP,网页平面自学光盘 传智播客百万年薪诚邀技术牛人共创IT培训学科",
						Store.YES, Index.ANALYZED));

		IndexWriter indexWriter = LuceneUtils.getIndexWriter();
		indexWriter.addDocument(document1);
	}
}
