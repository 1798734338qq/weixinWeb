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
		log.info("ִ��ˢ��΢�ź�̨�����session����");
		String text = WeixinConmment.refererCookie(properPo);
		if (StrUtil.isNotBlank(text)) {
			if (text.indexOf("����") == -1) {
				log.error(text);
				log.error("ˢ��sessionʧ�ܣ�cookie��ʧЧ������ϵ����Ա��");
				CronUtil.remove("referer_seesion");
				WeixinConmment.errTip(properPo);
			} else {
				log.debug("ˢ��session�ӿڷ������ݡ�{}��", text);
				referTimer();
			}
		} else {
			log.error(text);
			log.error("ˢ��sessionʧ�ܣ�cookie��ʧЧ������ϵ����Ա��");
			CronUtil.remove("referer_seesion");
			WeixinConmment.errTip(properPo);
		}
	}

	/**
	 * ����ˢ�����񵹼�ʱ
	 */
	private void referTimer() {
		Random rand = new Random();
		// ������� 5 ~ 26 ֮�����֣���ʽrand.nextInt(MAX - MIN + 1) + MIN; // randNumber ������ֵΪһ��
		// MIN �� MAX ��Χ�ڵ������
		String corn = "0 */"
				+ (rand.nextInt(properPo.getSession_max() - properPo.getSession_min() + 1) + properPo.getLatest_min())
				+ " * * * *";
		log.info("ˢ��session�ɹ����´�ˢ��seession���ڡ�{}����", corn);
		CronUtil.remove("referer_seesion");
		CronUtil.schedule("referer_seesion", corn, new ReferCookieTask(properPo));
	}

}
