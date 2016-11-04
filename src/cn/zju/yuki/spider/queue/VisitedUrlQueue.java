package cn.zju.yuki.spider.queue;

import java.util.HashMap;
import java.util.Map;

public class VisitedUrlQueue {
	// 已抓取url队列
//	private static LinkedList<String> visitedUrlQueue = new LinkedList<String>();
	private static Map<String,String> visitedDesc = new HashMap<String,String>();
	
	public synchronized static void addElement(String url,String desc){
		System.out.println("爬取网页: " + desc + "	 " + url);
		visitedDesc.put(url, desc);
	}
	
	public synchronized static boolean isEmpty(){
		return visitedDesc.isEmpty();
	}
	
	public static int size(){
		return visitedDesc.size();
	}
	
	public static boolean isContains(String url){
		return visitedDesc.containsKey(url);
	}
}
