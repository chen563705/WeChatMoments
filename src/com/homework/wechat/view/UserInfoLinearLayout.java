package com.homework.wechat.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homework.wechat.MyApplication;
import com.homework.wechat.R;

public class UserInfoLinearLayout extends LinearLayout {
	private ImageView bgImage;
	private TextView nameText;
	private ImageView avatarImage;
	private int width;
	private int height;

	public UserInfoLinearLayout(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public UserInfoLinearLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public UserInfoLinearLayout(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		height = (int) (MyApplication.getInstance().getDisplayHeight()* 0.5f + 0.5f);
		int heightSpec = MeasureSpec
				.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		width = MeasureSpec.getSize(widthMeasureSpec);
        //����profile-image �ĸ�Ϊ�����ֵ�0.8�����Ϊ�����ִ�С
		LinearLayout.LayoutParams parame1 = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, (int) (height * 0.8));
		bgImage.setLayoutParams(parame1);

        //����avatarͼƬ��СΪ������ȵ�0.25
		LinearLayout.LayoutParams parame3 = new LinearLayout.LayoutParams(
				(int) (width * 0.25f + 0.5f), (int) (width * 0.25f + 0.5f));
		avatarImage.setLayoutParams(parame3);
		
		super.onMeasure(widthMeasureSpec, heightSpec);
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		
		bgImage = (ImageView) findViewById(R.id.bg_image);

		nameText=(TextView) findViewById(R.id.name_text);
		
		//spaceLinear = (LinearLayout) findViewById(R.id.space_linear);

		avatarImage = (ImageView) findViewById(R.id.avatar_image);

	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		//bg����ʾλ�ã���0��0��ʼ����Ӧ�����С
		int childWidth=bgImage.getMeasuredWidth();
		int childHeignt=bgImage.getMeasuredHeight();
		bgImage.layout(0, 0, childWidth, childHeignt);
		//avatar��bg_image��space_linear���棬����λ�ڸ����ֵ�����
		childWidth=avatarImage.getMeasuredWidth();
		childHeignt=avatarImage.getMeasuredHeight();
		int mRight=30;
		int mtop=bgImage.getMeasuredHeight()-(int)(childHeignt*0.66);
		avatarImage.layout(r-mRight-childWidth, mtop, r-mRight, mtop+childHeignt);
		//name_textView ������avatart�ұ�ƽ��
		childWidth=nameText.getMeasuredWidth();
		childHeignt=nameText.getMeasuredHeight();
		mtop =bgImage.getMeasuredHeight()-(int)(avatarImage.getMeasuredHeight()*0.33);
		mRight=avatarImage.getMeasuredWidth()+30+15;
		nameText.layout(r-mRight-childWidth, mtop, r-mRight, mtop+childHeignt);
	}

}
