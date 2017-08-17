package com.homework.wechat.bean;

import com.google.gson.annotations.SerializedName;

/**
 * 用户消息
 * @author chenjiayin
 *
 */
public class UserInfo {
	@SerializedName("profile-image")
	private String profileImage;
	private String avatar;
	private String username;
	private String nick;

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	@Override
	public String toString() {
		return "profileImage=" + profileImage + ",avatar=" + avatar
				+ ",username=" + username + ",nick=" + nick;
	}

}
