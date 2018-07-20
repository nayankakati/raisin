package com.raisin.server.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.bind.JAXBException;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.http.HttpResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.raisin.domains.ServerInfo;
import com.raisin.domains.SinkRequest;
import com.raisin.domains.ResourceAResponse;
import com.raisin.domains.ResourceBResponse;
import com.raisin.domains.XMLMessage;
import com.raisin.helpers.ServerConnectHelper;
import com.raisin.publishers.Publisher;
import com.raisin.publishers.PublisherImpl;
import com.raisin.subscribers.SourceASubscriber;
import com.raisin.subscribers.SourceBSubscriber;
import com.raisin.subscribers.Subscriber;

/**
 * Created by nayan.kakati on 11/16/17.
 * This class acts as a service for publisher and subscriber
 */
public class PubSubServiceImpl implements PubSubService {

	private static final String DONE = "done";
	private static final String ORPHANED = "orphaned";
	private static final String JOINED = "joined";

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public void readAndWriteMessageToServer(ServerInfo server) {

		ResourceAResponse resourceAResponse = null;
		ResourceBResponse resourceBResponse = null;

		//List to make checking simpler between two sources
		List<String> sourceAMessageList = new ArrayList<String>();
		List<String> sourceBMessageList = new ArrayList<String>();
		List<String> sourceABJoinedMessageList = new ArrayList<String>();

		boolean serverStatus = true;
		boolean aResourceStatus = true;
		boolean bResourceStatus = true;

		try{
			  do {
					//read all from resource A
					if(aResourceStatus && serverStatus) {
						resourceAResponse = readFromResourceA(server);
					}

					if (resourceAResponse != null) {
						if(DONE.equals(resourceAResponse.getStatus())) {
							resourceAResponse = null;
							aResourceStatus = false;
						} else {
							sourceAMessageList.add(resourceAResponse.getId());
						}
					}

					//read all from resource B
					if (bResourceStatus && serverStatus) {
						resourceBResponse = this.readFromResourceB(server);
					}

					if (resourceBResponse != null) {
						if(DONE.equals(resourceBResponse.getStatus())) {
							resourceBResponse = null;
							bResourceStatus = false;
						} else {
							sourceBMessageList.add(resourceBResponse.getId());
						}
					}

					serverStatus = true;
					//check for joined, orphaned and defective

					//Sink Joined
					sourceABJoinedMessageList = sendJoinedSinkToServer(server, sourceAMessageList, sourceBMessageList, sourceABJoinedMessageList);

					//Sinked sourceA Orphaned {}
					if(!sourceABJoinedMessageList.isEmpty() && !sourceAMessageList.isEmpty() && !sourceBMessageList.isEmpty() &&
						 resourceBResponse != null && sourceBMessageList.contains(resourceBResponse.getId())) {
						System.out.println("Sending sink to server A  ....------>....");
						sendSinkRequestToServer(new SinkRequest(sourceAMessageList.get(0), ORPHANED), server);
						sourceAMessageList.remove(0);
						resourceAResponse = readFromResourceA(server);
						resourceBResponse = null;
						serverStatus = false;
					}

					//Sinked sourceB Orphaned
					if(!sourceABJoinedMessageList.isEmpty() && !sourceBMessageList.isEmpty() && !sourceAMessageList.isEmpty() &&
						null != resourceAResponse && sourceAMessageList.contains(resourceAResponse.getId())) {
						System.out.println("Sending sink to server B ....------>....");
						sendSinkRequestToServer(new SinkRequest(sourceBMessageList.get(0), ORPHANED), server);
						sourceBMessageList.remove(0);
						serverStatus = false;
						resourceBResponse = readFromResourceB(server);
						resourceAResponse = null;
					}


					//If B resource is finished now only A would be there which we would sink it with server
					if (sourceAMessageList.size() > 2) {
						sendSinkRequestToServer(new SinkRequest(sourceAMessageList.get(0), ORPHANED), server);
						sourceAMessageList.remove(0);
					}

					//If A resource is finished now only B would be there which we would sink it with server
					if (sourceBMessageList.size() > 2) {
						sendSinkRequestToServer(new SinkRequest(sourceBMessageList.get(0), ORPHANED), server);
						sourceBMessageList.remove(0);
					}

				} while(aResourceStatus || bResourceStatus); // Terminate when both the source has been exhausted

			  System.out.println("Finished Pub/Sub Application.....");
		} catch (Exception message) {
			System.out.println("Exception occured while reading from with resource A or resource B with exception  " + message.getLocalizedMessage());
		}
	}

