package com.tappayments.automation.qabatch.stress;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tappayments.automation.qabatch.App;
import com.tappayments.automation.qabatch.config.ConfigManager;
import com.tappayments.automation.qabatch.utils.AppConstants;
import com.tappayments.automation.qabatch.utils.AppUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BatchUploader {

    private static final String[] FILES = {
            "upload-bulk-invoice-1.csv",
            "upload-bulk-invoice-2.csv",
            "upload-bulk-invoice-3.csv",
            "upload-bulk-invoice-4.csv",
            "upload-bulk-invoice-5.csv"
    };

    // Number of concurrent threads
    private static final int THREAD_COUNT = Integer.parseInt(ConfigManager.getPropertyValue(AppConstants.THREAD_COUNT));
    private static final String FILE_UPLOAD_URL = ConfigManager.getPropertyValue(AppConstants.BASE_URI_VALUE) + AppConstants.PRESIGNED_URL;

    public static void main(String[] args) {
        try {

            ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
            Random random = new Random();

            // Submit 10 tasks to the thread pool for concurrent uploads
            for (int i = 0; i < THREAD_COUNT; i++) {
                executorService.submit(() -> {
                    try {
                        // Randomly select a file from the list
                        String fileName = FILES[random.nextInt(FILES.length)];
                        ClassLoader classLoader = BatchUploader.class.getClassLoader();
                        File file = new File(classLoader.getResource(fileName).getFile());

                        // Call the method to generate presigned URL and upload the file
                        String preSignedUrl = AppUtils.generatePresignedUrl(FILE_UPLOAD_URL);  // Assuming you already have this method
                        AppUtils.uploadFile(preSignedUrl, file);  // Assuming the uploadFile method accepts file as a parameter
                        System.out.println("Uploaded: " + fileName);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }

            // Shutdown the executor service after all tasks are submitted
            executorService.shutdown();
            executorService.awaitTermination(1, TimeUnit.MINUTES);  // Wait for all threads to finish

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
