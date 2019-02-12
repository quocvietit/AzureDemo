import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlobDirectory;
import com.microsoft.azure.storage.blob.CloudBlockBlob;
import utils.AzureBlobUtil;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

//import com.microsoft.azure.AzureClient;
//import com.microsoft.azure.storage.CloudStorageAccount;
//import com.microsoft.azure.storage.StorageException;
//import com.microsoft.azure.storage.blob.CloudBlobClient;
//import utils.AzureBlobUtil;
//import java.net.Authenticator;


public class Application {
	public static void main(String[] args) throws ExecutionException, InterruptedException, MalformedURLException, IOException, URISyntaxException, StorageException, InvalidKeyException {
//		String poolId = System.getenv("POOL_ID");
//
//		String batchUri = System.getenv("BATCH_URI");
//		String clientId = System.getenv("CLIENT_ID");
//		String clientSecret = System.getenv("CLIENT_SECRET");
//		String applicationDomain = System.getenv("APPLICATION_DOMAIN");
//		String tenantId = System.getenv("TENANT_ID");
//
//		String batchEndpoint = "https://batch.core.windows.net/";
//		String authenticationEndpoint = String.format("https://login.microsoftonline.com/%s/oauth2/authorize", tenantId);
//
//
//		String authority = "https://login.microsoftonline.com/common";
//		String appUri = "https://storage.azure.com/";
//
//		ExecutorService executorService = Executors.newFixedThreadPool(1);
//		AuthenticationContext authenticationContext = new AuthenticationContext(authority, false, executorService);
//		ClientCredential credential = new ClientCredential(clientId, clientSecret);
//		Future<AuthenticationResult> future = authenticationContext.acquireToken(appUri, credential, null);
//		AuthenticationResult result = future.get();
//		System.out.println(result.getAccessToken());
//		System.out.println(result);

//		//Create Batch Client
//		AzureBatchUtil azureBatchUtil = new AzureBatchUtil();
//		BatchClient batchClient = azureBatchUtil.createBacthClient(batchUri, clientId, clientSecret, applicationDomain, batchEndpoint, authenticationEndpoint);
//
//		//Create batch job
//		System.out.println("Create job");
//		String jobId = "job-id-1";
//		boolean isJob = azureBatchUtil.createJob(batchClient, jobId, poolId);
//
//		if(isJob){
//			System.out.println("Create job success!");
//
//			System.out.println("Create task");
//			//Create job task
//			String cmd = "cmd /c \"dir\"";
//			String taskId = "task-id-1 ";
//
//			boolean isTask = azureBatchUtil.createTask(batchClient, taskId, cmd, jobId);
//
//			if(isTask){
//				System.out.println("Create task success!");
//			}else {
//				System.out.println("Create task fail!");
//			}
//
//		}else{
//			System.out.println("Create job fail!");
//		}
//		String storageId = System.getenv("STORAGE_ID");
//		String storageUri = System.getenv("STORAGE_URI");
//		String storageEndpoint = "https://storage.core.windows.net/";

//		ApplicationTokenCredentials applicationTokenCredentials = new ApplicationTokenCredentials(storageUri, clientId, clientSecret, applicationDomain, storageEndpoint);

//		//--------------Storage-----------------
		String connectionString = System.getenv("CONNECTION_STRING");
		String containerName = "cic-charts";
		String directory = "ahihi";
		String blobName1 = "file1.txt";
		String blobName2 = "file2.txt";
		File file = new File("./src/main/resources/a.txt");
		//File file1 = new File("./src/main/resources/test1.txt");
		Map<String, String> metadata = new HashMap<String, String>();
		metadata.put("key1", "value1");

		AzureBlobUtil azureBlobUtil = new AzureBlobUtil();
		CloudBlobClient cloudBlobClient = azureBlobUtil.createBlobClient(connectionString);
		CloudBlobContainer cloudBlobContainer = azureBlobUtil.createContainer(cloudBlobClient, containerName);


		byte[] content = Files.readAllBytes(Paths.get(file.getAbsolutePath()));

		azureBlobUtil.uploadFile(cloudBlobContainer, blobName1, directory, file, metadata);
		URI uri = azureBlobUtil.uploadContent(cloudBlobContainer, blobName2, directory, content, metadata);
		System.out.println(uri.toString());

		azureBlobUtil.downloadFile(cloudBlobContainer, blobName2, directory, "file.txt");

		CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(directory);
		CloudBlockBlob cloudBlockBlob = cloudBlobContainer.getBlockBlobReference(directory);
		if (cloudBlockBlob.deleteIfExists()) {
			System.out.println("Delete");
		}else{
			System.out.println("Error");
		}

		azureBlobUtil.deleteBlobDirectory(cloudBlobContainer, directory);
	}
}
