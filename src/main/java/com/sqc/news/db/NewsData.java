package com.sqc.news.db;

import java.util.Date;

public class NewsData {
	private String title;
	private String source;
	private String editor;
	private Date  date;
	private Date  inserttime;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Date getInserttime() {
		return inserttime;
	}
	public void setInserttime(Date inserttime) {
		this.inserttime = inserttime;
	}
	@Override
	public String toString() {
		return "NewsData [title=" + title + ", source=" + source + ", editor=" + editor + ", publishtime=" + date
				+ ", inserttime=" + inserttime + "]";
	}
	
	
}
