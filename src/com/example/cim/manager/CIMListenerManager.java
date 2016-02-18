package com.example.cim.manager;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Context;

import com.example.cim.comparator.CIMMessageReceiveComparator;
import com.example.cim.listener.OnCIMMessageListener;

public class CIMListenerManager {

	private static ArrayList<OnCIMMessageListener> cimListeners = new ArrayList<OnCIMMessageListener>();

	public static void registerMessageListener(OnCIMMessageListener listener,
			Context mContext) {
		if (!cimListeners.contains(listener)) {
			cimListeners.add(listener);
			// ∞¥’’Ω” ’À≥–Úµπ–Ú
			Collections.sort(cimListeners, new CIMMessageReceiveComparator(
					mContext));
		}
	}

	public static void removeMessageListener(OnCIMMessageListener listener) {
		for (int i = 0; i < cimListeners.size(); i++) {
			if (listener.getClass() == cimListeners.get(i).getClass()) {
				cimListeners.remove(i);
			}
		}
	}

	public static ArrayList<OnCIMMessageListener> getCIMListeners() {
		return cimListeners;
	}
}
