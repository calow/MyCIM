package com.example.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.example.tool.Tool;

import android.content.Context;


import dalvik.system.DexClassLoader;

public class ToolLoaderUtil extends DexClassLoader {

	public static Map<String, Tool> tools = new HashMap<String, Tool>();

	private static final HashMap<String, ToolLoaderUtil> loaders = new HashMap<String, ToolLoaderUtil>();

	public ToolLoaderUtil(String dexPath, String optimizedDirectory,
			String libraryPath, ClassLoader parent) {
		super(dexPath, optimizedDirectory, libraryPath, parent);
	}

	/**
	 * 方法名：getToolLoader
	 * 
	 * @param jarPath
	 *            jar包路径
	 * @param context
	 *            Context上下文对象
	 * @param parentLoader
	 *            父Classloader对象
	 * @return 返回之前用过的保存下来的ClassLoader
	 */
	public static ToolLoaderUtil getToolLoader(String jarPath,
			Context context, ClassLoader parentLoader) {
		ToolLoaderUtil loader = loaders.get(jarPath);
		if (loader != null) {
			return loader;
		}
		File optimizedDirectory = context.getFilesDir();
		loader = new ToolLoaderUtil(jarPath, optimizedDirectory.getPath(),
				null, parentLoader);
		loaders.put(jarPath, loader);
		return loader;
	}

	/**
	 * @方法名：getClazz
	 * @描述：获取某个本地工具的加载类
	 * @param fileName
	 *            文件名
	 * @return 返回某个工具的加载类
	 */
	public static Tool getTool(String fileName) {
		return tools.get(fileName);
	}

	/**
	 * @方法名：putClazz
	 * @描述：将某个本地工具的加载类put到map中进行保存
	 * @param fileName
	 *            文件名
	 * @param clazz
	 *            某个本地工具的加载类
	 */
	public static synchronized void putTool(String fileName, Tool tool) {
		tools.put(fileName, tool);
	}

	/**
	 * @方法名：removeClazz
	 * @描述：移除某个工具的加载类（工具更新后移除旧的加载类时使用）
	 * @param fileName
	 *            文件名
	 */
	public static synchronized void removeTool(String fileName) {
		tools.remove(fileName);
	}

	/**
	 * #方法名：isContainsClazz
	 * 
	 * @描述：判断map中是否存在某个本地工具的加载类
	 * @param fileName
	 *            文件名
	 * @return 返回判断结果
	 */
	public static boolean isContainsTool(String fileName) {
		return tools.containsKey(fileName);
	}

}
