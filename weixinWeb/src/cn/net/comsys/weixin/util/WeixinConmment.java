package cn.net.comsys.weixin.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.cache.file.LFUFileCache;
import cn.hutool.core.util.StrUtil;
import cn.hutool.db.DbUtil;
import cn.hutool.extra.mail.MailUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.init.WebContextListener;
import cn.net.comsys.weixin.po.WeixinProperPo;

public class WeixinConmment {
	private static Log log = LogFactory.get();
	static String url = "https://mp.weixin.qq.com/misc/faq?action=getfaq&lang=zh_CN&f=json&cginame=cgi-bin/home&token<@>token<@>&t=home/index";
	static String referer = "https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=<@>token<@>";
	static String cookie_strs = "RK=6fwkEKgxXz; pgv_pvi=1298967552; o_cookie=139811679; pgv_pvid=5756234688; _ga=GA1.2.1192382855.1521535228; ua_id=0ArHOJRJBYEWjviAAAAAAC8DTVw4UjkNGYavZEMV4aY=; ptcz=07861b6732edff932bba507a96bbfc14cfe68ac51af821b024f02d0fa49de10a; pac_uid=1_139811679; eas_sid=j175i2w6s2S0a2w7Z969Y2j5w5; pt2gguin=o0139811679; dm_login_weixin_rem=; dm_login_weixin_scan=; mm_lang=zh_CN; noticeLoginFlag=1; pgv_si=s2576541696; cert=JpdY4kRPcejiN3sgeOZhiksQcbQcWRCd; openid2ticket_oteAZtxVd1hfJr2kTV1XCUqOHY64=dNOPlepo2ZctEibJ5eOkiTFXQFADMbUEdxVThTu991U=; pgv_info=ssid=s1118890862; sig=h0177f907c5f631ff5e29873d201651a84c20065d90cfe2d13b8cfa228b2ece2f6f2ac443e88194d9d1; qm_authimgs_id=2; qm_verifyimagesession=h01aa95f11f5d1edc14f5d82945c0399100785b9c195027619e660be3c5df9c0aa9bf08e2963cded455; logout_page=dm_loginpage; uuid=300159dd07e7b20099fb59588a3d452d; data_bizuin=3071926453; bizuin=3261158173; data_ticket=sBOfYe6aQLIcsTVHrVZjFwEynV++qa4JcixZJWiZq9yD0pcKwlThbhIncTluZghC; slave_sid=cVgyTUtEU01Cd0lyaU5VZVpzN2JscVZBM2k1TkRVRHVtTjVadFdneHBGcXlGZXJKM3V0Z2wyQ1pTMlRfZmRpN1pZckM3NEN1SDZmUkVYeGVmYnhTQ3JxalhBQWl3NHM2eXZWS3RkMEFnVlRMZnY3OWNsWTE4TDJBdmZCdzhzM2pWN1AwV3VPWVRONDVtM1p2; slave_user=gh_17b0f6dcdf90; xid=4b3b9df032cab21aac951e18e453e6f8";
	static String get_news = "https://mp.weixin.qq.com/cgi-bin/appmsg?token=<@>token<@>&lang=zh_CN&f=json&ajax=1&random=<@>random<@>&action=list_ex&begin=<@>begin<@>&count=<@>pageSize<@>&query=&fakeid=<@>fakeid<@>&type=9";
	public static boolean ERROR_STATE = false;
	public static boolean freq_control = false;

	@SuppressWarnings("rawtypes")
	public static Map<String, List<Map>> CACHE_ARTICLE = new HashMap<>();

	private static Map<String, String> getHeaders(String referer) {
		Map<String, String> headers = new HashMap<>();
		headers.put("Accept-Encoding", "gzip, deflate, br");
		headers.put("Accept-Language", "zh-CN,zh;q=0.9,es;q=0.8,zh-TW;q=0.7");
		headers.put("Connection", "keep-alive");

		headers.put("Host", "mp.weixin.qq.com");
		headers.put("Referer", referer);
		headers.put("X-Requested-With", "XMLHttpRequest");
		headers.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/70.0.3538.102 Safari/537.36");
		headers.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
		return headers;
	}

