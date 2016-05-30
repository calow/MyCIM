package com.example.cim.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.cim.R;
import com.example.cim.adapter.GridViewAdapter;
import com.example.cim.adapter.ViewPagerAdapter;
import com.example.cim.model.ChatEmoji;
import com.example.cim.util.FaceConversionUtil;

public class ChatRelativeLayout extends RelativeLayout implements
		OnItemClickListener, OnClickListener {

	private Context context;

	private ImageButton ib_face;
	private EditText et_message;
	private RelativeLayout rl_facetable;
	private ViewPager vp_contains;
	private LinearLayout ll_image;

	private List<View> pageViews;
	private List<List<ChatEmoji>> emojis;

	private List<GridViewAdapter> mAdapters;

	private List<ImageView> bottomPoionts;

	/** 当前表情页 */
	private int current = 0;

	/** 表情页的监听事件 */
	private OnCorpusSelectedListener mListener;

	public ChatRelativeLayout(Context context) {
		super(context);
		this.context = context;
	}

	public ChatRelativeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public ChatRelativeLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		emojis = FaceConversionUtil.getInstance(context).emojiLists;
		onCreate();
	}

	private void onCreate() {
		initView();
		initViewpager();
		initPoint();
		initData();
	}

	private void initView() {
		ib_face = (ImageButton) findViewById(R.id.ib_face);
		ib_face.setOnClickListener(this);
		et_message = (EditText) findViewById(R.id.et_message);
		et_message.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					if (rl_facetable.getVisibility() == View.VISIBLE) {
						rl_facetable.setVisibility(View.GONE);
					}
				} else {
					InputMethodManager imm = (InputMethodManager) context
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
				}
			}
		});
		rl_facetable = (RelativeLayout) findViewById(R.id.rl_facetable);
		vp_contains = (ViewPager) findViewById(R.id.vp_contains);
		ll_image = (LinearLayout) findViewById(R.id.ll_image);
	}

	private void initViewpager() {
		pageViews = new ArrayList<View>();
		// 左侧添加空白view
		View leftView = new View(context);
		// 设置透明背景
		leftView.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(leftView);

		mAdapters = new ArrayList<GridViewAdapter>();
		for (int i = 0; i < emojis.size(); i++) {
			GridView view = new GridView(context);
			GridViewAdapter adapter = new GridViewAdapter(context,
					emojis.get(i));
			view.setAdapter(adapter);
			mAdapters.add(adapter);
			view.setOnItemClickListener(this);
			view.setNumColumns(7);
			view.setBackgroundColor(Color.TRANSPARENT);
			view.setHorizontalSpacing(1);
			view.setVerticalSpacing(1);
			view.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
			view.setCacheColorHint(0);
			view.setPadding(5, 0, 5, 0);
			view.setSelector(new ColorDrawable(Color.TRANSPARENT));
			view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			view.setGravity(Gravity.CENTER);
			pageViews.add(view);
		}
		View rightView = new View(context);
		// 设置透明背景
		leftView.setBackgroundColor(Color.TRANSPARENT);
		pageViews.add(rightView);
	}

	private void initPoint() {
		bottomPoionts = new ArrayList<ImageView>();
		ImageView imageView;
		for (int i = 0; i < pageViews.size(); i++) {
			imageView = new ImageView(context);
			imageView.setImageResource(R.drawable.d1);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
			params.leftMargin = 10;
			params.rightMargin = 10;
			params.width = 8;
			params.height = 8;
			ll_image.addView(imageView, params);
			if (i == 0 || i == pageViews.size() - 1) {
				imageView.setVisibility(View.GONE);
			}
			if (i == 1) {
				imageView.setImageResource(R.drawable.d2);
			}
			bottomPoionts.add(imageView);
		}
	}

	private void initData() {
		vp_contains.setAdapter(new ViewPagerAdapter(pageViews));
		vp_contains.setCurrentItem(1);
		current = 1;
		vp_contains.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// 如果是第一屏或者是最后一屏禁止滑动
				if (arg0 == 0 || arg0 == pageViews.size() - 1) {
					if (arg0 == 0) {
						vp_contains.setCurrentItem(arg0 + 1);
					} else {
						vp_contains.setCurrentItem(arg0 - 1);
					}
				} else {
					current = arg0;
					// 描绘分页点
					draw_Point(arg0);
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	public void draw_Point(int index) {
		for (int i = 0; i < bottomPoionts.size(); i++) {
			if (i == index) {
				bottomPoionts.get(i).setImageResource(R.drawable.d2);
			} else {
				bottomPoionts.get(i).setImageResource(R.drawable.d1);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_face:
			et_message.clearFocus();//失去焦点
			if (rl_facetable.getVisibility() == View.VISIBLE) {
				rl_facetable.setVisibility(View.GONE);
			} else {
				rl_facetable.setVisibility(View.VISIBLE);
				InputMethodManager imm = (InputMethodManager) context
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(et_message.getWindowToken(), 0);
			}
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		ChatEmoji emoji = (ChatEmoji) mAdapters.get(current - 1).getItem(
				position);
		if (emoji.getId() == R.drawable.face_del_icon) {
			int selection = et_message.getSelectionStart();
			String text = et_message.getText().toString();
			if (selection > 0) {
				String text2 = text.substring(selection - 1);
				if ("]".equals(text2)) {
					int start = text.lastIndexOf("[");
					int end = selection;
					et_message.getText().delete(start, end);
					return;
				}
				et_message.getText().delete(selection - 1, selection);
			}
		}
		if (!TextUtils.isEmpty(emoji.getCharacter())) {
			if (mListener != null) {
				mListener.onCorpusSelected(emoji);
			}
			SpannableString spannableString = FaceConversionUtil.getInstance(
					context).addFace(context, emoji.getId(),
					emoji.getCharacter());
			et_message.append(spannableString);
		}
	}

	public interface OnCorpusSelectedListener {

		void onCorpusSelected(ChatEmoji emoji);

		void onCorpusDeleted();
	}

}
