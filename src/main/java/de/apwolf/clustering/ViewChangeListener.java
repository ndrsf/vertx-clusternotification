package de.apwolf.clustering;

import org.infinispan.notifications.Listener;
import org.infinispan.notifications.cachemanagerlistener.annotation.ViewChanged;
import org.infinispan.notifications.cachemanagerlistener.event.ViewChangedEvent;

@Listener // required for Infinispans event mechanism
public class ViewChangeListener {

	@ViewChanged // required for Infinispans event mechanism, method must be public
	public void handleViewChange(ViewChangedEvent e) {
		System.out.println("ViewChangedEvent: " + e);
	}
}