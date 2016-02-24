package com.example.testwebview;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import android.content.Context;

import com.example.sax.Feature;
import com.example.sax.SaxParseXml;

public class FeatureManage {

	public static FeatureManage sFeatureManage;

	// clazz对象容器
	public Map<String, Class> clazzMap = new HashMap<String, Class>();

	public Context mContext;

	private FeatureManage(Context context) {
		this.mContext = context;
		// 加载properties中的Feature，路径为：file:///android_asset/data/properties.xml
		SAXParser parser = null;
		try {
			// 构建SAXParser
			parser = SAXParserFactory.newInstance().newSAXParser();
			// 实例化 DefaultHandler对象
			SaxParseXml parseXml = new SaxParseXml();
			// 加载资源文件 转化为一个输入流
			InputStream stream = context.getResources().getAssets()
					.open("data/properties.xml");
			// 调用parse()方法
			parser.parse(stream, parseXml);
			// 遍历结果
			List<Feature> list = parseXml.getList();
			for (Feature feature : list) {
				if (feature.getName() != null && !feature.getName().equals("")
						&& feature.getValue() != null
						&& !feature.getValue().equals("")) {
					saveClazz(feature);
				}
			}
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FeatureManage getInstance(Context context) {
		if (sFeatureManage == null) {
			synchronized (FeatureManage.class) {
				if (sFeatureManage == null) {
					sFeatureManage = new FeatureManage(context);
				}
			}
		}
		return sFeatureManage;
	}

	public void saveClazz(Feature feature) {
		String name = feature.getName();
		String value = feature.getValue();
		Class clazz = null;
		try {
			clazz = Class.forName(value);
			clazzMap.put(name, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public IFeature getFeature(String path) {
		IFeature iFeature = null;
		if (clazzMap.containsKey(path)) {
			try {
				iFeature = (IFeature) clazzMap.get(path).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return iFeature;
	}

}
