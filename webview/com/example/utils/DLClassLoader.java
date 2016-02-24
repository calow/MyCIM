package com.example.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import dalvik.system.DexClassLoader;

public class DLClassLoader extends DexClassLoader {

	private static final HashMap<String, DLClassLoader> mPluginClassloaders = new HashMap<String, DLClassLoader>();

	public DLClassLoader(String dexPath, String optimizedDirectory,
			String libraryPath, java.lang.ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

	/**
	 * 方法名：getClassLoader
	 * @param dexPath jar包路径
	 * @param context Context上下文对象
	 * @param parentLoader 父Classloader对象
	 * @return 返回之前用过的保存下来的ClassLoader
	 */
	public static DLClassLoader getClassLoader(String dexPath, Context context,
			ClassLoader parentLoader) {
		DLClassLoader dLClassLoader = mPluginClassloaders.get(dexPath);
		if (dLClassLoader != null) {
			return dLClassLoader;
		}
		File optimizedDirectory = context.getApplicationContext().getDir(
				"Dex", 0);// jar包解压路径
		dLClassLoader = new DLClassLoader(dexPath,
				optimizedDirectory.getPath(), null, parentLoader);
		mPluginClassloaders.put(dexPath, dLClassLoader);
		return dLClassLoader;

	}

}
