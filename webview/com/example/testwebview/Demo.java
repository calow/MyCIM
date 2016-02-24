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
	 * @param toolId
	 *            工具Id
	 * @param action
	 *            具体的action操作
	 * @param callbackId
	 *            回调Id
	 * @param params
	 *            传给插件的参数
	 */
	public void loadExtend(String toolId, String action, String callbackId,
			String params) {
		// 参数初始化
		String sessionId = null;
		String fileName = toolId + ".jar";// 文件名（插件名称）
		String toolVersion = null;// 工具最新版本Id
		IWebviewImpl pWebview = new IWebviewImpl(mContext, mWebView);
		JSONObject pArgs = null;
		try {
			pArgs = new JSONObject(params);
		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("Demo", "前台传给插件的参数格式错误！");
		}
		boolean available = isNetworkAvailable(mContext);// 网络是否可用状态
		if (isJarExit(mContext, fileName)) {
			// 工具存在
			if (available) {

				// --------------------测试使用-------------------------
				if (sessionId == null || sessionId.equals("")) {
					sessionId = getSessionId();
				}
				// --------------------之后要删除-----------------------

				// 工具存在、网络可用、判断是否最新版本
				Map<String, String> map = getToolCurrentVersionIdWithController(
						toolId, sessionId);
				toolVersion = map.get("toolCurrentVersionID");
				if (isNewestJar(mContext, fileName, toolVersion)) {
					// 是最新版本、判断是否为有页面工具、无页面则运行工具
					runToolAndAdjustIfWithPages(pWebview, mContext, fileName,
							callbackId, action, toolId, pArgs);
				} else {
					// 不是最新版本、下载工具、判断是否为有页面工具、无页面则运行工具
					// downloadToolAndRunToolAndReturnResult(pWebview, mContext,
					// fileName, callbackId, action, toolId, pArgs,
					// toolVersion, sessionId);
				}
			} else {
				// 工具存在、网络不可用、不判断是否最新版本、判断是否为有页面工具、无页面则运行工具、有页面则返回结果给前台解压jar包
				// runToolAndAdjustIfWithPages(pWebview, context, fileName,
				// CallBackID, action, toolId, pArgsArray);
			}
		} else {
			// 工具不存在
			if (available) {

				// --------------------测试使用---------------------------
				if (sessionId == null || sessionId.equals("")) {
					sessionId = getSessionId();
				}
				// --------------------之后要删除-------------------------

				// 工具不存在、网络可用
				Map<String, String> map = getToolCurrentVersionIdWithController(
						toolId, sessionId);
				toolVersion = map.get("toolCurrentVersionID");
				// downloadToolAndRunToolAndReturnResult(pWebview, context,
				// fileName, CallBackID, action, toolId, pArgsArray,
				// toolVersion, sessionId);
			} else {
				// 工具不存在、网络不可用
				// returnRunToolErrorForWithoutNetwork(pWebview, CallBackID);
			}
		}
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
	@SuppressWarnings("deprecation")
	public File getJarFile(Context context, String fileName) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		File jarFile = null;
		// if (sdCardExist) {
		// File sdDir = Environment.getExternalStorageDirectory();// 获取手机SD卡根目录
		// File jarDir = new File(sdDir.getPath() + File.separator
		// + "WeToBand" + File.separator + "Jar");
		// jarFile = new File(sdDir.getPath() + File.separator + "WeToBand"
		// + File.separator + "Jar" + File.separator + fileName);
		// if (!jarDir.exists()) {// SD卡文件夹是否存在，没有则创建
		// jarDir.mkdirs();
		// }
		// } else {
		// // 创建文件夹
		// File dir = context.getDir("WeToBandJar", Context.MODE_PRIVATE
		// | Context.MODE_WORLD_READABLE
		// | Context.MODE_WORLD_WRITEABLE);
		// // 新建文件
		// jarFile = new File(dir.getPath() + File.separator + fileName);
		// }

		if (sdCardExist) {
			File sdDir = Environment.getDataDirectory();// 获取手机SD卡根目录
			File jarDir = new File(sdDir.getPath() + File.separator + "Jar");
			jarFile = new File(sdDir.getPath() + File.separator + "Jar"
					+ File.separator + fileName);
			if (!jarDir.exists()) {// SD卡文件夹是否存在，没有则创建
				jarDir.mkdirs();
			}
		} else {
			// 创建文件夹
			File dir = context.getDir("Jar", Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			// 新建文件
			jarFile = new File(dir.getPath() + File.separator + fileName);
		}

		return jarFile;
	}

	public String getSessionId() {
		InputStream is = null;
		String[] param = null;
		String fileUrl = Constant.url
				+ "userLogin?username=zhuangweihao&password=123456";// 登录url
		try {
			URL url = new URL(fileUrl);
			HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
			ucon.setRequestMethod("POST");
			ucon.connect();
			ucon.getOutputStream().flush();
			int code = ucon.getResponseCode();

			// 读取响应内容
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
				sTotalString = "远程服务器连接失败,错误代码:" + code;
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
	 * @方法：getToolCurrentVersionIdWithController
	 * @描述：调用controller，通过工具id获取工具版本信息
	 * @param toolId
	 *            工具id
	 * @param sessionId
	 *            会话id
	 * @return 返回工具版本信息
	 */
	public Map<String, String> getToolCurrentVersionIdWithController(
			String toolId, String sessionId) {
		Map<String, String> map = null;
		// 生成一个httpclient对象
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
				// 关闭连接.
				httpclient.getConnectionManager().shutdown();
			}
		}
		return map;
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
	 * @描述：判断本地工具是否为有页面工具
	 * @param pWebview
	 *            webview对象
	 * @param context
	 *            上下文对象
	 * @param fileName
	 *            文件名
	 * @param CallBackID
	 *            回调Id
	 * @param action
	 *            调用的本地工具的action方法
	 * @param toolId
	 *            工具Id
	 * @param pArgsArray
	 *            传给工具的参数
	 */
	public void runToolAndAdjustIfWithPages(final IWebview pWebview,
			Context context, String fileName, String CallBackID, String action,
			String toolId, JSONObject pArgs) {
		File jarFile = getJarFile(context, fileName);// jar包File对象
		String jarPath = jarFile.getAbsolutePath();// jar包存放全路径
		String isWithPages = SAX(context, jarPath, "WithPages");
		if (isWithPages == null || isWithPages.equals("0")) {
			// 无页面工具，运行工具并返回运行结果和交换结果
			runToolNoPageAndReturnResult(pWebview, context, fileName,
					CallBackID, action, toolId, pArgs);
		} else if (isWithPages != null && isWithPages.equals("1")) {
			// 有页面工具，返回所执行工具为有页面工具结果
			runToolWithPagesAndReturnResult(pWebview, CallBackID, fileName,
					toolId);
		} else {
			// 返回withpages参数错误
			returnToolWithPagesParamError(pWebview, CallBackID);
		}
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
	 * @方法：runToolNoPageAndReturnResult
	 * @描述：运行工具并返回运行结果
	 * @param pWebview
	 *            webview对象
	 * @param context
	 *            context上下文
	 * @param fileName
	 *            文件名称
	 * @param CallBackID
	 *            返回结果回调方法id
	 * @param action
	 *            调用插件的action方法
	 * @param toolId
	 *            工具id
	 * @param pArgsArray
	 *            要传给工具的参数
	 */
	public void runToolNoPageAndReturnResult(final IWebview pWebview,
			Context context, String fileName, String CallBackID, String action,
			String toolId, JSONObject pArgs) {
		IFeature lib = runPlugin(context, fileName);
		if (lib != null) {
			// 返回运行成功结果
			returnRunSuccessResult(pWebview, CallBackID);
			// 调用该插件的excute方式执行本地工具操作并返回交换结果
			lib.execute(pWebview, action, CallBackID, pArgs);
		} else {
			// 加载jar包插件类失败、返回工具运行失败标志
			removeToolVersion(context, fileName);// 删除本地的工具版本信息
			deleteDexFile(context, toolId);// 删除该本地工具解压后的Dex文件
			deleteJarFile(context, fileName);// 删除该本地工具的jar包
			deleteJarUnZipPath(context, toolId);// 删除本地jar包解压文件
			returnRunErrorResult(pWebview, CallBackID);// 返回加载失败结果
		}
	}

	/**
	 * @方法名：runPlugin
	 * @描述：动态加载jar包，并运行插件方法
	 * @param pWebview
	 *            WebView对象
	 * @param context
	 *            Context上下文对象
	 * @param fileName
	 *            文件名
	 * @param action
	 *            要执行插件的具体action操作
	 * @param pArgs
	 *            传递到插件中的参数
	 * @return 返回调用插件后获取到的数据
	 */
	@SuppressWarnings({ "rawtypes" })
	@SuppressLint("NewApi")
	public IFeature runPlugin(Context context, String fileName) {
		if (ClassLoaderUtil.isContainsClazz(fileName)) {
			return ClassLoaderUtil.getClazz(fileName);
		}
		File jarFile = getJarFile(context, fileName);// jar包File对象
		String jarPath = jarFile.getAbsolutePath();// jar包存放全路径
		String packagePathAndName = SAX(context, jarPath, "EntryClass");
		DexClassLoader clazz = DLClassLoader.getClassLoader(jarPath, context,
				context.getClassLoader());
		Class libProviderClazz = null;
		try {
			// 加载指定的工具插件
			libProviderClazz = clazz.loadClass(packagePathAndName);
			IFeature lib = (IFeature) libProviderClazz.newInstance();
			// 实例化该插件的类对象
			ClassLoaderUtil.putClazz(fileName, lib);
			return lib;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @方法：returnRunSuccessResult
	 * @描述：返回加载运行工具类成功结果
	 * @param pWebview
	 *            webview对象
	 * @param CallBackID
	 *            回调方法id
	 */
	public void returnRunSuccessResult(final IWebview pWebview,
			String CallBackID) {
		// 加载jar包插件类成功、先返回工具运行结果
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// 返回结果到前端用
			retJSONObj.putOpt("code", "200");
			retJSONObj.putOpt("content", "runTool success");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.OK, JSUtil.CONTINUED);
	}

	/**
	 * @方法：removeToolVersion
	 * @描述：移除工具的版本信息
	 * @param context
	 *            context上下文对象
	 * @param fileName
	 *            文件名
	 */
	public void removeToolVersion(Context context, String fileName) {
		SharedPreferences sp = context.getSharedPreferences(
				"toolVersionStorage", Context.MODE_PRIVATE);
		Editor editor = sp.edit();
		editor.remove(fileName);
		editor.commit();// 提交修改
	}

	/**
	 * @方法名：deleteDexFile
	 * @描述：更新工具版本的时候删除dex文件
	 * @param context
	 *            Context上下文对象
	 * @param toolId
	 *            工具Id
	 */
	@SuppressWarnings("deprecation")
	public void deleteDexFile(Context context, String toolId) {
		String fileName = toolId + ".dex";
		// 创建文件夹
		File dir = context.getDir("Dex", Context.MODE_PRIVATE
				| Context.MODE_WORLD_READABLE | Context.MODE_WORLD_WRITEABLE);
		// 新建文件
		File dexFile = new File(dir.getPath() + File.separator + fileName);
		if (dexFile.exists()) {
			dexFile.delete();
		}
	}

	/**
	 * @方法：deleteJarFile
	 * @描述：删除jar包文件
	 * @param context
	 *            context上下文对象
	 * @param fileName
	 *            文件名称
	 */
	public void deleteJarFile(Context context, String fileName) {
		File jarFile = getJarFile(context, fileName);// jar包File对象
		if (jarFile.exists()) {
			jarFile.delete();
		}
	}

	/**
	 * @描述：删除jar包解压文件
	 * @param context
	 *            上下文对象
	 * @param toolId
	 *            工具Id
	 */
	public void deleteJarUnZipPath(Context context, String toolId) {
		File jarPath = getJarUnZipPath(context, toolId);
		if (jarPath.exists()) {
			recurDelete(jarPath);
		}
	}

	/**
	 * @描述：获取jar包解压路径
	 * @param context
	 *            上下问对象
	 * @param toolId
	 *            工具Id
	 * @return 返回文件夹对象
	 */
	@SuppressWarnings("deprecation")
	public File getJarUnZipPath(Context context, String toolId) {
		boolean sdCardExist = Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
		File jarFile = null;
		if (sdCardExist) {
			File sdDir = Environment.getDataDirectory();// 获取手机SD卡根目录
			File jarDir = new File(sdDir.getPath() + File.separator
					+ "JarUnZip");
			jarFile = new File(sdDir.getPath() + File.separator + "JarUnZip"
					+ File.separator + toolId);
			if (!jarDir.exists()) {// SD卡文件夹是否存在，没有则创建
				jarDir.mkdirs();
			}
		} else {
			// 创建文件夹
			File dir = context.getDir("JarUnZip", Context.MODE_PRIVATE
					| Context.MODE_WORLD_READABLE
					| Context.MODE_WORLD_WRITEABLE);
			// 新建文件
			jarFile = new File(dir.getPath() + File.separator + toolId);
		}
		return jarFile;
	}

	/**
	 * @描述：递归删除jar包解压文件目录
	 * @param f
	 *            文件对象
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
	 * @方法：returnRunErrorResult
	 * @描述：返回加载运行工具类失败结果
	 * @param pWebview
	 *            webview对象
	 * @param CallBackID
	 *            回调方法的id
	 */
	public void returnRunErrorResult(final IWebview pWebview, String CallBackID) {
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// 返回结果到前端用
			retJSONObj.putOpt("code", "500");
			retJSONObj.putOpt("content", "loadclass error!");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.ERROR, JSUtil.UNCONTINUED);
	}

	/**
	 * @描述：运行工具时，判断为有页面工具，返回结果
	 * @param pWebview
	 *            webview对象
	 * @param CallBackID
	 *            回调Id
	 */
	public void runToolWithPagesAndReturnResult(final IWebview pWebview,
			String CallBackID, String fileName, String toolId) {
		Context context = pWebview.getContext();
		Activity activity = pWebview.getActivity();
		// 解压jar包
		File releasePath = getJarUnZipPath(context, toolId);
		File jarPath = getJarFile(pWebview.getContext(), fileName);
		if (!releasePath.exists()) {
			JarUtils.unJar(jarPath, releasePath);
		}

		// 返回结果
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// 返回结果到前端用
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
	 * @描述：config.xml文件中的withpages参数填写格式错误，返回结果
	 * @param pWebview
	 *            webview对象
	 * @param CallBackID
	 *            回调id
	 */
	public void returnToolWithPagesParamError(final IWebview pWebview,
			String CallBackID) {
		JSONObject retJSONObj = null;
		try {
			retJSONObj = new JSONObject();// 返回结果到前端用
			retJSONObj.putOpt("code", "501");
			retJSONObj.putOpt("content", "WithPages param type error");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		JSUtil.execCallback(pWebview, CallBackID, retJSONObj.toString(),
				JSUtil.ERROR, JSUtil.UNCONTINUED);
	}

}
