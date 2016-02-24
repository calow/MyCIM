package com.example.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParseXml extends DefaultHandler {
	// 存放遍历集合
	private List<Feature> list;
	// 构建Student对象
	private Feature feature;
	// 用来存放每次遍历后的元素名称(节点名称)
	private String tagName;

	public List<Feature> getList() {
		return list;
	}

	public void setList(List<Feature> list) {
		this.list = list;
	}

	public Feature getFeature() {
		return feature;
	}

	public void setFeature(Feature feature) {
		this.feature = feature;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * 开始解析文档时调用
	 */
	@Override
	public void startDocument() throws SAXException {
		list = new ArrayList<Feature>();
	}

	/**
	 * 标签开始节点被调用，获取标签属性值
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("Feature")) {
			feature = new Feature();
			// 获取Feature节点上的name属性值
			feature.setName(attributes.getValue(0));
			// 获取Feature节点上的value属性值
			feature.setValue(attributes.getValue(1));
		}
		this.tagName = qName;
	}
	
	/**
	 * 读取标签中的内容
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
//		String date=new String(ch,start,length); data为标签内容
		super.characters(ch, start, length);
	}

	/**
	 * 标签结束节点被调用
	 */
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if(qName.equals("Feature")){
            this.list.add(this.feature);
        }
        this.tagName=null;
	}

	/**
	 * 解析文档结束时被调用
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	
	
	

}
