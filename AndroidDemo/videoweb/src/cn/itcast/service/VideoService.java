package cn.itcast.service;

import java.util.List;

import cn.itcast.domain.Video;

public interface VideoService {

	/**
	 * 返回最新的视频资讯
	 * @return
	 * @throws Exception
	 */
	public List<Video> getLastVideos() throws Exception;

}