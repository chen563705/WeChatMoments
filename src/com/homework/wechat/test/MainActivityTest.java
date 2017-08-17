package com.homework.wechat.test;

import org.junit.After;
import org.junit.Before;

import com.homework.wechat.activity.MainActivity;
import com.homework.wechat.view.RefrashAndLoadListView;

import android.content.Intent;
import android.test.InstrumentationTestCase;

/**
 * activityµƒUI≤‚ ‘¿‡
 * @author chenjiayin
 *
 */
public class MainActivityTest extends InstrumentationTestCase {
	private MainActivity mActivity;
	//private RefrashAndLoadListView listView;
	@Before
	public void setUp() throws Exception {
		Intent intent=new Intent();
		intent.setClassName("com.homework.wechat.activity", MainActivity.class.getName());
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mActivity =(MainActivity)getInstrumentation().startActivitySync(intent);
		//listView=(RefrashAndLoadListView)mActivity.getListView();
	}

	@After
	public void tearDown() throws Exception {
		mActivity.finish();
	}

}
