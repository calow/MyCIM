package com.example.utils;

import java.util.HashMap;
import java.util.Map;

import com.example.testwebview.IFeature;

public class ClassLoaderUtil {

	public static Map<String, IFeature> clazz = new HashMap<String, IFeature>();

	/**
	 * @方法名：getClazz
	 * @描述：获取某个本地工具的加载类
	 * @param fileName 文件名
	 * @return 返回某个工具的加载类
	 */
	public static IFeature getClazz(String fileName) {
		return ClassLoaderUtil.clazz.get(fileName);
	}

	/**
	 * @方法名：putClazz
	 * @描述：将某个本地工具的加载类put到map中进行保存
	 * @param fileName 文件名
	 * @param clazz 某个本地工具的加载类
	 */
	public static synchronized void putClazz(String fileName, IFeature clazz) {
		ClassLoaderUtil.clazz.put(fileName, clazz);
	}

	/**
	 * @方法名：removeClazz
	 * @描述：移除某个工具的加载类（工具更新后移除旧的加载类时使用）
	 * @param fileName 文件名
	 */
	public static synchronized void removeClazz(String fileName){
		ClassLoaderUtil.clazz.remove(fileName);
	}
	
	/**
	 * #方法名：isContainsClazz
	 * @描述：判断map中是否存在某个本地工具的加载类
	 * @param fileName 文件名
	 * @return 返回判断结果
	 */
	public static boolean isContainsClazz(String fileName){
		return ClassLoaderUtil.clazz.containsKey(fileName);
	}
}
