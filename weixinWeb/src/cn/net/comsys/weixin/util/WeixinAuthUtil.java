package cn.net.comsys.weixin.util;

import java.io.File;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.init.WebContextListener;
import cn.net.comsys.weixin.po.PublicAccountPo;

public class WeixinAuthUtil {

	private static Log log = LogFactory.get();
	
	//FREDCONTROL 抓取正常进行标识 未被禁
	public static boolean FREDCONTROL = false;

	public static String WECHATERRORMSG = "微信公众号账号已被微信官方封禁，请第二天扫码登录，继续抓取";
	
	static Map<String, String> cookie_map = new HashMap<>();

	static String WEIXIN_LOGIN_URL = "https://mp.weixin.qq.com/cgi-bin/bizlogin?action=startlogin";
	
	static String WEIXIN_LOGIN_QRCODE = "https://mp.weixin.qq.com/cgi-bin/loginqrcode?action=getqrcode&param=4300&rd=";
	
	static String WEIXIN_LOGIN_TASK = "https://mp.weixin.qq.com/cgi-bin/loginqrcode?action=ask&token=&lang=zh_CN&f=json&ajax=1";

	static String WEIXIN_LOGIN_TOKEN = "https://mp.weixin.qq.com/cgi-bin/bizlogin?action=login";

