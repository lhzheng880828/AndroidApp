package cn.itcast.formbean;

import org.apache.struts.action.ActionForm;
import org.apache.struts.upload.FormFile;

public class VideoForm extends ActionForm {
	private String format;
	private String title;	
	private Integer timelength;
	private FormFile video;
	
	public FormFile getVideo() {
		return video;
	}

	public void setVideo(FormFile video) {
		this.video = video;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getTimelength() {
		return timelength;
	}

	public void setTimelength(Integer timelength) {
		this.timelength = timelength;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
}
