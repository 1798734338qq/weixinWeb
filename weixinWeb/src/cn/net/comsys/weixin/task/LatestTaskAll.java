package cn.net.comsys.weixin.task;

import java.util.Random;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.po.WeixinProperPo;
import cn.net.comsys.weixin.util.WeixinConmment;

public class LatestTaskAll implements Task {
	private Log log = LogFactory.get();
	WeixinProperPo properPo = new WeixinProperPo();
	String fakeid = "";
	Integer min_slepp_long = 60;
	Integer max_sleep_long = 80;

	public LatestTaskAll(WeixinProperPo properPo, String fakeid, Integer min_slepp_long, Integer max_sleep_long) {
		this.properPo = properPo;
		this.fakeid = fakeid;
		if (min_slepp_long != null) {
			this.min_slepp_long = min_slepp_long;
		}
		if (max_sleep_long != null) {
			this.max_sleep_long = max_sleep_long;
		}
	}

	@Override
	public void execute() {
		try {
			CronUtil.remove("referer_latest_all");
		} catch (Exception ex) {
		}
		if (!WeixinConmment.ERROR_STATE) {
			log.info("执行抓取所有公众号文章内容操作");
			if (properPo.getFakeids() != null && properPo.getFakeids().size() > 0) {
				try {
					WeixinConmment.latestAll(properPo, fakeid, min_slepp_long, max_sleep_long);
					if (WeixinConmment.freq_control) {
						log.debug("由于微信接口频繁访问限制，过4小时在进行数据抓取！");
						referTimer("0 0 */4 * * *");
					} else {
						referTimer(null);
					}
				} catch (Exception e) {
					log.error("执行抓取所有文章内容失败，cookie已失效，请联系管理员！");
					log.error(e);
					WeixinConmment.errTip(properPo);
				}
			} else {
				log.error("无有效的需抓取公众号信息！");
				referTimer(null);
			}
		} else {
			log.error("微信公众号session失效请重新扫描登录！");
		}
	}

	/**
	 * 重置刷新任务倒计时
	 */
	private void referTimer(String corn_i) {
		Random rand = new Random();
		// MIN 和 MAX 范围内的随机数
		String corn = "0 0 */"
				+ (rand.nextInt(properPo.getLatest_max() - properPo.getLatest_min() + 1) + properPo.getLatest_min())
				+ " * * *";
		if (StrUtil.isNotBlank(corn_i)) {
			corn = corn_i;
			WeixinConmment.freq_control = false;
		}
		log.info("获取文章数据成功，下次获取文章数据周期【{}】！", corn);
		CronUtil.schedule("referer_latest_all", corn,
				new LatestTaskAll(properPo, fakeid, min_slepp_long, max_sleep_long));
	}

}
