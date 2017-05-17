package com.gable.socket.thread;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.gable.socket.utils.InitUtil;

/**
 * 从云服务器上下载数据写入磁盘
 * 
 * @author mj
 *
 */
public class FileUploadThread implements Runnable {

	Logger log = Logger.getLogger(FileUploadThread.class);

	private String filePath;

	public FileUploadThread(String filePath) {
		this.filePath = filePath;
	}

	@Override
	public void run() {
		try {
			//初始化客戶端
			OSSClient ossClient = new OSSClient(InitUtil.propertiesMap.get("ENDPOINT"),
					InitUtil.propertiesMap.get("ACCESSKEYID"), InitUtil.propertiesMap.get("ACCESSKEYSECRET"));
			//多文件下载，写入
			String[] fileArray = filePath.split(",");
			for (int i = 0; i < fileArray.length; i++) {
				OSSObject ossObject = ossClient.getObject(InitUtil.propertiesMap.get("BUCKETNAME"), fileArray[i]);
				if (ossObject != null) {
					InputStream in = ossObject.getObjectContent();
					byte[] bytes = input2byte(in);
					in.close();
					
					File file = new File(InitUtil.propertiesMap.get("LOCALSAVEPATH") + fileArray[i]);
					if (!file.getParentFile().exists()) {
						file.getParentFile().mkdirs();
					}
					OutputStream ouputStream = new FileOutputStream(file);
					ouputStream.write(bytes);
					ouputStream.flush();
					ouputStream.close();
				}
			}
			ossClient.shutdown();
		} catch (Exception e) {
			log.error("_____文件操作失败："+e.toString());
		}
	}

	public static final byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
}
