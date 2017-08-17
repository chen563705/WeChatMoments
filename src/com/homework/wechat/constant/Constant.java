package com.homework.wechat.constant;

import com.squareup.okhttp.Request;

public class Constant {
    
	//����ˢ����Ϣ
	public static final int REFRESH_MSG=0;
	//�������ȡ�ɹ���Ϣ
	public static final int GETDATA_SUC_MSG=1;
	//�������ȡ��Ϣʧ��
	public static final int GETDATA_FAL_MSG=2;
	//�û���Ϣ
	public static final String USER_INFO="http://thoughtworks-ios.herokuapp.com/user/jsmith";
	//tweets��Ϣ
	public static final String TWEETS_DATA="http://thoughtworks-ios.herokuapp.com/user/jsmith/tweets";


	public enum QueueType {
		FIFO, LIFO
	}
	
	
	/**
	 * ˢ��״̬��ö����
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
