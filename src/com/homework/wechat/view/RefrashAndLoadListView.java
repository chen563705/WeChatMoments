package com.homework.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ImageView;
import android.widget.ListView;

import com.homework.wechat.R;
import com.homework.wechat.constant.Constant;
import com.homework.wechat.constant.Constant.State;
import com.homework.wechat.inteface.IRefrashAndILoadListener;

public class RefrashAndLoadListView extends ListView implements
		OnScrollListener {
	private View header;
	private ImageView reFrashImage;
	private int width;
	private int headerHeight;
	private int mFirstVisibleItem;
	private int mScrollState;
	private int startY;
	private boolean flag;
	private IRefrashAndILoadListener iReFrashAndLoad;
	private State state = Constant.State.IDLE;
	// �ײ�
	private View footer;// �ײ����֣�
	private int totalItemCount;// ��������
	private int lastVisibleItem;// ���һ���ɼ���item��
	boolean isLoading;// ���ڼ��أ�

	public RefrashAndLoadListView(Context context) {
		super(context);
		initView(context);
	}

	public RefrashAndLoadListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public RefrashAndLoadListView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initView(context);
	}

	/**
	 * ��hander�����listView��ȥ
	 * 
	 * @param context
	 */
	private void initView(Context context) {
		LayoutInflater inflater = LayoutInflater.from(context);
		// ����
		header = inflater.inflate(R.layout.header_layout, null);
		reFrashImage = (ImageView) header.findViewById(R.id.frash);
		viewMeasure(header);
		headerHeight = header.getMeasuredHeight();
		// ��hander���ϱ߾�����Ϊ�����߶ȵĸ������ﵽ����Ч��
		setPaddingTop(-header.getMeasuredHeight());
		this.addHeaderView(header);
		// �ײ�
		footer = inflater.inflate(R.layout.footer_layout, null);
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
		viewMeasure(footer);

		this.addFooterView(footer);
		this.setOnScrollListener(this);
	}

	/**
	 * view���������֪ͨ�������������С
	 * 
	 * @param view
	 */
	private void viewMeasure(View view) {
		ViewGroup.LayoutParams groupParams = view.getLayoutParams();
		if (groupParams == null) {
			groupParams = new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT);
		}
		width = MeasureSpec.makeMeasureSpec(groupParams.width,
				MeasureSpec.EXACTLY);
		int height;
		int tempHeight = groupParams.height;
		if (tempHeight > 0) {
			height = MeasureSpec.makeMeasureSpec(tempHeight,
					MeasureSpec.EXACTLY);
		} else {
			height = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
		}
		view.measure(width, height);
	}

	/**
	 * ����hander���ϱ߾�
	 * 
	 * @param padding
	 */
	private void setPaddingTop(int padding) {
		header.setPadding(header.getPaddingLeft(), padding,
				header.getPaddingRight(), header.getPaddingBottom());
		// Animation am=new TranslateAnimation(header.getPaddingLeft(),
		// header.getPaddingRight(), header.getPaddingBottom(), padding);
		// header.setAnimation(am);
		// �����ػ�
		header.invalidate();
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		this.mScrollState = scrollState;
		if (totalItemCount == lastVisibleItem
				&& scrollState == SCROLL_STATE_IDLE) {
			if (!isLoading) {
				isLoading = true;
				footer.findViewById(R.id.load_layout).setVisibility(
						View.VISIBLE);
				// ���ظ���
				iReFrashAndLoad.onLoad();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		this.mFirstVisibleItem = firstVisibleItem;
		this.lastVisibleItem = firstVisibleItem + visibleItemCount;
		this.totalItemCount = totalItemCount;
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		switch (ev.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (mFirstVisibleItem == 0) {
					flag = true;
					startY = (int) ev.getY();
				}
				break;
			case MotionEvent.ACTION_UP:
				if (state.equals(State.REALSE)) {
					state = State.FRASH;
					// �����������ݣ�
					iReFrashAndLoad.onReflash();
				} else if (state.equals(State.PULL)) {
					state = Constant.State.IDLE;
					flag = false;
				}
				reflashView(-headerHeight);
				break;
			case MotionEvent.ACTION_MOVE:
				move(ev);
				break;
		}

		return super.onTouchEvent(ev);
	}

	/**
	 * ����������
	 * 
	 * @param ev
	 */
	private void move(MotionEvent ev) {
		if (!flag) {
			return;
		}
		int curY = (int) ev.getY();
		int space = curY - startY;
		int paddingTop = space - headerHeight;
		switch (state) {
			case IDLE:
				if (space > 0) {
					state = Constant.State.PULL;
				}
				return;
			case PULL:
				if (space > headerHeight + 30
						&& mScrollState == SCROLL_STATE_TOUCH_SCROLL) {
					state = Constant.State.REALSE;
				}
				break;
			case REALSE:
				if (space < headerHeight + 30) {
					state = Constant.State.PULL;
				} else if (space < 0) {
					state = Constant.State.IDLE;
					flag = false;
				}
				break;
		}
		reflashView(paddingTop);
	}

	/**
	 * ����״̬���ý���
	 */
	private void reflashView(int paddingTop) {
		// reFrashImage
		// ˳ʱ����ת
		Animation am1 = new RotateAnimation(0, +360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		am1.setDuration(10000);
		am1.setFillAfter(true);

		Animation am2 = new RotateAnimation(0, -360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		am2.setDuration(10000);
		am2.setFillAfter(true);

		switch (state) {
			case IDLE:
				reFrashImage.clearAnimation();
				setPaddingTop(paddingTop);
				break;
			case PULL:
				reFrashImage.clearAnimation();
				setPaddingTop(paddingTop);
				reFrashImage.setAnimation(am1);
				break;
			case REALSE:
				reFrashImage.clearAnimation();
				setPaddingTop(paddingTop);
				reFrashImage.setAnimation(am2);
				break;
			case FRASH:
				reFrashImage.clearAnimation();
				setPaddingTop(paddingTop);
				reFrashImage.setAnimation(am2);
				break;
		}
	}

	/**
	 * ˢ����ɺ����
	 */
	public void refrashComplete() {
		state = State.IDLE;
		flag = false;
		reflashView(-headerHeight);
	}

	/**
	 * �������
	 */
	public void loadComplete() {
		isLoading = false;
		footer.findViewById(R.id.load_layout).setVisibility(View.GONE);
	}

	/**
	 * ��Ӽ�����
	 * 
	 * @param iReFrash
	 */
	public void setReFrashListener(IRefrashAndILoadListener iReFrashAndLoad) {
		this.iReFrashAndLoad = iReFrashAndLoad;
	}

}
