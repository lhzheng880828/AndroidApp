package cn.itcast.service.impl;

import java.util.ArrayList;
import java.util.List;
import cn.itcast.domain.Video;
import cn.itcast.service.VideoService;

public class VideoServiceBean implements VideoService {

	public List<Video> getLastVideos() throws Exception{
		//��ѯ���ݿ�
		List<Video> videos = new ArrayList<Video>();
		videos.add(new Video(78, "ϲ�������̫��ȫ��", 90));
		videos.add(new Video(78, "ʵ�Ľ���ֱ��������Ԯ��ϰ", 20));
		videos.add(new Video(78, "����¡VS����", 30));
		return videos;
	}
}
