package net.fabricmc.notnotmelonclient.config;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class ModMenu implements ModMenuApi {
	@Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
		return (parentScreen) -> Config.build().generateScreen(parentScreen);
	}
}
