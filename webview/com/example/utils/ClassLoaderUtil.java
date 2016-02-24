package com.example.utils;

import java.util.HashMap;
import java.util.Map;

import com.example.testwebview.IFeature;

public class ClassLoaderUtil {

	public static Map<String, IFeature> clazz = new HashMap<String, IFeature>();

	/**
	 * @��������getClazz
	 * @��������ȡĳ�����ع��ߵļ�����
	 * @param fileName �ļ���
	 * @return ����ĳ�����ߵļ�����
	 */
	public static IFeature getClazz(String fileName) {
		return ClassLoaderUtil.clazz.get(fileName);
	}

	/**
	 * @��������putClazz
	 * @��������ĳ�����ع��ߵļ�����put��map�н��б���
	 * @param fileName �ļ���
	 * @param clazz ĳ�����ع��ߵļ�����
	 */
	public static synchronized void putClazz(String fileName, IFeature clazz) {
		ClassLoaderUtil.clazz.put(fileName, clazz);
	}

	/**
	 * @��������removeClazz
	 * @�������Ƴ�ĳ�����ߵļ����ࣨ���߸��º��Ƴ��ɵļ�����ʱʹ�ã�
	 * @param fileName �ļ���
	 */
	public static synchronized void removeClazz(String fileName){
		ClassLoaderUtil.clazz.remove(fileName);
	}
	
	/**
	 * #��������isContainsClazz
	 * @�������ж�map���Ƿ����ĳ�����ع��ߵļ�����
	 * @param fileName �ļ���
	 * @return �����жϽ��
	 */
	public static boolean isContainsClazz(String fileName){
		return ClassLoaderUtil.clazz.containsKey(fileName);
	}
}
