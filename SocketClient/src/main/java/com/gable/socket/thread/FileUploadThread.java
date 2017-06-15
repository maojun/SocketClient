package com.gable.socket.thread;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSONObject;
import com.gable.socket.utils.HttpHelper;
import com.gable.socket.utils.InitUtil;

/**
 * 从服务器上下载数据写入磁盘
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
			Map<String,String> parameters = new HashMap<String,String>();
			parameters.put("filePath", filePath);
			//http请求服务器上的文件流
			String result = HttpHelper.postRequestResponseBodyAsString(InitUtil.propertiesMap.get("FILEREQUEST"), parameters);
			//完整文件写入地址：流
			//D:/gable/xxxx.jpg:流
			List<Map<String,String>> list = (List<Map<String,String>>) JSONObject.parse(result);
			for(Map<String,String> b : list){
				for(Entry<String, String> entry : b.entrySet()){
					String filename = entry.getKey();
					String bs = entry.getValue();
					try {
						byte [] by = bs.getBytes("ISO-8859-1");
						//将流写入到本地
						getFile(by,filename);
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
			}
			
		} catch (Exception e) {
			log.error("_____文件操作失败："+e.toString());
		}
	}
	 //根据byte数组，生成文件 
    public static void getFile(byte[] bfile, String filePath) {  
        BufferedOutputStream bos = null;  
        FileOutputStream fos = null;  
        File file = null;  
        try {  
            file = new File(filePath);  
            if(!file.getParentFile().exists()){
            	file.getParentFile().mkdirs();
            }
            fos = new FileOutputStream(file);  
            bos = new BufferedOutputStream(fos);  
            bos.write(bfile);  
        } catch (Exception e) {  
            e.printStackTrace();  
        } finally {  
            if (bos != null) {  
                try {  
                    bos.close();  
                } catch (IOException e1) {  
                   e1.printStackTrace();  
                }  
            }  
            if (fos != null) {  
                try {  
                    fos.close();  
                } catch (IOException e1) {  
                    e1.printStackTrace();  
                }  
            }  
        }  
    }    
}