	static Random random = new Random();

	
	public static String login() {
		Map<String, Object> post_map = new HashMap<>();
		post_map.put("username", WebContextListener.weixinProperPo.getWeixin_username());
		post_map.put("pwd", WebContextListener.weixinProperPo.getWeixin_password());
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
			@SuppressWarnings("unchecked")
			Map<String, Object> map = JSONObject.parseObject(login.body(), Map.class);
			if (map != null && map.get("redirect_url") != null) {
				return WEIXIN_LOGIN_URL;
			}
		}
		return null;
	}

	
	public static void getQrcode(String file_path, String referer_url) {
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
		code.writeBody(new File(file_path));
	}

	
	public static Integer getQrcodeState(String referer_url) {
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
		log.debug(task.body());
		if (task.getStatus() == 200) {
			for (HttpCookie cookie : task.getCookies()) {
				cookie_map.put(cookie.getName(), cookie.getValue());
			}
			Map<?, ?> map = JSONObject.parseObject(task.body(), Map.class);
			if (map != null && map.get("status") != null) {
				if (Integer.valueOf(map.get("status") + "") == 4) {
					System.out.println(" 4 ==> task.getCookies() ["+task.getCookies()+"]");
				}else if (Integer.valueOf(map.get("status") + "") == 1) {
					System.out.println(" 1 ==> task.getCookies() ["+task.getCookies()+"]");
				}
				return Integer.valueOf(map.get("status") + "");
			}
		}
		return null;
	}

	public static String getToken(String referer_url) {
		Map<String, Object> post_map = new HashMap<>();
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

		headers.put("Referer", referer_url);
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		HttpResponse token = HttpRequest.post(WEIXIN_LOGIN_TOKEN).form(post_map).addHeaders(headers)
				.setFollowRedirects(true).cookie("").execute();
		log.debug(token.body());
		if (token.getStatus() == 200) {
			for (HttpCookie cookie : token.getCookies()) {
				cookie_map.put(cookie.getName(), cookie.getValue());
			}
			System.out.println("token.getCookies() is ["+token.getCookies()+"]");
			Map<?, ?> map = JSONObject.parseObject(token.body(), Map.class);
			if (map != null && map.get("redirect_url") != null) {
				String url = map.get("redirect_url") + "";
				String token_str = getParam(url, "token");
				if (StrUtil.isNotBlank(token_str)) {
					setCookie(referer_url, "https://mp.weixin.qq.com" + url);
					return token_str;
				}
			}
		}
		return null;
	}

	public static void setCookie(String referer_url, String url) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");

		headers.put("Referer", referer_url);
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		StringBuffer cookie_buffer = new StringBuffer();
		for (String key : cookie_map.keySet()) {
			cookie_buffer.append(key).append("=").append(cookie_map.get(key)).append(";");
		}
		HttpResponse cookie = HttpRequest.get(url).addHeaders(headers).setFollowRedirects(true)
				.cookie(cookie_buffer.toString()).execute();
		if (cookie.getStatus() == 200) {
			cookie_map.clear();
			for (HttpCookie cookie_ : cookie.getCookies()) {
				cookie_map.put(cookie_.getName(), cookie_.getValue());
			}
		}
	}

	public static String getCookies() {
		StringBuffer cookie_buffer = new StringBuffer();
		for (String key : cookie_map.keySet()) {
			cookie_buffer.append(key).append("=").append(cookie_map.get(key)).append("; ");
		}

		return cookie_buffer.toString();
	}

	
	public static String getParam(String url, String name) {
		url += "&";
		String pattern = "(\\?|&){1}#{0,1}" + name + "=[a-zA-Z0-9]*(&{1})";

		Pattern r = Pattern.compile(pattern);

		Matcher m = r.matcher(url);
		if (m.find()) {
			return m.group(0).split("=")[1].replace("&", "");
		} else {
			return null;
		}
	}
	
	public static PublicAccountPo getPublicAccountPo(String token, String publicAccountName) {
		PublicAccountPo publicAccountPo = new PublicAccountPo();
		Map<String, Object> post_map = new HashMap<>();
		post_map.put("action", "search_biz");
		post_map.put("begin", 0);
		post_map.put("count", 10);
		post_map.put("query", publicAccountName);
		post_map.put("token", token);
		post_map.put("lang", "zh_CN");
		post_map.put("f", "json");
		post_map.put("ajax", 1);

		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");

		headers.put("Referer", "https://mp.weixin.qq.com/cgi-bin/appmsg?t=media/appmsg_edit_v2&action=edit&isNew=1&type=10&token="+token+"&lang=zh_CN");
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		
		HttpResponse publicAccountInfo = HttpRequest.get("https://mp.weixin.qq.com/cgi-bin/searchbiz").addHeaders(headers).form(post_map).execute();
		
		log.debug(publicAccountInfo.body());
		if (publicAccountInfo.getStatus() == 200) {
			JSONObject obj = JSONObject.parseObject(publicAccountInfo.body());
			String base_resp = obj.getString("base_resp");
			JSONObject obj1 = JSONObject.parseObject(base_resp);
			if ((obj1.getInteger("ret")+0) == 0) {
				JSONArray resultArray = JSONArray.parseArray(obj.getString("list"));
				for (Object res : resultArray) {
					JSONObject resObj = JSONObject.parseObject(res+"");
					
						if (resObj.getString("nickname").equals(publicAccountName)) {
							publicAccountPo = JSONObject.parseObject(res+"", PublicAccountPo.class);
							log.debug(publicAccountPo.toString());
							
						}
					
					
				}
			}else if ((obj1.getInteger("ret")+0) == 200013) {
				FREDCONTROL  = true;
				MailUtil.sendMail(WECHATERRORMSG);
			}else {
				FREDCONTROL  = true;
				MailUtil.sendMail("微信公众号cookie失效");
			}
		}
		
		return publicAccountPo;
	}
	
	public static void refresh(String token) {
		try {
			Map<String, String> headers = new HashMap<>();
			headers.put("Accept-Encoding", "gzip, deflate, br");
			headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
			headers.put("Connection", "keep-alive");
	
			headers.put("Referer", "https://mp.weixin.qq.com/cgi-bin/frame?t=notification/index_frame&lang=zh_CN&token="+token);
			headers.put("X-Requested-With", "XMLHttpRequest");
			headers.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
			headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
			
			StringBuffer cookie_buffer = new StringBuffer();
			for (String key : cookie_map.keySet()) {
				cookie_buffer.append(key).append("=").append(cookie_map.get(key)).append(";");
			}
			HttpResponse result = HttpRequest.get("https://mp.weixin.qq.com/cgi-bin/settingpage?t=setting/index&action=index&token="+token+"&lang=zh_CN").addHeaders(headers)
					.cookie(cookie_buffer.toString()).execute();
			log.debug(result.body());
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static List<PublicAccountPo> getPublicAccountPos(String token, List<String> publicAccountName) {
		
		List<PublicAccountPo> pos = new ArrayList<PublicAccountPo>();
		for (String s : publicAccountName) {
			pos.add(getPublicAccountPo(token, s));
		}
		
		return pos;
	}
	
	
}
