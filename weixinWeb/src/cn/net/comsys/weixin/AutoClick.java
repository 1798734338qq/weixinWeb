package cn.net.comsys.weixin;

import cn.hutool.core.lang.Console;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

public class AutoClick {
	public static void main(String[] args) {
//		HttpResponse login = HttpRequest.post(WEIXIN_LOGIN_URL)
//				.setFollowRedirects(true).execute();
		
		
		
		
		CronUtil.setMatchSecond(true);
		CronUtil.start(true);
	}
}
