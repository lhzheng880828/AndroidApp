package cn.itcast.service;

import java.util.List;

import cn.itcast.domain.Video;

public interface VideoService {

	/**
	 * �������µ���Ƶ��Ѷ
	 * @return
	 * @throws Exception
	 */
	public List<Video> getLastVideos() throws Exception;

}