package nato.ivct.gui.client;

import java.util.HashMap;
import java.util.List;

import org.eclipse.scout.rt.client.context.ClientRunContexts;
import org.eclipse.scout.rt.client.job.ModelJobs;
import org.eclipse.scout.rt.client.ui.action.menu.IMenu;
import org.eclipse.scout.rt.client.ui.desktop.IDesktop;
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable;
import org.eclipse.scout.rt.shared.notification.INotificationHandler;
import org.slf4j.LoggerFactory;

import nato.ivct.gui.shared.HeartBeatNotification;
import nato.ivct.gui.shared.Icons;

public class HeartBeatNotificationHandler implements INotificationHandler<HeartBeatNotification> {

	org.slf4j.Logger logger = LoggerFactory.getLogger(getClass());
	
	public static final HashMap<String, HeartBeatNotification> hbLastReceivedMap = new HashMap<>();
	
	public static HeartBeatNotification lastReceivedFromSender(String hbSender) {
		return hbLastReceivedMap.get(hbSender);
	}

	@Override
	public void handleNotification(HeartBeatNotification notification) {
		// inform client about test case log message
		ModelJobs.schedule(new IRunnable() {
			@Override
			public void run() throws Exception {
				logger.trace("Heartbeat notification received for " + notification.heartBeatSender);
				
				if (!notification.heartBeatSender.isEmpty())
					hbLastReceivedMap.put(notification.heartBeatSender, notification);
				
				IDesktop desktop = ClientSession.get().getDesktop();
				
				// try...catch to avoid exception when calling getMenuByClass and Desktop is still in the opening state
				try {
					if (desktop != null && desktop.isOpened()) {
						IMenu statusMenu = null;
						List<IMenu> menus = Desktop.CURRENT.get().getMenus();
						for(IMenu menu : menus) {
							if (menu.hasProperty("hbSender") && menu.getProperty("hbSender").toString().equalsIgnoreCase(notification.heartBeatSender)) {
								statusMenu = menu;
								break;
							}
						}
						
						if (statusMenu == null)
							// no fitting menu found, hence nothing to do
							return;
						
						switch (notification.notifyState) {
							case OK:
								statusMenu.setIconId(Icons.GreenBullet_32x32);
								break;
							case WARNING:
								statusMenu.setIconId(Icons.YellowBullet_32x32);
								break;
							case CRITICAL:
							case DEAD:
								statusMenu.setIconId(Icons.RedBullet_32x32);
								break;
							case UNKNOWN:
							default:
								statusMenu.setIconId(Icons.WhiteBullet_32x32);
						}
					}
					else {
						logger.trace("Desktop not opened!");
					}
				} catch (Exception exc) {
					exc.printStackTrace();
				}
			}
		}, ModelJobs.newInput(ClientRunContexts.copyCurrent()));
	}
}
