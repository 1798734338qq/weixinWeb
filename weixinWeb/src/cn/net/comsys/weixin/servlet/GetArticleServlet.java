package cn.net.comsys.weixin.servlet;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.util.StrUtil;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.db.Page;
import cn.hutool.db.handler.RsHandler;
import cn.hutool.db.sql.Direction;
import cn.hutool.db.sql.Order;
import cn.hutool.json.JSONUtil;

@WebServlet(name = "getArticleServlet", urlPatterns = "/getArticle", loadOnStartup = 1)
public class GetArticleServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.sendError(403);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setContentType("text/html;charset=UTF-8");
		Map<String, Object> res_map = new HashMap<>();
		String fakeid = req.getParameter("fakeid");
		int pageNumber = 1;
		int pageSize = 10;
		boolean ispage = false;
		if (req.getParameter("pageNumber") != null && req.getParameter("pageSize") != null) {
			ispage = true;
			pageNumber = Integer.valueOf(req.getParameter("pageNumber"));
			pageSize = Integer.valueOf(req.getParameter("pageSize"));
		}

		res_map.put("datas", "[]");
		res_map.put("state", false);
		if (StrUtil.isNotBlank(fakeid)) {
			List<Map> all = null;
			try {
				if (ispage) {
					Number count = Db.use().queryNumber("select count(1) from t_article where fakeid = ?", fakeid);
					res_map.put("total", count.intValue());
					res_map.put("pageNumber", pageNumber);
					res_map.put("pageSize", pageSize);
					Order order = new Order("update_time",Direction.DESC);
					all = Db.use().page(Entity.create("t_article").set("fakeid", fakeid),
							new Page(pageNumber, pageSize,order), new RsHandler<List<Map>>() {

								@Override
								public List<Map> handle(ResultSet res) throws SQLException {
									List<Map> list = new ArrayList();
									while (res.next()) {
										Map<String, Object> item = new HashMap<>();
										ResultSetMetaData data = res.getMetaData();
										for (int i = 0; i < data.getColumnCount(); i++) {
											String key = data.getColumnName(i + 1);
											item.put(key, res.getString(key));
										}
										list.add(item);
									}
									return list;
								}
							});
				} else {
					all = Db.use().query("select * from t_article where fakeid = ? order by update_time desc", Map.class, fakeid);
					res_map.put("total", all.size());
				}

				if (all != null) {
					res_map.put("datas", all);
					res_map.put("state", true);
				} else {
					res_map.put("state", true);
					res_map.put("msg", "无数据");
				}
			} catch (SQLException e) {
				e.printStackTrace();
				res_map.put("state", false);
				res_map.put("msg", "查询数据报错！");
			}
		} else {
			res_map.put("state", false);
			res_map.put("msg", "无效公众号ID");
		}

		resp.getWriter().write(JSONUtil.toJsonStr(res_map));
	}
}
