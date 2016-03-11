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
	 * ��������getToolLoader
	 * 
	 * @param jarPath
	 *            jar��·��
	 * @param context
	 *            Context�����Ķ���
	 * @param parentLoader
	 *            ��Classloader����
	 * @return ����֮ǰ�ù��ı���������ClassLoader
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
	 * @��������getClazz
	 * @��������ȡĳ�����ع��ߵļ�����
	 * @param fileName
	 *            �ļ���
	 * @return ����ĳ�����ߵļ�����
	 */
	public static Tool getTool(String fileName) {
		return tools.get(fileName);
	}

	/**
	 * @��������putClazz
	 * @��������ĳ�����ع��ߵļ�����put��map�н��б���
	 * @param fileName
	 *            �ļ���
	 * @param clazz
	 *            ĳ�����ع��ߵļ�����
	 */
	public static synchronized void putTool(String fileName, Tool tool) {
		tools.put(fileName, tool);
	}

	/**
	 * @��������removeClazz
	 * @�������Ƴ�ĳ�����ߵļ����ࣨ���߸��º��Ƴ��ɵļ�����ʱʹ�ã�
	 * @param fileName
	 *            �ļ���
	 */
	public static synchronized void removeTool(String fileName) {
		tools.remove(fileName);
	}

	/**
	 * #��������isContainsClazz
	 * 
	 * @�������ж�map���Ƿ����ĳ�����ع��ߵļ�����
	 * @param fileName
	 *            �ļ���
	 * @return �����жϽ��
	 */
	public static boolean isContainsTool(String fileName) {
		return tools.containsKey(fileName);
	}

}
