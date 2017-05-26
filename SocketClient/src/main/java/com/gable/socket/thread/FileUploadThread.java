package com.gable.socket.thread;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObject;
import com.gable.socket.utils.InitUtil;
import com.gable.socket.utils.MD5Util;

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
			//多文件下载，替换文件域名，写入本地磁盘。
			//http://xxxxx/dispatch/639b566d-f6e2-4b0a-8dfb-1fab4465345b.jpg
			//替换成  dispatch/639b566d-f6e2-4b0a-8dfb-1fab4465345b.jpg
			String[] fileArray = filePath.split(",");
			List<String> fileList = replaceFile(fileArray);
			for (int i = 0; i < fileList.size(); i++) {
				OSSObject ossObject = ossClient.getObject(InitUtil.propertiesMap.get("BUCKETNAME"), fileList.get(i));
				if (ossObject != null) {
					InputStream in = ossObject.getObjectContent();
					byte[] bytes = input2byte(in);
					in.close();
					send(fileList.get(i), bytes, ossObject.getObjectMetadata().getContentLength());
//					File file = new File(InitUtil.propertiesMap.get("LOCALSAVEPATH") + );
//					if (!file.getParentFile().exists()) {
//						file.getParentFile().mkdirs();
//					}
//					OutputStream ouputStream = new FileOutputStream(file);
//					ouputStream.write(bytes);
//					ouputStream.flush();
//					ouputStream.close();
				}
			}
			ossClient.shutdown();
		} catch (Exception e) {
			log.error("_____文件操作失败："+e.toString());
		}
	}

	public final byte[] input2byte(InputStream inStream) throws IOException {
		ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
		byte[] buff = new byte[100];
		int rc = 0;
		while ((rc = inStream.read(buff, 0, 100)) > 0) {
			swapStream.write(buff, 0, rc);
		}
		byte[] in2b = swapStream.toByteArray();
		return in2b;
	}
	
	public List<String> replaceFile(String [] fileArray){
		List<String> fileList = new ArrayList<String>();
		for (int i = 0; i < fileArray.length; i++) {
			String file = fileArray[i].replace(InitUtil.propertiesMap.get("FILEURL"), "");
			fileList.add(file);
		}
		return fileList;
	}
	
	public static boolean send(String url,byte [] bytes,long fileSize){
		InputStream in =null;
		try {
			in = new ByteArrayInputStream(bytes);
			int length = 1048576;
			boolean bl = true;
			byte b[] = new byte[length];
			int streamNum = (int) Math.floor(fileSize / length);
			int leave = (int) fileSize % length;
			for (int i = 0; i < streamNum; i++) {    //当没有读取完时，继续读取
				in.read(b, 0, length);
				bl = sendPost(new String(b, "ISO-8859-1"),url);
				if (bl == false) {
					return false;
				}
			}
			if (leave > 0) {
				b = new byte[leave];
				in.read(b, 0, leave);
				sendPost(new String(b, "ISO-8859-1"),url);
			}

		}catch (Exception e){
			e.getStackTrace();
		}finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	/**
	 * java模拟post请求去访问文件服务器
	 * @param fileInfo
	 * @return
	 */
	public static boolean sendPost(String fileInfo,String url){
		HttpClient client = new HttpClient();
		PostMethod method =null;
		String RESULTURL = InitUtil.propertiesMap.get("LOCALSAVEPATH")+"/FileUpload/fileInfo";
		try{
			method = new PostMethod(RESULTURL);
			method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
			Map<String,String> headMap = headerMap();
			method.setRequestHeader("token", headMap.get("token").toString());
			method.setRequestHeader("timestamp", headMap.get("timestamp").toString() );
			method.setRequestHeader("checksum", headMap.get("checksum").toString() );
			method.addParameter("urls", url);
			fileInfo = encryptString(fileInfo);
			method.addParameter("fileInfo", fileInfo);
			HttpMethodParams param = method.getParams();
			param.setContentCharset("UTF-8");
			int resultCode = client.executeMethod(method);
			if(resultCode != 200){
				return false;
			}
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return false;
	}
	
	//加载头部分信息签名验证，
	public static Map<String,String> headerMap (){
		Map<String,String> map = new HashMap<String,String>();
		long time = new Date().getTime();
		String token = "authority";
		map.put("token", token);
		map.put("timestamp", time+"");
		String checkSum = MD5Util.md5(token+time);
		map.put("checksum", checkSum);
		return map;
	}
	
	/**
	 * 将传递参数dec加密转base64传递
	 * @param str
	 * @return
	 * @throws Exception
	 */
	public static String encryptString(String str) throws Exception{
		String resultString =  org.apache.commons.codec.binary.Base64.encodeBase64String(str.getBytes("ISO-8859-1"));
		return resultString;
	}
}
