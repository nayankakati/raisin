package com.raisin.server.services;

import com.raisin.domains.ServerInfo;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public interface PubSubService {

	void readAndWriteMessageToServer(ServerInfo server);
}
