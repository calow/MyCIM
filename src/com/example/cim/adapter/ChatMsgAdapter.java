package com.example.cim.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.model.ChatMsgEntity;
import com.example.cim.util.FaceConversionUtil;

public class ChatMsgAdapter extends BaseAdapter {

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	private List<ChatMsgEntity> mList;

	private Context mContext;

	private LayoutInflater mInflater;

	public ChatMsgAdapter(List<ChatMsgEntity> list, Context context) {
		this.mList = list;
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		ChatMsgEntity entity = mList.get(position);

		if (entity.isComMeg()) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}
	}

	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ChatMsgEntity entity = mList.get(position);
		boolean isComMsg = entity.isComMeg();
		int statu = entity.getSendStatu();
		ViewHolder viewHolder;
		if (convertView == null) {
			if (isComMsg) {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = mInflater.inflate(
						R.layout.chatting_item_msg_text_right, null);
			}
			viewHolder = new ViewHolder();
			viewHolder.mSendTime = (TextView) convertView
					.findViewById(R.id.tv_sendtime);
			viewHolder.mUserHead = (ImageView) convertView
					.findViewById(R.id.iv_userhead);
			viewHolder.mSenderName = (TextView) convertView.findViewById(R.id.tv_sendername);
			viewHolder.mContent = (TextView) convertView
					.findViewById(R.id.tv_chatcontent);
			viewHolder.isComMsg = isComMsg;
			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_loading);
			if(!isComMsg){
				if(statu == -1){
					viewHolder.mImageView.setImageResource(R.drawable.circle_run);
					AnimationDrawable drawable = (AnimationDrawable) viewHolder.mImageView.getDrawable();
					drawable.start();
				}else if(statu == 1 || statu == 0){
					viewHolder.mImageView.setVisibility(View.GONE);
				}else if(statu == -2){
					viewHolder.mImageView.setImageResource(R.drawable.fie);
				}
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.mSendTime.setText(entity.getDate());
		viewHolder.mUserHead.setImageBitmap(BitmapFactory.decodeResource(
				mContext.getResources(), entity.getResId()));
		viewHolder.mSenderName.setText(entity.getName());
		SpannableString spannableString = FaceConversionUtil.getInstance(
				mContext).getExpressionString(mContext, entity.getText());
		viewHolder.mContent.setText(spannableString);
		viewHolder.isComMsg = entity.isComMeg();
		return convertView;
	}

	class ViewHolder {
		public TextView mSendTime;
		public ImageView mUserHead;
		public TextView mContent;
		public boolean isComMsg = true;
		public ImageView mImageView;//ÏûÏ¢×´Ì¬
		public TextView mSenderName;
	}

}
