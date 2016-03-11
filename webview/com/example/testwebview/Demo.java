package com.example.testwebview;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.webkit.WebView;

import com.example.cim.nio.constant.Constant;
import com.example.tool.Tool;
import com.example.utils.JarUtils;
import com.example.utils.ToolLoaderUtil;

import dalvik.system.DexClassLoader;

public class Demo {
	Context mContext;
	WebView mWebView;
	Handler mHandler;

	/** Instantiate the interface and set the context */
	public Demo(Context c, WebView v, Handler h) {
		mContext = c;
		mWebView = v;
		mHandler = h;
	}

	/** Show a toast from the web page */
	// ���target ���ڵ���API 17������Ҫ��������ע��
	// @JavascriptInterface
	public void showAlert(String toast) {
		mWebView.loadUrl("javascript:(function(){alert(\"123\");})()");
	}

	/**
	 * @������asynExecute
	 * @�������첽����App����Ĳ��
	 * @param pluginName
	 *            �������
	 * @param action
	 *            ���ò���ľ���action
	 * @param callbackId
	 *            �ص�Id
	 * @param params
	 *            ��������Ĳ���
	 */
	public void asynExecute(String pluginName, String action,
			String callbackId, String params) {
		IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
		JSONObject pArgs = null;
		IFeature iFeature = null;
		try {
			pArgs = new JSONObject(params);
			iFeature = FeatureManage.getInstance(mContext).getFeature(
					pluginName);
			iFeature.execute(pWebview, action, callbackId, pArgs);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @������execute
	 * @������ͬ������App����Ĳ��
	 * @return ���ؼ��ع��߻�ȡ���Ľ��
	 */
	public String execute() {
		return "abc";
	}

	@SuppressWarnings("rawtypes")
	public void createWebView(String params) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(params);
			Bundle data = new Bundle();
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				data.putString(key, (String) jsonObject.get(key));
			}
			Message msg = mHandler.obtainMessage();
			msg.arg1 = 1;
			msg.setData(data);
			mHandler.sendMessage(msg);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @������loadExtend
	 * @����������App֮��Ĳ��
	 * @param callbackId
	 *            �ص�Id
	 * @param params
	 *            ��������Ĳ����������У�toolId��tvId��
	 */
	public void loadExtend(final String callbackId, final String param) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// ������ʼ��
				try {
					JSONObject params = new JSONObject(param);
					String toolId = params.getString("toolId");
					String tvId = params.getString("tvId");
					params.put("callBackID", callbackId);
					String fileName = toolId + ".jar";// �ļ�����������ƣ�
					IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
					boolean available = isNetworkAvailable(mContext);// �����Ƿ����״̬
					if (isJarExit(mContext, fileName)) {// ���ߴ���
						if (isNewestJar(mContext, fileName, tvId)) {// �����°汾
							// �����°汾�����й���
							adjustToolTypeAndDo(pWebview, params, fileName);
						} else {// �������°汾
							if (available) {// ������á����ع���
								downloadToolAndAdjustToolTypeThenDo(pWebview,
										params, fileName);
							} else {// ���粻���á����оɰ湤��
								adjustToolTypeAndDo(pWebview, params, fileName);
							}
						}
					} else {// ���߲�����
						if (available) {// ������á����ع��߲����й���
							downloadToolAndAdjustToolTypeThenDo(pWebview,
									params, fileName);
						} else {// ����
							returnErrorMessage(pWebview, callbackId, 401,
									"���߲����������粻����");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * @������toolAction
	 * @��������ҳ�湤�ߵ��������ط���
	 * @param callbackId �ص�Id
	 * @param param ���ݽ����Ĳ�������ҪtoolId��
	 */
	public void toolAction(final String callbackId, final String param) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// ������ʼ��
				try {
					JSONObject params = new JSONObject(param);
					params.put("callBackID", callbackId);
					String toolId = params.getString("toolId");
					params.put("callBackID", callbackId);
					String fileName = toolId + ".jar";
					IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
					Tool tool = getTool(pWebview, mContext, fileName);
					executeAct(pWebview, params, tool);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		}).start();
	}

	/**
	 * @������isNetworkAvailable
	 * @�������жϵ�ǰ�����Ƿ����
	 * @param context
	 *            �����Ķ���
	 * @return ���������Ƿ����
	 */
	public boolean isNetworkAvailable(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo[] infos = manager.getAllNetworkInfo();
		if (infos != null && infos.length > 0) {
			for (int i = 0; i < infos.length; i++) {
				// �жϵ�ǰ����״̬�Ƿ�Ϊ����״̬
				if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @��������isJarExit
	 * @�������жϱ��ع����Ƿ��Ѿ�����
	 * @param context
	 *            Context�����Ķ���
	 * @param fileName
	 *            �жϵ��ļ�����
	 * @return ����true��false
	 */
	public boolean isJarExit(Context context, String fileName) {
		File jarFile = getJarFile(context, fileName);
		if (jarFile.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * @��������filePath
	 * @�������ж�SD���Ƿ���ڣ�����������ļ�������SD��·��File���󣬲������򷵻��ֻ�����·��File����
	 * @param context
	 *            Context�����Ķ���
	 * @param fileName
	 *            �ļ���
	 * @return ����File����
	 */
	public File getJarFile(Context context, String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		File jarFile = null;
		if (sdCardExist) {
			File sdDir = context
					.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			File jarDir = new File(sdDir.getPath() + File.separator + "Jar");
			jarFile = new File(sdDir.getPath() + File.separator + "Jar"
					+ File.separator + fileName);
			if (!jarDir.exists()) {// SD���ļ����Ƿ���ڣ�û���򴴽�
				jarDir.mkdirs();
			}
		} else {
			File dir = context.getFilesDir();
			jarFile = new File(dir.getPath() + File.separator + fileName);
		}

		return jarFile;
	}

	/**
	 * @��������isNewestJar
	 * @�������жϹ����Ƿ������°汾
	 * @param context
	 *            Context����������
	 * @param fileName
	 *            �ļ�����
	 * @param toolVersion
	 *            ���߰汾
	 * @return ���ع����Ƿ����°汾�жϽ��
	 */
	public boolean isNewestJar(Context context, String fileName,
			String toolVersion) {
		if (fileName.equals("") || fileName == null || toolVersion == null
				|| toolVersion.equals("")) {
			return false;
		}
		SharedPreferences sp = context.getSharedPreferences(
				"toolVersionStorage", Context.MODE_PRIVATE);
		String toolVersionOld = sp.getString(fileName, "");
		if (toolVersionOld.equals(toolVersion)) {
			return true;
		}
		return false;
	}

	/**
	 * �жϹ������ͣ������ݹ�������ִ�в�ͬ����
	 * 
	 * @param pWebview
	 *            webview����
	 * @param object
	 *            ��������
	 * @param fileName
	 *            ��������
	 * @throws JSONException
	 */
	public void adjustToolTypeAndDo(final IWebview pWebview, JSONObject object,
			String fileName) throws JSONException {
		Context context = pWebview.getContext();
		final String runType = object.getString("runType");
		final String toolId = object.getString("toolId");
		if (runType.equals("1")) {// ��ҳ��
			// ��ѹjar��
			final File unZipPatn = getJarUnZipPath(pWebview.getContext(),
					toolId);
			File jarPath = getJarFile(pWebview.getContext(), fileName);
			if (!unZipPatn.exists()) {
				JarUtils.unJar(jarPath, unZipPatn);
			}
			pWebview.getActivity().runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String url = getUrl(unZipPatn.getPath(), toolId, runType);
					pWebview.loadUrl(url);
				}
			});
		} else {// ��ҳ��
			Tool tool = getTool(pWebview, context, fileName);
			executeToolMain(pWebview, object, tool);
		}
	}

	/**
	 * ��ȡ���߽�ѹ�󣬹��ߵ�main.jspҳ��·��
	 * 
	 * @param unZipPath
	 *            ���߽�ѹ·��
	 * @param toolId
	 *            ����Id
	 * @param runType
	 *            ��������
	 * @return ���ع��߽�ѹ�󣬹��ߵ�main.jspҳ��·��
	 */
	public String getUrl(String unZipPath, String toolId, String runType) {
		String url = Constant.locationUrl + unZipPath + "/main.jsp?toolId="
				+ toolId + "&runType=" + runType;
		return url;
	}

	@SuppressWarnings("rawtypes")
	@SuppressLint("NewApi")
	public Tool getTool(IWebview pWebview, Context context, String fileName) {
		if (ToolLoaderUtil.isContainsTool(fileName)) {
			return ToolLoaderUtil.getTool(fileName);
		}
		File jarFile = getJarFile(context, fileName);// jar��File����
		String jarPath = jarFile.getAbsolutePath();// jar�����ȫ·��
		String classPathAndName = SAX(context, jarPath, "EntryClass");
		DexClassLoader loader = ToolLoaderUtil.getToolLoader(jarPath, context,
				context.getClassLoader());
		Class clazz = null;
		Tool tool = null;
		try {
			// ����ָ���Ĺ��߲��
			clazz = loader.loadClass(classPathAndName);
			tool = (Tool) clazz.newInstance();
			// ʵ�����ò���������
			ToolLoaderUtil.putTool(fileName, tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tool;
	}

	/**
	 * @������SAX
	 * @��������SAX��ʽ����XML�ļ�
	 * @param context
	 *            Context�����Ķ���
	 * @param jarPath
	 *            XML�ļ�·��
	 * @return Ҫ��̬�������ȫ·��
	 */
	public String SAX(Context context, String jarPath, String entry) {
		String value = null;
		try {
			JarFile mJarFile = new JarFile(jarPath);
			ZipEntry entity = mJarFile.getEntry("assets/config.xml");
			InputStream in = mJarFile.getInputStream(entity);
			SAXReader reader = new SAXReader();
			org.dom4j.Document document = reader.read(in);
			org.dom4j.Element element = document.getRootElement();
			value = element.elementText(entry).trim();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return value;
	}

	/**
	 * ִ����ҳ�湤�ߵ�ToolMain����
	 * 
	 * @param pWebview
	 *            webview����
	 * @param object
	 *            ��������
	 * @param tool
	 *            ������
	 * @throws JSONException
	 */
	public void executeToolMain(IWebview pWebview, JSONObject object, Tool tool)
			throws JSONException {
		String CallBackID = object.getString("callBackID");
		if (tool != null) {
			tool.toolMain(pWebview, object);
		} else {
			returnErrorMessage(pWebview, CallBackID, 401, "���ص��Ĺ�����Ϊnull");
		}
	}
	
	/**
	 * ִ����ҳ�湤�ߵ�act����
	 * 
	 * @param pWebview
	 *            webview����
	 * @param object
	 *            ��������
	 * @param tool
	 *            ������
	 * @throws JSONException
	 */
	public void executeAct(IWebview pWebview, JSONObject object, Tool tool)
			throws JSONException {
		String CallBackID = object.getString("callBackID");
		if (tool != null) {
			tool.act(pWebview, object);
		} else {
			returnErrorMessage(pWebview, CallBackID, 401, "���ص��Ĺ�����Ϊnull");
		}
	}

	/**
	 * ���ر��ع��ߣ����жϹ������ͣ����ݹ�������ִ�в�ͬ����
	 * 
	 * @param pWebview
	 *            webview����
	 * @param object
	 *            ��������
	 * @param fileName
	 *            ��������
	 * @throws JSONException
	 */
	public void downloadToolAndAdjustToolTypeThenDo(IWebview pWebview,
			JSONObject object, String fileName) throws JSONException {
		Context context = pWebview.getContext();
		String tvId = object.getString("tvId");
		File jarFile = getJarFile(context, fileName);
		boolean isSuccess = getToolContentByToolInfoWithController(tvId,
				jarFile);// ����Jar��
		if (isSuccess) {
			SetToolVersion(context, fileName, tvId);
			adjustToolTypeAndDo(pWebview, object, fileName);
		} else {
			if (jarFile.exists()) {
				jarFile.delete();
			}
		}
	}

	/**
	 * @������getToolContentByToolInfoWithController
	 * @���������ع���jar��
	 * @param toolObjID
	 *            ����objId
	 * @param toolVersionID
	 *            ���߰汾Id
	 * @param jarFile
	 *            �������ش��·��
	 * @param sessionId
	 *            �����������sessionId
	 * @return �������ؽ��
	 */
	public boolean getToolContentByToolInfoWithController(String tvId,
			File jarFile) {
		// ����һ��httpclient����
		boolean result = false;
		HttpClient httpclient = new DefaultHttpClient();
		String ajaxUrl = Constant.SERVER_URL
				+ "/tool/tool_download_tvId.action?tvId=" + tvId;
		HttpGet httpget = new HttpGet(ajaxUrl);
		InputStream in = null;
		FileOutputStream fos = null;
		try {
			HttpResponse response = httpclient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				fos = new FileOutputStream(jarFile);
				if (entity != null) {
					in = entity.getContent();
				}
				int l = -1;
				byte[] tmp = new byte[1024];
				while ((l = in.read(tmp)) != -1) {
					fos.write(tmp, 0, l);
				}
				fos.flush();
				if (entity != null) {
					entity.consumeContent();
				}
				result = true;
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			if (jarFile.exists()) {
				jarFile.delete();
			}
		} catch (IOException e) {
			e.printStackTrace();
			if (jarFile.exists()) {
				jarFile.delete();
			}
		} finally {
			// �رյͲ�����
			try {
				if (in != null) {
					in.close();
				}
				if (httpclient != null) {
					// �ر�����.
					httpclient.getConnectionManager().shutdown();
				}
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * @��������SetToolVersion
	 * @param context
	 *            �����Ķ���
	 * @param fileName
	 *            �ļ�����
	 * @param toolVersion
	 *            ���߰汾
	 */
	public void SetToolVersion(Context context, String fileName,
			String toolVersion) {
		if (fileName.equals("") || fileName == null || toolVersion == null
				|| toolVersion.equals("")) {
			return;
		}
		SharedPreferences sp = context.getSharedPreferences(
				"toolVersionStorage", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.putString(fileName, toolVersion);
		editor.commit();// �ύ�޸�
	}

	/**
	 * �ص�js��Error�����������ؽ��
	 * 
	 * @param pWebview
	 *            webview����
	 * @param CallBackID
	 *            �ص�Id
	 * @param code
	 *            ����codeֵ
	 * @param content
	 *            ��������
	 * @throws JSONException
	 */
	public void returnErrorMessage(IWebview pWebview, String CallBackID,
			int code, String content) throws JSONException {
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("code", code);
		jsonObject.put("content", content);
		JSUtil.execCallback(pWebview, CallBackID, jsonObject.toString(),
				JSUtil.ERROR, JSUtil.UNCONTINUED);
	}

	/**
	 * @��������ȡjar����ѹ·��
	 * @param context
	 *            �����ʶ���
	 * @param toolId
	 *            ����Id
	 * @return �����ļ��ж���
	 */
	public File getJarUnZipPath(Context context, String toolId) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		File unZipPath = null;
		if (sdCardExist) {
			File sdDir = context
					.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			File unZipRoot = new File(sdDir.getPath() + File.separator
					+ "JarUnZip");
			unZipPath = new File(sdDir.getPath() + File.separator + "JarUnZip"
					+ File.separator + toolId);
			if (!unZipRoot.exists()) {// SD���ļ����Ƿ���ڣ�û���򴴽�
				unZipRoot.mkdirs();
			}
		} else {
			File dir = context.getFilesDir();
			unZipPath = new File(dir.getPath() + File.separator + toolId);
		}
		return unZipPath;
	}
}
