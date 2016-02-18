package com.example.cim.manager;

import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.cim.exception.CIMSessionDisableException;
import com.example.cim.exception.NetWorkDisableException;
import com.example.cim.exception.WriteToClosedSessionException;
import com.example.cim.filter.ClientMessageCodecFactory;
import com.example.cim.nio.constant.CIMConstant;
import com.example.cim.nio.mutual.Message;
import com.example.cim.nio.mutual.ReplyBody;
import com.example.cim.nio.mutual.SentBody;

public class CIMConnectorManager {

	private NioSocketConnector mConnector;
	private ConnectFuture mFuture;
	private IoSession mSession;

	Context mContext;

	static CIMConnectorManager sManager;

	private ExecutorService executor;

	// 链接失败广播
	public static final String ACTION_CONNECTION_FAILED = "com.example.cim.CONNECTION_FAILED";
	// 链接成功广播
	public static final String ACTION_CONNECTION_SUCCESS = "com.example.cim.CONNECTION_SUCCESS";
	// 发送sendbody失败广播
	public static final String ACTION_SENT_FAILED = "com.example.cim.SENT_FAILED";
	// CIM连接状态
	public static final String ACTION_CONNECTION_STATUS = "com.example.cim.CONNECTION_STATUS";
	// 发送sendbody成功广播
	public static final String ACTION_SENT_SUCCESS = "com.example.cim.SENT_SUCCESS";
	// 网络变化广播
	public static final String ACTION_NETWORK_CHANGED = "android.net.conn.CONNECTIVITY_CHANGE";
	// 消息广播action
	public static final String ACTION_MESSAGE_RECEIVED = "com.exmaple.cim.MESSAGE_RECEIVED";
	// 发送sendbody成功后获得replaybody回应广播
	public static final String ACTION_REPLY_RECEIVED = "com.example.cim.REPLY_RECEIVED";
	// 未知异常
	public static final String ACTION_UNCAUGHT_EXCEPTION = "com.example.cim.UNCAUGHT_EXCEPTION";
	// 链接意外关闭广播
	public static final String ACTION_CONNECTION_CLOSED = "com.example.cim.CONNECTION_CLOSED";

