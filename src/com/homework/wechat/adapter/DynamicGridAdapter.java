package com.homework.wechat.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.homework.wechat.R;
import com.homework.wechat.bean.Tweet;
import com.homework.wechat.bean.Tweet.Image;
import com.homework.wechat.util.ImageLoadUtil;

public class DynamicGridAdapter extends BaseAdapter {
	private List<Tweet.Image> file=new ArrayList<Tweet.Image>();
	private LayoutInflater mLayoutInflater;

	public DynamicGridAdapter(List<Tweet.Image> files, Context context) {
		this.file.addAll(files);
		mLayoutInflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return file.size();
	}

	@Override
	public Image getItem(int position) {
		return file.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public boolean isEnabled(int position) {
		if(getCount()==4 && position==2){
		   return false;
		}
		return true;
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MyGridViewHolder viewHolder;
		if (convertView == null) {
			viewHolder = new MyGridViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.gridview_item,
					parent, false);
			
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.album_image);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (MyGridViewHolder) convertView.getTag();
		}
		if (getCount() == 1) {
			//300,250
			viewHolder.imageView.setLayoutParams(new android.widget.AbsListView.LayoutParams(parent.getWidth()*3/5-20, parent.getWidth()*3/5));
		}else{
			viewHolder.imageView.setLayoutParams(new android.widget.AbsListView.LayoutParams((parent.getWidth()-6)/3, (parent.getWidth()-6)/3));
		}
		String url = getItem(position).getUrl();
		if(!isEnabled(position)){
			convertView.setVisibility(View.GONE);
			file.add(position+1, file.get(position));
			notifyDataSetChanged();
			return convertView;
		}
		//ImageLoader.getInstance().displayImage(url, viewHolder.imageView);
		ImageLoadUtil.getInstance().loadImage(url, viewHolder.imageView, true);

		return convertView;
	}

	
	private static class MyGridViewHolder {
		ImageView imageView;
	}
}
