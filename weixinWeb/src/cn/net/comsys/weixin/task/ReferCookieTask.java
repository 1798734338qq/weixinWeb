package cn.net.comsys.weixin.task;

import java.util.Random;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.po.WeixinProperPo;
import cn.net.comsys.weixin.util.WeixinConmment;

public class ReferCookieTask implements Task {
	private Log log = LogFactory.get();
	WeixinProperPo properPo = new WeixinProperPo();

	public ReferCookieTask(WeixinProperPo properPo) {
		this.properPo = properPo;
	}

	@Override
	public void execute() {
		log.info("执行刷新微信后台管理端session任务");
		String text = WeixinConmment.refererCookie(properPo);
		if (StrUtil.isNotBlank(text)) {
			if (text.indexOf("发布") == -1) {
				log.error(text);
				log.error("刷新session失败，cookie已失效，请联系管理员！");
				CronUtil.remove("referer_seesion");
				WeixinConmment.errTip(properPo);
			} else {
				log.debug("刷新session接口返回数据【{}】", text);
				referTimer();
			}
		} else {
			log.error(text);
			log.error("刷新session失败，cookie已失效，请联系管理员！");
			CronUtil.remove("referer_seesion");
			WeixinConmment.errTip(properPo);
		}
	}

	/**
	 * 重置刷新任务倒计时
	 */
	private void referTimer() {
		Random rand = new Random();
		// 生成随机 5 ~ 26 之间数字，公式rand.nextInt(MAX - MIN + 1) + MIN; // randNumber 将被赋值为一个
		// MIN 和 MAX 范围内的随机数
		String corn = "0 */"
				+ (rand.nextInt(properPo.getSession_max() - properPo.getSession_min() + 1) + properPo.getLatest_min())
				+ " * * * *";
		log.info("刷新session成功，下次刷新seession周期【{}】！", corn);
		CronUtil.remove("referer_seesion");
		CronUtil.schedule("referer_seesion", corn, new ReferCookieTask(properPo));
	}

}
