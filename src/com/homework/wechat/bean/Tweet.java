package com.homework.wechat.bean;

import java.util.List;

/**
 * ÍÆÎÄ
 * @author chenjiayin
 *
 */
public class Tweet {
	private String content;
	private List<Image> images;
	private UserInfo sender;
	private List<Comment> comments;

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public UserInfo getSender() {
		return sender;
	}

	public void setSender(UserInfo sender) {
		this.sender = sender;
	}


	public List<Comment> getComments() {
		return comments;
	}

	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
	
	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}


	/**
	 * Image
	 * @author chenjiayin
	 *
	 */
	public class Image{
		private String url;

		public String getUrl() {
			return url;
		}

		public void setUrl(String url) {
			this.url = url;
		}
	}
}
