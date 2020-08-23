package com.yc.web.core;

import java.util.HashMap;
import java.util.Map;

public class HttpSession {
	public Map<String, Object> session = new HashMap<String, Object>();

	private String jsessionid = null;

	/**
	 * 往session中存值
	 *
	 * @author 养了一只杨羊羊
	 * @time 2020年8月22日上午11:26:19
	 *
	 * @param key
	 * @param value
	 */
	public void setAttribute(String key, Object value) {
		this.session.put(key, value);
		
		//刷新服务器里面的总session -》 一个sessionid对应一个session对象
		
		//更新最后一次访问的时间
	}

	/**
	 * 从session中根据键获取对应的值
	 * 
	 * @param key
	 * @return
	 */
	public Object getAttribute(String key) {
		if (!session.containsKey(key)) {
			return null;
		}
		return session.get(key);
	}

	public void setJsessionId(String jsession) {
		this.jsessionid = jsession;
	}
}
