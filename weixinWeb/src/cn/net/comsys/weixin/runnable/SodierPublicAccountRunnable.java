package cn.net.comsys.weixin.runnable;

import java.util.List;

import cn.hutool.db.Db;
import cn.hutool.db.Entity;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import cn.net.comsys.weixin.po.PublicAccountPo;
import cn.net.comsys.weixin.po.WeixinArticlePo;
import cn.net.comsys.weixin.util.WeixinArticleUtil;
import cn.net.comsys.weixin.util.WeixinAuthUtil;

public class SodierPublicAccountRunnable implements Runnable{

	private String token;
	
	private List<String> publicAccountName; 
	
	private Log log = LogFactory.get();
	public SodierPublicAccountRunnable(String token, List<String> publicAccountName) {
		// TODO Auto-generated constructor stub
		this.token = token;
		this.publicAccountName = publicAccountName;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		log.debug("SodierPublicAccountRunnable is running");
		for (String name : publicAccountName) {
			try {
				List<Entity> publicAccounts = Db.use().query("select * from T_GRAPE_WECHATPUBLICACCOUNT WHERE NICKNAME = ?", new Object[] {name});
				
				PublicAccountPo po = new PublicAccountPo();
				if (publicAccounts.size() == 0) {
					if (WeixinAuthUtil.FREDCONTROL == false) {
						po = WeixinAuthUtil.getPublicAccountPo(token, name);
						Entity entity = Entity.create("T_GRAPE_WECHATPUBLICACCOUNT").parseBean(po);
						Db.use().insert(entity);
						Thread.sleep(60000);
					}else {
						log.debug("账号被封禁了");
					}
					
				}else {
					Entity entity = publicAccounts.get(0);
					po.setAlias(entity.getStr("ALIAS"));
					po.setFakeid(entity.getStr("FAKEID"));
					po.setNickname(entity.getStr("NICKNAME"));
					po.setRound_head_img(entity.getStr("ROUND_HEAD_IMG"));
					po.setService_type(entity.getStr("SERVICE_TYPE"));
					
				}
				if (WeixinAuthUtil.FREDCONTROL == false) {
					List<WeixinArticlePo> pos = WeixinArticleUtil.getArticle(po.getFakeid(), token, name);
					for (WeixinArticlePo weixinArticlePo : pos) {
						Entity entity = Entity.create("T_GRAPE_WECHATARTICLE").parseBean(weixinArticlePo);
						List<?> data = Db.use().query("SELECT * FROM T_GRAPE_WECHATARTICLE WHERE AID = ? and TITLE = ? ", new Object[] {weixinArticlePo.getAid(), weixinArticlePo.getTitle()});
						if (data.size() == 0) {
							Db.use().insert(entity);
						}
						
					}
				}else {
					log.debug("账号被封禁了");
				}
				
				Thread.sleep(60000);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		log.debug("SodierPublicAccountRunnable end.");
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public List<String> getPublicAccountName() {
		return publicAccountName;
	}

	public void setPublicAccountName(List<String> publicAccountName) {
		this.publicAccountName = publicAccountName;
	}

	
	
}
