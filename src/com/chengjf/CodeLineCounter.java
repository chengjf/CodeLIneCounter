package com.chengjf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 源代码行数统计 可以统计源代码文件总数，总行数，有效代码行数，空白代码行数，注释代码行数 在对单个源文件的操作时使用多线程加快处理速度
 * 其中，文件后缀标识，空白符和注释符均可以指定，容易扩展到其他语言
 * 
 * @author chengjf
 * @date 2015-05-14
 * @version 0.0.1
 */
public class CodeLineCounter {

	/**
	 * 源文件数目
	 */
	private static int fileCount = 0;
	/**
	 * 总行数
	 */
	private static int totalLineCount = 0;
	/**
	 * 有效代码行数
	 */
	private static int codeLineCount = 0;
	/**
	 * 空白代码行数
	 */
	private static int blankLineCount = 0;
	/**
	 * 注释代码行数
	 */
	private static int commentLineCount = 0;

	/**
	 * java文件后缀
	 */
	private static final String JAVA_EXTENSION_NAME = "java";
	/**
	 * 空白行标识
	 */
	private static final String[] BLANK_LINE_STR = { "" };
	/**
	 * 注释行标识
	 */
	private static final String[] COMMENT_LINE_STR = { "//", "/*", "*", "*/" };

	/**
	 * 线程池
	 */
	private static Executor executor = Executors.newCachedThreadPool();

	/**
	 * 主程序入口
	 * 
	 * @param args 接受一个参数，参数为源代码路径
	 */
	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		CodeLineCounter codeLineCounter = new CodeLineCounter();
		codeLineCounter.process(new File(args[0]));

		String msg = "代码文件数量：" + fileCount + "\n全部代码行数：" + totalLineCount
				+ "\n有效代码行数：" + codeLineCount + "\n空白代码行数：" + blankLineCount
				+ "\n注释代码行数：" + commentLineCount
				+ "\n-------------------------------------";
		System.out.println(msg);

		long endTime = System.currentTimeMillis();
		System.out.println("Total Time is: " + (endTime - startTime));
	}

	/**
	 * 对给定的文件进行处理，使用多线程对单个文件进行处理
	 * 
	 * @param file 要处理的文件，可以为文件目录或文件
	 */
	public void process(final File file) {
		if (file.isFile() && file.getName().endsWith(JAVA_EXTENSION_NAME)) {
			CodeLineCounter.executor.execute(new Runnable() {

				@Override
				public void run() {
					count(file);
				}
			});
			fileCount++;
		} else {
			File[] filenames = file.listFiles();
			if (filenames != null) {
				for (File f : filenames) {
					process(f);
				}
			}
		}
	}

	/**
	 * 对单个文件进行处理
	 * 
	 * @param file 要进行统计的源代码文件
	 * @return
	 */
	private void count(File file) {
		BufferedReader br = null;

		int totalCount = 0;
		int blankCount = 0;
		int commentCount = 0;
		int codeCount = 0;

		try {
			try {
				br = new BufferedReader(new FileReader(file));
				String line = "";
				while ((line = br.readLine()) != null) {
					totalCount++;
					String str = line.trim();
					if (this.isBlankCode(str)) {
						blankCount++;
					} else if (this.isCommentCode(str)) {
						commentCount++;
					} else {
						codeCount++;
					}
				}
			} finally {
				if (br != null) {
					br.close();
				}
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			CodeLineCounter.totalLineCount += totalCount;
			CodeLineCounter.blankLineCount += blankCount;
			CodeLineCounter.commentLineCount += commentCount;
			CodeLineCounter.codeLineCount += codeCount;
		}

	}

	/**
	 * 判断是否是空行
	 * 
	 * @param line 需要判断的行
	 * @return true是空行，false不是空行
	 */
	private boolean isBlankCode(String line) {
		for (String str : BLANK_LINE_STR) {
			if (line.equals(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是注释行
	 * 
	 * @param line 需要判断的行
	 * @return true是注释行，false不是注释行
	 */
	private boolean isCommentCode(String line) {
		for (String str : COMMENT_LINE_STR) {
			if (line.startsWith(str)) {
				return true;
			}
		}
		return false;
	}
}
