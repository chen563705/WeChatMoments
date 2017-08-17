package com.homework.wechat.adapter;

import java.util.List;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.homework.wechat.MyApplication;
import com.homework.wechat.R;
import com.homework.wechat.bean.Comment;
import com.homework.wechat.bean.Tweet;
import com.homework.wechat.bean.UserInfo;
import com.homework.wechat.util.ImageLoadUtil;
import com.homework.wechat.view.MyGridView;
import com.homework.wechat.view.UserInfoLinearLayout;

public class MomentsAdapter extends BaseAdapter {
	private List<Tweet> mList;
	private LayoutInflater mInflater;
	private Context mContext;
	private UserInfo userInfo;

	public MomentsAdapter(Context context, List<Tweet> list) {
		mInflater = LayoutInflater.from(context);
		mContext = context;
		userInfo = MyApplication.getInstance().getUserInfo();
		this.mList = list;
	}

	@Override
	public int getCount() {
		return mList == null ? 0 : mList.size();
	}

	@Override
	public Tweet getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateUserInfo() {
		userInfo = MyApplication.getInstance().getUserInfo();
	}

	public void onDateChange(List<Tweet> mList) {
		this.mList = mList;
		this.notifyDataSetChanged();
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// ��ʼ������
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.list_item, null);
			// �û�
			holder.customLinear = (UserInfoLinearLayout) convertView
					.findViewById(R.id.bg_view);
			holder.user_bg = (ImageView) convertView
					.findViewById(R.id.bg_image);
			holder.user_avator = (ImageView) convertView
					.findViewById(R.id.avatar_image);
			holder.user_name = (TextView) convertView
					.findViewById(R.id.name_text);
			// ����
			holder.avator = (ImageView) convertView.findViewById(R.id.avator);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.gridView = (MyGridView) convertView
					.findViewById(R.id.gridView);
			holder.mCommentLayout = (LinearLayout) convertView
					.findViewById(R.id.comment_layout);
			// ����ʱ��
			holder.mCreatedAtView = (TextView) convertView
					.findViewById(R.id.created_at);
			//int width=MyApplication.getInstance().getDispalyWidth()/6;
			//holder.avator.setLayoutParams(new android.widget.RelativeLayout.LayoutParams(width,width));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		final Tweet bean = getItem(position);
		//�����û�item
		setUserInfoItem(holder,position);
		//��������(�����ߣ���Ϣ��,ͼƬ)item
		setContentItem(holder, bean);
		// ����
		setCommentsItem(holder, bean);

		return convertView;
	}
   
	
	/**
	 * ��������item
	 * @param holder
	 * @param bean
	 */
	private void setCommentsItem(ViewHolder holder, final Tweet bean) {
		if (bean.getComments() != null && bean.getComments().size() > 0) {
			holder.mCreatedAtView.setCompoundDrawablesWithIntrinsicBounds(0, 0,
					0, R.drawable.bg_top);
			holder.mCommentLayout.setVisibility(View.VISIBLE);
			holder.mCommentLayout.removeAllViews();
			for (Comment tempComment : bean.getComments()) {
				TextView t = addCommentsTextView(tempComment);
				holder.mCommentLayout.addView(t);
			}
		}
	}

	/**
	 * ������������һ��textView
	 * @param tempComment
	 * @return
	 */
	private TextView addCommentsTextView(Comment tempComment) {
		TextView t = new TextView(mContext);
		t.setLayoutParams(new LinearLayout.LayoutParams(
				new ViewGroup.LayoutParams(
						ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT)));
		t.setTextSize(14);
		// html��ʽ����������ɫ
		String str = String
				.format("<font color=\"#576B95\">%s</font><font color=\"#000000\">: %s</font>",
						tempComment.getSender().getNick(),
						tempComment.getContent());
		t.setText(Html.fromHtml(str));
		return t;
	}

	/**
	 * ��������item
	 * @param holder
	 * @param bean
	 */
	private void setContentItem(ViewHolder holder, final Tweet bean) {
		//���ط�����ͼƬ
		ImageLoadUtil.getInstance().loadImage(bean.getSender().getAvatar(),
				holder.avator, true);
		//�����ǳƺ�����
		holder.name.setText(bean.getSender().getNick());
		holder.content.setText(bean.getContent());
		// ������Ƭ
		if (bean.getImages() != null && bean.getImages().size() > 0) {
			holder.gridView.setVisibility(View.VISIBLE);
			holder.gridView.setAdapter(new DynamicGridAdapter(bean.getImages(),
					mContext));
		} else {
			holder.gridView.setVisibility(View.GONE);
		}
	}

   /**
    * �����û���Ϣ
    * @param holder
    * @param position
    */
	private void setUserInfoItem(ViewHolder holder, int position) {
		// ֻ�е�һ��item�����û���Ϣ
		if (position == 0 && userInfo != null) {
			holder.customLinear.setVisibility(View.VISIBLE);
			ImageLoadUtil.getInstance().loadImage(userInfo.getProfileImage(),
					holder.user_bg, true);

			holder.user_name.setText(userInfo.getNick());
			ImageLoadUtil.getInstance().loadImage(userInfo.getAvatar(),
					holder.user_avator, true);
		} else {
			holder.customLinear.setVisibility(View.GONE);
		}
	}

	private static class ViewHolder {
		// �û�
		private View customLinear;
		private ImageView user_bg;
		private TextView user_name;
		private ImageView user_avator;

		// ����
		private TextView name;
		private ImageView avator;
		private TextView content;
		private MyGridView gridView;
		private TextView mCreatedAtView;
		private LinearLayout mCommentLayout;
	}
}
