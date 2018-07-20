package com.raisin.helpers;

import java.net.URI;
import java.net.URISyntaxException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLInputFactory;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;

import com.raisin.domains.ServerInfo;
import com.raisin.domains.XMLMessage;

/**
 * Created by nayan.kakati on 11/16/17.
 */
public class ServerConnectHelper {


	private static HttpClient httpClient;
	private static HttpGet httpResourceA;
	private static HttpGet httpResourceB;
	private static HttpPost postHttpResource;
	private static JAXBContext context;
	private static Unmarshaller unmarshaller;
	private static XMLInputFactory factory;


	// create client using Builder pattern
	public static HttpClient getHttpClient() {
			if(httpClient == null) {
				httpClient = HttpClientBuilder.create().build();
			}
		return httpClient;
	}


	public static HttpGet getHttpResourceA(ServerInfo serverInfo) throws URISyntaxException {
			if(httpResourceA == null)
				httpResourceA = new HttpGet(getUri(serverInfo,serverInfo.getSourceA()));
		return httpResourceA;
	}

	public static HttpGet getHttpResourceB(ServerInfo serverInfo) throws URISyntaxException {
			if(httpResourceB == null)
				httpResourceB = new HttpGet(getUri(serverInfo,serverInfo.getSourceB()));
		return httpResourceB;
	}

	public static HttpPost getHttpPostResource(ServerInfo serverInfo) throws URISyntaxException {
			if(postHttpResource == null) {
				postHttpResource = new HttpPost(getUri(serverInfo,serverInfo.getSinkA()));
		}
		return postHttpResource;
	}

	public static void createJAXBContext() throws JAXBException {
		if(context == null) {
			context = JAXBContext.newInstance(XMLMessage.class);
		}
	}

	public static Unmarshaller createUnmarshaller() throws JAXBException {
		if(unmarshaller == null) {
			unmarshaller = context.createUnmarshaller();
		}
		return unmarshaller;
	}

	@SuppressWarnings("restriction")
	public static XMLInputFactory getXMLInputFactory() {
		if(factory == null) {
			factory = XMLInputFactory.newInstance();
		}
		return factory;
	}

	static URI getUri(ServerInfo serverInfo, String source) throws URISyntaxException {
		StringBuilder url = new StringBuilder();
		url.append("http://").append(serverInfo.getHost()).append(":").append(serverInfo.getPort()).append("/").append(source);
		return new URI(url.toString());
	}
}
