package com.homework.wechat.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;

import com.homework.wechat.MyApplication;
import com.homework.wechat.R;
import com.homework.wechat.adapter.MomentsAdapter;
import com.homework.wechat.bean.Tweet;
import com.homework.wechat.constant.Constant;
import com.homework.wechat.inteface.IRefrashAndILoadListener;
import com.homework.wechat.view.RefrashAndLoadListView;

public class MainActivity extends ListActivity implements
		IRefrashAndILoadListener {

	public static final String TAG = MainActivity.class.getSimpleName();
	private MomentsAdapter mAdapter;
	private RefrashAndLoadListView pullToListView;
	private Handler myhandle;
	private List<Tweet> resultData = new ArrayList<Tweet>();
	private List<Tweet> LoadDate = new ArrayList<Tweet>();
	private int displayCount = 5;
	private int loadIndex = 0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// xml的listView
		pullToListView = (RefrashAndLoadListView) getListView();
		pullToListView.setReFrashListener(this);
		mAdapter = new MomentsAdapter(MainActivity.this, LoadDate);
		// handler消息处理
		myhandle = new MyHandle();
		filterData(MyApplication.getInstance().getTweets(myhandle));
		setListAdapter(mAdapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//横竖屏切换时会调用
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * 对请求回来的数据进行处理
	 */
	private void filterData(List<Tweet> requestData) {
		if (requestData != null) {
			// 新请求，局部变量重新初始化
			displayCount = 5;
			loadIndex = 0;
			LoadDate.clear();
			// 去除冗余数据
			resultData = validateData(requestData);
			// 加载数据
			load5MoreItem();
			//刷新用户消息
			mAdapter.updateUserInfo();
		}
	}

	/**
	 * 验证网络数据的合法性 1.推文中不包含发送者消息，抛弃 2.推文中图片和文字都不存在，抛弃 3.评论中不存在发送着消息，抛弃
	 * 
	 * @param result
	 * @return
	 */
	private List<Tweet> validateData(List<Tweet> result) {
		List<Tweet> validateList = new ArrayList<Tweet>();
		if (result.size() > 0) {
			for (Tweet temp : result) {
				if (temp.getSender() != null
						&& (temp.getContent() != null || temp.getImages() != null)) {
					validateList.add(temp);
				}
			}
		}
		return validateList;
	}

	/**
	 * 数据加载完成，通知listView
	 */
	private void notifyListViewUpdate() {
		mAdapter.onDateChange(LoadDate);
		pullToListView.refrashComplete();
		pullToListView.loadComplete();
	}

	/**
	 * 加载更多数据，一次追加5个
	 */
	private void load5MoreItem() {

		for (; loadIndex < displayCount && loadIndex < resultData.size(); loadIndex++) {
			LoadDate.add(resultData.get(loadIndex));
		}
		// 加载完成后，将加载数据个数更新
		displayCount += loadIndex;
	}

	@Override
	public void onReflash() {
		MyApplication.getInstance().getTweets(myhandle);
	}

	@Override
	public void onLoad() {
		load5MoreItem();
		notifyListViewUpdate();
	}

	// 内部handler处理消息
	class MyHandle extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// 处理刷新消息
			switch (msg.what) {
				case Constant.GETDATA_SUC_MSG:
					List<Tweet> temp=(List<Tweet>)msg.obj;
					filterData(temp);
					Log.i(TAG, "msg reFrash success");
					break;
				case Constant.GETDATA_FAL_MSG:

					break;
				default:
					break;
			}
			notifyListViewUpdate();
		}
	}

}
