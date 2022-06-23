package software.tnb.hyperfoil.service;

import software.tnb.common.config.Configuration;

public class HyperfoilConfiguration  extends Configuration {
	
	public static final String KEEP_RUNNING = "hyperfoil.keep.running";

	public static boolean keepRunning() {
		return getBoolean(KEEP_RUNNING, false);
	}
}
