package com.example.cim.view;

import java.util.List;

import com.example.cim.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ViewPagerIndicator extends LinearLayout {

	/**
	 * 绘制三角形的画笔
	 */
	private Paint mPaint;
	/**
	 * path构成�?个三角形
	 */
	private Path mPath;
	/**
	 * 三角形的宽度
	 */
	private int mTriangleWidth;
	/**
	 * 三角形的高度
	 */
	private int mTriangleHeight;

	/**
	 * 三角形的宽度为单个Tab�?1/6
	 */
	private static final float RADIO_TRIANGEL = 1.0f / 6;
	/**
	 * 三角形的�?大宽�?
	 */
	private final int DIMENSION_TRIANGEL_WIDTH = (int) (getScreenWidth() / 3 * RADIO_TRIANGEL);

	/**
	 * 初始时，三角形指示器的偏移量
	 */
	private int mInitTranslationX;
	/**
	 * 手指滑动时的偏移�?
	 */
	private float mTranslationX;

	/**
	 * 与只绑定的viewpager
	 */
	public ViewPager mViewPager;
	/**
	 * tab上的内容
	 */
	private List<String> mTabTitles;
	/**
	 * 默认的Tab数量
	 */
	private static final int COUNT_DEFAULT_TAB = 4;
	/**
	 * tab数量
	 */
	private int mTabVisibleCount = COUNT_DEFAULT_TAB;
	/**
	 * 标题正常时的颜色
	 */
	private static final int COLOR_TEXT_NORMAL = 0x77FFFFFF;
	/**
	 * 标题选中时的颜色
	 */
	private static final int COLOR_TEXT_HIGHLIGHTCOLOR = 0xFFFFFFFF;

	public ViewPagerIndicator(Context context) {
		this(context, null);
	}

	public ViewPagerIndicator(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.ViewPagerIndicator);
		mTabVisibleCount = a.getInt(R.styleable.ViewPagerIndicator_item_count,
				COUNT_DEFAULT_TAB);
		a.recycle();

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.parseColor("#ffffffff"));
		mPaint.setStyle(Style.FILL);
		mPaint.setPathEffect(new CornerPathEffect(3));
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {
		canvas.save();
		canvas.translate(mInitTranslationX + mTranslationX, getHeight() + 1);
		canvas.drawPath(mPath, mPaint);
		canvas.restore();
		super.dispatchDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {

		super.onSizeChanged(w, h, oldw, oldh);
		mTriangleWidth = (int) (w / mTabVisibleCount * RADIO_TRIANGEL);
		mTriangleWidth = Math.min(DIMENSION_TRIANGEL_WIDTH, mTriangleWidth);

		// 初始化三角形
		initTriangle();

		mInitTranslationX = getWidth() / mTabVisibleCount / 2 - mTriangleWidth
				/ 2;
	}

	/**
	 * 初始化三角形
	 */
	private void initTriangle() {
		mPath = new Path();
		mTriangleHeight = (int) (mTriangleWidth / 2 / Math.sqrt(2));
		mPath.moveTo(0, 0);
		mPath.lineTo(mTriangleWidth, 0);
		mPath.lineTo(mTriangleWidth / 2, -mTriangleHeight);
		mPath.close();
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.e("ViewPagerIndicator", "onFinishInflate");
		int cCount = getChildCount();
		Log.e("ViewPagerIndicator", "size = " + cCount);
	}

	// 对外的ViewPager的回调接�?
	public interface PageChangeListener {

		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels);

		public void onPageSelected(int position);

		public void onPageScrollStateChanged(int state);
	}

	// 对外的ViewPager的回调接�?
	private PageChangeListener onPageChangeListener;

	/**
	 * 设置viewpager
	 * 
	 * @param viewPager
	 * @param pos
	 */
	public void setViewPager(ViewPager viewPager, int pos) {
		this.mViewPager = viewPager;

		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				// 设置字体颜色高亮
				resetTextViewColor();
				highLightTextView(position);

				if (onPageChangeListener != null) {
					onPageChangeListener.onPageSelected(position);
				}
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {

				scroll(position, positionOffset);

				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrolled(position,
							positionOffset, positionOffsetPixels);
				}
			}

			@Override
			public void onPageScrollStateChanged(int state) {
				if (onPageChangeListener != null) {
					onPageChangeListener.onPageScrollStateChanged(state);
				}
			}
		});
		// 设置当前�?
		mViewPager.setCurrentItem(pos);
		// 高亮
		highLightTextView(pos);
	}

	/**
	 * 设置tab的标题内�? 可�?�，可以自己在布�?文件中写�?
	 * 
	 * @param tabTittles
	 */
	public void setTabTittles(List<String> tabTittles) {
		if (tabTittles != null && tabTittles.size() > 0) {
			this.removeAllViews();
			this.mTabTitles = tabTittles;

			for (String title : mTabTitles) {
				addView(generateTextView(title));
			}
			setItemClickEvent();
		}
	}

	/**
	 * 根据标题生成我们的TextView
	 * 
	 * @param text
	 * @return
	 */
	public TextView generateTextView(String text) {
		TextView tv = new TextView(getContext());
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.MATCH_PARENT);
		lp.width = getScreenWidth() / mTabVisibleCount;
		tv.setGravity(Gravity.CENTER);
		tv.setTextColor(COLOR_TEXT_NORMAL);
		tv.setText(text);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		tv.setLayoutParams(lp);
		return tv;
	}

	/**
	 * 获得屏幕的宽�?
	 * 
	 * @return
	 */
	public int getScreenWidth() {
		WindowManager wm = (WindowManager) getContext().getSystemService(
				Context.WINDOW_SERVICE);
		DisplayMetrics metrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(metrics);
		return metrics.widthPixels;
	}

	/**
	 * 设置点击事件
	 */
	private void setItemClickEvent() {
		int cCount = getChildCount();
		for (int i = 0; i < cCount; i++) {
			final int j = i;
			View view = getChildAt(i);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mViewPager.setCurrentItem(j);
				}
			});
		}
	}

	/**
	 * 设置可见标题个数
	 * 
	 * @param tabVisibleCount
	 */
	public void setTabVisibleCount(int tabVisibleCount) {
		this.mTabVisibleCount = tabVisibleCount;
	}

	/**
	 * 重置文本颜色
	 */
	private void resetTextViewColor() {
		for (int i = 0; i < getChildCount(); i++) {
			View view = getChildAt(i);
			if (view instanceof TextView) {
				((TextView) view).setTextColor(COLOR_TEXT_NORMAL);
			}
		}
	}

	/**
	 * 高亮文本
	 * 
	 * @param position
	 */
	private void highLightTextView(int position) {
		View view = getChildAt(position);
		if (view instanceof TextView) {
			((TextView) view).setTextColor(COLOR_TEXT_HIGHLIGHTCOLOR);
		}
	}

	/**
	 * 指示器跟随手指滚动，以及容器滚动
	 * 
	 * @param position
	 * @param offset
	 */
	public void scroll(int position, float offset) {

		mTranslationX = (position + offset) * getWidth() / mTabVisibleCount;

		int tabWidth = getScreenWidth() / mTabVisibleCount;

		if (offset > 0 && position >= (mTabVisibleCount - 2)
				&& getChildCount() > mTabVisibleCount
				&& position < (getChildCount() - 2)) {

			if (mTabVisibleCount != 1) {
				this.scrollTo((position - (mTabVisibleCount - 2)) * tabWidth
						+ (int) (offset * tabWidth), 0);
			} else {
				this.scrollTo(position * tabWidth + (int) (offset * tabWidth),
						0);
			}
		}
		invalidate();
	}
}
