package com.example.cim.comparator;

import java.util.Comparator;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;

import com.example.cim.listener.OnCIMMessageListener;
import com.example.cim.nio.constant.CIMConstant;

public class CIMMessageReceiveComparator implements
		Comparator<OnCIMMessageListener> {

	Context mContext;

	public CIMMessageReceiveComparator(Context context) {
		mContext = context;
	}

	@Override
	public int compare(OnCIMMessageListener lhs, OnCIMMessageListener rhs) {
		Integer order1 = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
		Integer order2 = CIMConstant.CIM_DEFAULT_MESSAGE_ORDER;
		ActivityInfo info;

		if (lhs instanceof Activity) {

			try {
				info = mContext.getPackageManager().getActivityInfo(
						((Activity) (lhs)).getComponentName(),
						PackageManager.GET_META_DATA);
				if (info.metaData != null) {
					order1 = info.metaData.getInt("CIM_RECEIVE_ORDER");
				}

			} catch (Exception e) {
			}
		}

		if (rhs instanceof Activity) {
			try {
				info = mContext.getPackageManager().getActivityInfo(
						((Activity) (rhs)).getComponentName(),
						PackageManager.GET_META_DATA);
				if (info.metaData != null) {
					order2 = info.metaData.getInt("CIM_RECEIVE_ORDER");
				}

			} catch (Exception e) {
			}
		}
		return order2.compareTo(order1);
	}

}
