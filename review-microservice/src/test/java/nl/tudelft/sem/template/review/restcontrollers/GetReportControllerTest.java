 package nl.tudelft.sem.template.review.restcontrollers;

 import nl.tudelft.sem.template.model.BookData;
 import nl.tudelft.sem.template.review.services.GetReportServiceImpl;
 import org.junit.jupiter.api.Test;
 import org.junit.runner.RunWith;
 import org.mockito.InjectMocks;
 import org.mockito.Mock;
 import org.mockito.junit.MockitoJUnitRunner;
 import org.springframework.boot.test.context.SpringBootTest;
 import org.springframework.http.MediaType;
 import org.springframework.http.ResponseEntity;
 import org.springframework.test.web.servlet.MockMvc;
 import org.springframework.test.web.servlet.setup.MockMvcBuilders;

 import static org.mockito.Mockito.*;
 import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
 import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


 @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
 @RunWith(MockitoJUnitRunner.class)
 class GetReportControllerTest {

     @Mock
     GetReportServiceImpl service;

     @InjectMocks
     private GetReportController controller;

     @Test
     void testGetItemById() throws Exception {
         BookData bd = new BookData(1L);
         when(service.getReport(1L, 1L, "report")).thenReturn(ResponseEntity.ok(bd));
         MockMvc mvc = MockMvcBuilders.standaloneSetup(controller).build();

         mvc.perform(get("/getReport/1/1/report"))
                 .andExpect(status().isOk())
                 .andExpect(content().contentType(MediaType.APPLICATION_JSON));

         verify(service, times(1)).getReport(1L, 1L, "report");

     }
 }
