package com.example.testwebview;

public class JSUtil {

	public static final int OK = 0;
	public static final int ERROR = 1;

	public static final int CONTINUED = 0;
	public static final int UNCONTINUED = 1;

	public static void execCallback(final IWebview iWebview,
			final String callbackId, final String content, final int method,
			final int continued) {
		iWebview.getWebview().post(new Runnable() {

			@Override
			public void run() {
				iWebview.getWebview().loadUrl(
						"javascript:callback('" + callbackId + "','" + method
								+ "','" + content + "','" + continued + "')");
			}
		});

	}
}
