package com.example.cim.adapter;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.cim.R;
import com.example.cim.model.RecentChat;
import com.example.cim.util.FileUtil;
import com.example.cim.util.ImgUtil;
import com.example.cim.util.ImgUtil.OnLoadBitmapListener;
import com.example.cim.util.SystemMethod;
import com.example.cim.view.IphoneTreeView;
import com.example.cim.view.IphoneTreeView.IphoneTreeHeaderAdapter;

public class ExpAdapter extends BaseExpandableListAdapter implements
		IphoneTreeHeaderAdapter {

	private static final String TAG = "ExpAdapter";
	private Context mContext;
	private HashMap<String, List<RecentChat>> maps;
	private IphoneTreeView mIphoneTreeView;
	private View mSearchView;
	private HashMap<String, SoftReference<Bitmap>> hashMaps = new HashMap<String, SoftReference<Bitmap>>();
	private String dir = FileUtil.getRecentChatPath();

	// Î±Êý¾Ý
	private HashMap<Integer, Integer> groupStatusMap;
	private String[] groups = { "ÎÒµÄºÃÓÑ", "¼ÒÈË", "S2S76°à", "S2S73°à", "S1S24°à",
			"S1S5°à", "Ç×ÆÝ" };
	private String[][] children = {
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" },
			{ "ËÎ»ÛÇÇ", "ÕÂÔóÌì", "ËÎÜç", "º«Ð¢Öé", "¾°Ìð", "ÁõÒà·Æ", "¿µÒÝçû", "µË×ÏÆå" } };
	private String[][] childPath = {
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" },
			{ dir + "songhuiqiao.jpg", dir + "zhangzetian.jpg",
					dir + "songqian.jpg", dir + "hangxiaozhu.jpg",
					dir + "jingtian.jpg", dir + "liuyifei.jpg",
					dir + "kangyikun.jpg", dir + "dengziqi.jpg" } };

	private String[][] onlineStatu = {
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" },
			{ "1", "1", "1", "1", "1", "1", "1", "1" } };

	private String[][] childrendFeel = null;

	public ExpAdapter(Context context, HashMap<String, List<RecentChat>> maps,
			IphoneTreeView mIphoneTreeView, View searchView) {
		this.mContext = context;
		this.maps = maps;
		this.mIphoneTreeView = mIphoneTreeView;
		groupStatusMap = new HashMap<Integer, Integer>();
		mSearchView = searchView;
		initTreeData();
	}

	public Object getChild(int groupPosition, int childPosition) {
		return children[groupPosition][childPosition];
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		if(children[groupPosition] != null){
			return children[groupPosition].length;
		}else{
			return 0;
		}
	}

	public Object getGroup(int groupPosition) {
		return groups[groupPosition];
	}

	public int getGroupCount() {
		if(groups != null){
			return groups.length;
		}else{
			return 0;
		}
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}

	public boolean hasStableIds() {
		return true;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		GroupHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_constact_child, null);
			holder = new GroupHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.contact_list_item_name);
			holder.feelView = (TextView) convertView
					.findViewById(R.id.cpntact_list_item_state);
			holder.iconView = (ImageView) convertView.findViewById(R.id.icon);
			convertView.setTag(holder);
		} else {
			holder = (GroupHolder) convertView.getTag();
		}

		String path = childPath[groupPosition][childPosition];
		if (hashMaps.containsKey(path)) {
			holder.iconView.setImageBitmap(hashMaps.get(path).get());
			// ÁíÒ»¸öµØ·½»º´æÊÍ·Å×ÊÔ´
			ImgUtil.getInstance().reomoveCache(path);
		} else {
			holder.iconView.setTag(path);
			ImgUtil.getInstance().loadBitmap(path, new OnLoadBitmapListener() {
				@Override
				public void loadImage(Bitmap bitmap, String path) {
					ImageView iv = (ImageView) mIphoneTreeView
							.findViewWithTag(path);
					if (bitmap != null && iv != null) {
						bitmap = SystemMethod.toRoundCorner(bitmap, 15);
						iv.setImageBitmap(bitmap);

						if (!hashMaps.containsKey(path)) {
							hashMaps.put(path,
									new SoftReference<Bitmap>(bitmap));
						}
					}
				}
			});

		}
		holder.nameView.setText(getChild(groupPosition, childPosition)
				.toString());
		holder.feelView.setText("°®Éú»î...°®Android...");
		return convertView;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		ChildHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.fragment_constact_head_view, null);
			holder = new ChildHolder();
			holder.nameView = (TextView) convertView
					.findViewById(R.id.group_name);
			holder.onLineView = (TextView) convertView
					.findViewById(R.id.online_count);
			holder.iconView = (ImageView) convertView
					.findViewById(R.id.group_indicator);
			convertView.setTag(holder);
		} else {
			holder = (ChildHolder) convertView.getTag();
		}
		holder.nameView.setText(groups[groupPosition]);
		holder.onLineView.setText(getChildrenCount(groupPosition) + "/"
				+ getChildrenCount(groupPosition));
		if (isExpanded) {
			holder.iconView.setImageResource(R.drawable.qb_down);
		} else {
			holder.iconView.setImageResource(R.drawable.qb_right);
		}
		return convertView;
	}

	@Override
	public int getTreeHeaderState(int groupPosition, int childPosition) {
		final int childCount = getChildrenCount(groupPosition);
		if (childPosition == childCount - 1) {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_PUSHED_UP;
		} else if (childPosition == -1
				&& !mIphoneTreeView.isGroupExpanded(groupPosition)) {
			// mSearchView.setVisibility(View.VISIBLE);
			return PINNED_HEADER_GONE;
		} else {
			// mSearchView.setVisibility(View.GONE);
			return PINNED_HEADER_VISIBLE;
		}
	}

	@Override
	public void configureTreeHeader(View header, int groupPosition,
			int childPosition, int alpha) {
		((TextView) header.findViewById(R.id.group_name))
				.setText(groups[groupPosition]);
		((TextView) header.findViewById(R.id.online_count))
				.setText(getChildrenCount(groupPosition) + "/"
						+ getChildrenCount(groupPosition));
	}

	@Override
	public void onHeadViewClick(int groupPosition, int status) {
		groupStatusMap.put(groupPosition, status);
	}

	@Override
	public int getHeadViewClickStatus(int groupPosition) {
		if (groupStatusMap.containsKey(groupPosition)) {
			return groupStatusMap.get(groupPosition);
		} else {
			return 0;
		}
	}

	class GroupHolder {
		TextView nameView;
		TextView feelView;
		ImageView iconView;
	}

	class ChildHolder {
		TextView nameView;
		TextView onLineView;
		ImageView iconView;
	}

	@SuppressWarnings("rawtypes")
	public void initTreeData() {
		if (maps != null && maps.size() > 0) {
			String[] parent = new String[maps.size()];// ·Ö×éÃû³Æ
			String[][] cs = new String[maps.size()][];// ×´Ì¬
			String[][] cn = new String[maps.size()][];// Ãû³Æ
			String[][] cp = new String[maps.size()][];// Í·ÏñÂ·¾¶
			String[][] cf = new String[maps.size()][];// ¸ÐÊÜ
			Set keySet = maps.keySet();// ·µ»Ø¼üµÄ¼¯ºÏ
			Iterator it = keySet.iterator();
			int i = 0;
			while (it.hasNext()) {
				// groupName
				String key = (String) it.next();
				parent[i] = key;
				// childrend
				List<RecentChat> rc = maps.get(key);
				if (rc != null) {
					String[] childStatu = new String[rc.size()];
					String[] childName = new String[rc.size()];
					String[] childPath = new String[rc.size()];
					String[] childFeel = new String[rc.size()];
					for (int j = 0; j < rc.size(); j++) {
						childStatu[j] = rc.get(j).getStatu();
						childName[j] = rc.get(j).getUserName();
						childPath[j] = rc.get(j).getImgPath();
						childFeel[j] = rc.get(j).getUserFeel();
					}
					cs[i] = childStatu;
					cn[i] = childName;
					cp[i] = childPath;
					cf[i] = childFeel;
				}
				i++;
			}
			groups = parent;
			children = cn;
			onlineStatu = cs;
			childPath = cp;
			childrendFeel = cf;
		}
	}
	
	public void updateData(HashMap<String, List<RecentChat>> map){
		maps = map;
		initTreeData();
	}
}
