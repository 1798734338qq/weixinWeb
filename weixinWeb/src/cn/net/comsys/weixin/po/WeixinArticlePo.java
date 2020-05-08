package cn.net.comsys.weixin.po;

import java.io.Serializable;


public class WeixinArticlePo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	private String aid;
	
	private String cover;
	
	private String create_time;
	
	private String digest;
	
	private String link;
	
	private String title;
	
	private String update_time;
	
	private String pubulicAccount;

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public String getCreate_time() {
		return create_time;
	}

	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUpdate_time() {
		return update_time;
	}

	public void setUpdate_time(String update_time) {
		this.update_time = update_time;
	}

	public String getPubulicAccount() {
		return pubulicAccount;
	}

	public void setPubulicAccount(String pubulicAccount) {
		this.pubulicAccount = pubulicAccount;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "the WeixinArticlePo is => this.title is 【"+this.title+"】, this.pubulicAccount is 【"+this.pubulicAccount+"】, this.link is 【"+this.link+"】."  ;
	}	
}
