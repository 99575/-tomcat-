package com.yc.web.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yc.tomcat.core.TomcatConstants;

public class HttpServletRequest implements ServletRequest {
	private String method; // 请求方式
	private Map<String, String> parameter = new HashMap<String, String>();
	private String url;// 请求资源地址
	private String protocalVersion;
	private InputStream is;// 请求流
	private BufferedReader read;

	private volatile HttpSession session = new HttpSession();
	private boolean checkJSessionId = false;
	private String jsessionid;
	private Cookie[] cookies;

	public HttpServletRequest(InputStream is) {
		this.is = is;
	}

	@Override
	public void parse() {
		try {
			read = new BufferedReader(new InputStreamReader(is));
			String line = null;
			List<String> headStrs = new ArrayList<String>();
			while ((line = read.readLine()) != null && !"".equals(line)) {//
				headStrs.add(line);
			}
			parseFristLine(headStrs.get(0));// 解析起始行
			parseParameter(headStrs);// 解析获取参数

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 获取参数
	private void parseParameter(List<String> headStrs) {
		// 处理请求地址中的参数
		String str = headStrs.get(0).split(" ")[1];// 请求地址
		if (str.contains("?")) {
			str = str.substring(str.indexOf("?") + 1);// 获取请求参数
			String[] params = str.split("&");
			String[] temp;
			for (String param : params) {
				temp = param.split("=");
				try {
					this.parameter.put(temp[0], URLDecoder.decode(temp[1],"utf-8"));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
		}

		if (TomcatConstants.REQUEST_METHOD_POST.equals(this.method)) {// 说明是post请求
			int len = 0;
			for (String head : headStrs) {
				if (head.contains("Content-Length:")) {
					len = Integer.parseInt((head.substring(head.indexOf(":") + 1)).trim());
					break;
				}
			}

			if (len <= 0) {
				return;
			}

			// 如果要处理文件 则还需要content-Type头部字段
			// System.out.println(123);
			try {
				
				char[] ch = new char[1024 * 10];
				int total = 0, count = 0;//   count是每一次读到的大小，total是读到的总大小
				StringBuffer sbf=new StringBuffer();
				while ((count = read.read(ch)) != -1) {
					System.out.println(ch.toString());
					sbf.append(ch,0,count);
					total += count;
					if (total >= len) {
						break;
					}
				}
				
				str= URLDecoder.decode(sbf.toString(),"UTF-8");
				//System.out.println(str);
				//str = str.substring(str.indexOf("?") + 1);// }取请求参数
				String[] params = str.split("&");
				String[] temp;
				for (String param : params) {
					temp = param.split("=");
					this.parameter.put(temp[0], temp[1]);
				}

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		//System.out.println("parameter" + this.parameter);
	}

	// 解析起始行
	private void parseFristLine(String str) {
		String[] strs = str.split(" ");
		this.method = strs[0];
		if (strs[1].contains("?")) {
			this.url = strs[1].substring(0, strs[1].indexOf("?"));
		} else {
			this.url = strs[1];
		}
		this.protocalVersion = strs[2];

	}

	@Override
	public String getParameter(String key) {
		return this.parameter.getOrDefault(key, null);
	}

	@Override
	public String getUrl() {
		return this.url;
	}

	@Override
	public String getMethod() {
		return this.method;
	}

	@Override
	public HttpSession getSession() {
		return this.session;
	}

	@Override
	public Cookie[] getCookies() {
		return this.cookies;
	}

	@Override
	public boolean checkJSessionId() {
		return this.checkJSessionId;
	}

	@Override
	public String getJSessionId() {
		return this.jsessionid;
	}

	public String getProtocalVersion() {
		return protocalVersion;
	}

}
