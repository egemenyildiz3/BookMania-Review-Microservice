// package nl.tudelft.sem.template.review.RESTcontrollers;

// import nl.tudelft.sem.template.model.Review;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.web.server.LocalServerPort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import java.util.Map;

// import static org.junit.jupiter.api.Assertions.assertEquals;


// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
// class GetReportControllerTest {

//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;

//     @Test
//     void testGetItemById() {
//         // Create a test item and save it to the in-memory database
//         Review rev = new Review(1L,2L,10L,"wow","review",5L);
//         rev = restTemplate.postForObject("/api/review", rev, Review.class);

//         // Make a request to the controller endpoint
//         ResponseEntity<Review> responseEntity = restTemplate.getForEntity("/api/getReport/{bookId}/{userId}/{info}", Review.class, Map.of("bookId",2L, "userId", 1L, "info","report"));

//         // Verify the response
//         assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
//         assertEquals(rev.getBookId(), responseEntity.getBody().getId());
//         // Add more assertions based on your specific use case
//     }
// }
