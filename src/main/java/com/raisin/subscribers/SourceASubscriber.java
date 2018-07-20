package com.raisin.subscribers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.raisin.domains.ServerInfo;
import com.raisin.helpers.ServerConnectHelper;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class SourceASubscriber implements Subscriber {

	private static Subscriber resourceSubscriberA;
	
	public static Subscriber getInstance() {
			if (resourceSubscriberA == null) {
				resourceSubscriberA = new SourceASubscriber();
		  }
		return resourceSubscriberA;
	}
	
	public HttpResponse subscribe(ServerInfo serverInfo) throws URISyntaxException, IOException {
		return ServerConnectHelper.getHttpClient().execute(ServerConnectHelper.getHttpResourceA(serverInfo));
	}
}