	private List<String> sendJoinedSinkToServer(ServerInfo server, List<String> aList, List<String> bList, List<String> joined) {
		if(!aList.isEmpty() && !bList.isEmpty()) {
			joined = bList.parallelStream().filter(aList::contains).collect(Collectors.toList());
			//If present on both then send both to sink
			if(!joined.isEmpty()) {
					joined.forEach(sourceId -> {
						try {
							System.out.println("Sending sink to server JOINED ....------>....");
							sendSinkRequestToServer(new SinkRequest(sourceId, JOINED), server);
							aList.remove(sourceId);
							bList.remove(sourceId);
						} catch (URISyntaxException exception) {
							System.out.println("Malformed URI found!!!");
						}
					});
			}
		}
		return joined;
	}

	// Private methods to send sink request to server
	public void sendSinkRequestToServer(SinkRequest sinkRequest, ServerInfo serverInfo) throws URISyntaxException {
		Publisher publisher = new PublisherImpl();
		try {
			String json = objectMapper.writeValueAsString(sinkRequest);
			HttpResponse response = publisher.publish(json, serverInfo);
			String sinkresponse = this.getResponse(response);
			System.out.print("Response from Sinking --------> " + sinkresponse);
		} catch (IOException message) {
			System.out.println("Exception occured while sending a sink request with Exception " + message.getLocalizedMessage());
		}
	}

	// Private methods with respect to Resources A
	private ResourceAResponse readFromResourceA(ServerInfo serverInfo) {
		ResourceAResponse resourceAResponse = this.getResourceAResponse(serverInfo);
		if (resourceAResponse != null && resourceAResponse.isDefective()) {
			resourceAResponse = this.readFromResourceA(serverInfo);
		}
		return resourceAResponse;
	}

	private ResourceAResponse getResourceAResponse(ServerInfo serverInfo) {
		Subscriber sourceA = SourceASubscriber.getInstance();
		HttpResponse response = null;
		ResourceAResponse resourceAResponse = null;
		try {
			response = sourceA.subscribe(serverInfo);
			resourceAResponse = objectMapper.readValue(this.getResponse(response), ResourceAResponse.class);
			response = null;
		} catch (Exception e) {
			//Got defective source
			resourceAResponse = new ResourceAResponse();
			resourceAResponse.setDefective(true);
		}
		return resourceAResponse;
	}

	//Private methods with respect to Resources B
	private ResourceBResponse readFromResourceB(ServerInfo serverInfo) {
		ResourceBResponse resourceBResponse = this.getResourceBResponse(serverInfo);
		//continue until a valid source found, no defective
		if (resourceBResponse != null && resourceBResponse.isDefective()) {
			resourceBResponse = this.readFromResourceB(serverInfo);
		}
		return resourceBResponse;
	}

	private ResourceBResponse getResourceBResponse(ServerInfo serverInfo) {
		Subscriber sourceB = SourceBSubscriber.getInstance();
		HttpResponse response = null;
		ResourceBResponse resourceBResponse = null;
		try {
			response = sourceB.subscribe(serverInfo);
			resourceBResponse = createXMLResourceBResponse(this.getResponse(response));
			response = null;
		} catch (Exception e) {
			//Got defective source
			resourceBResponse = new ResourceBResponse();
			resourceBResponse.setDefective(true);
		}
		return resourceBResponse;
	}

	private ResourceBResponse createXMLResourceBResponse(String xml) {
		ResourceBResponse resourceBResponse = new ResourceBResponse();
		try {
			ServerConnectHelper.createJAXBContext();
			XMLStreamReader reader =  ServerConnectHelper.getXMLInputFactory().createXMLStreamReader(new StringReader(xml));
			XMLMessage xmlMessage = (XMLMessage) ServerConnectHelper.createUnmarshaller().unmarshal(reader);
			if (null != xmlMessage && null != xmlMessage.getId() && null != xmlMessage.getId().getValue()) {
				resourceBResponse.setId(xmlMessage.getId().getValue());
			}
			if (null != xmlMessage && null != xmlMessage.getDone()) {
				resourceBResponse.setStatus(DONE);
			}
		} catch (XMLStreamException | FactoryConfigurationError | JAXBException e) {
			resourceBResponse.setDefective(true);
		}
		return resourceBResponse;
	}

	private String getResponse(HttpResponse response) throws IOException {
		BufferedReader reader = null;
		StringBuilder convertedResult = new StringBuilder();
		try {
			reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			String lineResult = "";
			while ((lineResult = reader.readLine()) != null) {
				convertedResult.append(lineResult);
			}
		} catch (Exception e) {
			System.err.println("Exception occurecd while reading response ");
		}
		finally {
				reader.close();
		}
		return convertedResult.toString();
	}

}
