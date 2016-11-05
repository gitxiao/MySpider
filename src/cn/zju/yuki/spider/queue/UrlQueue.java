package cn.zju.yuki.spider.queue;

import java.util.LinkedList;

public class UrlQueue {
	//url队列
	private static LinkedList<String> urlQueue = new LinkedList<String>();

	public synchronized static void addElement(String url){
		if(!isContains(url)){
			urlQueue.add(url);
			System.out.println("UrlQueue addElement 待爬取url: " + url);
//			System.out.println("addElement 添加后");
//			traversal();
		}else{
//			System.out.println("已爬取的网址, 不再进入队列:" + url);
		}
	}
	
	public synchronized static void addLastElement(String url){
		urlQueue.addLast(url);
	}
	
	public synchronized static String outElement(){
//		System.out.println("outElement 取出前:");
//		traversal();

		String url = urlQueue.removeFirst();
		
//		System.out.println("outElement 取出后:");
//		traversal();
		return url;
	}
	
	public synchronized static boolean isEmpty(){
		return urlQueue.isEmpty();
	}
	
	public static int size(){
		return urlQueue.size();
	}
	
	public static boolean isContains(String url){
		return urlQueue.contains(url);
	}
	
	public static void traversal(){
		System.out.println("UrlQueue 遍历开始===============================================>>>");
		for(String url : urlQueue){
			System.out.println("url = " + url);
		}
		System.out.println("UrlQueue 遍历结束===============================================<<<");
	}
}
