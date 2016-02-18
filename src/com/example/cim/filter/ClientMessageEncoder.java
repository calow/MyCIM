package com.example.cim.filter;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

import com.example.cim.nio.constant.CIMConstant;

public class ClientMessageEncoder extends ProtocolEncoderAdapter {

	@Override
	public void encode(IoSession iosession, Object message, ProtocolEncoderOutput out)
			throws Exception {
		IoBuffer ioBuffer = IoBuffer.allocate(320).setAutoExpand(true);
		ioBuffer.put(message.toString().getBytes("UTF-8"));
		ioBuffer.put(CIMConstant.MESSAGE_SEPARATE);
		ioBuffer.flip();
		out.write(ioBuffer);
	}

}
