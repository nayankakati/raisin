import com.raisin.domains.ServerInfo;
import com.raisin.server.services.PubSubService;
import com.raisin.server.services.PubSubServiceImpl;

/**
 * Created by nayan.kakati on 11/16/17.
 */

/** Task breakdown
 * Task1 :- Read all from resource A
 * Task2 :- Read all from resource B
 * Task3 :- Check for joined, orphaned and defective
 * Task4 :- Publish joined and orphaned to back to server
 * Task5 :- Check for 406 response
 */
public class PubSubApplication {

	public static void main(String...args) {

		//Set the details from python file to this
		//Input can be taken from a file or properties file or using Scanner class
		//For fast and simplicity, I have used direct object with server details
		ServerInfo server = new ServerInfo(7299,"localhost","/sink/a","/source/a","/source/b");
		PubSubService pubSubService = new PubSubServiceImpl();
		pubSubService.readAndWriteMessageToServer(server);
	}
}