	public static String refererCookie(WeixinProperPo properPo) {
		HttpResponse get = HttpRequest.get(properPo.getCookie_url()).setFollowRedirects(true)
				.addHeaders(getHeaders(properPo.getReferer_url())).cookie(properPo.getWeixin_cookies()).execute();
		properPo.setErr_html(get.body());
		if (get.getStatus() == 200) {
			return get.body();
		} else {
			return "��¼��ʱ";
		}
	}

	public static void latest(WeixinProperPo properPo) throws InterruptedException {
		Integer min_slepp_long = 30, max_sleep_long = 60;
		for (String fakeid : properPo.getFakeids()) {
			Random random = new Random();
			int i = 1;
			if (StrUtil.isNotBlank(fakeid)) {
				List<String> CACHE_TITLE = DBUtil.getExistIds(fakeid);

				List<Map> atr_list = new ArrayList<>();
				List<Map> page_item = getArtcle(i, 5, properPo, fakeid);
				boolean is_exit = false;
				while (page_item != null && page_item.size() > 0 && !is_exit) {
					if (page_item != null && page_item.size() > 0) {
						// atr_list.addAll(page_item);
						for (Map map : page_item) {
							String title = map.get("aid") + "";
							if (CACHE_TITLE.contains(title)) {
								
								is_exit = true;
								break;
							} else {
								CACHE_TITLE.add(title);
								atr_list.add(map);
							}
						}
						if (!is_exit) {
							int sleep_timer = random.nextInt(max_sleep_long - min_slepp_long + 1) + min_slepp_long;
							log.debug("���ߡ�{}����", sleep_timer);
							Thread.sleep(sleep_timer * 1000);
							i++;
							page_item = getArtcle(i, 5, properPo, fakeid);
						}
					}
					if (i > 5) {
						is_exit = true;
						log.debug("���ݽ϶࣬���ó�ʼ��������ѯ����");
					}
				}

				// List<Map> atr_list = getArtcle(1, 5, properPo, fakeid);
				for (Map map : atr_list) {
					map.put("fakeid", fakeid);
					DBUtil.addArticle(map);
				}
				log.debug("ץȡ���ں�ID��{}���ɹ����ӿڷ��ء�{}���������ݡ�{}��", fakeid, atr_list.size(), JSONUtil.toJsonStr(atr_list));
				if (!freq_control) {
					WebContextListener.weixinProperPo.setFreq_control_pageNumber(null);
				}
			} else {
				log.error("��Ч�Ĺ��ں�ID");
			}
		}
	}

