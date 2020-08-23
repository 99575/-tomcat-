package com.yc.tomcat.core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;

import com.yc.web.core.HttpServletRequest;
import com.yc.web.core.HttpServletResponse;
import com.yc.web.core.Servlet;
import com.yc.web.core.ServletRequest;
import com.yc.web.core.ServletResponse;

public class ServerService implements Runnable {
	private Socket sk = null;
	private InputStream is = null;
	private OutputStream os = null;

	public ServerService(Socket sk) {
		this.sk = sk;
	}

	@Override
	public void run() {
		try {
			this.is = sk.getInputStream();
			this.os = sk.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		// 处理请求
		ServletRequest request = new HttpServletRequest(is);

		// 解析请求
		request.parse();

		// 响应请求
		// 请求的servlet还是静态资源，那如何判断是动态资源呢﹖如果是要映射到恸态资源，则肯定会配置到对应项目的web.xml文件中
		// 所以，我们必须在服务器启动的时候就自动扫描每个项目下的web.xml文件，解析其中的映射配置
		String url = request.getUrl();
		
		// url= /DayFresh/ login?id=123&name=xs
		String urlstr = url.substring(1);// 去掉最前的/ ->DayFresh/login?id=123&name=x
		String projectName = urlstr.substring(0, urlstr.indexOf("/"));
		
		ServletResponse response = new HttpServletResponse("/"+projectName,os);

		// 是不是动态资源地址
		String clazz = ParseUrlPattern.getClass(url);// 如果能取到处理类，则说明是动态资源
		if (clazz == null || "".equals(clazz)) { // 当成静态资源访问
			response.sendRedirect(url);
			return;
		}
		/*
		 * 处理动态资源 我的规则﹔所有的动态资源处理代码->servlet代码必须放到当期项目下面的bin目录中
		 */
		// 要动态的加载当前访的这个项目下面的bin目录中的类
		URLClassLoader loader = null; // 类加载器
		URL classpath = null; // 要加载的这个类的地址

		try {
			classpath = new URL("file", null, TomcatConstants.BASE_PATH + "\\" + projectName + "\\bin");

			// 创建一个类加载器，告诉它到这个路径下加载类
			loader = new URLClassLoader(new URL[] { classpath });

			// 通过类加载器，加载我们需要的这个类->是一个我们自己定义的saxMl95类
			Class<?> cls = loader.loadClass(clazz);
			//System.out.println(clazz);
			Servlet servlet = (Servlet) cls.newInstance(); // 实例化这个类

			// 将这个请求交给Servlet的service ()方法处理
			servlet.service(request, response);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送500错误码
	 *
	 * @author 养了一只杨羊羊
	 * @time 2020年8月21日上午11:03:56
	 *
	 * @param e
	 */
	private void send500(IOException e) {

	}

}
