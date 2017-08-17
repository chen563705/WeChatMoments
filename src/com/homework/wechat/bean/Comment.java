package com.homework.wechat.bean;

/**
 * ÆÀÂÛ®º
 * @author chenjiayin
 *
 */
public class Comment {
	private String content;
	private UserInfo sender;

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

}
