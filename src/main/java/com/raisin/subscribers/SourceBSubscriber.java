package com.raisin.subscribers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.raisin.domains.ServerInfo;
import com.raisin.helpers.ServerConnectHelper;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class SourceBSubscriber implements Subscriber {

	private static Subscriber resourceSubscriberB;
	
	public static Subscriber getInstance() {
			if (null == resourceSubscriberB) {
				resourceSubscriberB = new SourceBSubscriber();
			}
		return resourceSubscriberB;
	}
	
	public HttpResponse subscribe(ServerInfo serverInfo) throws URISyntaxException, IOException {
		return ServerConnectHelper.getHttpClient().execute(ServerConnectHelper.getHttpResourceB(serverInfo));
	}
}
