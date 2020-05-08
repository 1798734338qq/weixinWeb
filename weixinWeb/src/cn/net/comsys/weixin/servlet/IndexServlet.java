package cn.net.comsys.weixin.servlet;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.cron.CronUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.init.WebContextListener;
import cn.net.comsys.weixin.task.LatestTask;

@WebServlet(name = "setCron", urlPatterns = "/setCron", loadOnStartup = 1)
public class IndexServlet extends HttpServlet {
	private Log log = LogFactory.get();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		Map<String, Object> res_map = new HashMap<>();
		res_map.put("state", false);
		res_map.put("msg", "重新启动抓取任务成功");
		try {
			try {
				CronUtil.remove("referer_latest");
			} catch (Exception e) {
			}
			String corn_1 = "0 */1 * * * *";
			log.info("重新启动获取文章数据【{}】任务！", corn_1);
			CronUtil.schedule("referer_latest", corn_1, new LatestTask(WebContextListener.weixinProperPo));
			res_map.put("state", true);
		} catch (Exception e) {
			res_map.put("msg", "重新启动抓取任务失败，错误信息：" + e.getLocalizedMessage());
		}
	}
}
