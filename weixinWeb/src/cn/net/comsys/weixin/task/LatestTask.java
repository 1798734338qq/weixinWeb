package cn.net.comsys.weixin.task;

import java.util.Random;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.po.WeixinProperPo;
import cn.net.comsys.weixin.util.WeixinConmment;

public class LatestTask implements Task {
	private Log log = LogFactory.get();
	WeixinProperPo properPo = new WeixinProperPo();

	public LatestTask(WeixinProperPo properPo) {
		this.properPo = properPo;
	}

	@Override
	public void execute() {
		try {
			CronUtil.remove("referer_latest");
		} catch (Exception ex) {
		}
		if (!WeixinConmment.ERROR_STATE) {
			if (properPo.getFakeids() != null && properPo.getFakeids().size() > 0) {
				try {
					WeixinConmment.latest(properPo);
					if (WeixinConmment.freq_control) {
						referTimer("0 0 0/4 * * *");
					} else {
						referTimer(null);
					}
				} catch (Exception e) {
					log.error(e);
					WeixinConmment.errTip(properPo);
				}
			} else {
				referTimer(null);
			}
		}else {
		}
	}

	/**
	 * ����ˢ�����񵹼�ʱ
	 */
	private void referTimer(String corn_i) {
		Random rand = new Random();
		// MIN �� MAX ��Χ�ڵ������
		String corn = "0 0 */"
				+ (rand.nextInt(properPo.getLatest_max() - properPo.getLatest_min() + 1) + properPo.getLatest_min())
				+ " * * *";
		if (StrUtil.isNotBlank(corn_i)) {
			corn = corn_i;
			WeixinConmment.freq_control = false;
		}
		log.info("��ȡ�������ݳɹ����´λ�ȡ�����������ڡ�{}����", corn);
		CronUtil.schedule("referer_latest", corn, new LatestTask(properPo));
	}

}
