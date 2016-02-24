package com.example.testwebview;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.dom4j.io.SAXReader;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.WebView;

import com.example.constant.Constant;
import com.example.utils.ClassLoaderUtil;
import com.example.utils.DLClassLoader;
import com.example.utils.JarUtils;

import dalvik.system.DexClassLoader;

public class Demo {
	Context mContext;
	WebView mWebView;
	Handler mHandler;

	/** Instantiate the interface and set the context */
	Demo(Context c, WebView v, Handler h) {
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
	 * @param toolId
	 *            ����Id
	 * @param action
	 *            �����action����
	 * @param callbackId
	 *            �ص�Id
	 * @param params
	 *            ��������Ĳ���
	 */
	public void loadExtend(String toolId, String action, String callbackId,
			String params) {
		// ������ʼ��
		String sessionId = null;
		String fileName = toolId + ".jar";// �ļ�����������ƣ�
		String toolVersion = null;// �������°汾Id
		IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
		JSONObject pArgs = null;
		try {
			pArgs = new JSONObject(params);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("Demo", "ǰ̨��������Ĳ�����ʽ����");
		}
		boolean available = isNetworkAvailable(mContext);// �����Ƿ����״̬
		if (isJarExit(mContext, fileName)) {
			// ���ߴ���
			if (available) {

				// --------------------����ʹ��-------------------------
				if (sessionId == null || sessionId.equals("")) {
					sessionId = getSessionId();
				}
				// --------------------֮��Ҫɾ��-----------------------

				// ���ߴ��ڡ�������á��ж��Ƿ����°汾
				Map<String, String> map = getToolCurrentVersionIdWithController(
						toolId, sessionId);
				toolVersion = map.get("toolCurrentVersionID");
				if (isNewestJar(mContext, fileName, toolVersion)) {
					// �����°汾���ж��Ƿ�Ϊ��ҳ�湤�ߡ���ҳ�������й���
					runToolAndAdjustIfWithPages(pWebview, mContext, fileName,
							callbackId, action, toolId, pArgs);
				} else {
					// �������°汾�����ع��ߡ��ж��Ƿ�Ϊ��ҳ�湤�ߡ���ҳ�������й���
					// downloadToolAndRunToolAndReturnResult(pWebview, mContext,
					// fileName, callbackId, action, toolId, pArgs,
					// toolVersion, sessionId);
				}
			} else {
				// ���ߴ��ڡ����粻���á����ж��Ƿ����°汾���ж��Ƿ�Ϊ��ҳ�湤�ߡ���ҳ�������й��ߡ���ҳ���򷵻ؽ����ǰ̨��ѹjar��
				// runToolAndAdjustIfWithPages(pWebview, context, fileName,
				// CallBackID, action, toolId, pArgsArray);
			}
		} else {
			// ���߲�����
			if (available) {

				// --------------------����ʹ��---------------------------
				if (sessionId == null || sessionId.equals("")) {
					sessionId = getSessionId();
				}
				// --------------------֮��Ҫɾ��-------------------------

				// ���߲����ڡ��������
				Map<String, String> map = getToolCurrentVersionIdWithController(
						toolId, sessionId);
				toolVersion = map.get("toolCurrentVersionID");
				// downloadToolAndRunToolAndReturnResult(pWebview, context,
				// fileName, CallBackID, action, toolId, pArgsArray,
				// toolVersion, sessionId);
			} else {
				// ���߲����ڡ����粻����
				// returnRunToolErrorForWithoutNetwork(pWebview, CallBackID);
			}
		}
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
	@SuppressWarnings("deprecation")
	public File getJarFile(Context context, String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		File jarFile = null;
		// if (sdCardExist) {
		// File sdDir = Environment.getExternalStorageDirectory();// ��ȡ�ֻ�SD����Ŀ¼
		// File jarDir = new File(sdDir.getPath() + File.separator
		// + "WeToBand" + File.separator + "Jar");
		// jarFile = new File(sdDir.getPath() + File.separator + "WeToBand"
		// + File.separator + "Jar" + File.separator + fileName);
		// if (!jarDir.exists()) {// SD���ļ����Ƿ���ڣ�û���򴴽�
		// jarDir.mkdirs();
		// }
		// } else {
		// // �����ļ���
		// File dir = context.getDir("WeToBandJar", Context.MODE_PRIVATE
		// | Context.MODE_WORLD_READABLE
		// | Context.MODE_WORLD_WRITEABLE);
		// // �½��ļ�
		// jarFile = new File(dir.getPath() + File.separator + fileName);
		// }

		if (sdCardExist) {
			File sdDir = Environment.getDataDirectory();// ��ȡ�ֻ�SD����Ŀ¼
			File jarDir = new File(sdDir.getPath() + File.separator + "Jar");
			jarFile = new File(sdDir.getPath() + File.separator + "Jar"
					+ File.separator + fileName);
			if (!jarDir.exists()) {// SD���ļ����Ƿ���ڣ�û���򴴽�
				jarDir.mkdirs();
			}
		} else {
			// �����ļ���
			File dir = context.getDir("Jar", Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			// �½��ļ�
			jarFile = new File(dir.getPath() + File.separator + fileName);
		}

		return jarFile;
	}

	public String getSessionId() {
		InputStream is = null;
		String[] param = null;
		String fileUrl = Constant.url
				+ "userLogin?username=zhuangweihao&password=123456";// ��¼url
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setRequestMethod("POST");
			ucon.connect();
			ucon.getOutputStream().flush();
			int code = ucon.getResponseCode();

			// ��ȡ��Ӧ����
			String sCurrentLine = "";
			String sTotalString = "";
			if (code == 200) {
				is = ucon.getInputStream();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				while ((sCurrentLine = reader.readLine()) != null)
					if (sCurrentLine.length() > 0)
						sTotalString = sTotalString + sCurrentLine.trim();
			} else {
				sTotalString = "Զ�̷���������ʧ��,�������:" + code;
			}
			String session_value = ucon.getHeaderField("Set-Cookie");
			param = session_value.split(";");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (param != null) {
			return param[0].split("=")[1];
		} else {
			return null;
		}
	}

	/**
	 * @������getToolCurrentVersionIdWithController
	 * @����������controller��ͨ������id��ȡ���߰汾��Ϣ
	 * @param toolId
	 *            ����id
	 * @param sessionId
	 *            �Ựid
	 * @return ���ع��߰汾��Ϣ
	 */
	public Map<String, String> getToolCurrentVersionIdWithController(
			String toolId, String sessionId) {
		Map<String, String> map = null;
		// ����һ��httpclient����
		HttpClient httpclient = new DefaultHttpClient();
		String ajaxUrl = Constant.url + "getToolsByobjId?objID=" + toolId;
		HttpGet httpget = new HttpGet(ajaxUrl);
		httpget.addHeader(new BasicHeader("Cookie", "JSESSIONID=" + sessionId));
		try {
			HttpResponse response = httpclient.execute(httpget);
			int code = response.getStatusLine().getStatusCode();
			if (code == HttpStatus.SC_OK) {
				String strResult = EntityUtils.toString(response.getEntity());
				JSONObject object = new JSONObject(strResult);
				map = new HashMap<String, String>();
				map.put("toolCurrentVersionID",
						object.getString("toolCurrentVersionID"));
				map.put("tvID", object.getString("tvID"));
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {
			if (httpclient != null) {
				// �ر�����.
				httpclient.getConnectionManager().shutdown();
			}
		}
		return map;
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
	 * @�������жϱ��ع����Ƿ�Ϊ��ҳ�湤��
	 * @param pWebview
	 *            webview����
	 * @param context
	 *            �����Ķ���
	 * @param fileName
	 *            �ļ���
	 * @param CallBackID
	 *            �ص�Id
	 * @param action
	 *            ���õı��ع��ߵ�action����
	 * @param toolId
	 *            ����Id
	 * @param pArgsArray
	 *            �������ߵĲ���
	 */
	public void runToolAndAdjustIfWithPages(final IWebview pWebview,
			Context context, String fileName, String CallBackID, String action,
			String toolId, JSONObject pArgs) {
		File jarFile = getJarFile(context, fileName);// jar��File����
		String jarPath = jarFile.getAbsolutePath();// jar�����ȫ·��
		String isWithPages = SAX(context, jarPath, "WithPages");
		if (isWithPages == null || isWithPages.equals("0")) {
			// ��ҳ�湤�ߣ����й��߲��������н���ͽ������
			runToolNoPageAndReturnResult(pWebview, context, fileName,
					CallBackID, action, toolId, pArgs);
		} else if (isWithPages != null && isWithPages.equals("1")) {
			// ��ҳ�湤�ߣ�������ִ�й���Ϊ��ҳ�湤�߽��
			runToolWithPagesAndReturnResult(pWebview, CallBackID, fileName,
					toolId);
		} else {
			// ����withpages��������
			returnToolWithPagesParamError(pWebview, CallBackID);
		}
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
	 * @������runToolNoPageAndReturnResult
	 * @���������й��߲��������н��
	 * @param pWebview
	 *            webview����
	 * @param context
	 *            context������
	 * @param fileName
	 *            �ļ�����
	 * @param CallBackID
	 *            ���ؽ���ص�����id
	 * @param action
	 *            ���ò����action����
	 * @param toolId
	 *            ����id
	 * @param pArgsArray
	 *            Ҫ�������ߵĲ���
	 */
	public void runToolNoPageAndReturnResult(final IWebview pWebview,
			Context context, String fileName, String CallBackID, String action,
			String toolId, JSONObject pArgs) {
		IFeature lib = runPlugin(context, fileName);
		if (lib != null) {
			// �������гɹ����
			returnRunSuccessResult(pWebview, CallBackID);
			// ���øò����excute��ʽִ�б��ع��߲��������ؽ������
			lib.execute(pWebview, action, CallBackID, pArgs);
		} else {
			// ����jar�������ʧ�ܡ����ع�������ʧ�ܱ�־
			removeToolVersion(context, fileName);// ɾ�����صĹ��߰汾��Ϣ
			deleteDexFile(context, toolId);// ɾ���ñ��ع��߽�ѹ���Dex�ļ�
			deleteJarFile(context, fileName);// ɾ���ñ��ع��ߵ�jar��
			deleteJarUnZipPath(context, toolId);// ɾ������jar����ѹ�ļ�
			returnRunErrorResult(pWebview, CallBackID);// ���ؼ���ʧ�ܽ��
		}
	}

	/**
	 * @��������runPlugin
	 * @��������̬����jar���������в������
	 * @param pWebview
	 *            WebView����
	 * @param context
	 *            Context�����Ķ���
	 * @param fileName
	 *            �ļ���
	 * @param action
	 *            Ҫִ�в���ľ���action����
	 * @param pArgs
	 *            ���ݵ�����еĲ���
	 * @return ���ص��ò�����ȡ��������
	 */
	@SuppressWarnings({ "rawtypes" })
	@SuppressLint("NewApi")
	public IFeature runPlugin(Context context, String fileName) {
		if (ClassLoaderUtil.isContainsClazz(fileName)) {
			return ClassLoaderUtil.getClazz(fileName);
		}
		File jarFile = getJarFile(context, fileName);// jar��File����
		String jarPath = jarFile.getAbsolutePath();// jar�����ȫ·��
		String packagePathAndName = SAX(context, jarPath, "EntryClass");
		DexClassLoader clazz = DLClassLoader.getClassLoader(jarPath, context,
				context.getClassLoader());
		Class libProviderClazz = null;
		try {
			// ����ָ���Ĺ��߲��
			libProviderClazz = clazz.loadClass(packagePathAndName);
			IFeature lib = (IFeature) libProviderClazz.newInstance();
			// ʵ�����ò���������
			ClassLoaderUtil.putClazz(fileName, lib);
			return lib;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @������returnRunSuccessResult
	 * @���������ؼ������й�����ɹ����
	 * @param pWebview
	 *            webview����
	 * @param CallBackID
	 *            �ص�����id
	 */
	public void returnRunSuccessResult(final IWebview pWebview,
			String CallBackID) {
		// ����jar�������ɹ����ȷ��ع������н��
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// ���ؽ����ǰ����
			retJSONObj.putOpt("code", "200");
			retJSONObj.putOpt("content", "runTool success");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.OK, JSUtil.CONTINUED);
	}

	/**
	 * @������removeToolVersion
	 * @�������Ƴ����ߵİ汾��Ϣ
	 * @param context
	 *            context�����Ķ���
	 * @param fileName
	 *            �ļ���
	 */
	public void removeToolVersion(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(
				"toolVersionStorage", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(fileName);
		editor.commit();// �ύ�޸�
	}

	/**
	 * @��������deleteDexFile
	 * @���������¹��߰汾��ʱ��ɾ��dex�ļ�
	 * @param context
	 *            Context�����Ķ���
	 * @param toolId
	 *            ����Id
	 */
	@SuppressWarnings("deprecation")
	public void deleteDexFile(Context context, String toolId) {
		String fileName = toolId + ".dex";
		// �����ļ���
		File dir = context.getDir("Dex", Context.MODE_PRIVATE
				| Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		// �½��ļ�
		File dexFile = new File(dir.getPath() + File.separator + fileName);
		if (dexFile.exists()) {
			dexFile.delete();
		}
	}

	/**
	 * @������deleteJarFile
	 * @������ɾ��jar���ļ�
	 * @param context
	 *            context�����Ķ���
	 * @param fileName
	 *            �ļ�����
	 */
	public void deleteJarFile(Context context, String fileName) {
		File jarFile = getJarFile(context, fileName);// jar��File����
		if (jarFile.exists()) {
			jarFile.delete();
		}
	}

	/**
	 * @������ɾ��jar����ѹ�ļ�
	 * @param context
	 *            �����Ķ���
	 * @param toolId
	 *            ����Id
	 */
	public void deleteJarUnZipPath(Context context, String toolId) {
		File jarPath = getJarUnZipPath(context, toolId);
		if (jarPath.exists()) {
			recurDelete(jarPath);
		}
	}

	/**
	 * @��������ȡjar����ѹ·��
	 * @param context
	 *            �����ʶ���
	 * @param toolId
	 *            ����Id
	 * @return �����ļ��ж���
	 */
	@SuppressWarnings("deprecation")
	public File getJarUnZipPath(Context context, String toolId) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // �ж�sd���Ƿ����
		File jarFile = null;
		if (sdCardExist) {
			File sdDir = Environment.getDataDirectory();// ��ȡ�ֻ�SD����Ŀ¼
			File jarDir = new File(sdDir.getPath() + File.separator
					+ "JarUnZip");
			jarFile = new File(sdDir.getPath() + File.separator + "JarUnZip"
					+ File.separator + toolId);
			if (!jarDir.exists()) {// SD���ļ����Ƿ���ڣ�û���򴴽�
				jarDir.mkdirs();
			}
		} else {
			// �����ļ���
			File dir = context.getDir("JarUnZip", Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			// �½��ļ�
			jarFile = new File(dir.getPath() + File.separator + toolId);
		}
		return jarFile;
	}

	/**
	 * @�������ݹ�ɾ��jar����ѹ�ļ�Ŀ¼
	 * @param f
	 *            �ļ�����
	 */
	public void recurDelete(File f) {
		for (File fi : f.listFiles()) {
			if (fi.isDirectory()) {
				recurDelete(fi);
			} else {
				fi.delete();
			}
		}
		f.delete();
	}

	/**
	 * @������returnRunErrorResult
	 * @���������ؼ������й�����ʧ�ܽ��
	 * @param pWebview
	 *            webview����
	 * @param CallBackID
	 *            �ص�������id
	 */
	public void returnRunErrorResult(final IWebview pWebview, String CallBackID) {
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// ���ؽ����ǰ����
			retJSONObj.putOpt("code", "500");
			retJSONObj.putOpt("content", "loadclass error!");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.ERROR, JSUtil.UNCONTINUED);
	}

	/**
	 * @���������й���ʱ���ж�Ϊ��ҳ�湤�ߣ����ؽ��
	 * @param pWebview
	 *            webview����
	 * @param CallBackID
	 *            �ص�Id
	 */
	public void runToolWithPagesAndReturnResult(final IWebview pWebview,
			String CallBackID, String fileName, String toolId) {
		Context context = pWebview.getContext();
		Activity activity = pWebview.getActivity();
		// ��ѹjar��
		File releasePath = getJarUnZipPath(context, toolId);
		File jarPath = getJarFile(pWebview.getContext(), fileName);
		if (!releasePath.exists()) {
			JarUtils.unJar(jarPath, releasePath);
		}

		// ���ؽ��
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// ���ؽ����ǰ����
			retJSONObj.putOpt("type", "runToolResult");
			retJSONObj.putOpt("result", "isWithPagesTool");
			retJSONObj.putOpt("filePath", releasePath.getParent());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.OK, JSUtil.CONTINUED);
	}

	/**
	 * @������config.xml�ļ��е�withpages������д��ʽ���󣬷��ؽ��
	 * @param pWebview
	 *            webview����
	 * @param CallBackID
	 *            �ص�id
	 */
	public void returnToolWithPagesParamError(final IWebview pWebview,
			String CallBackID) {
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// ���ؽ����ǰ����
			retJSONObj.putOpt("code", "501");
			retJSONObj.putOpt("content", "WithPages param type error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.ERROR, JSUtil.UNCONTINUED);
	}

}
