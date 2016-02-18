package com.example.cim.filter;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.example.cim.nio.constant.CIMConstant;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;

public class ClientMessageDecoder extends CumulativeProtocolDecoder {

	IoBuffer mBuffer = IoBuffer.allocate(320).setAutoExpand(true);

	@Override
	protected boolean doDecode(IoSession iosession, IoBuffer iobuffer,
			ProtocolDecoderOutput out) throws Exception {

		boolean complete = false;

		while (iobuffer.hasRemaining()) {
			byte b = iobuffer.get();
			if (b == CIMConstant.MESSAGE_SEPARATE) {
				complete = true;
				break;
			}else{
				mBuffer.put(b);
			}
		}

		if (complete) {
			mBuffer.flip();
			byte[] bytes = new byte[mBuffer.limit()];
			mBuffer.get(bytes);
			String message = new String(bytes, "UTF-8");
			mBuffer.clear();
			System.out.println("ClientMessageDecoder:" + message);

			Object msg = mappingMessageObject(message);
			out.write(msg);
		}

		return complete;
	}

	private Object mappingMessageObject(String message) throws Exception {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(new ByteArrayInputStream(message
				.toString().getBytes("UTF-8")));
		
		String name = doc.getDocumentElement().getTagName();
		
		if(name.equals("reply")){
			ReplyBody reply = new ReplyBody();
			reply.setKey(doc.getElementsByTagName("key").item(0).getTextContent());
			reply.setCode(doc.getElementsByTagName("code").item(0).getTextContent());
			reply.setMessage(doc.getElementsByTagName("message").item(0).getTextContent());
			NodeList items = doc.getElementsByTagName("data").item(0).getChildNodes();
			for(int i = 0; i < items.getLength(); i++){
				Node node = items.item(i);
				reply.getData().put(node.getNodeName(), node.getNodeValue());
			}
			return reply;
		}
		
		if(name.equals("message")){
			Message body = new Message();
			body.setType(doc.getElementsByTagName("type").item(0).getTextContent());
			body.setContent(doc.getElementsByTagName("content").item(0).getTextContent());
			body.setFile(doc.getElementsByTagName("file").item(0).getTextContent());
			body.setFileType(doc.getElementsByTagName("fileType").item(0).getTextContent());
			body.setTitle(doc.getElementsByTagName("title").item(0).getTextContent());
			body.setSender(doc.getElementsByTagName("sender").item(0).getTextContent());
			body.setReceiver(doc.getElementsByTagName("receiver").item(0).getTextContent());
			body.setFormat(doc.getElementsByTagName("format").item(0).getTextContent());
			body.setMid(doc.getElementsByTagName("mid").item(0).getTextContent());
			body.setTimestamp(Long.valueOf(doc.getElementsByTagName("timestamp").item(0).getTextContent()));
			body.setGroupId(doc.getElementsByTagName("groupId").item(0).getTextContent());
			body.setGroupName(doc.getElementsByTagName("groupName").item(0).getTextContent());
			body.setSenderName(doc.getElementsByTagName("senderName").item(0).getTextContent());
			body.setMessageSetId(doc.getElementsByTagName("messageSetId").item(0).getTextContent());
			return body;
		}
		return null;
	}

}
