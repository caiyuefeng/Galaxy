package com.galaxy.earth;

import com.galaxy.earth.exception.GalaxyException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 蔡月峰
 * @version 1.0
 *
 * @date Create in 21:12 2019/9/15
 *
 */
public class FileUtils {

	public static void mkdir(File dir) throws GalaxyException {
		File parent = dir.getParentFile();
		if (!parent.isDirectory()) {
			mkdir(parent);
		}
		if (!dir.mkdir()) {
			throw new GalaxyException(String.format("文件夹:%s创建失败", dir.getAbsolutePath()));
		}
	}

	/**
	 * 按行读取输入文件
	 *
	 * @param file 待读取文件
	 * @return 读取行集
	 * @throws IOException 1
	 */
	public static List<String> listLines(File file) throws IOException {
		List<String> lines = new ArrayList<>();
		try (Reader reader = new FileReader(file);
			 BufferedReader bufferedReader = new BufferedReader(reader)) {
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		} catch (FileNotFoundException e) {
			throw new IOException(e);
		}
		return lines;
	}

	/**
	 * 按行输出数据到指定文件
	 *
	 * @param destFile 待输出文件
	 * @param lines    待输出数据集
	 * @throws IOException 1
	 */
	public static void writeLines(File destFile, List<String> lines) throws IOException {
		try (Writer writer = new FileWriter(destFile);
			 BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
			for (String line : lines) {
				bufferedWriter.write(line + "\n");
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			throw new IOException(e);
		}
	}

	/**
	 * 获取指定路径下所有Jar文件 并以URL格式返回
	 *
	 * @param path 路径
	 * @return URL集合
	 */
	public static List<URL> getAllJarFile(String path) {
		List<File> files = getAllFile(path);
		return files.stream().filter(file -> file.getName().endsWith(".jar"))
				.map(file -> {
					try {
						return file.toURI().toURL();
					} catch (IOException e) {
						GalaxyLog.CONSOLE_FILE_ERROR(String.format("文件%s转换URL异常", file.getAbsolutePath()), e);
					}
					return null;
				}).collect(Collectors.toList());
	}

	/**
	 * 获取指定路径下的所有文件
	 *
	 * @param rootPath 文件路径
	 * @return 文件集合
	 */
	public static List<File> getAllFile(String rootPath) {
		List<File> files = new ArrayList<>();
		File root = new File(rootPath);
		if (root.exists()) {
			getAllFile(root, files);
		}
		return files;
	}

	private static void getAllFile(File rootDir, List<File> files) {
		if (rootDir.isFile()) {
			files.add(rootDir);
		}
		if (rootDir.isDirectory()) {
			File[] children = rootDir.listFiles();
			if (children != null) {
				for (File child : children) {
					getAllFile(child, files);
				}
			}
		}
	}
}
