package com.homework.wechat.constant;

import com.squareup.okhttp.Request;

public class Constant {
    
	//上拉刷新消息
	public static final int REFRESH_MSG=0;
	//从网络获取成功消息
	public static final int GETDATA_SUC_MSG=1;
	//从网络获取消息失败
	public static final int GETDATA_FAL_MSG=2;
	//用户信息
	public static final String USER_INFO="http://thoughtworks-ios.herokuapp.com/user/jsmith";
	//tweets信息
	public static final String TWEETS_DATA="http://thoughtworks-ios.herokuapp.com/user/jsmith/tweets";


	public enum QueueType {
		FIFO, LIFO
	}
	
	
	/**
	 * 刷新状态的枚举类
	 * 
	 * @author chenjiayin
	 * 
	 */
	public enum State {
		IDLE(0), PULL(1), REALSE(2), FRASH(3);
		
		private int state;

		private State(int state) {
			this.state = state;
		}

		public int getState(){
			return state;
		}
	}

}
