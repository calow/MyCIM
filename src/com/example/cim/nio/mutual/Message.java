package com.example.cim.nio.mutual;

import java.io.Serializable;
/**
 * 消息对象
 * @author  @author 3979434@qq.com
 *
 */
public class Message implements Serializable {

	/**
	 * @author  3979434@qq.com
	 * 消息对象
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 消息Id(messageId)
	 */
	private String mid;
	
	
	/**
	 * 消息类型(messageType)
	 */
	private String type;
	/**
	 * 消息标题
	 */
	private String title;
	/**
	 * 消息类容，于type 组合为任何类型消息，content 根据 format 可表示为 text,json ,xml数据格式
	 */
	private String content;

	/**
	 * 消息发送者账号(fromUserId)
	 */
	private String sender;
	/**
	 * 消息发送者接收者(toUserId)
	 */
	private String receiver;

	/**
	 * 文件 id(ResourceId)
	 */
	private String file;
    /**
     * 文件类型
     */
	private String fileType;

	/**
	 * content 内容格式
	 */
	private String format = "txt";

	/**
	 * 消息发送时间(sendTime)
	 */
	private long timestamp;
	
	/**
	 * 群组Id
	 */
	private String groupId;
	
	/**
	 * 群组名称
	 */
	private String groupName;
	
	/**
	 * 发送者名称
	 */
	private String senderName;
	
	/**
	 * 消息集合Id
	 */
	private String messageSetId;
	
	public Message()
	{
		timestamp = System.currentTimeMillis();
	}
	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	@Override
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		buffer.append("<message>");
		buffer.append("<mid>").append(mid).append("</mid>");
		buffer.append("<type>").append(type).append("</type>");
		buffer.append("<title>").append(this.getTitle()==null?"":this.getTitle()).append("</title>");
		buffer.append("<content><![CDATA[").append(this.getContent()==null?"":this.getContent()).append("]]></content>");
		buffer.append("<file>").append(this.getFile()==null?"":this.getFile()).append("</file>");
		buffer.append("<fileType>").append(this.getFileType()==null?"":this.getFileType()).append("</fileType>");
		buffer.append("<sender>").append(this.getSender()==null?"":this.getSender()).append("</sender>");
		buffer.append("<receiver>").append(this.getReceiver()==null?"":this.getReceiver()).append("</receiver>");
		buffer.append("<format>").append(this.getFormat()==null?"":this.getFormat()).append("</format>");
		buffer.append("<timestamp>").append(timestamp).append("</timestamp>");
		buffer.append("<groupId>").append(this.getGroupId()==null?"":this.getGroupId()).append("</groupId>");
		buffer.append("<groupName>").append(this.getGroupName()==null?"":this.getGroupName()).append("</groupName>");
		buffer.append("<senderName>").append(this.getSenderName()==null?"":this.getSenderName()).append("</senderName>");
		buffer.append("<messageSetId>").append(this.getMessageSetId()==null?"":this.getMessageSetId()).append("</messageSetId>");
		buffer.append("</message>");
		return buffer.toString();
	}
	public String toXmlString()
	{
		return toString();
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getGroupId() {
		return groupId;
	}
	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	public String getGroupName() {
		return groupName;
	}
	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
	public String getSenderName() {
		return senderName;
	}
	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}
	public String getMessageSetId() {
		return messageSetId;
	}
	public void setMessageSetId(String messageSetId) {
		this.messageSetId = messageSetId;
	}
	
	
	
}
