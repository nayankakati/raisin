package com.raisin.publishers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.raisin.domains.ServerInfo;
import com.raisin.helpers.ServerConnectHelper;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class PublisherImpl implements Publisher {

	public HttpResponse publish(String json, ServerInfo serverInfo) throws URISyntaxException, IOException {
		StringEntity entity = new StringEntity(json);
		HttpPost post = ServerConnectHelper.getHttpPostResource(serverInfo);
		post.setEntity(entity);
		return ServerConnectHelper.getHttpClient().execute(post);
	}
}
