package com.homework.wechat;

import java.io.IOException;
import java.util.List;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.homework.wechat.bean.Tweet;
import com.homework.wechat.bean.UserInfo;
import com.homework.wechat.constant.Constant;
import com.homework.wechat.util.ImageLoadUtil;
import com.homework.wechat.util.OkHttpUtil;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

public class MyApplication extends Application {

	private static MyApplication instance;
	private int displayWidth;
	private int displayHeight;
	private UserInfo userInfo;
	private List<Tweet> tweetsList;
	private Gson gson;
	// ��һ���������Ӧjson�ַ���
	private String lastResp;

	public static MyApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		gson = new Gson();
		// ��ȡ��ǰ�ֱ���
		getWindowDisplay();
		// okhttp ���û�get����
		getUserInfo();
		// ����tweets��Ϣ������null����ʾ��ǰ�������ݴ���
		getTweets(null);
	}

	/**
	 * �û���Ϣget����
	 * 
	 * @return
	 */
	public UserInfo getUserInfo() {

		if (userInfo == null) {
			Request userInfoReq = OkHttpUtil
					.getRequestByGet(Constant.USER_INFO);
			Call userInfoCall = OkHttpUtil.getHttpClient().newCall(userInfoReq);
			// get �첽get����
			userInfoCall.enqueue(new Callback() {
				@Override
				public void onResponse(Response res) throws IOException {
					String resStr = res.body().string();
					userInfo = gson.fromJson(resStr, UserInfo.class);
				}

				@Override
				public void onFailure(Request arg0, IOException arg1) {

				}
			});
		}
		return userInfo;
	}

	/**
	 * ��ȡ������Ϣ
	 * 
	 * @return
	 */
	public List<Tweet> getTweets(final Handler handler) {
		// ��handler��Ϊ��ʱ��ǿ�Ʒ�������ˢ������
		if (null != tweetsList || null != handler) {
			Request tweetReq = OkHttpUtil.getRequestByGet(Constant.TWEETS_DATA);
			Call call = OkHttpUtil.getHttpClient().newCall(tweetReq);
			// get �첽get����
			call.enqueue(new Callback() {
				@Override
				public void onResponse(Response resp) throws IOException {
					String resultStr = resp.body().string();
					//���ν�����ϴν����һ�£���Ҫˢ��ҳ��
					if (!resultStr.equals(lastResp)) {
						tweetsList = gson.fromJson(resultStr,
								new TypeToken<List<Tweet>>() {
								}.getType());
						if (null != handler) {
							Message msg = new Message();
							msg.obj = tweetsList;
							msg.what = Constant.GETDATA_SUC_MSG;
							handler.sendMessage(msg);
						}
						//����ǰ������浽��һ�εĽ��
						lastResp = resultStr;
					}
				}

				@Override
				public void onFailure(Request resp, IOException arg1) {
				}
			});
		}
		return tweetsList;
	}

	/**
	 * ��ȡ��ǰ�ֻ��ķֱ����
	 * 
	 * @return
	 */
	private void getWindowDisplay() {
		DisplayMetrics displayMetrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(displayMetrics);
		displayWidth = displayMetrics.widthPixels;
		displayHeight = displayMetrics.heightPixels;
	}

	public int getDispalyWidth() {
		return displayWidth;
	}

	public int getDisplayHeight() {
		return displayHeight;
	}

	public int getMemoryCacheSize() {
		// OutOfMemory exception.
		final int memClass = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE))
				.getMemoryClass();
		return 1024 * 1024 * memClass / 8;
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		ImageLoadUtil.getInstance().shutDownThreadPool();
	}

	@Override
	public void onLowMemory() {
		super.onLowMemory();
		ImageLoadUtil.getInstance().shutDownThreadPool();
	}

}
