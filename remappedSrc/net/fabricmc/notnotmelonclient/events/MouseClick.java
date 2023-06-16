package net.fabricmc.notnotmelonclient.events;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;

import static net.fabricmc.notnotmelonclient.Main.client;

public interface MouseClick {
	void onClick(int button, double mouseX, double mouseY);

	Event<MouseClick> EVENT = EventFactory.createArrayBacked(MouseClick.class,
		(listeners) -> (int button, double mouseX, double mouseY) -> {
			double scale = client.getWindow().getScaleFactor();
			mouseX /= scale;
			mouseY /= scale;
			for (MouseClick listener : listeners)
				listener.onClick(button, mouseX, mouseY);
		});
}
