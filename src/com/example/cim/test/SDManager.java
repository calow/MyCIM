package com.example.cim.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

import com.example.cim.util.FileUtil;

public class SDManager {

	private Context mContext;

	private String[] names = { "songhuiqiao.jpg", "zhangzetian.jpg",
			"songqian.jpg", "hangxiaozhu.jpg", "jingtian.jpg", "liuyifei.jpg",
			"kangyikun.jpg", "dengziqi.jpg" };

	public SDManager(Context mContext) {
		this.mContext = mContext;
	}

	public void moveUserIcon() {
		String path = FileUtil.getRecentChatPath();
		FileOutputStream out = null;
		InputStream is = null;
		for (int i = 0; i < names.length; i++) {
			try {
				is = mContext.getResources().getAssets().open(names[i]);
				out = new FileOutputStream(new File(path + names[i]));
				int len = 0;
				byte[] buffer = new byte[1024];
				while ((len = is.read(buffer)) != -1) {
					out.write(buffer, 0, len);
					out.flush();
				}
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
