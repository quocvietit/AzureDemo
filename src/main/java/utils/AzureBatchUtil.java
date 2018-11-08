package utils;

import com.microsoft.azure.batch.BatchClient;
import com.microsoft.azure.batch.auth.BatchApplicationTokenCredentials;
import com.microsoft.azure.batch.auth.BatchUserTokenCredentials;
import com.microsoft.azure.batch.protocol.models.*;

import java.io.IOException;

public class AzureBatchUtil {
	public BatchClient createBacthClient(String baseUrl, String clientId, String applicationSecret, String applicationDomain, String batchEndpoint, String authenticationEndpoint) {
		BatchApplicationTokenCredentials batchApplicationTokenCredentials = new BatchApplicationTokenCredentials(baseUrl, clientId, applicationSecret, applicationDomain, batchEndpoint, authenticationEndpoint);
		BatchClient batchClient = BatchClient.open(batchApplicationTokenCredentials);

		return batchClient;
	}

	public boolean createPool(BatchClient batchClient, String poolId) {
		return true;
	}

	public boolean createJob(BatchClient batchClient, String jobId, String poolId) {
		try {
			// Get pool information
			PoolInformation poolInformation = new PoolInformation();
			poolInformation.withPoolId(poolId);

			//Create job params
			JobAddParameter jobAddParameter = new JobAddParameter();
			jobAddParameter.withId(jobId)
					.withPoolInfo(poolInformation)
					.withOnAllTasksComplete(OnAllTasksComplete.TERMINATE_JOB);

			// Create job
			batchClient.jobOperations().createJob(jobAddParameter);

			// Check job exists
			CloudJob job = batchClient.jobOperations().getJob(jobId);
			return true;
		} catch (IOException ex) {
			System.out.print("IOException: " + ex);
			return false;
		} catch (BatchErrorException ex) {
			System.out.print("BatchErrorException: " + ex);
			return false;
		}
	}

	public boolean createTask(BatchClient batchClient, String taskId, String commandLine, String jobId) {
		try {
			//Create task params
			TaskAddParameter taskAddParameter = new TaskAddParameter();
			taskAddParameter.withId(taskId)
					.withCommandLine(commandLine);

			//Create task
			batchClient.taskOperations().createTask(jobId, taskAddParameter);

			// Get task
			CloudTask task = batchClient.taskOperations().getTask(jobId, taskId);
			return true;
		} catch (IOException ex) {
			System.out.print("IOException: " + ex);
			return false;
		} catch (BatchErrorException ex) {
			System.out.print("BatchErrorException: " + ex);
			return false;
		}
	}
}
