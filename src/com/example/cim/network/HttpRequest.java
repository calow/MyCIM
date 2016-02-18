package com.example.cim.network;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.AbstractHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

public class HttpRequest {

	private HttpCompliteListener listener;

	public static String JSESSIONID = null;

	public HttpRequest(HttpCompliteListener listener) {
		this.listener = listener;
	}

	public void httpPost(final String url, final Map<String, String> map,
			final File file) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				HttpPost httpPost = new HttpPost(url);
				MultipartEntity mpEntity = new MultipartEntity(); // 文件传输
				String json = null;
				try {
					if (file != null) {
						ContentBody cbFile = new FileBody(file);
						mpEntity.addPart("file", cbFile);
					}
					for (String key : map.keySet()) {
						if (map.get(key) != null) {
							StringBody stringBody = new StringBody(
									map.get(key), Charset.forName("UTF-8"));
							mpEntity.addPart(key, stringBody);
						}
					}
					// 设置参数实体
					httpPost.setEntity(mpEntity);
					// 获取HttpClient对象
					HttpClient httpClient = new DefaultHttpClient();
					// 连接超时
					httpClient.getParams().setParameter(
							CoreConnectionPNames.CONNECTION_TIMEOUT, 30000);
					// 请求超时
					httpClient.getParams().setParameter(
							CoreConnectionPNames.SO_TIMEOUT, 30000);
					if (JSESSIONID != null) {
						httpPost.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
					}
					HttpResponse httpResp = httpClient.execute(httpPost);
					int code = httpResp.getStatusLine().getStatusCode();
					if (code == 200) {
						json = EntityUtils.toString(httpResp.getEntity(),
								"UTF-8");
						/* 获取cookieStore */
						CookieStore cookieStore = ((AbstractHttpClient) httpClient)
								.getCookieStore();
						List<Cookie> cookies = cookieStore.getCookies();
						for (int i = 0; i < cookies.size(); i++) {
							if ("JSESSIONID".equals(cookies.get(i).getName())) {
								JSESSIONID = cookies.get(i).getValue();
								break;
							}
						}
						listener.onResponseSuccess(json);
					} else {
						listener.onResponseError(code);
					}
				} catch (Exception e) {
					e.printStackTrace();
					listener.onRequestException(e);
				}
			}
		}).start();
	}

	public void httpUrlConnection(final String url,
			final Map<String, String> map, final File file) {

	}

	public void httpPost(String url, Map<String, String> map) {
		httpPost(url, map, null);
	}

	public interface HttpCompliteListener {

		public void onResponseSuccess(String json);

		public void onResponseError(int code);

		public void onRequestException(Exception e);

	}
}
