package cn.itcast.service.impl;

import java.util.ArrayList;
import java.util.List;
import cn.itcast.domain.Video;
import cn.itcast.service.VideoService;

public class VideoServiceBean implements VideoService {

	public List<Video> getLastVideos() throws Exception{
		//查询数据库
		List<Video> videos = new ArrayList<Video>();
		videos.add(new Video(78, "喜羊羊与灰太狼全集", 90));
		videos.add(new Video(78, "实拍舰载直升东海救援演习", 20));
		videos.add(new Video(78, "喀麦隆VS荷兰", 30));
		return videos;
	}
}
