package com.example.sax;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxParseXml extends DefaultHandler {
	// ��ű�������
	private List<Feature> list;
	// ����Student����
	private Feature feature;
	// �������ÿ�α������Ԫ������(�ڵ�����)
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
	 * ��ʼ�����ĵ�ʱ����
	 */
	@Override
	public void startDocument() throws SAXException {
		list = new ArrayList<Feature>();
	}

	/**
	 * ��ǩ��ʼ�ڵ㱻���ã���ȡ��ǩ����ֵ
	 */
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		if (qName.equals("Feature")) {
			feature = new Feature();
			// ��ȡFeature�ڵ��ϵ�name����ֵ
			feature.setName(attributes.getValue(0));
			// ��ȡFeature�ڵ��ϵ�value����ֵ
			feature.setValue(attributes.getValue(1));
		}
		this.tagName = qName;
	}
	
	/**
	 * ��ȡ��ǩ�е�����
	 */
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
//		String date=new String(ch,start,length); dataΪ��ǩ����
		super.characters(ch, start, length);
	}

	/**
	 * ��ǩ�����ڵ㱻����
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
	 * �����ĵ�����ʱ������
	 */
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
	}
	
	
	
	

}
