package nato.ivct.gui.client;

import java.util.Locale;

import org.eclipse.scout.rt.client.AbstractClientSession;
import org.eclipse.scout.rt.client.IClientSession;
import org.eclipse.scout.rt.client.session.ClientSessionProvider;
import org.eclipse.scout.rt.client.ui.ClientUIPreferences;
import org.eclipse.scout.rt.platform.nls.LocaleUtility;
import org.eclipse.scout.rt.shared.services.common.code.CODES;

import nato.ivct.gui.client.Desktop;

/**
 * <h3>{@link ClientSession}</h3>
 *
 * @author hzg
 */
public class ClientSession extends AbstractClientSession {
	
	public static final String PREF_USER_LOCALE = "PREF_USER_LOCALE";
	public static final String DEF_LOG_LEVEL = "DEF_LOG_LEVEL";
	public static final String CUR_LOG_LEVEL = "CUR_LOG_LEVEL";

	public ClientSession() {
		super(true);
	}

	/**
	 * @return The {@link IClientSession} which is associated with the current
	 *         thread, or {@code null} if not found.
	 */
	public static ClientSession get() {
		return ClientSessionProvider.currentSession(ClientSession.class);
	}

	@Override
	protected void execLoadSession() {
		// ping the server to get the initial shared variables and blocks until they were received
		initializeSharedVariables();
		
		// pre-load all known code types
		CODES.getAllCodeTypes("nato.ivct.gui.shared");

	    // The locale needs to be set before the Desktop is created.
	    String localeString = ClientUIPreferences.getClientPreferences(ClientSession.get()).get(PREF_USER_LOCALE, null);
	    if (localeString != null) {
	      Locale userLocale = LocaleUtility.parse(localeString);
	      setLocale(userLocale);
	    }

	    setDesktop(new Desktop());
	}
}
