/* Copyright 2017, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package de.fraunhofer.iosb.tc;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.LoggerFactory;

import de.fraunhofer.iosb.testrunner.JMSTestRunner;
import nato.ivct.commander.Factory;

/**
 * Servlet implementation class TcRunner
 */
public class TcRunner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean initialized = false;
	public static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(Factory.class);

	/**
	 * 
	 */
	public TcRunner() {
		super();
		LOGGER.info("TcRunner instanciated");
	}
	
	public String getGreeting() {
		return "hi...";
	}

	/**
	 * 
	 */
	public void init(ServletConfig config) throws ServletException {
		JMSTestRunner.main(null);
		initialized = true;
		LOGGER.info("TcRunner initialized");
	}

	/**
	 * 
	 */
	public void destroy() {
		initialized = false;
		LOGGER.info("TcRunner destroyed");
	}

	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("TcRunner doGet");
		if (initialized) {
			response.getWriter().append("Test Case Engine is running at: ").append(request.getContextPath());
		}
	}

	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		LOGGER.info("TcRunner doPost");
		doGet(request, response);
	}

}
