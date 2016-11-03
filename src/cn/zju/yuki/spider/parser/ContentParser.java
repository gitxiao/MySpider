package cn.zju.yuki.spider.parser;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;

public class ContentParser {
	public Object parse(FetchedPage fetchedPage){
		Object targetObject = null;
		Document doc = Jsoup.parse(fetchedPage.getContent());
		
		Elements elemTitle = doc.getElementsByTag("title");
		String title = elemTitle.html();
//		System.out.println("标题:" + title + ",elemTitle.get(0) = " + elemTitle.get(0));
		System.out.println("网页标题:" + title);
		
		
//		Element elemContent = doc.getElementById("content");
//		String content = elemContent.html();
		
		// 如果当前页面包含目标数据
		if(containsTargetData(fetchedPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO
		}
		
		// 将URL放入已爬取队列
		VisitedUrlQueue.addElement(fetchedPage.getUrl());
		
		// 根据当前页面和URL获取下一步爬取的URLs
		// TODO
		
		return targetObject; 
	}
	
	private boolean containsTargetData(String url, Document contentDoc){
		// 通过URL判断
		// TODO
		
//		System.out.println(contentDoc.toString());
		// 通过content判断，比如需要抓取class为grid_view中的内容
		if(contentDoc.getElementsByClass("grid_view") != null){
			System.out.println(contentDoc.getElementsByClass("grid_view").toString());
			return true;
		}
		
		return false;
	}
}
