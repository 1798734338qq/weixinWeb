package cn.net.comsys.weixin;

import java.io.File;
import java.net.HttpCookie;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONObject;

import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;

public class Main {
	static Map<String, String> cookie_map = new HashMap<>();

	static String login_name = "1798734338@qq.com";
	static String login_pwd = "3621155e9f2d306881aa725300601ba0";

	static String WEIXIN_LOGIN_URL = "https://mp.weixin.qq.com/cgi-bin/bizlogin?action=startlogin";
	// »ñÈ¡É¨ÂëµØÖ·
	static String WEIXIN_LOGIN_QRCODE = "https://mp.weixin.qq.com/cgi-bin/loginqrcode?action=getqrcode&param=4300&rd=";
	// ÅÐ¶ÏÉ¨Âë×´Ì¬
	static String WEIXIN_LOGIN_TASK = "https://mp.weixin.qq.com/cgi-bin/loginqrcode?action=ask&token=&lang=zh_CN&f=json&ajax=1";
	
	static Random random = new Random();

	public static void main(String[] args) {
		// loadHome();
		String ref = login();
		//getQrcode(ref);
		getQrcodeState(ref);
	}

	private static String login() {
		Map<String, Object> post_map = new HashMap<>();
		post_map.put("username", login_name);
		post_map.put("pwd", login_pwd);
		post_map.put("imgcode", "");
		post_map.put("f", "json");
		post_map.put("userlang", "zh_CN");
		post_map.put("redirect_url", "");
		post_map.put("token", "");
		post_map.put("lang", "zh_CN");
		post_map.put("ajax", "1");

		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");

		headers.put("Host", "mp.weixin.qq.com");
		headers.put("Referer", "https://mp.weixin.qq.com/");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

		HttpResponse login = HttpRequest.post(WEIXIN_LOGIN_URL).form(post_map).addHeaders(headers)
				.setFollowRedirects(true).cookie("").execute();

		if (login.getStatus() == 200) {
			for (HttpCookie cookie : login.getCookies()) {
				cookie_map.put(cookie.getName(), cookie.getValue());
			}
			Map map = JSONObject.parseObject(login.body(), Map.class);
			if (map != null && map.get("redirect_url") != null) {
				return WEIXIN_LOGIN_URL;
			}
		}
		return null;
	}

	private static void getQrcode(String referer_url) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");
		headers.put("Accept", "image/webp,image/apng,image/*,*/*;q=0.8");

		headers.put("Referer", referer_url);
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");

		StringBuffer cookie_buffer = new StringBuffer();
		for (String key : cookie_map.keySet()) {
			cookie_buffer.append(key).append("=").append(cookie_map.get(key)).append(";");
		}
		String qrcode_url = WEIXIN_LOGIN_QRCODE + random.nextInt(999);
		System.out.println(qrcode_url);
		HttpResponse code = HttpRequest.get(qrcode_url).addHeaders(headers).setFollowRedirects(true)
				.cookie(cookie_buffer.toString()).execute();
		System.out.println(code.writeBody(new File("c:\\test.png")));
	}
	
	private static void getQrcodeState(String referer_url) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");

		headers.put("Referer", referer_url);
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");

		StringBuffer cookie_buffer = new StringBuffer();
		for (String key : cookie_map.keySet()) {
			cookie_buffer.append(key).append("=").append(cookie_map.get(key)).append(";");
		}
		HttpResponse task = HttpRequest.get(WEIXIN_LOGIN_TASK).addHeaders(headers).setFollowRedirects(true)
				.cookie(cookie_buffer.toString()).execute();
		System.out.println(task.body());
	}
}
