package com.example.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarUtils {

	/**
	 * 解压jar文件
	 * 
	 * @param jarFile
	 *            要解压的jar文件路径
	 * @param destination
	 *            解压到哪里
	 * @throws IOException
	 */
	public static void unJar(String jarFile, String destination) {
		File jar = new File(jarFile);
		File dir = new File(destination);
		unJar(jar, dir);
	}

	/**
	 * 解压jar文件
	 * 
	 * @param jarFile
	 *            要解压的jar文件路径
	 * @param destination
	 *            解压到哪里
	 * @throws IOException
	 */
	public static void unJar(File jarFile, File destination) {
		JarFile jar = null;
		try {
			if (destination.exists() == false) {
				destination.mkdirs();
			}
			jar = new JarFile(jarFile);
			Enumeration<JarEntry> en = jar.entries();
			JarEntry entry = null;
			InputStream input = null;
			BufferedOutputStream bos = null;
			File file = null;
			while (en.hasMoreElements()) {
				entry = en.nextElement();
				input = jar.getInputStream(entry);
				file = new File(destination, entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
					continue;
				} else {
					file.getParentFile().mkdirs();
				}
				bos = new BufferedOutputStream(new FileOutputStream(file));
				byte[] buffer = new byte[8192];
				int length = -1;
				while (true) {
					length = input.read(buffer);
					if (length == -1)
						break;
					bos.write(buffer, 0, length);
				}
				bos.close();
				input.close();
				// IOUtils.copy(input, bos);
			}

			Manifest mf = jar.getManifest();
			if (mf != null) {
				File f = new File(destination, "META-INF/MANIFEST.MF");
				File parent = f.getParentFile();
				if (parent.exists() == false) {
					parent.mkdirs();
				}
				OutputStream out = new FileOutputStream(f);
				mf.write(out);
				out.flush();
				out.close();
			}

		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (jar != null) {
				try {
					jar.close();
				} catch (Exception e) {
				}
			}
		}
	}

}
