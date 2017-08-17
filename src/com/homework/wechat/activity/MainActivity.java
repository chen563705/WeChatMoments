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
		// xml��listView
		pullToListView = (RefrashAndLoadListView) getListView();
		pullToListView.setReFrashListener(this);
		mAdapter = new MomentsAdapter(MainActivity.this, LoadDate);
		// handler��Ϣ����
		myhandle = new MyHandle();
		filterData(MyApplication.getInstance().getTweets(myhandle));
		setListAdapter(mAdapter);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//�������л�ʱ�����
		super.onConfigurationChanged(newConfig);
	}
	
	/**
	 * ��������������ݽ��д���
	 */
	private void filterData(List<Tweet> requestData) {
		if (requestData != null) {
			// �����󣬾ֲ��������³�ʼ��
			displayCount = 5;
			loadIndex = 0;
			LoadDate.clear();
			// ȥ����������
			resultData = validateData(requestData);
			// ��������
			load5MoreItem();
			//ˢ���û���Ϣ
			mAdapter.updateUserInfo();
		}
	}

	/**
	 * ��֤�������ݵĺϷ��� 1.�����в�������������Ϣ������ 2.������ͼƬ�����ֶ������ڣ����� 3.�����в����ڷ�������Ϣ������
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
	 * ���ݼ�����ɣ�֪ͨlistView
	 */
	private void notifyListViewUpdate() {
		mAdapter.onDateChange(LoadDate);
		pullToListView.refrashComplete();
		pullToListView.loadComplete();
	}

	/**
	 * ���ظ������ݣ�һ��׷��5��
	 */
	private void load5MoreItem() {

		for (; loadIndex < displayCount && loadIndex < resultData.size(); loadIndex++) {
			LoadDate.add(resultData.get(loadIndex));
		}
		// ������ɺ󣬽��������ݸ�������
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

	// �ڲ�handler������Ϣ
	class MyHandle extends Handler {

		@Override
		public void handleMessage(Message msg) {
			// ����ˢ����Ϣ
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
