package cn.net.comsys.weixin.po;

import java.io.Serializable;

public class PublicAccountPo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String fakeid;
	
	private String nickname;
	
	private String alias;
	
	private String round_head_img;
	
	private String service_type;

	public String getFakeid() {
		return fakeid;
	}

	public void setFakeid(String fakeid) {
		this.fakeid = fakeid;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getAlias() {
		return alias;
	}

	public void setAlias(String alias) {
		this.alias = alias;
	}

	public String getRound_head_img() {
		return round_head_img;
	}

	public void setRound_head_img(String round_head_img) {
		this.round_head_img = round_head_img;
	}

	public String getService_type() {
		return service_type;
	}

	public void setService_type(String service_type) {
		this.service_type = service_type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "the PublicAccountPo contains => this.nickname is 【"+this.nickname+"】, this.alias is 【"+this.alias+"】 , this.fakeid is 【"+this.fakeid+"】";
	}
}
