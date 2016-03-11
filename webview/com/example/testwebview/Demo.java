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
	// 如果target 大于等于API 17，则需要加上如下注解
	// @JavascriptInterface
	public void showAlert(String toast) {
		mWebView.loadUrl("javascript:(function(){alert(\"123\");})()");
	}

	/**
	 * @方法：asynExecute
	 * @描述：异步加载App里面的插件
	 * @param pluginName
	 *            插件名称
	 * @param action
	 *            调用插件的具体action
	 * @param callbackId
	 *            回调Id
	 * @param params
	 *            传给插件的参数
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
	 * @方法：execute
	 * @描述：同步加载App里面的插件
	 * @return 返回加载工具获取到的结果
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
	 * @方法：loadExtend
	 * @描述：加载App之外的插件
	 * @param callbackId
	 *            回调Id
	 * @param params
	 *            传即那里的参数（必须有：toolId、tvId）
	 */
	public void loadExtend(final String callbackId, final String param) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 参数初始化
				try {
					JSONObject params = new JSONObject(param);
					String toolId = params.getString("toolId");
					String tvId = params.getString("tvId");
					params.put("callBackID", callbackId);
					String fileName = toolId + ".jar";// 文件名（插件名称）
					IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
					boolean available = isNetworkAvailable(mContext);// 网络是否可用状态
					if (isJarExit(mContext, fileName)) {// 工具存在
						if (isNewestJar(mContext, fileName, tvId)) {// 是最新版本
							// 是最新版本、运行工具
							adjustToolTypeAndDo(pWebview, params, fileName);
						} else {// 不是最新版本
							if (available) {// 网络可用、下载工具
								downloadToolAndAdjustToolTypeThenDo(pWebview,
										params, fileName);
							} else {// 网络不可用、运行旧版工具
								adjustToolTypeAndDo(pWebview, params, fileName);
							}
						}
					} else {// 工具不存在
						if (available) {// 网络可用、下载工具并运行工具
							downloadToolAndAdjustToolTypeThenDo(pWebview,
									params, fileName);
						} else {// 报错
							returnErrorMessage(pWebview, callbackId, 401,
									"工具不存在且网络不可用");
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).start();

	}

	/**
	 * @方法：toolAction
	 * @描述：有页面工具的请求拦截方法
	 * @param callbackId 回调Id
	 * @param param 传递进来的参数（需要toolId）
	 */
	public void toolAction(final String callbackId, final String param) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				// 参数初始化
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
	 * @方法：isNetworkAvailable
	 * @描述：判断当前网络是否可用
	 * @param context
	 *            上下文对象
	 * @return 返回网络是否可用
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
				// 判断当前网络状态是否为连接状态
				if (infos[i].getState() == NetworkInfo.State.CONNECTED) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * @方法名：isJarExit
	 * @描述：判断本地工具是否已经存在
	 * @param context
	 *            Context上下文对象
	 * @param fileName
	 *            判断的文件名称
	 * @return 返回true或false
	 */
	public boolean isJarExit(Context context, String fileName) {
		File jarFile = getJarFile(context, fileName);
		if (jarFile.exists()) {
			return true;
		}
		return false;
	}

	/**
	 * @方法名：filePath
	 * @描述：判断SD卡是否存在，存在则根据文件名返回SD卡路径File对象，不存在则返回手机储存路径File对象
	 * @param context
	 *            Context上下文对象
	 * @param fileName
	 *            文件名
	 * @return 返回File对象
	 */
	public File getJarFile(Context context, String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		File jarFile = null;
		if (sdCardExist) {
			File sdDir = context
					.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			File jarDir = new File(sdDir.getPath() + File.separator + "Jar");
			jarFile = new File(sdDir.getPath() + File.separator + "Jar"
					+ File.separator + fileName);
			if (!jarDir.exists()) {// SD卡文件夹是否存在，没有则创建
				jarDir.mkdirs();
			}
		} else {
			File dir = context.getFilesDir();
			jarFile = new File(dir.getPath() + File.separator + fileName);
		}

		return jarFile;
	}

	/**
	 * @方法名：isNewestJar
	 * @描述：判断工具是否是最新版本
	 * @param context
	 *            Context对象上下文
	 * @param fileName
	 *            文件名称
	 * @param toolVersion
	 *            工具版本
	 * @return 返回工具是否最新版本判断结果
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
	 * 判断工具类型，并根据工具类型执行不同操作
	 * 
	 * @param pWebview
	 *            webview对象
	 * @param object
	 *            参数集合
	 * @param fileName
	 *            工具名称
	 * @throws JSONException
	 */
	public void adjustToolTypeAndDo(final IWebview pWebview, JSONObject object,
			String fileName) throws JSONException {
		Context context = pWebview.getContext();
		final String runType = object.getString("runType");
		final String toolId = object.getString("toolId");
		if (runType.equals("1")) {// 有页面
			// 解压jar包
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
		} else {// 无页面
			Tool tool = getTool(pWebview, context, fileName);
			executeToolMain(pWebview, object, tool);
		}
	}

	/**
	 * 获取工具解压后，工具的main.jsp页面路径
	 * 
	 * @param unZipPath
	 *            工具解压路径
	 * @param toolId
	 *            工具Id
	 * @param runType
	 *            工具类型
	 * @return 返回工具解压后，工具的main.jsp页面路径
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
		File jarFile = getJarFile(context, fileName);// jar包File对象
		String jarPath = jarFile.getAbsolutePath();// jar包存放全路径
		String classPathAndName = SAX(context, jarPath, "EntryClass");
		DexClassLoader loader = ToolLoaderUtil.getToolLoader(jarPath, context,
				context.getClassLoader());
		Class clazz = null;
		Tool tool = null;
		try {
			// 加载指定的工具插件
			clazz = loader.loadClass(classPathAndName);
			tool = (Tool) clazz.newInstance();
			// 实例化该插件的类对象
			ToolLoaderUtil.putTool(fileName, tool);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tool;
	}

	/**
	 * @方法：SAX
	 * @描述：用SAX方式解析XML文件
	 * @param context
	 *            Context上下文对象
	 * @param jarPath
	 *            XML文件路径
	 * @return 要动态加载类的全路径
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
	 * 执行无页面工具的ToolMain方法
	 * 
	 * @param pWebview
	 *            webview对象
	 * @param object
	 *            参数集合
	 * @param tool
	 *            工具类
	 * @throws JSONException
	 */
	public void executeToolMain(IWebview pWebview, JSONObject object, Tool tool)
			throws JSONException {
		String CallBackID = object.getString("callBackID");
		if (tool != null) {
			tool.toolMain(pWebview, object);
		} else {
			returnErrorMessage(pWebview, CallBackID, 401, "加载到的工具类为null");
		}
	}
	
	/**
	 * 执行有页面工具的act方法
	 * 
	 * @param pWebview
	 *            webview对象
	 * @param object
	 *            参数集合
	 * @param tool
	 *            工具类
	 * @throws JSONException
	 */
	public void executeAct(IWebview pWebview, JSONObject object, Tool tool)
			throws JSONException {
		String CallBackID = object.getString("callBackID");
		if (tool != null) {
			tool.act(pWebview, object);
		} else {
			returnErrorMessage(pWebview, CallBackID, 401, "加载到的工具类为null");
		}
	}

	/**
	 * 下载本地工具，并判断工具类型，根据工具类型执行不同操作
	 * 
	 * @param pWebview
	 *            webview对象
	 * @param object
	 *            参数集合
	 * @param fileName
	 *            工具名称
	 * @throws JSONException
	 */
	public void downloadToolAndAdjustToolTypeThenDo(IWebview pWebview,
			JSONObject object, String fileName) throws JSONException {
		Context context = pWebview.getContext();
		String tvId = object.getString("tvId");
		File jarFile = getJarFile(context, fileName);
		boolean isSuccess = getToolContentByToolInfoWithController(tvId,
				jarFile);// 下载Jar包
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
	 * @方法：getToolContentByToolInfoWithController
	 * @描述：下载工具jar包
	 * @param toolObjID
	 *            工具objId
	 * @param toolVersionID
	 *            工具版本Id
	 * @param jarFile
	 *            工具下载存放路径
	 * @param sessionId
	 *            与服务器连接sessionId
	 * @return 返回下载结果
	 */
	public boolean getToolContentByToolInfoWithController(String tvId,
			File jarFile) {
		// 生成一个httpclient对象
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
			// 关闭低层流。
			try {
				if (in != null) {
					in.close();
				}
				if (httpclient != null) {
					// 关闭连接.
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
	 * @方法名：SetToolVersion
	 * @param context
	 *            上下文对象
	 * @param fileName
	 *            文件名称
	 * @param toolVersion
	 *            工具版本
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
		editor.commit();// 提交修改
	}

	/**
	 * 回调js端Error方法，并返回结果
	 * 
	 * @param pWebview
	 *            webview对象
	 * @param CallBackID
	 *            回调Id
	 * @param code
	 *            返回code值
	 * @param content
	 *            返回内容
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
	 * @描述：获取jar包解压路径
	 * @param context
	 *            上下问对象
	 * @param toolId
	 *            工具Id
	 * @return 返回文件夹对象
	 */
	public File getJarUnZipPath(Context context, String toolId) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		File unZipPath = null;
		if (sdCardExist) {
			File sdDir = context
					.getExternalFilesDir(Environment.DIRECTORY_MOVIES);
			File unZipRoot = new File(sdDir.getPath() + File.separator
					+ "JarUnZip");
			unZipPath = new File(sdDir.getPath() + File.separator + "JarUnZip"
					+ File.separator + toolId);
			if (!unZipRoot.exists()) {// SD卡文件夹是否存在，没有则创建
				unZipRoot.mkdirs();
			}
		} else {
			File dir = context.getFilesDir();
			unZipPath = new File(dir.getPath() + File.separator + toolId);
		}
		return unZipPath;
	}
}
