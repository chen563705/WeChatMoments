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
	// 上一次请求的响应json字符串
	private String lastResp;

	public static MyApplication getInstance() {
		return instance;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;
		gson = new Gson();
		// 获取当前分辨率
		getWindowDisplay();
		// okhttp 发用户get请求
		getUserInfo();
		// 请求tweets消息，参数null，表示当前不对数据处理
		getTweets(null);
	}

	/**
	 * 用户信息get请求
	 * 
	 * @return
	 */
	public UserInfo getUserInfo() {

		if (userInfo == null) {
			Request userInfoReq = OkHttpUtil
					.getRequestByGet(Constant.USER_INFO);
			Call userInfoCall = OkHttpUtil.getHttpClient().newCall(userInfoReq);
			// get 异步get请求
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
	 * 获取推文信息
	 * 
	 * @return
	 */
	public List<Tweet> getTweets(final Handler handler) {
		// 当handler不为空时，强制发送请求，刷新数据
		if (null != tweetsList || null != handler) {
			Request tweetReq = OkHttpUtil.getRequestByGet(Constant.TWEETS_DATA);
			Call call = OkHttpUtil.getHttpClient().newCall(tweetReq);
			// get 异步get请求
			call.enqueue(new Callback() {
				@Override
				public void onResponse(Response resp) throws IOException {
					String resultStr = resp.body().string();
					//本次结果和上次结果不一致，需要刷新页面
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
						//将当前结果保存到上一次的结果
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
	 * 获取当前手机的分辨辨率
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
