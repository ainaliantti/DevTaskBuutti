package com.example;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class DevTaskClient {
    public static void main(String[] args) {

        clientExample();
        singleClientTests();
        twoClientTests();
        malformedRequest();
    }

    private static void clientExample() {
        String url = "http://localhost:8080/string?user=user1";
        System.out.println("Sending GET-request to URL: " + url);
        String response = httpGet(url);
        System.out.println("Got the response: " + response);
        String encryptedString = new BCryptPasswordEncoder().encode(response);
        System.out.println("Sending POST with the encrypted string: " + encryptedString);
        response = httpPost(url, encryptedString);
        System.out.println("Got the response: " + response);
    }

    private static void singleClientTests() {
        String url = "http://localhost:8080/string?user=user1";
        System.out.println("Sending GET-request to the: " + url + " 5 times");
        String response0 = httpGet(url);
        System.out.println(response0);
        System.out.println(httpGet(url));
        System.out.println(httpGet(url));
        System.out.println(httpGet(url));
        String response4 = httpGet(url);
        System.out.println(response4);
        System.out.println("Encrypting the first and last responses and sending them:");
        String encryptedString0 = new BCryptPasswordEncoder().encode(response0);
        String encryptedString4 = new BCryptPasswordEncoder().encode(response4);
        System.out.println(httpPost(url, encryptedString0));
        System.out.println(httpPost(url, encryptedString4));
    }

    private static void twoClientTests() {
        String url0 = "http://localhost:8080/string?user=user1";
        String url1 = "http://localhost:8080/string?user=user2";

        System.out.println("Sending GET-request to URL: " + url0);
        String response0 = httpGet(url0);
        System.out.println("Got the response: " + response0);
        System.out.println("Sending GET-request to URL: " + url1);
        String response1 = httpGet(url1);
        System.out.println("Got the response: " + response1);
        String encryptedString0 = new BCryptPasswordEncoder().encode(response0);
        String encryptedString1 = new BCryptPasswordEncoder().encode(response1);

        System.out.println("Sending the 2nd encoded string to the first URL and 1st to the 2nd");
        System.out.println(httpPost(url0, encryptedString1));
        System.out.println(httpPost(url1, encryptedString0));

        System.out.println("Sending both responses to the right addresses twice");
        System.out.println(httpPost(url0, encryptedString0));
        System.out.println(httpPost(url0, encryptedString0));
        System.out.println(httpPost(url1, encryptedString1));
        System.out.println(httpPost(url1, encryptedString1));
    }

    private static void malformedRequest() {
        String url = "http://localhost:8080/string";
        System.out.println("Sending GET-request to URL: " + url);
        String response = httpGet(url);
        System.out.println("Got the response: " + response);

    }


        private static String httpGet(String url) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();
        } catch (Exception e) {
            return null;
        }

    }

    private static String httpPost(String url, String data) {
        try (CloseableHttpClient client = HttpClientBuilder.create().build()) {

            HttpPost request = new HttpPost(url);
            request.setEntity(new StringEntity(data));

            HttpResponse response = client.execute(request);

            BufferedReader bufReader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));

            StringBuilder builder = new StringBuilder();

            String line;

            while ((line = bufReader.readLine()) != null) {
                builder.append(line);
                builder.append(System.lineSeparator());
            }

            return builder.toString();
        } catch (Exception e) {
            return null;
        }
    }
}