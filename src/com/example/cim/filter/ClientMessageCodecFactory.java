package com.example.cim.filter;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFactory;
import org.apache.mina.filter.codec.ProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolEncoder;

public class ClientMessageCodecFactory implements ProtocolCodecFactory {

	private final ClientMessageEncoder mEncoder;
	private final ClientMessageDecoder mDecoder;

	public ClientMessageCodecFactory() {
		mEncoder = new ClientMessageEncoder();
		mDecoder = new ClientMessageDecoder();
	}

	@Override
	public ProtocolDecoder getDecoder(IoSession session) throws Exception {
		return mDecoder;
	}

	@Override
	public ProtocolEncoder getEncoder(IoSession session) throws Exception {
		return mEncoder;
	}

}
