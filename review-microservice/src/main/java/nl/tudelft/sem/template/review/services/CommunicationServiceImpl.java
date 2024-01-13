package nl.tudelft.sem.template.review.services;

import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class CommunicationServiceImpl {
    private final String userMicroUrl = "http://localhost:8080";

    private final String bookMicroUrl = "http://localhost:8081";

    public CommunicationServiceImpl() {
    }


    public boolean isAdmin(Long userId) {
        //TODO make http request to endpoint for admin
//        try {
//            String url = userMicroUrl + "/check/role/1";
//            URL obj = new URL(url);
//            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//
//            // Set the request method
//            connection.setRequestMethod("GET");
//
//            int responseCode = connection.getResponseCode();
//            if (responseCode == HttpURLConnection.HTTP_OK) {
//                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String inputLine;
//                StringBuilder response = new StringBuilder();
//
//                while ((inputLine = in.readLine()) != null) {
//                    response.append(inputLine);
//                }
//                in.close();
//
//                // Print the response
//                System.out.println(response.toString());
//            } else {
//                System.out.println("Failed with status code " + responseCode);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return true;
    }


    public boolean existsBook(Long bookId) {
        //TODO make http request to endpoint for book
        try {
            String url = bookMicroUrl + "/book/getById/1";
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // Set the request method
            connection.setRequestMethod("GET");

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Print the response
                System.out.println(response.toString());
            } else {
                System.out.println("Failed with status code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
        //return true;
    }

    public boolean existsUser(Long userId) {
        //TODO make http request to endpoint for user
        return true;
    }


}
