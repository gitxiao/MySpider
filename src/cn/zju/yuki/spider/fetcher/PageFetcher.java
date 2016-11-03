package cn.zju.yuki.spider.fetcher;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.web.spider.utils.UrlUtils;
import cn.zju.yuki.spider.model.FetchedPage;
import cn.zju.yuki.spider.queue.UrlQueue;

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
	 * 根据url爬取网页内容
	 * @param url
	 * @return
	 */
	public FetchedPage getContentFromUrl(String url){
		String content = null;
		int statusCode = 500;
		String encode = null;				//编码应自动获取
		// 创建Get请求，并设置Header
		HttpGet getHttp = new HttpGet(url);	
		getHttp.setHeader("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:16.0) Gecko/20100101 Firefox/16.0");
		HttpResponse response = null;
		
		try{
			encode = getCharset(url);
//			System.out.println("encode = " + encode);
			// 获得信息载体
			response = client.execute(getHttp);
			statusCode = response.getStatusLine().getStatusCode();
			HttpEntity entity = response.getEntity();	
			
			if(entity != null){
				// 转化为文本信息, 设置爬取网页的字符集，防止乱码
				content = EntityUtils.toString(entity, encode);
//				content = EntityUtils.toString(entity, encode == null ? "gbk" : encode);
//				content = EntityUtils.toString(entity, encode == null ? "utf-8" : encode);
//				content = EntityUtils.toString(entity, encode == null ? "unicode" : encode);
			}
		}catch(Exception e){
			e.printStackTrace();
			
			// 因请求超时等问题产生的异常，将URL放回待抓取队列，重新爬取
			Log.info(">> Put back url: " + url);
			UrlQueue.addFirstElement(url);
		}

		return new FetchedPage(url, content, statusCode);
	}
	
	
	/**获取网页的编码格式
	 * 
	 */
	public String getCharset(String link) {   
		  String result = null;     
		  HttpURLConnection conn = null;   
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
		         String line = reader.readLine();     
		         while(line != null) {     
//		        	 System.out.println("line = " + line);
		             if(line.contains("Content-Type")) {    
		                 result = findCharset(line);     
		                 break;     
		             }else if(line.contains("<iframe")){
		            	 String iframe = findIframeUrl(line);
		            	 String newUrl = link + iframe;
		            	 return getCharset(newUrl);
		             }
		             line = reader.readLine();     
		         }     
		     }     
		 } catch (Exception e) {     
		     // TODO Auto-generated catch block     
		     e.printStackTrace();     
		 } finally {   
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
	 
	private String findIframeUrl(String line){
//		System.out.println("findIframeUrl line = " + line);
		int x = line.indexOf("src=");     
	    int y = line.lastIndexOf('\"');     
	    if(x < 0)     
	        return null;     
	    else if( y >= 0)    
	        return line.substring(x + 5, y);    
	    else  
	        return line.substring(x + 5);   
	}
}
