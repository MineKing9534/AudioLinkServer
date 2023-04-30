package de.mineking.audiolink.server.processing;

import java.io.DataOutputStream;
import java.io.IOException;

public interface DataCreator {
	void createData(DataOutputStream out) throws IOException;
}
