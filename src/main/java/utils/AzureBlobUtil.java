package utils;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.*;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.util.Iterator;
import java.util.Map;

public class AzureBlobUtil {
	public CloudBlobClient createBlobClient(String connectionString) throws URISyntaxException, InvalidKeyException {
		CloudStorageAccount cloudStorageAccount = CloudStorageAccount.parse(connectionString.trim());
		CloudBlobClient cloudBlobClient = cloudStorageAccount.createCloudBlobClient();

		return cloudBlobClient;
	}

//	public CloudBlobClient creaCloudBlobClient(String storageAccountName, String storageAccountKey){
//		CloudStorageAccount cloudStorageAccount = CloudStorageAccount.
//	}

	public CloudBlobContainer createContainer(CloudBlobClient cloudBlobClient, String containerName) throws URISyntaxException, StorageException {
		CloudBlobContainer cloudBlobContainer = cloudBlobClient.getContainerReference(containerName);

		if (!cloudBlobContainer.createIfNotExists()) {
			System.out.println("Create blob container: " + containerName);
		} else {
			System.out.println("Can not create blob container: " + containerName);
		}

		return cloudBlobContainer;
	}


	public CloudBlockBlob createBlob(CloudBlobContainer cloudBlobContainer, String blobName, String path, Map<String, String> metadata) throws StorageException, URISyntaxException {
		CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(path);
		CloudBlockBlob cloudBlockBlob = cloudBlobDirectory.getBlockBlobReference(blobName);

		Iterator it = metadata.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			cloudBlockBlob.getMetadata().put(pair.getKey().toString(), pair.getValue().toString());
		}

		return cloudBlockBlob;
	}

	public void uploadFile(CloudBlobContainer cloudBlobContainer, String blobName, String path, File file, Map<String, String> metadata) throws IOException, StorageException, URISyntaxException {
		CloudBlockBlob cloudBlockBlob = createBlob(cloudBlobContainer, blobName, path, metadata);
		cloudBlockBlob.uploadFromFile(file.getAbsolutePath());
	}

	public URI uploadContent(CloudBlobContainer cloudBlobContainer, String blobName, String path, byte[] byteArray, Map<String, String> metadata) throws IOException, StorageException, URISyntaxException {
		CloudBlockBlob cloudBlockBlob = createBlob(cloudBlobContainer, blobName, path, metadata);
		cloudBlockBlob.uploadFromByteArray(byteArray, 0, byteArray.length);

		return cloudBlockBlob.getUri();
	}

	public boolean downloadFile(CloudBlobContainer cloudBlobContainer, String blobName, String path, String pathFile) throws URISyntaxException, StorageException, IOException {
		CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(path);
		CloudBlockBlob cloudBlockBlob = cloudBlobDirectory.getBlockBlobReference(blobName);

		if (!cloudBlockBlob.exists()) {
			return false;
		}

		cloudBlockBlob.downloadToFile(pathFile);
		return true;
	}

	public void deleteBlobDirectory(CloudBlobContainer cloudBlobContainer, String directoryName) throws URISyntaxException, StorageException {
		CloudBlobDirectory cloudBlobDirectory = cloudBlobContainer.getDirectoryReference(directoryName);
		String directoryUri = cloudBlobDirectory.getUri().toString();

		Iterable<ListBlobItem> blobs = cloudBlobDirectory.listBlobs();
		for (ListBlobItem blobItem : blobs) {
			String blobBlockUri = blobItem.getUri().toString();
			String blobBlock = blobBlockUri.replace(directoryUri, "");

			CloudBlockBlob cloudBlockBlob = cloudBlobDirectory.getBlockBlobReference(blobBlock);
			System.out.println(cloudBlockBlob.getUri());
			if (!cloudBlockBlob.deleteIfExists()){
				String directory = directoryName.concat("/").concat(blobBlock);
				deleteBlobDirectory(cloudBlobContainer, directory);
			}
		}
	}

}
