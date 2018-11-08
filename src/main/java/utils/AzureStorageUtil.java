package utils;

import com.microsoft.azure.storage.*;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;

import java.net.URISyntaxException;

public class AzureStorageUtil {
	public CloudStorageAccount createStorageAccountClient(String clientId, String applicationSecret) throws URISyntaxException {
		StorageCredentials storageCredentials = new StorageCredentialsAccountAndKey(clientId, "fSE3L21kJTdkU3Y/RV5DXlpvMCo1dUZ6WjswcWVTYUUpfGo5UDEkTlIqUUhOQ21uLztFTUM6NXE7d0c=");
		CloudStorageAccount cloudStorageAccount =  new CloudStorageAccount(storageCredentials, true);
		return cloudStorageAccount;
	}

	public CloudBlobClient createBlobClient(CloudStorageAccount cloudStorageAccount){
		CloudBlobClient cloudBlobClient = cloudStorageAccount.createCloudBlobClient();

		return cloudBlobClient;
	}

	public boolean createContainer(CloudBlobClient cloudBlobClient, String containerName) throws URISyntaxException, StorageException {
		CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);

		return cloudBlobContainer.createIfNotExists();
	}
}
