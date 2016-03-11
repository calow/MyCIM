package com.example.cim.fragment;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import com.example.cim.R;
import com.example.cim.adapter.ToolsAdapter;
import com.example.cim.asynctask.AsyncTaskBase;
import com.example.cim.model.ToolEntity;
import com.example.cim.network.API;
import com.example.cim.nio.constant.Constant;
import com.example.cim.view.CustomListView;
import com.example.cim.view.CustomListView.OnRefreshListener;
import com.example.cim.view.LoadingView;
import com.example.testwebview.WebviewActivity;
import com.example.tool.Tool;
import com.example.utils.JarUtils;
import com.example.utils.ToolLoaderUtil;

import dalvik.system.DexClassLoader;

public class PhoneToolFragment extends Fragment implements OnItemClickListener {

	private static final String TAG = "PhoneToolFragment";
	private Context mContext;
	private View mBaseView;
	private View mSearchView;
	private CustomListView mCustomListView;
	private ToolsAdapter adapter;
	private LoadingView mLoadingView;

	LinkedList<ToolEntity> tes = new LinkedList<ToolEntity>();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = getActivity();
		mBaseView = inflater.inflate(R.layout.fragment_phonetoollist, null);
		mSearchView = inflater.inflate(R.layout.common_search_l, null);
		findView();
		init();
		return mBaseView;
	}

	public void findView() {
		mCustomListView = (CustomListView) mBaseView
				.findViewById(R.id.lv_tools);
		mLoadingView = (LoadingView) mBaseView.findViewById(R.id.loading);
	}

	public void init() {
		adapter = new ToolsAdapter(mContext, tes, mCustomListView);
		mCustomListView.setAdapter(adapter);

		mCustomListView.addHeaderView(mSearchView);

		mCustomListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				new AsyncRefresh().execute(0);
			}
		});
		mCustomListView.setOnItemClickListener(this);

		mCustomListView.setCanLoadMore(false);
		new ToolsAsyncTask(mLoadingView).execute(0);
	}

	private class ToolsAsyncTask extends AsyncTaskBase {

		List<ToolEntity> recentTes = new ArrayList<ToolEntity>();

		public ToolsAsyncTask(LoadingView loadingView) {
			super(loadingView);
		}

		@Override
		protected Integer doInBackground(Integer... params) {
			int result = -1;
			recentTes = new ArrayList<ToolEntity>();
			try {
				String response = API.httpPost(API.PhoneToolList_URL, null);
				if (response != null && !response.equals("")
						&& !response.equals("null")) {
					JSONObject object = new JSONObject(response);
					JSONArray data = object.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = data.getJSONObject(i);
						ToolEntity entity = new ToolEntity();
						entity.setToolId(jsonObject.getString("toolId"));
						entity.setToolName(jsonObject.getString("toolName"));
						entity.setPlateform(jsonObject.getString("platform"));
						entity.setDescription(jsonObject
								.getString("description"));
						entity.setTvId(jsonObject.getString("tvId"));
						recentTes.add(entity);
					}
				}
				if (recentTes.size() >= 0) {
					result = 1;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return result;
		}

		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			tes.clear();
			tes.addAll(recentTes);
			adapter.notifyDataSetChanged();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	private class AsyncRefresh extends
			AsyncTask<Integer, Integer, List<ToolEntity>> {

		private List<ToolEntity> recentTes = new ArrayList<ToolEntity>();

		@Override
		protected List<ToolEntity> doInBackground(Integer... params) {
			recentTes = new ArrayList<ToolEntity>();
			try {
				String response = API.httpPost(API.PhoneToolList_URL, null);
				if (response != null && !response.equals("")
						&& !response.equals("null")) {
					JSONObject object = new JSONObject(response);
					JSONArray data = object.getJSONArray("data");
					for (int i = 0; i < data.length(); i++) {
						JSONObject jsonObject = data.getJSONObject(i);
						ToolEntity entity = new ToolEntity();
						entity.setToolId(jsonObject.getString("toolId"));
						entity.setToolName(jsonObject.getString("toolName"));
						entity.setPlateform(jsonObject.getString("platform"));
						entity.setDescription(jsonObject
								.getString("description"));
						entity.setTvId(jsonObject.getString("tvId"));
						recentTes.add(entity);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return recentTes;
		}

		@Override
		protected void onPostExecute(List<ToolEntity> result) {
			super.onPostExecute(result);
			if (result != null) {
				updateItemStatu(result);
			}
			mCustomListView.onRefreshComplete();
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (id > -1) {
			ToolEntity te = (ToolEntity) adapter.getItem(Integer
					.parseInt(String.valueOf(id)));
			
			String url = null;
			if (te.getPlateform().equals("1")) {// �ն˹���
				downloadToolAndDo(te);
				return;
			} else if (te.getPlateform().equals("2")) {// �ƶ�����
				url = Constant.SERVER_URL + "/tool/tool_run.action?toolId="
						+ te.getToolId();
			}
			Intent intent = new Intent(mContext, WebviewActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("title", te.getToolName());
			mContext.startActivity(intent);
			Activity activity = (Activity)mContext;
			activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
		} else {
			System.out.println("�����򱻵����");
		}
	}

	public void updateItemStatu(List<ToolEntity> list) {
		tes.clear();
		tes.addAll(list);
		adapter.notifyDataSetChanged();
	}

	public void downloadToolAndDo(final ToolEntity te) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String toolId = te.getToolId();
				String tvId = te.getTvId();
				String toolName = te.getToolName();
				String fileName = toolId + ".jar";// �ļ�����������ƣ�
				boolean available = isNetworkAvailable(mContext);// �����Ƿ����״̬
				try {
					if (isJarExit(mContext, fileName)) {// ���ߴ���
						if (isNewestJar(mContext, fileName, tvId)) {// �����°汾
							// �����°汾�����й���
							releaseToolAndDo(fileName, toolId, toolName);
						} else {// �������°汾
							if (available) {// ������á����ع���
								downloadToolAndreleaseToolThenDo(fileName, tvId, toolId, toolName);
							} else {// ���粻���á����оɰ湤��
								releaseToolAndDo(fileName, toolId, toolName);
							}
						}
					} else {// ���߲�����
						if (available) {// ������á����ع��߲����й���
							downloadToolAndreleaseToolThenDo(fileName, tvId, toolId, toolName);
						} else {// ����
							Activity activity = (Activity)mContext;
							activity.runOnUiThread(new Runnable() {
								
								@SuppressWarnings("static-access")
								@Override
								public void run() {
									Toast toast = new Toast(mContext);
									toast.makeText(mContext, "���й��߳���", 1000).show();
								}
							});
						}
					}
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
	public void releaseToolAndDo(String fileName, String toolId, String toolName)
			throws JSONException {
		// ��ѹjar��
		final File unZipPatn = getJarUnZipPath(mContext, toolId);
		File jarPath = getJarFile(mContext, fileName);
		if (!unZipPatn.exists()) {
			JarUtils.unJar(jarPath, unZipPatn);
		}
		String url = getUrl(unZipPatn.getPath(), toolId);
		Intent intent = new Intent(mContext, WebviewActivity.class);
		intent.putExtra("url", url);
		intent.putExtra("title", toolName);
		mContext.startActivity(intent);
		Activity activity = (Activity)mContext;
		activity.overridePendingTransition(R.anim.activity_new, R.anim.activity_out);
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

	@SuppressWarnings("rawtypes")
	@SuppressLint("NewApi")
	public Tool getTool(Context context, String fileName) {
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
	public void downloadToolAndreleaseToolThenDo(String fileName,
			String tvId, String toolId, String toolName) throws JSONException {
		File jarFile = getJarFile(mContext, fileName);
		boolean isSuccess = getToolContentByToolInfoWithController(tvId,
				jarFile);// ����Jar��
		if (isSuccess) {
			SetToolVersion(mContext, fileName, tvId);
			releaseToolAndDo(fileName, toolId, toolName);
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
	public String getUrl(String unZipPath, String toolId) {
		String url = Constant.locationUrl + unZipPath + "/main.jsp?toolId="
				+ toolId;
		return url;
	}
}
