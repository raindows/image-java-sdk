package com.qcloud.sign;

import java.util.Random;

public class FileCloudSign {

	/**
	    @func   app_sign    时效性签名
	    @param  appid       Qcloud上申请的业务IDhttp://app.qcloud.com
	    @param  secret_id   Qcloud上申请的密钥id
	    @param  secret_key  Qcloud上申请的密钥key
	    @param  expired     签名过期时间
	    @param  userid      业务账号系统,没有可以不填
	    @param  sign        生成的签名
    */
	public static int appSign(String appId, String secret_id, String secret_key,
			long expired, String userid, StringBuffer mySign) {
		return appSignBase(appId, secret_id, secret_key, expired, userid, null, mySign);
	}

	 /**
	    @func   app_sign_once   绑定资源的签名,只有这个资源可以使用
	    @param  appid       Qcloud上申请的业务IDhttp://app.qcloud.com
	    @param  secret_id   Qcloud上申请的密钥id
	    @param  secret_key  Qcloud上申请的密钥key
	    @param  userid      业务账号系统,没有可以不填
	    @param  url         签名绑定的资源    
	    @param  sign        生成的签名
    */    
    public static int appSignOnce(String appId, String secret_id, String secret_key,
    		String userid, String url, StringBuffer mySign)
    {
        return appSignBase(appId, secret_id, secret_key, 0, userid, url, mySign);
    }
    
	private static int appSignBase(String appId, String secret_id,
			String secret_key, long expired, String userid, String url,
			StringBuffer mySign) {
		

		if (empty(secret_id) || empty(secret_key))
    	{
            return -1;
    	}
    	
    	String puserid = "";
    	if (!empty(userid))
    	{
			if (userid.length() > 64)
			{
                return -2;
			}
			puserid = userid;
    	}
    	
    	StringBuffer fileid = new StringBuffer("");
    	if (!empty(url))
    	{
			if (url.length() > 256)
			{
                return -3;
			}
			int ret = getFileidFromUrl(url, fileid);
            if (ret != 0)
            {
                return -3;
            }
    	}
    	
        long now = System.currentTimeMillis() / 1000;    
        int rdm = Math.abs(new Random().nextInt());
        String plain_text = "a=" + appId + "&k=" + secret_id + "&e=" + expired + "&t=" + now + "&r=" + rdm + "&u=" + puserid + "&f=" + fileid.toString();

        byte[] bin = hashHmac(plain_text, secret_key);

        byte[] all = new byte[bin.length + plain_text.getBytes().length];
        System.arraycopy(bin, 0, all, 0, bin.length);
        System.arraycopy(plain_text.getBytes(), 0, all, bin.length, plain_text.getBytes().length);
        
        mySign.append(Base64Util.encode(all));
        
        return 0;
	}

	private static byte[] hashHmac(String plain_text, String accessKey) {
		
		try {
			return HMACSHA1.getSignature(plain_text, accessKey);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private static int getFileidFromUrl(String url, StringBuffer fileid) {
		
		 /*
        http://1.1.1.1/1009_eb090aa932f04fbfb4cb29be8dcc264e.f20.mp4
        http://1.1.1.1/77/123456/c06dd409-20bd-4a0a-84e4-e6c45fc5fb25/orignal
        */
        if (url.startsWith("http://"))
        {
            url = url.substring(7);
            String[] url_explode = url.split("/");
            if (url_explode.length >= 4)
            {
        		fileid.append(url_explode[3]);
        		return 0;
            }
            
            if (url_explode.length >= 2)
            {
                String[] vinfo_explode = url_explode[1].split(".");
                fileid.append(vinfo_explode[0]);	
                return 0;
            }
        }
	    return -1;
	}
    
	public static boolean empty(String s){
		if (s == null || s.trim().equals("") || s.trim().equals("null")) {
			return true;
		}
		return false;
	}
		
}