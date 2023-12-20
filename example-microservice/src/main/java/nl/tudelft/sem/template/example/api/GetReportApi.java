/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package nl.tudelft.sem.template.example.api;

import nl.tudelft.sem.template.example.api.ApiUtil;
import nl.tudelft.sem.template.example.model.BookData;
import nl.tudelft.sem.template.example.model.BookData;
import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Generated;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
@Validated
@Tag(name = "bookData", description = "hand out report (only for authors)")
public interface GetReportApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /getReport/{bookId}/{userId}/{info} : report about a book
     * generates and returns a report about the reviews and interactions of the book; depending on the value of info, we return a report if the user is an author, for rating and interactions, we do not check the user and just return the information
     *
     * @param bookId the bookId of the book to generate a report for (required)
     * @param userId the userId that requests the report - should be checked if it is the author of the book (required)
     * @param info what the user is requesting (required)
     * @return Successful operation (status code 200)
     *         or cannot find book (status code 400)
     *         or cannot find user (status code 401)
     */
    @Operation(
        operationId = "getReportBookIdUserIdInfoGet",
        summary = "report about a book",
        description = "generates and returns a report about the reviews and interactions of the book; depending on the value of info, we return a report if the user is an author, for rating and interactions, we do not check the user and just return the information",
        tags = { "bookData" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", schema = @Schema(implementation = BookData.class))
            }),
            @ApiResponse(responseCode = "400", description = "cannot find book"),
            @ApiResponse(responseCode = "401", description = "cannot find user")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/getReport/{bookId}/{userId}/{info}",
        produces = { "application/json" }
    )
    default ResponseEntity<BookData> getReportBookIdUserIdInfoGet(
        @Parameter(name = "bookId", description = "the bookId of the book to generate a report for", required = true, in = ParameterIn.PATH) @PathVariable("bookId") Long bookId,
        @Parameter(name = "userId", description = "the userId that requests the report - should be checked if it is the author of the book", required = true, in = ParameterIn.PATH) @PathVariable("userId") String userId,
        @Parameter(name = "info", description = "what the user is requesting", required = true, in = ParameterIn.PATH) @PathVariable("info") String info
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "{ \"neutralRev\" : 4, \"avrRating\" : 0, \"mostUpvotedComment\" : 1, \"positiveRev\" : 20, \"negativeRev\" : 15, \"mostUpvotedReview\" : 6, \"bookId\" : 23 }";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
