package cn.net.comsys.weixin.util;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.json.JSONUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.init.WebContextListener;

public class DBUtil {
	static List<String> keys = new ArrayList<>();
	static List<String> keys_config = new ArrayList<>();
	private static Log log = LogFactory.get();
	static {
		keys.add("aid");
		keys.add("title");
		keys.add("item_show_type");
		keys.add("appmsgid");
		keys.add("link");
		keys.add("itemidx");
		keys.add("digest");
		keys.add("update_time");
		keys.add("cover");
		keys.add("fakeid");

		keys_config.add("weixin_token");
		keys_config.add("weixin_cookies");
		keys_config.add("weixin_grad_fakeid");
		keys_config.add("weixin_username");
		keys_config.add("weixin_password");
	}

	public static int addArticle(Map<String, Object> item) {
		try {
			String aid = getString(item.get("aid"), null);
			if (aid != null) {
				if (getArticleCount(aid) <= 0) {
					Entity entity = Entity.create("t_article");
					for (String key : keys) {
						entity.set(key, getString(item.get(key), ""));
					}
					return Db.use().insert(entity);
				}
			} else {
				log.error("错误数据，数据信息【{}】", JSONUtil.toJsonStr(item));
			}

		} catch (Exception e) {
			log.error("添加文章数据失败，错误信息：");
			log.error(e);
		}
		return 0;
	}

	public static void saveConfig() {
		int count = 0;
		try {
			Number number = Db.use().queryNumber("select * from t_config");
			if(number!=null) {
				count = number.intValue();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		if (count > 0) {
			try {
				Db.use().execute("delete from t_config");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		Entity entity = Entity.create("t_config");
		for (String key : keys_config) {
			entity.set(key, getString(WebContextListener.CACHE_CONFIG.get(key), ""));
		}
		try {
			Db.use().insert(entity);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static int getArticleCount(String aid) {
		try {
			Number number = Db.use().queryNumber("select count(1) from t_article where aid = ?", aid);
			return number.intValue();
		} catch (Exception e) {
			log.error("查询文章【{}】出错！", aid);
			log.error(e);
		}
		return 0;
	}

	public static List<String> getExistIds(String fakeid) {
		try {
			List<Entity> list = Db.use().query("select aid from t_article where fakeid = ?", fakeid);
			List<String> res_list = new ArrayList<>();
			for (Entity entity : list) {
				res_list.add(entity.getStr("aid"));
			}
			return res_list;
		} catch (Exception e) {
			log.error("查询文章【{}】，存在的文章ID出错！", fakeid);
			log.error(e);
		}
		return null;
	}

	private static String getString(Object obj1, String def) {
		try {
			if (obj1 != null) {
				return String.valueOf(obj1);
			}
		} catch (Exception e) {
			return def;
		}
		return def;
	}
}
