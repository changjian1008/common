package cn.common.tools.common;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequest {
	/**
	 * 从网络Url中下载文件
	 * 
	 * @param urlStr
	 * @throws IOException
	 */
	public void downLoadFromUrl(Configuration hadoopConfig, String hadoopPath, String urlStr, String toekn)
			throws IOException {
		URL url = new URL(urlStr);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		// 设置超时间为3秒
		conn.setConnectTimeout(30 * 1000);
		// 防止屏蔽程序抓取而返回403错误
		conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		conn.setRequestProperty("lfwywxqyh_token", toekn);
		// 得到输入流
		InputStream inputStream = conn.getInputStream();
		// 获取自己数组
		readInputStream(inputStream, hadoopConfig, hadoopPath);
		if (inputStream != null) {
			inputStream.close();
		}
		System.out.println("info:" + url + " download success");

	}

	/**
	 * 从输入流中获取字节数组
	 * 
	 * @param inputStream
	 * @return
	 * @throws IOException
	 */
	public void readInputStream(InputStream inputStream, Configuration hadoopConfig, String hadoopPath) {
		FSDataOutputStream fsdOutputStream = null;
		FileSystem hdfs = null;
		try {
			hdfs = FileSystem.get(hadoopConfig);
			Path path = new Path(hadoopPath);

			fsdOutputStream = hdfs.create(path);
			byte[] buffer = new byte[1024];
			int len = 0;

			while ((len = inputStream.read(buffer)) != -1) {
				fsdOutputStream.write(buffer, 0, len);

			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (fsdOutputStream != null) {
				try {
					fsdOutputStream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (hdfs != null) {
				try {
					//hdfs.closeAll();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	public static void main(String[] args) throws IOException {

		// HttpRequest.downLoadFromUrl(
		// "http://doc.avc-ott.com/docs/dmp/222_111_20190116154545_6d2b48ba-ede2-4528-a321-e3bd2d983cf4_tag.txt",
		// "222_111_20190116154545_6d2b48ba-ede2-4528-a321-e3bd2d983cf4_tag.txt",
		// "E:\\httptest", "");
		// System.out.println("下载完成");

	}

}