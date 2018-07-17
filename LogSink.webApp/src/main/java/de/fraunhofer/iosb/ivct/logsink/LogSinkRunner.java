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

package de.fraunhofer.iosb.ivct.logsink;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nato.ivct.commander.Factory;

/**
 * Servlet implementation class LogSinkRunner
 */
public class LogSinkRunner extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static ReportEngine reportEngine = null;
	private static LogSink logSink;


	/**
	 * 
	 */
	public LogSinkRunner() {
		super();
	}

	public String getTestCaseResults() {
		return reportEngine.status.toString();
	}

	/**
	 * 
	 */
	public void init(ServletConfig config) throws ServletException {
		reportEngine = new ReportEngine();
		(Factory.createCmdStartTestResultListener(reportEngine)).execute();
		(Factory.createCmdQuitListener(reportEngine)).execute();
		logSink = new LogSink();
		logSink.init();
	}

	/**
	 * 
	 */
	public void destroy() {
	}

	/**
	 * 
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		response.getWriter().append("Log Sink is running at: ").append(request.getContextPath());
	}


	/**
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

}
