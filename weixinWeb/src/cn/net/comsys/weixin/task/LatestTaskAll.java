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
			log.info("ִ��ץȡ���й��ں��������ݲ���");
			if (properPo.getFakeids() != null && properPo.getFakeids().size() > 0) {
				try {
					WeixinConmment.latestAll(properPo, fakeid, min_slepp_long, max_sleep_long);
					if (WeixinConmment.freq_control) {
						log.debug("����΢�Žӿ�Ƶ���������ƣ���4Сʱ�ڽ�������ץȡ��");
						referTimer("0 0 */4 * * *");
					} else {
						referTimer(null);
					}
				} catch (Exception e) {
					log.error("ִ��ץȡ������������ʧ�ܣ�cookie��ʧЧ������ϵ����Ա��");
					log.error(e);
					WeixinConmment.errTip(properPo);
				}
			} else {
				log.error("����Ч����ץȡ���ں���Ϣ��");
				referTimer(null);
			}
		} else {
			log.error("΢�Ź��ں�sessionʧЧ������ɨ���¼��");
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
		CronUtil.schedule("referer_latest_all", corn,
				new LatestTaskAll(properPo, fakeid, min_slepp_long, max_sleep_long));
	}

}
