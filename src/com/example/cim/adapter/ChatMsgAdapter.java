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
import android.widget.RelativeLayout;
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
//		ChatMsgEntity entity = mList.get(position);
//		int statu = entity.getSendStatu();
//		ViewHolder viewHolder;
//		if (convertView == null) {
//			if (getItemViewType(position) == IMsgViewType.IMVT_COM_MSG) {
//				convertView = mInflater.inflate(
//						R.layout.chatting_item_msg_text_left, null);
//			} else {
//				convertView = mInflater.inflate(
//						R.layout.chatting_item_msg_text_right, null);
//			}
//			viewHolder = new ViewHolder();
//			viewHolder.mIsComeMsg = getItemViewType(position);
//			viewHolder.mSendTime = (TextView) convertView
//					.findViewById(R.id.tv_sendtime);
//			viewHolder.mUserHead = (ImageView) convertView
//					.findViewById(R.id.iv_userhead);
//			viewHolder.mSenderName = (TextView) convertView.findViewById(R.id.tv_sendername);
//			viewHolder.mContent = (TextView) convertView
//					.findViewById(R.id.tv_chatcontent);
//			viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_loading);
//			convertView.setTag(viewHolder);
//		} else {
//			viewHolder = (ViewHolder) convertView.getTag();
//			if(viewHolder.mIsComeMsg != getItemViewType(position)){
//				if (getItemViewType(position) == IMsgViewType.IMVT_COM_MSG) {
//					convertView = mInflater.inflate(
//							R.layout.chatting_item_msg_text_left, null);
//				} else {
//					convertView = mInflater.inflate(
//							R.layout.chatting_item_msg_text_right, null);
//				}
//				viewHolder = new ViewHolder();
//				viewHolder.mIsComeMsg = getItemViewType(position);
//				viewHolder.mSendTime = (TextView) convertView
//						.findViewById(R.id.tv_sendtime);
//				viewHolder.mUserHead = (ImageView) convertView
//						.findViewById(R.id.iv_userhead);
//				viewHolder.mSenderName = (TextView) convertView.findViewById(R.id.tv_sendername);
//				viewHolder.mContent = (TextView) convertView
//						.findViewById(R.id.tv_chatcontent);
//				viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_loading);
//				convertView.setTag(viewHolder);
//			}
//		}
//		viewHolder.mSendTime.setText(entity.getDate());
//		viewHolder.mUserHead.setImageBitmap(BitmapFactory.decodeResource(
//				mContext.getResources(), entity.getResId()));
//		viewHolder.mSenderName.setText(entity.getName());
//		SpannableString spannableString = FaceConversionUtil.getInstance(
//				mContext).getExpressionString(mContext, entity.getText());
//		viewHolder.mContent.setText(spannableString);
//		if(viewHolder.mIsComeMsg == IMsgViewType.IMVT_TO_MSG){
//			if(statu == -1){
//				viewHolder.mImageView.setVisibility(View.VISIBLE);
//				viewHolder.mImageView.setImageResource(R.drawable.circle_run);
//				AnimationDrawable drawable = (AnimationDrawable) viewHolder.mImageView.getDrawable();
//				drawable.start();
//			}else if(statu == 1 || statu == 0){
//				viewHolder.mImageView.setVisibility(View.GONE);
//			}else if(statu == -2){
//				viewHolder.mImageView.setVisibility(View.VISIBLE);
//				viewHolder.mImageView.setImageResource(R.drawable.fie);
//			}
//		}
//		return convertView;
		ChatMsgEntity entity = mList.get(position);
		int statu = entity.getSendStatu();
		WholeViewHolder wvh;
		RelativeLayout mLeftRL;
		RelativeLayout mRightRL;
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.chatting_item_msg_text, null);
			mLeftRL = (RelativeLayout) convertView.findViewById(R.id.rl_left_content);
			mRightRL = (RelativeLayout) convertView.findViewById(R.id.rl_right_content);
			wvh = new WholeViewHolder();
			wvh.mSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			
			wvh.mLeftRL = mLeftRL;
			wvh.mLeftUserHead = (ImageView) convertView.findViewById(R.id.iv_userhead_left);
			wvh.mLeftSenderName = (TextView) convertView.findViewById(R.id.tv_sendername_left);
			wvh.mLeftContent = (TextView) convertView.findViewById(R.id.tv_chatcontent_left);
			
			wvh.mRightRL = mRightRL;
			wvh.mRightUserHead = (ImageView) convertView.findViewById(R.id.iv_userhead_right);
			wvh.mRightSenderName = (TextView) convertView.findViewById(R.id.tv_sendername_right);
			wvh.mRightContent = (TextView) convertView.findViewById(R.id.tv_chatcontent_right);
			wvh.mRightStatu = (ImageView) convertView.findViewById(R.id.iv_loading);
			convertView.setTag(wvh);
		}else{
			wvh = (WholeViewHolder) convertView.getTag();
		}
		wvh.mSendTime.setText(entity.getDate());
		SpannableString spannableString = FaceConversionUtil.getInstance(
				mContext).getExpressionString(mContext, entity.getText());
		if (getItemViewType(position) == IMsgViewType.IMVT_COM_MSG) {
			wvh.mRightRL.setVisibility(View.GONE);
			wvh.mLeftRL.setVisibility(View.VISIBLE);
			wvh.mLeftUserHead.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), entity.getResId()));
			wvh.mLeftSenderName.setText(entity.getName());
			wvh.mLeftContent.setText(spannableString);
		}else{
			wvh.mLeftRL.setVisibility(View.GONE);
			wvh.mRightRL.setVisibility(View.VISIBLE);
			wvh.mRightUserHead.setImageBitmap(BitmapFactory.decodeResource(
					mContext.getResources(), entity.getResId()));
			wvh.mRightSenderName.setText(entity.getName());
			wvh.mRightContent.setText(spannableString);
			wvh.mRightStatu.setVisibility(View.VISIBLE);
			if(statu == -1){
				wvh.mRightStatu.setImageResource(R.drawable.circle_run);
				AnimationDrawable drawable = (AnimationDrawable) wvh.mRightStatu.getDrawable();
				drawable.start();
			}else if(statu == 1 || statu == 0){
				wvh.mRightStatu.setVisibility(View.GONE);
			}else if(statu == -2){
				wvh.mRightStatu.setImageResource(R.drawable.fie);
			}
		}
		return convertView;
	}

	class ViewHolder {
		public int mIsComeMsg;
		public TextView mSendTime;
		public ImageView mUserHead;
		public TextView mContent;
		public ImageView mImageView;//消息状态
		public TextView mSenderName;
	}
	
	class WholeViewHolder{
		public TextView mSendTime;//左右共同拥有的视图
		
		//左边独有视图
		public RelativeLayout mLeftRL;
		public ImageView mLeftUserHead;
		public TextView mLeftSenderName;
		public TextView mLeftContent;
		
		//右边独有视图
		public RelativeLayout mRightRL;
		public ImageView mRightUserHead;
		public TextView mRightSenderName;
		public TextView mRightContent;
		public ImageView mRightStatu;
	}

}