	private CIMConnectorManager(Context context) {
		mContext = context;
		executor = Executors.newFixedThreadPool(3);
		mConnector = new NioSocketConnector();
		mConnector.setConnectTimeoutMillis(10 * 1000);
		mConnector.getSessionConfig().setBothIdleTime(180);
		mConnector.getSessionConfig().setKeepAlive(true);
		mConnector.getFilterChain().addLast("logger", new LoggingFilter());
		mConnector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ClientMessageCodecFactory()));
		mConnector.setHandler(mAdapter);
	}

	public static CIMConnectorManager getManager(Context context) {
		if (sManager == null) {
			synchronized (CIMConnectorManager.class) {
				if (sManager == null) {
					sManager = new CIMConnectorManager(context);
				}
			}
		}
		return sManager;
	}

	public void connect(final String cimServerHost, final int cimServerPort) {
		if (!netWorkAvailable(mContext)) {
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", new NetWorkDisableException());
			mContext.sendBroadcast(intent);
			return;
		}
		executor.submit(new Runnable() {

			@Override
			public void run() {
				syncConnection(cimServerHost, cimServerPort);
			}
		});
	}

	public static boolean netWorkAvailable(Context context) {
		try {
			ConnectivityManager nw = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = nw.getActiveNetworkInfo();
			return networkInfo != null;
		} catch (Exception e) {
		}

		return false;
	}

	private synchronized void syncConnection(final String cimServerHost,
			final int cimServerPort) {
		try {
			if (isConnected()) {
				return;
			}
			InetSocketAddress remoteSocketAddress = new InetSocketAddress(
					cimServerHost, cimServerPort);
			mFuture = mConnector.connect(remoteSocketAddress);
			mFuture.awaitUninterruptibly();
			mSession = mFuture.getSession();
		} catch (Exception e) {
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_FAILED);
			intent.putExtra("exception", e);
			mContext.sendBroadcast(intent);
			System.out.println("******************CIM连接服务器失败  " + cimServerHost
					+ ":" + cimServerPort);
		}
	}

	public boolean isConnected() {
		if (mSession == null || mConnector == null) {
			return false;
		}
		return mSession.isConnected();
	}

	public void send(final SentBody sentBody) {
		executor.execute(new Runnable() {

			@Override
			public void run() {
				if (mSession != null && mSession.isConnected()) {
					WriteFuture wf = mSession.write(sentBody);
					wf.awaitUninterruptibly(5, TimeUnit.SECONDS);
					if (!wf.isWritten()) {
						Intent intent = new Intent();
						intent.setAction(ACTION_SENT_FAILED);
						intent.putExtra("exception",
								new WriteToClosedSessionException());
						intent.putExtra("sentBody", sentBody);
						mContext.sendBroadcast(intent);
					}
				} else {
					Intent intent = new Intent();
					intent.setAction(ACTION_SENT_FAILED);
					intent.putExtra("exception",
							new CIMSessionDisableException());
					intent.putExtra("sentBody", sentBody);
					mContext.sendBroadcast(intent);
				}
			}
		});
	}

	public void closeSession() {
		if (mSession != null) {
			mSession.close(false);
		}
	}

	public void destroy() {
		if (sManager.mSession != null) {
			sManager.mSession.close(false);
			sManager.mSession.removeAttribute("account");
		}

		if (sManager.mConnector != null && !sManager.mConnector.isDisposed()) {
			sManager.mConnector.dispose();
		}
		sManager = null;
	}

	public void deliverIsConnected() {
		Intent intent = new Intent();
		intent.setAction(ACTION_CONNECTION_STATUS);
		intent.putExtra(CIMPushManager.KEY_CIM_CONNECTION_STATUS, isConnected());
		mContext.sendBroadcast(intent);
	}

	IoHandlerAdapter mAdapter = new IoHandlerAdapter() {

		@Override
		public void sessionCreated(IoSession session) throws Exception {
			System.out.println("******************CIM连接服务器成功:"
					+ session.getLocalAddress());
			Intent intent = new Intent();
			intent.setAction(ACTION_CONNECTION_SUCCESS);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void sessionOpened(IoSession session) throws Exception {
			session.getConfig().setBothIdleTime(180);
		}

		@Override
		public void messageReceived(IoSession session, Object message)
				throws Exception {
			if (message instanceof Message) {
				// 收到消息
				Intent intent = new Intent();
				intent.setAction(ACTION_MESSAGE_RECEIVED);
				intent.putExtra("message", (Message) message);
				System.out.println("connector收到消息");
				mContext.sendBroadcast(intent);
			}
			if (message instanceof ReplyBody) {
				// 收到回复消息
				Intent intent = new Intent();
				intent.setAction(ACTION_REPLY_RECEIVED);
				intent.putExtra("replyBody", (ReplyBody) message);
				System.out.println("connector收到回复消息");
				mContext.sendBroadcast(intent);
			}
		}

		@Override
		public void messageSent(IoSession session, Object message)
				throws Exception {
			Intent intent = new Intent();
			intent.setAction(ACTION_SENT_SUCCESS);
			intent.putExtra("sentBody", (SentBody) message);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void sessionIdle(IoSession session, IdleStatus status)
				throws Exception {
			System.out.println("******************CIM与服务器连接空闲:"
					+ session.getLocalAddress());
			SentBody sentBody = new SentBody();
			sentBody.setKey(CIMConstant.RequestKey.CLIENT_HEARTBEAT);
			send(sentBody);
		}

		@Override
		public void exceptionCaught(IoSession session, Throwable cause)
				throws Exception {
			Intent intent = new Intent();
			intent.setAction(ACTION_UNCAUGHT_EXCEPTION);
			intent.putExtra("exception", cause);
			mContext.sendBroadcast(intent);
		}

		@Override
		public void sessionClosed(IoSession session) throws Exception {
			System.out.println("******************CIM与服务器断开连接:"
					+ session.getLocalAddress());
			if (CIMConnectorManager.this.mSession.getId() == session.getId()) {
				Intent intent = new Intent();
				intent.setAction(ACTION_CONNECTION_CLOSED);
				mContext.sendBroadcast(intent);
			}
		}

	};

}
