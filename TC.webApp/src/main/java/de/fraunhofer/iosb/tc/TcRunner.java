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
//import de.fraunhofer.iosb.testrunner.JMSTestRunner;

/**
 * Servlet implementation class TcRunner
 */
public class TcRunner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static boolean initialized = false;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public TcRunner() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getGreeting() {
		return "hi...";
	}

	/**
	 * 
	 */
	public void init(ServletConfig config) throws ServletException {
		// TODO Auto-generated method stub

		//JMSTestRunner.main(null);
		initialized = true;

	}

	/**
	 * 
	 */
	public void destroy() {
		// TODO Auto-generated method stub

		initialized = false;
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		if (initialized) {
			response.getWriter().append("Test Case Engine is running at: ").append(request.getContextPath());
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
