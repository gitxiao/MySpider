package cn.zju.yuki.spider.model;

public class FetchedPage {
	private String url;
	private String content;
	private int statusCode;
	private int antiMode = -1;			//反爬模式,默认为-1,即该网页没有反爬措施
	
	public FetchedPage(){
		
	}
	
	public FetchedPage(String url, String content, int statusCode){
		this.url = url;
		this.content = content;
		this.statusCode = statusCode;
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public int getAntiMode() {
		return antiMode;
	}

	public void setAntiMode(int antiMode) {
		this.antiMode = antiMode;
	}
	
	
}
