package com.raisin.subscribers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.raisin.domains.ServerInfo;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public interface Subscriber {
	
	HttpResponse subscribe(ServerInfo serverInfo) throws URISyntaxException, IOException;
	
	//InputStream subscribe(String url) throws IOException;
}
