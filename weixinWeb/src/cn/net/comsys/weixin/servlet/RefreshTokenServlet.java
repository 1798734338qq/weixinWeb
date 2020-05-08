package cn.net.comsys.weixin.servlet;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
import cn.hutool.setting.dialect.Props;
import cn.net.comsys.weixin.init.WebContextListener;
import cn.net.comsys.weixin.runnable.SodierPublicAccountRunnable;
import cn.net.comsys.weixin.util.ReadWechatPublicAccountUtil;
import cn.net.comsys.weixin.util.WeixinAuthUtil;

@WebServlet(name = "refreshTokenServlet", urlPatterns = "/refresh/token", loadOnStartup = 1)
public class RefreshTokenServlet extends HttpServlet {
	private Log log = LogFactory.get();
	private static final long serialVersionUID = 1L;
	private String redirect_url = "";

	static Props props = new Props("config.properties");
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		String type = req.getParameter("type");
		String username = props.getProperty("weixin_username");
		String password = props.getProperty("weixin_password");
		if (StrUtil.isBlank(type)) {
			type = "1";
		}
		Map<String, Object> resp_map = new HashMap<>();
		String path = req.getServletContext().getRealPath("/") + File.separator + "code" + File.separator;
		File root_file = new File(path);
		if (!root_file.exists()) {
			root_file.mkdirs();
		}
		resp_map.put("state", true);
		try {
			String uuid = UUID.randomUUID().toString();
			String new_code_img_path = path + uuid + ".png";
			String code_img_url = "/code/" + uuid + ".png";
			switch (type) {
			case "1":
				try {
					if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password)) {
						WebContextListener.weixinProperPo.setWeixin_username(username);
						WebContextListener.weixinProperPo.setWeixin_password(password);
					}
					redirect_url = WeixinAuthUtil.login();
					if (StrUtil.isNotBlank(redirect_url)) {
						WeixinAuthUtil.getQrcode(new_code_img_path, redirect_url);
						resp_map.put("datas", code_img_url);
					}else {
						resp_map.put("state", false);
					}
				} catch (Exception e) {
					log.error(e);
					resp_map.put("state", false);
				}
				break;
			case "2":
				try {
					WeixinAuthUtil.getQrcode(new_code_img_path, redirect_url);
					resp_map.put("datas", code_img_url);
				} catch (Exception e) {
					log.error(e);
					resp_map.put("state", false);
				}
				break;
			case "3":
				try {
					resp_map.put("datas", WeixinAuthUtil.getQrcodeState(redirect_url));
				} catch (Exception e) {
					log.error(e);
					resp_map.put("state", false);
				}
				break;
			case "4":
				try {
					String token = WeixinAuthUtil.getToken(redirect_url);
					String xmlpath = this.getClass().getResource("/").getPath()+"weChatPublicAccount.xml";
					List<String> publicAccountName = ReadWechatPublicAccountUtil.parseDocument(xmlpath);
					WeixinAuthUtil.FREDCONTROL = false;
					
					ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);
			        
					scheduledExecutorService.scheduleAtFixedRate( new SodierPublicAccountRunnable(token, publicAccountName), 0, 1, TimeUnit.DAYS);
					
					scheduledExecutorService.scheduleAtFixedRate(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							WeixinAuthUtil.refresh(token);
						}
					}, 2, 2, TimeUnit.HOURS);

				} catch (Exception e) {
					log.error(e);
					resp_map.put("state", false);
				}
				break;
			default:
				break;
			}

		} catch (Exception e) {
			log.error(e);
		}

		resp.getWriter().write(JSONUtil.toJsonStr(resp_map));
	}


}