	public static void latestAll(WeixinProperPo properPo, String fakeid, Integer min_slepp_long, Integer max_sleep_long)
			throws Exception {
		Random random = new Random();
		// �ӵ�һҲ��ʼ��ȡ
		int i = 1;
		if (StrUtil.isNotBlank(fakeid)) {
			if (WebContextListener.weixinProperPo.getFreq_control_pageNumber() != null) {
				i = WebContextListener.weixinProperPo.getFreq_control_pageNumber();
				log.debug("���ڽӿ����������ϴ�ץȡҳ����{}�������ڼ�����������ץȡ", i);
			}
			List<String> CACHE_TITLE = DBUtil.getExistIds(fakeid);

			List<Map> atr_list = new ArrayList<>();

			List<Map> page_item = getArtcle(i, 5, properPo, fakeid);
			boolean is_exit = false;
			while (page_item != null && page_item.size() > 0 && !is_exit) {
				if (page_item != null && page_item.size() > 0) {
					// atr_list.addAll(page_item);
					for (Map map : page_item) {
						String title = map.get("aid") + "";
						if (CACHE_TITLE.contains(title)) {
							log.error("���ػ����д����ظ����ݣ�ֹͣץȡ����");
							is_exit = true;
							break;
						} else {
							CACHE_TITLE.add(title);
							atr_list.add(map);
						}
					}
					if (!is_exit) {
						int sleep_timer = random.nextInt(max_sleep_long - min_slepp_long + 1) + min_slepp_long;
						log.debug("���ߡ�{}����", sleep_timer);
						Thread.sleep(sleep_timer * 1000);

						i++;
						page_item = getArtcle(i, 5, properPo, fakeid);
						WebContextListener.weixinProperPo.setFreq_control_pageNumber(i);
					}
				}
			}
			if (atr_list != null && atr_list.size() > 0) {
				for (Map map : atr_list) {
					map.put("fakeid", fakeid);
					DBUtil.addArticle(map);
				}
			}
			log.debug("ץȡ���ں�ID��{}���ɹ����ӿڷ��ء�{}���������ݡ�{}��", fakeid, atr_list.size(), JSONUtil.toJsonStr(atr_list));
			if (!freq_control) {
				WebContextListener.weixinProperPo.setFreq_control_pageNumber(null);
			}
		} else {
			log.error("��Ч�Ĺ��ں�ID");
		}
	}

	private static List<Map> getArtcle(Integer begin, Integer pageSize, WeixinProperPo properPo, String fakeid) {
		if (begin == null) {
			begin = 0;
		}
		if (pageSize == null) {
			pageSize = 5;
		}

		String url = new String(properPo.getLatest_url());
		url = url.replace("<@>begin<@>", (begin - 1) * pageSize + "");
		url = url.replace("<@>pageSize<@>", pageSize + "");
		url = url.replace("<@>random<@>", Math.random() + "");
		url = url.replace("<@>token<@>", properPo.getWeixin_token());
		url = url.replace("<@>fakeid<@>", fakeid);
		log.debug("��ʼץȡ�ڡ�{}��ҳ,URL��{}��", begin, url);

		HttpResponse get = HttpRequest.get(url).setFollowRedirects(true)
				.addHeaders(getHeaders(properPo.getReferer_url())).cookie(properPo.getWeixin_cookies()).execute();
		properPo.setErr_html(get.body());
		if (get.getStatus() == 200 && get.body().indexOf("invalid") == -1) {
			log.debug(get.body());
			if (get.body().indexOf("\"ret\":200013") != -1) {
				freq_control = true;
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> res_map = JSONObject.parseObject(get.body(), Map.class);
			if (res_map != null && res_map.get("app_msg_list") != null) {
				@SuppressWarnings("rawtypes")
				List<Map> atr_list = JSONArray.parseArray(res_map.get("app_msg_list").toString(), Map.class);
				log.debug("ץȡ�ڡ�{}��ҳ�ɹ����ӿڷ��ء�{}���������ݡ�{}��", begin, atr_list.size(),
						JSONUtil.toJsonStr(res_map.get("app_msg_list")));
				return atr_list;
			}
		} else {
			log.debug(get.body());
			ERROR_STATE = true;
			properPo.setErr_html("<font style='coloe:red'>΢�Ź��ں�sessionʧЧ������ɨ���¼��</font>");
			WeixinConmment.errTip(properPo);
			log.error("ץȡ���¡�{}��ʧ��,�ڡ���ҳʧ�ܣ�����ϵ����Ա", fakeid, begin);
			throw new RuntimeException("ץȡ����ʧ�ܣ�����sessionʧЧ����ϵ����Ա");
		}
		return null;
	}

	public static void errTip(WeixinProperPo properPo) {
		String context = properPo.getErr_html();
		if (StrUtil.isBlank(properPo.getErr_html())) {
			context = "ץȡ���ں�����ʧ��,����ʷ���������";
		}
		MailUtil.send(properPo.getWeixin_error_tip_tos(), "ץȡ���ں�����ʧ����ʾ", context, true);
	}
}
