package cn.net.comsys.weixin.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.init.WebContextListener;
import cn.net.comsys.weixin.task.LatestTaskAll;
import cn.net.comsys.weixin.util.WeixinConmment;

@WebServlet(name = "initArticleServlet", urlPatterns = "/initArticle", loadOnStartup = 1)
public class InitArticleServlet extends HttpServlet {

	private Log log = LogFactory.get();
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String fakeid = req.getParameter("fakeid");
		String min_slepp_long = req.getParameter("min_slepp_long");
		String max_slepp_long = req.getParameter("max_slepp_long");
		String continue_pageNumber = req.getParameter("continue_pageNumber");
		if(!WeixinConmment.ERROR_STATE) {
			if (StrUtil.isNotBlank(fakeid) && StrUtil.isNotBlank(min_slepp_long) && StrUtil.isNotBlank(max_slepp_long)) {
				String ts = "error";
				try {
					new Runnable() {

						@Override
						public void run() {
							try {
								if (StrUtil.isNotBlank(continue_pageNumber)) {
									WebContextListener.weixinProperPo
											.setFreq_control_pageNumber(Integer.valueOf(continue_pageNumber));
								} else {
									WebContextListener.weixinProperPo.setFreq_control_pageNumber(null);
								}
								CronUtil.schedule("referer_latest_all", "* */1 * * * *",
										new LatestTaskAll(WebContextListener.weixinProperPo, fakeid,
												Integer.valueOf(min_slepp_long), Integer.valueOf(max_slepp_long)));
//								WeixinConmment.latestAll(WebContextListener.weixinProperPo, fakeid,
//										Integer.parseInt(min_slepp_long), Integer.parseInt(max_slepp_long));
							} catch (Exception e) {
								log.error(e);
							}
						}
					}.run();
					ts = "ok";
				} catch (Exception e) {
					ts = "初始化失败！";
				}
				resp.getWriter().write(ts);
			} else {
				log.error("初始化公众号文章失败，请求参数信箱【{}】", JSONUtil.toJsonStr(req.getParameterMap()));
				resp.sendError(403);
			}
		}else {
			resp.getWriter().write("微信公众号session失效请重新扫描登录！");
		}
	}
}
