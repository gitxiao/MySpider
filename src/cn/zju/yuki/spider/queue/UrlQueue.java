package cn.zju.yuki.spider.queue;

import java.util.LinkedList;

public class UrlQueue {
	//url队列
	private static LinkedList<String> urlQueue = new LinkedList<String>();

	public synchronized static void addElement(String url){
		if(!isContains(url)){
			urlQueue.add(url);
		}else{
			System.out.println("已爬取的网址, 不再进入队列:" + url);
		}
	}
	
	public synchronized static void addLastElement(String url){
		urlQueue.addLast(url);
	}
	
	public synchronized static String outElement(){
		return urlQueue.removeFirst();
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
}
