package com.eeontheway.android.applocker.feedback;

import com.eeontheway.android.applocker.login.UserInfo;

import java.io.Serializable;

/**
 * 用户反馈信息
 *
 * @author lishutong
 * @version v1.0
 * @Time 2016-2-8
 */
public class FeedBackInfo implements Serializable {
	private String id;
	private String parentId;
	private String content;
	private String contact;
	private String time;
	private String from;
	private boolean responsed;
	private boolean isTopic;
	private UserInfo userInfo;
	private String cid;

	/**
	 * 刷新信息
	 * @param newInfo 新的信息
     */
	public void update (FeedBackInfo newInfo) {
		content = newInfo.content;
		responsed = newInfo.responsed;
	}

	/**
	 * 获得对像ID
	 * @return 对像ID
     */
	public String getId() {
		return id;
	}

	/**
	 * 设置对像ID
	 * @param id 对像ID
     */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 是否是主题
	 * @return true/false
     */
	public boolean isTopic() {
		return isTopic;
	}

	/**
	 * 设置是否是主题
	 * @param topic
     */
	public void setTopic(boolean topic) {
		isTopic = topic;
	}

	/**
	 * 获取父ID
	 * @return 父ID
     */
	public String getParentId() {
		return parentId;
	}

	/**
	 * 设置父ID
	 * @param parentId 父ID
     */
	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	/**
	 * 获取反馈内容
	 * @return 反馈内容
     */
	public String getContent() {
		return content;
	}

	/**
	 * 设置反馈内容
	 * @param content 反馈内容
     */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * 获取联系方式
	 * @return 联系方式
     */
	public String getContact() {
		return contact;
	}

	/**
	 * 设置联系方式
	 * @param contact 联系方式
     */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * 获取创建时间
	 * @return 创建时间
     */
	public String getCreateTime() {
		return time;
	}

	/**
	 * 设置创建时间
	 * @param time 创建时间
     */
	public void setCreateTime(String time) {
		this.time = time;
	}

	/**
	 * 反馈信息的来源
	 * @return 客户端名称
     */
	public String getFrom() {
		return from;
	}

	/**
	 * 设置反馈信息来源
	 * @param from 反馈信息来源
     */
	public void setFrom(String from) {
		this.from = from;
	}

	/**
	 * 是否已经回复
	 * @return true 已回复; false 未回复
     */
	public boolean isResponsed() {
		return responsed;
	}

	/**
	 * 设置是否已经回复
	 * @param responsed true 已回复; false 未回复
     */
	public void setResponsed(boolean responsed) {
		this.responsed = responsed;
	}

	/**
	 * 获取该feedback的用户信息
	 * @return 用户信息
     */
	public UserInfo getUserInfo() {
		return userInfo;
	}

	/**
	 * 设置该feedback的用户信息
	 * @param userInfo 该feedback的用户信息
     */
	public void setUserInfo(UserInfo userInfo) {
		this.userInfo = userInfo;
	}

	/**
	 * 获取cid
	 * 如果发表该评论的用户是已注册用户，则返回null。从userinfo中查询cid
	 * @return 获取cid
     */
	public String getCid() {
		return cid;
	}

	/**
	 * 设置用户cid
	 * 仅当用户未登陆时使用
	 * @param cid 用户cid
     */
	public void setCid(String cid) {
		this.cid = cid;
	}
}
