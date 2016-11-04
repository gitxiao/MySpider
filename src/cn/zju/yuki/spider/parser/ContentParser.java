package cn.zju.yuki.spider.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.UrlQueue;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;

public class ContentParser {
	public Object parse(FetchedPage fetchedPage){
		Object targetObject = null;
		Document doc = Jsoup.parse(fetchedPage.getContent());
		
		Elements elemTitle = doc.getElementsByTag("title");
		String title = elemTitle.html();
//		System.out.println("标题:" + title + ",elemTitle.get(0) = " + elemTitle.get(0));
//		System.out.println("网页标题:" + title);
		
		VisitedUrlQueue.addElement(fetchedPage.getUrl(),title);			//网页添加到爬取结果页面,TODO 持久化工作
		
		
//		Element elemContent = doc.getElementById("content");
//		String content = elemContent.html();
		
		// 如果当前页面包含目标数据
//		if(true || containsTargetData(fetchedPage.getUrl(), doc)){
			// 解析并获取目标数据
			// TODO
		
			String newUrl = null;
			String urlDesc = null;
			String aLink = null;
			Pattern patternA = Pattern.compile("<a[\\s\\S]+?</a>");
			Matcher matcherA = patternA.matcher(fetchedPage.getContent());
			while(matcherA.find()){
				aLink = matcherA.group();
				newUrl = getUrlFromALink(fetchedPage.getUrl(),aLink);
				urlDesc = getDescOfALink(aLink);
//				System.out.println("aLink = " + aLink);
//				System.out.println("newUrl = " + newUrl);
//				System.out.println("urlDesc = " + urlDesc);
//				System.out.println(urlDesc + ":	" + newUrl);
				UrlQueue.addElement(newUrl);
			}
			

//		}
		
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
	
	
	/**
	 * 从超链接中拿出新的url地址
	 * @param aLink
	 * @return
	 */
	private String getUrlFromALink(String url,String aLink){
		String newUrl = null;
		Pattern patternHref = Pattern.compile("href=\"(.+?)\"");
		Matcher matcherHref = patternHref.matcher(aLink);
		if(matcherHref.find()){
			String href = matcherHref.group(1);
			if(href.length() < 2){
				href = "";			//有时href="#",这种不需要重新爬取
			}
			if(href.contains("http://") || href.contains("https://")){
				newUrl = href;
			}else{
				newUrl = url + href;
			}
		}
		return newUrl;
	}
	
	/**
	 * 从超链接中拿出链接描述
	 * @param aLink
	 * @return
	 */
	private String getDescOfALink(String aLink){
		String desc = getSubStringFrom(aLink,">","<");
		if(desc.contains("<img") || desc.contains("<Img") || desc.contains("src=")){
			desc = "图片链接";
		}
		return desc;
	}
	
	/**
	 * 获取字符串中某两个指定子串之间的子串
	 */
	private String getSubStringFrom(String father,String child0,String child1){
		String child = null;
		String temp = null;
		int index0 = father.indexOf(child0);
		int index1 = father.lastIndexOf(child1);
		if(index0 >= 0 && index1 > index0){
			temp = father.substring(index0 + 1,index1);
			child = getSubStringFrom(temp,child0,child1);
		}else{
			child = father;
		}
		return child;
	}
}
