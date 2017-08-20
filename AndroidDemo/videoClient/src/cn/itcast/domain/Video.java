package cn.itcast.domain;

public class Video {
	private Integer id;
	private String title;
	private Integer time;
	
	public Video(){}
	
	public Video(Integer id, String title, Integer time) {
		this.id = id;
		this.title = title;
		this.time = time;
	}
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Integer getTime() {
		return time;
	}
	public void setTime(Integer time) {
		this.time = time;
	}
	
}
