// package nl.tudelft.sem.template.review.RESTcontrollers;

// import nl.tudelft.sem.template.model.Review;
// import org.junit.jupiter.api.Test;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.context.SpringBootTest;
// import org.springframework.boot.test.web.client.TestRestTemplate;
// import org.springframework.boot.web.server.LocalServerPort;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.ResponseEntity;

// import java.util.Optional;

// import static org.junit.jupiter.api.Assertions.*;

// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

// class ReviewControllerTest {
//     @LocalServerPort
//     private int port;

//     @Autowired
//     private TestRestTemplate restTemplate;


//     @Test
//     void add() {
//         Review rev = new Review(1L,2L,10L,"wow","review",5L);
//         Review reviewResponseEntity = restTemplate.postForObject("/api/review", rev,Review.class);
//         assertEquals(rev.getId(), reviewResponseEntity.getId());
//     }
// }