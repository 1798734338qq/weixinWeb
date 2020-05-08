package cn.net.comsys.weixin.po;

import java.util.Arrays;
import java.util.List;

import cn.hutool.core.util.StrUtil;

public class WeixinProperPo {
	private String weixin_cookies;
	private String weixin_token;
	private String weixin_grad_fakeid;

	private String cookie_url;

	private String referer_url;
	
	private String latest_url;
	
	private String err_html = "";
	
	private List<String> weixin_error_tip_tos;
	
	private Integer session_min = 5;
	private Integer session_max = 25;
	
	private Integer latest_min = 1;
	private Integer latest_max = 3;
	
	private List<String> fakeids = null;
	
	private Integer freq_control_pageNumber = 1;
	
	private String weixin_username;
	private String weixin_password;
	
	public String getWeixin_username() {
		return weixin_username;
	}

	public void setWeixin_username(String weixin_username) {
		this.weixin_username = weixin_username;
	}

	public String getWeixin_password() {
		return weixin_password;
	}

	public void setWeixin_password(String weixin_password) {
		this.weixin_password = weixin_password;
	}

	public Integer getFreq_control_pageNumber() {
		return freq_control_pageNumber;
	}

	public void setFreq_control_pageNumber(Integer freq_control_pageNumber) {
		this.freq_control_pageNumber = freq_control_pageNumber;
	}

	public List<String> getFakeids() {
		return fakeids;
	}

	public void setFakeids(List<String> fakeids) {
		this.fakeids = fakeids;
	}

	public Integer getLatest_min() {
		return latest_min;
	}

	public void setLatest_min(Integer latest_min) {
		this.latest_min = latest_min;
	}

	public Integer getLatest_max() {
		return latest_max;
	}

	public void setLatest_max(Integer latest_max) {
		this.latest_max = latest_max;
	}

	public Integer getSession_min() {
		return session_min;
	}

	public void setSession_min(Integer session_min) {
		this.session_min = session_min;
	}

	public Integer getSession_max() {
		return session_max;
	}

	public void setSession_max(Integer session_max) {
		this.session_max = session_max;
	}

	public String getErr_html() {
		return err_html;
	}

	public void setErr_html(String err_html) {
		this.err_html = err_html;
	}

	public List<String> getWeixin_error_tip_tos() {
		return weixin_error_tip_tos;
	}

	public void setWeixin_error_tip_tos(List<String> weixin_error_tip_tos) {
		this.weixin_error_tip_tos = weixin_error_tip_tos;
	}

	public String getWeixin_cookies() {
		return weixin_cookies;
	}

	public void setWeixin_cookies(String weixin_cookies) {
		this.weixin_cookies = weixin_cookies;
	}

	public String getWeixin_token() {
		return weixin_token;
	}

	public void setWeixin_token(String weixin_token) {
		this.weixin_token = weixin_token;
	}

	public String getWeixin_grad_fakeid() {
		return weixin_grad_fakeid;
	}

	public void setWeixin_grad_fakeid(String weixin_grad_fakeid) {
		this.weixin_grad_fakeid = weixin_grad_fakeid;
		if(StrUtil.isNotBlank(weixin_grad_fakeid)) {
			this.fakeids = Arrays.asList(weixin_grad_fakeid.split(","));
		}
	}

	public String getCookie_url() {
		return cookie_url;
	}

	public void setCookie_url(String cookie_url) {
		this.cookie_url = cookie_url;
	}

	public String getReferer_url() {
		return referer_url;
	}

	public void setReferer_url(String referer_url) {
		this.referer_url = referer_url;
	}

	public String getLatest_url() {
		return latest_url;
	}

	public void setLatest_url(String latest_url) {
		this.latest_url = latest_url;
	}
	
}
