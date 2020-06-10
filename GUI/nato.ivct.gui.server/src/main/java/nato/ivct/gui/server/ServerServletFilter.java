/* Copyright 2020, Reinhard Herzog (Fraunhofer IOSB)

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License. */

package nato.ivct.gui.server;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.scout.rt.platform.BEANS;
import org.eclipse.scout.rt.server.commons.authentication.DevelopmentAccessController;
import org.eclipse.scout.rt.server.commons.authentication.ServiceTunnelAccessTokenAccessController;
import org.eclipse.scout.rt.server.commons.authentication.TrivialAccessController;
import org.eclipse.scout.rt.server.commons.authentication.TrivialAccessController.TrivialAuthConfig;

/**
 * <h3>{@link ServerServletFilter}</h3> This is the main server side servlet
 * filter.
 *
 * @author hzg
 */
public class ServerServletFilter implements Filter {

	private TrivialAccessController mTrivialAccessController;
	private ServiceTunnelAccessTokenAccessController mTunnelAccessController;
	private DevelopmentAccessController mDevelopmentAccessController;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		mTrivialAccessController = BEANS.get(TrivialAccessController.class)
				.init(new TrivialAuthConfig().withExclusionFilter(filterConfig.getInitParameter("filter-exclude")));
		mTunnelAccessController = BEANS.get(ServiceTunnelAccessTokenAccessController.class).init();
		mDevelopmentAccessController = BEANS.get(DevelopmentAccessController.class).init();
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest req = (HttpServletRequest) request;
		final HttpServletResponse resp = (HttpServletResponse) response;

		if (mTrivialAccessController.handle(req, resp, chain)) {
			return;
		}

		if (mTunnelAccessController.handle(req, resp, chain)) {
			return;
		}

		if (mDevelopmentAccessController.handle(req, resp, chain)) {
			return;
		}

		resp.sendError(HttpServletResponse.SC_FORBIDDEN);
	}

	@Override
	public void destroy() {
		mDevelopmentAccessController.destroy();
		mTunnelAccessController.destroy();
		mTrivialAccessController.destroy();
	}
}
