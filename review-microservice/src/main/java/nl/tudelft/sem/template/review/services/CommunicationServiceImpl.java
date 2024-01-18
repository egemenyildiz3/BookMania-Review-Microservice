package nl.tudelft.sem.template.review.services;

import org.springframework.stereotype.Service;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

@Service
public class CommunicationServiceImpl {
    private final String userMicroUrl = "http://localhost:8080";

    private final String bookMicroUrl = "http://localhost:8081";

    public CommunicationServiceImpl() {
    }

    /**
     * This method makes a http request to the given server and returns the response.
     *
     * @param server - the server to make the request to, starts with http://
     * @param id - the id of the object to check
     * @param admin - whether to check for the user being an admin
     * @param working - whether to make the actual http request
     * @return True if the query under the given parameters is true
     */
    public boolean getResponse(String server, Long id, boolean admin, boolean working) {
        if (!working) {
            return true;
        }
        String url;
        StringBuilder response = new StringBuilder();
        int responseCode;
        if (Objects.equals(server, bookMicroUrl)) {
            url = server + "/book/getById/" + id;
        } else {
            url = server + "/check/role/" + id;
        }
        try {
            URL obj = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // Set the request method
            connection.setRequestMethod("GET");

            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (admin) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String inputLine;
                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    return response.toString().toLowerCase().contains("admin");
                }

            } else {
                System.out.println("Failed with status code " + responseCode);
                return false;
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return true;

    }

    /**
     * Checks if the user with the given id is an admin.
     *
     * @param userId - the id of the user to check
     * @return - True if the user is an admin, false otherwise
     */
    public boolean isAdmin(Long userId) {
        //TODO make http request to endpoint for admin
        return getResponse(userMicroUrl, userId, true, true); //set the second boolean to true to make actual http request
        //return true;
    }

    /**
     * Checks if the book with the given id exists.
     *
     * @param bookId - the id of the book to check
     * @return - True if the book exists, false otherwise
     */
    public boolean existsBook(Long bookId) {
        //TODO make http request to endpoint for book
        return getResponse(bookMicroUrl, bookId, false, true);
        //return true;
    }

    /**
     * Checks if the user with the given id exists.
     *
     * @param userId - the id of the user to check
     * @return - True if the user exists, false otherwise
     */
    public boolean existsUser(Long userId) {
        //TODO make http request to endpoint for user
        return getResponse(userMicroUrl, userId, false, true);
        //return true;
    }

    /**
     * Checks if the user with the given id is the author of the book with the given id.
     *
     * @param bookId - the id of the book to check
     * @param userId - the id of the user to check
     * @return - True if the user is the author of the book, false otherwise
     */
    public boolean isAuthor(Long bookId, Long userId) {
        //TODO make http request to endpoint for author
        return true;
    }

    /**
     * Checks if the user with the given id is the author of the book with the given id by making an HTTP request.
     *
     * @param bookId - the id of the book to check
     * @param userId - the id of the user to check
     * @return - True if the user is the author of the book, false otherwise
     */
    public boolean isAuthorIntegration(Long bookId, Long userId) {
        //TODO make http request to endpoint for author
        String book;
        try {
            URL obj = new URL(bookMicroUrl + "/book/getById/" + bookId);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = reader.readLine()) != null) {
                    response.append(inputLine);
                }
                book = response.toString();
                System.out.println(book);
                reader.close();
                return book.contains("\"authorId\": " + userId);
            } else {
                System.out.println("Failed with status code" + responseCode);
                return false;
            }
        } catch (Exception e) {
            return false;
        }
    }
}
