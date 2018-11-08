import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
//import com.microsoft.azure.AzureClient;
import com.microsoft.azure.batch.BatchClient;
//import com.microsoft.azure.storage.CloudStorageAccount;
//import com.microsoft.azure.storage.StorageException;
//import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.batch.auth.BatchSharedKeyCredentials;
import com.microsoft.azure.batch.auth.BatchUserTokenCredentials;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import utils.AzureBatchUtil;
import utils.AzureStorageUtil;
//import utils.AzureStorageUtil;

import java.io.IOException;
//import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.concurrent.*;


public class Application {
	public static void main(String[] args) throws ExecutionException, InterruptedException, MalformedURLException, IOException, URISyntaxException, StorageException {
		String poolId = System.getenv("POOL_ID");

		String batchUri = System.getenv("BATCH_URI");
		String clientId = System.getenv("CLIENT_ID");
		String clientSecret = System.getenv("CLIENT_SECRET");
		String applicationDomain = System.getenv("APPLICATION_DOMAIN");
		String tenantId = System.getenv("TENANT_ID");

		String batchEndpoint = "https://batch.core.windows.net/";
		String authenticationEndpoint = String.format("https://login.microsoftonline.com/%s/oauth2/authorize", tenantId);


		String authority = "https://login.microsoftonline.com/common";
		String appUri = "https://batch.core.windows.net/";

		ExecutorService executorService = Executors.newFixedThreadPool(1);
		AuthenticationContext authenticationContext = new AuthenticationContext(authority, false, executorService);
		ClientCredential credential = new ClientCredential(clientId, clientSecret);
		Future<AuthenticationResult> future = authenticationContext.acquireToken(appUri, credential, null);
		AuthenticationResult result = future.get();
		System.out.println(result.getAccessToken());

		//Create Batch Client
		AzureBatchUtil azureBatchUtil = new AzureBatchUtil();
		BatchClient batchClient = azureBatchUtil.createBacthClient(batchUri, clientId, clientSecret, applicationDomain, batchEndpoint, authenticationEndpoint);

		//Create batch job
		System.out.println("Create job");
		String jobId = "job-id-1";
		boolean isJob = azureBatchUtil.createJob(batchClient, jobId, poolId);

		if(isJob){
			System.out.println("Create job success!");

			System.out.println("Create task");
			//Create job task
			String cmd = "cmd /c \"dir\"";
			String taskId = "task-id-1 ";

			boolean isTask = azureBatchUtil.createTask(batchClient, taskId, cmd, jobId);

			if(isTask){
				System.out.println("Create task success!");
			}else {
				System.out.println("Create task fail!");
			}

		}else{
			System.out.println("Create job fail!");
		}



		//--------------Storage-----------------
		String containerName = "container-name";
		AzureStorageUtil azureStorageUtil = new AzureStorageUtil();
		CloudStorageAccount cloudStorageAccount = azureStorageUtil.createStorageAccountClient("", clientId);

		CloudBlobClient cloudBlobClient = azureStorageUtil.createBlobClient(cloudStorageAccount);

		boolean isContainer = azureStorageUtil.createContainer(cloudBlobClient, containerName);

		if(isContainer){
			System.out.println("Create container ssuccess!");
		}else {
			System.out.println("Create container fail!");
		}

	}
}
