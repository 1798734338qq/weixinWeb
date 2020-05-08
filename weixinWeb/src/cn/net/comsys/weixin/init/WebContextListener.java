package cn.net.comsys.weixin.init;

import java.util.Map;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.hutool.setting.dialect.Props;
import cn.net.comsys.weixin.NewRun;
import cn.net.comsys.weixin.po.WeixinProperPo;

@WebListener
public class WebContextListener implements ServletContextListener {
	Props props = new Props("config.properties");
	private Log log = LogFactory.get();
	public static WeixinProperPo weixinProperPo = new WeixinProperPo();
	public static Map<String, Object> CACHE_CONFIG = null;

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {

	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		
		CronUtil.schedule("0 0 14 * * *", new Task() {
		    @Override
		    public void execute() {
		        System.out.println("1111");
		        for (int i = 0; i < 100; i++) {
					new Thread(new NewRun()).run();
				}
		    }
		});
//		try {
//			Number number = Db.use()
//					.queryNumber("select count(1) from sqlite_master where type = 'table' and name = 't_article'");
//			if (number.intValue() <= 0) {
//				Db.use().execute(
//						"create table t_article(aid text,title text,item_show_type text,appmsgid text,link text,itemidx text,digest text,update_time text,cover text,fakeid text)");
//			}
//			Number config = Db.use()
//					.queryNumber("select count(1) from sqlite_master where type = 'table' and name = 't_config'");
//			if (config.intValue() <= 0) {
//				Db.use().execute(
//						"create table t_config(weixin_token text,u text,weixin_grad_fakeid text,weixin_username text,weixin_password text)");
//			} else {
//				try {
//					CACHE_CONFIG = Db.use().query("select * from t_config", Map.class).get(0);
//				} catch (Exception e) {
//					log.error("���ݿ�������Ч��������Ϣ");
//				}
//			}
//		} catch (Exception e) {
//			log.error("���ݿ��ʼ��ʧ�ܣ�");
//			log.error(e);
//		}
//
//		weixinProperPo.setCookie_url(props.getProperty("weixin_cookie_url",
//				"https://mp.weixin.qq.com/misc/faq?action=getfaq&lang=zh_CN&f=json&cginame=cgi-bin/home&token<@>token<@>&t=home/index"));
//		weixinProperPo.setReferer_url(props.getProperty("weixin_referer_url",
//				"https://mp.weixin.qq.com/cgi-bin/home?t=home/index&lang=zh_CN&token=<@>token<@>"));
//		weixinProperPo.setLatest_url(props.getProperty("weixin_latest_url",
//				"https://mp.weixin.qq.com/cgi-bin/appmsg?token=<@>token<@>&lang=zh_CN&f=json&ajax=1&random=<@>random<@>&action=list_ex&begin=<@>begin<@>&count=<@>pageSize<@>&query=&fakeid=<@>fakeid<@>&type=9"));
//
//		weixinProperPo.setWeixin_cookies(props.getProperty("weixin_cookies"));
//		weixinProperPo.setWeixin_token(props.getProperty("weixin_token"));
//		weixinProperPo.setWeixin_grad_fakeid(props.getProperty("weixin_grad_fakeid"));
//		weixinProperPo.setWeixin_username(props.getProperty("weixin_username"));
//		weixinProperPo.setWeixin_password(props.getProperty("weixin_password"));
//		if (CACHE_CONFIG != null) {
//			weixinProperPo.setWeixin_cookies(CACHE_CONFIG.get("weixin_cookies") + "");
//			weixinProperPo.setWeixin_token(CACHE_CONFIG.get("weixin_token") + "");
//			weixinProperPo.setWeixin_grad_fakeid(CACHE_CONFIG.get("weixin_grad_fakeid") + "");
//			weixinProperPo.setWeixin_username(CACHE_CONFIG.get("weixin_username") + "");
//			weixinProperPo.setWeixin_password(CACHE_CONFIG.get("weixin_password") + "");
//		}
//
//		String session_timer = props.getProperty("weixin_session_timer", "5-25");
//		String[] session_timer_array = session_timer.split("-");
//		if (session_timer_array.length == 2) {
//			weixinProperPo.setSession_min(Integer.valueOf(session_timer_array[0]));
//			weixinProperPo.setSession_max(Integer.valueOf(session_timer_array[1]));
//		}
//
//		String latest_timer = props.getProperty("weixin_latest_timer", "1-3");
//		String[] latest_timer_array = latest_timer.split("-");
//		if (latest_timer_array.length == 2) {
//			weixinProperPo.setLatest_min(Integer.valueOf(latest_timer_array[0]));
//			weixinProperPo.setLatest_max(Integer.valueOf(latest_timer_array[1]));
//		}
//
//		String tos = props.getProperty("weixin_error_tip_tos", "luoyin@comsys.net.cn");
//		weixinProperPo.setWeixin_error_tip_tos(Arrays.asList(tos.split(";")));
//
//		if (CACHE_CONFIG != null) {
//			log.debug("���ݿ��д���������Ϣ������ץȡ���������������Ϣ��{}��", JSONUtil.toJsonStr(CACHE_CONFIG));
//			Random rand = new Random();
//			String corn_1 = "0 */1 * * * *";
//			log.info("������ȡ�������ݡ�{}������", corn_1);
//			CronUtil.schedule("referer_latest", corn_1, new LatestTask(weixinProperPo));
//		}
//
		CronUtil.setMatchSecond(true);
		CronUtil.start(true);
	}

}
