package cn.net.comsys.weixin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.po.WeixinArticlePo;

public class WeixinArticleUtil {

	private static Log log = LogFactory.get();
	
	public static List<WeixinArticlePo> getArticle(String fakeid, String token, String publicAccountName) {
		List<WeixinArticlePo> po = new ArrayList<WeixinArticlePo>();
		Map<String, Object> post_map = new HashMap<>();
		post_map.put("action", "list_ex");
		post_map.put("begin", 0);
		post_map.put("count", 20);
		post_map.put("fakeid", fakeid);
		post_map.put("type", 9);
		post_map.put("query", "");
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
		
		HttpResponse response = HttpRequest.get("https://mp.weixin.qq.com/cgi-bin/appmsg").addHeaders(headers).form(post_map).execute();
		log.debug(response.body());
		if (response.getStatus() == 200) {
			JSONObject obj = JSONObject.parseObject(response.body());
			String base_resp = obj.getString("base_resp");
			JSONObject obj1 = JSONObject.parseObject(base_resp);
			if ((obj1.getInteger("ret")+0) == 0) {
				po = JSONArray.parseArray(obj.getString("app_msg_list"), WeixinArticlePo.class);
				for (WeixinArticlePo weixinArticlePo : po) {
					weixinArticlePo.setPubulicAccount(publicAccountName);
					weixinArticlePo.setDigest(null);
				}
			}else if ((obj1.getInteger("ret")+0) == 200013) {
				WeixinAuthUtil.FREDCONTROL  = true;
				MailUtil.sendMail(WeixinAuthUtil.WECHATERRORMSG);
			}else {
				WeixinAuthUtil.FREDCONTROL  = true;
				MailUtil.sendMail("微信公众号cookie失效");
			}
		}
		
		return po;
	}
}
