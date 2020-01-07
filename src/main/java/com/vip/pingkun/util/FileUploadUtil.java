package com.vip.pingkun.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class FileUploadUtil {
    public static String accessKey = "L7U6vPPf1L6VWP7tmvTlFBLdn0u2AefzQs4u5nfO";
    public static String secretKey = "pUYavnlkG-py-s-bLrKD0hnFTovkRfj0X5RZZvUT";
    public static String url = "http://q3cysg1iz.bkt.clouddn.com/";

    public static String bucket = "pinkun";

    public static String getFileToken(){
        Auth auth = Auth.create(accessKey, secretKey);
        String upToken = auth.uploadToken(bucket);
        return upToken;
    }

    public static String uploadFile(MultipartFile file){
        //构造一个带指定Region对象的配置类
        Configuration cfg = new Configuration(Region.region2());
         //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);

        String upToken = getFileToken();

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        String key = null;
        try {
            byte[] uploadBytes = file.getBytes();
            try {
                Response response = uploadManager.put(uploadBytes, key, upToken);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                return url+putRet.hash;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (UnsupportedEncodingException ex) {
            //ignore
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
