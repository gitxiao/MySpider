package cn.zju.yuki.spider.fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.UrlQueue;
import cn.zju.yuki.spider.queue.VisitedUrlQueue;

public class PageFetcher {
	private static final Logger Log = Logger.getLogger(PageFetcher.class.getName());
	private HttpClient client;
	
	/**
	 * 创建HttpClient实例，并初始化连接参数
	 */
	public PageFetcher(){
		// 设置超时时间
		HttpParams params = new BasicHttpParams();
	    HttpConnectionParams.setConnectionTimeout(params, 10 * 1000);
	    HttpConnectionParams.setSoTimeout(params, 10 * 1000);	    
		client = new DefaultHttpClient(params);
	}
	
	/**
	 * 主动关闭HttpClient连接
	 */
	public void close(){
		client.getConnectionManager().shutdown();
	}
	
	
	/**
	 * 用URL
	 * @param url
	 * @return
	 * 超链接正则:<a[\s\S]+?</a>
	 */
	public FetchedPage getContentFromUrl(String urlStr){
		URL url = null;
		BufferedReader bReader = null;
		StringBuffer sb = new StringBuffer();
		String encode = null;
		String urlHeader = null;
		try {
			int index = urlStr.indexOf("//");
			url = new URL(urlStr);
			urlHeader = urlStr.substring(0,index + 2) + url.getHost();

			encode = getCharset(urlStr,urlHeader);
			if(encode == null){
				encode = "gbk";
			}
			System.out.println("encode = " + encode);
			bReader = new BufferedReader(new InputStreamReader(url.openStream(),Charset.forName(encode)));
			String temp = "";
			while((temp = bReader.readLine()) != null){
				sb.append(temp + '\n');
			}
			
			// 将URL放入已爬取队列
			
//			System.out.println("页面内容: sb.toString() = " + sb.toString());

						
		} catch (Exception e) {
			// 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
			e.printStackTrace();
			Log.info(">> Put back url: " + url);
			System.out.println("urlStr = " + urlStr);
			VisitedUrlQueue.addElementWithException(urlStr,"异常");
//			UrlQueue.addLastElement(urlStr);			//TODO 重新放回队列时应该计数,否则如果一直有异常,会无限重新爬取
		}
		return new FetchedPage(urlHeader,urlStr, sb.toString(), 1);
	}
	
	/**
	 * 用HttpGet
	 * 根据url爬取网页内容
	 * @param url
	 * @return
	 */
//	public FetchedPage getContentFromUrl_(String url){
//		String content = null;
//		int statusCode = 500;
//		String encode = null;				//编码应自动获取
//		// 创建Get请求，并设置Header
//		HttpGet getHttp = new HttpGet(url);	
//		getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
//		HttpResponse response = null;
//		
//		try{
//			encode = getCharset(url);
////			System.out.println("encode = " + encode);
//			// 获得信息载体
//			response = client.execute(getHttp);
//			statusCode = response.getStatusLine().getStatusCode();
//			HttpEntity entity = response.getEntity();	
//			
//			if(entity != null){
//				// 转化为文本信息, 设置爬取网页的字符集，防止乱码
//				content = EntityUtils.toString(entity, encode);
//			}
//		}catch(Exception e){
//			e.printStackTrace();
//			
//			// 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
//			Log.info(">> Put back url: " + url);
////			UrlQueue.addLastElement(url);				
//		}finally{
//		}
//
//		return new FetchedPage("",url, content, statusCode);
//	}
	
	
	/**
	 * 获取网页的编码格式
	 * 
	 */
	public String getCharset(String link,String urlHeader) {   
		  String result = null;     
		  HttpURLConnection conn = null;   
		  if(link.equals("http://vipmail.hebei.com.cn/cgi-bin/web2cgi/index.cgi")){
			  System.out.println("link = " + link);
		  }
		  String line = null;
		  try {     
		      URL url = new URL(link);     
		      conn = (HttpURLConnection)url.openConnection();     
		      conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1)");     
		      conn.connect();     
		      String contentType = conn.getContentType();    
//		      System.out.println("contentType = " + contentType);
		      //在header里面找charset     
		      result = findCharset(contentType);      
		      //如果没找到的话，则一行一行的读入页面的html代码，从html代码中寻找     
		      if(result == null){     
		         BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));     
		         line = reader.readLine();     
		         while(line != null) {     
//		        	 System.out.println("line = " + line);
		             if(line.contains("Content-Type") || line.contains("content-Type")) {    
		                 result = findCharset(line);     
		             } 
		             if(result != null){
		            	 break; 
		             }else if(line.contains("<iframe")){
		            	 String iframe = findIframeUrl(urlHeader,line);
//		            	 UrlQueue.addElement(iframe);				//TODO 	iframe如何处理, 是否需要添加到未爬取队列
		            	 return getCharset(iframe,urlHeader);
		             }else if(line.contains("location.href")){
		            	 String location = findLocation(urlHeader,line);
		            	 return getCharset(location,urlHeader);
		             }
		             line = reader.readLine();     
		         }     
		     }     
		 } catch (Exception e) {     
		     // TODO Auto-generated catch block     
		     e.printStackTrace();
		     System.out.println("异常 link = " + link);
		 } finally {   
			 System.out.println("line = " + line);
			 conn.disconnect();   
		 }   
		 return result;     
	 }     
	      
	 //辅助函数     
	 private String findCharset(String line) {    
//		 System.out.println("findCharset line = " + line);
	     int x = line.indexOf("charset=");     
	     int y = line.lastIndexOf('\"');     
	     if(x < 0)     
	         return null;     
	     else if(y >= 0)    
	         return line.substring(x + 8, y);    
	     else  
	         return line.substring(x + 8);   
	}
	 
	private String findIframeUrl(String link,String line){
		System.out.println("findIframeUrl line = " + line);
		int x = line.indexOf("src=");     
//	    int y = line.lastIndexOf('\"');     
	    int y = line.indexOf("\"", x + 6);
	    String iframe = null;
	    if(x < 0)     
	    	iframe = null;     
	    else if(y >= 0)    
	    	iframe = line.substring(x + 5, y);    
	    else  
	    	iframe = line.substring(x + 5);   
	    
	    iframe = iframe.trim();
	    
	    if(iframe.startsWith("http://") || iframe.startsWith("https://")){
	    	iframe = iframe;
		}else{
			iframe = link + iframe;
		}
	    
	    System.out.println("iframe = " + iframe);
	    return iframe;
	}
	
	private String findLocation(String link,String line){
		System.out.println("findLocation line = " + line);
		int length = "location.href".length();
		int x = line.indexOf("location.href");     
		int y1 = line.indexOf("\"", x + length);
		int y2 = line.indexOf("\"", y1 + 1);
		String location = null;
		if(x < 0)     
			location = null;     
		else if(y1 > 0 && y2 > y1)    
			location = line.substring(y1 + 1, y2);    
		else  
			location = line.substring(x + length - 1);   
		
		location = location.trim();
		
		if(location.startsWith("http://") || location.startsWith("https://")){
			location = location;
		}else{
			location = link + location;
		}
		
		System.out.println("location = " + location);
		return location;
	}
}
