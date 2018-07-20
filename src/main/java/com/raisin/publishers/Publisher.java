package com.raisin.publishers;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.HttpResponse;

import com.raisin.domains.ServerInfo;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public interface Publisher {

	HttpResponse publish(String json, ServerInfo serverInfo) throws URISyntaxException, IOException;
}
