/**
 * NOTE: This class is auto generated by OpenAPI Generator (https://openapi-generator.tech) (6.6.0).
 * https://openapi-generator.tech
 * Do not edit the class manually.
 */
package nl.tudelft.sem.template.example.api;

import nl.tudelft.sem.template.example.api.ApiUtil;
import nl.tudelft.sem.template.example.model.ReportComment;
import nl.tudelft.sem.template.example.model.ReportReview;
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
@Tag(name = "report", description = "reporting inappropriate reviews and comments")
public interface ReportedApi {

    default Optional<NativeWebRequest> getRequest() {
        return Optional.empty();
    }

    /**
     * GET /reported/comments/{userId} : see reported comments
     * shows all of the reported comments to the admin
     *
     * @param userId user that is trying to get reported comments, check if he is admin (required)
     * @return Successful operation (status code 200)
     *         or No reported reviews (status code 400)
     *         or No found user (status code 401)
     *         or permission denied - not admin (status code 403)
     */
    @Operation(
        operationId = "reportedCommentsUserIdGet",
        summary = "see reported comments",
        description = "shows all of the reported comments to the admin",
        tags = { "report" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReportComment.class)))
            }),
            @ApiResponse(responseCode = "400", description = "No reported reviews"),
            @ApiResponse(responseCode = "401", description = "No found user"),
            @ApiResponse(responseCode = "403", description = "permission denied - not admin")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/reported/comments/{userId}",
        produces = { "application/json" }
    )
    default ResponseEntity<List<ReportComment>> reportedCommentsUserIdGet(
        @Parameter(name = "userId", description = "user that is trying to get reported comments, check if he is admin", required = true, in = ParameterIn.PATH) @PathVariable("userId") Long userId
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"reason\" : \"offensive\", \"comment\" : { \"downvote\" : 345, \"timeCreated\" : \"2013-10-24T00:00:00.000+00:00\", \"id\" : 3, \"text\" : \"bad review\", \"reviewId\" : 3, \"userId\" : 10, \"upvote\" : 453 } }, { \"reason\" : \"offensive\", \"comment\" : { \"downvote\" : 345, \"timeCreated\" : \"2013-10-24T00:00:00.000+00:00\", \"id\" : 3, \"text\" : \"bad review\", \"reviewId\" : 3, \"userId\" : 10, \"upvote\" : 453 } } ]";
                    nl.tudelft.sem.template.example.api.ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }


    /**
     * GET /reported/reviews/{userId} : see reported reviews
     * shows all of the reported reviews to the admin
     *
     * @param userId user that is trying to get reported reviews, check if he is admin (required)
     * @return Successful operation (status code 200)
     *         or No reported reviews (status code 400)
     *         or No found user (status code 401)
     *         or permission denied - not admin (status code 403)
     */
    @Operation(
        operationId = "reportedReviewsUserIdGet",
        summary = "see reported reviews",
        description = "shows all of the reported reviews to the admin",
        tags = { "report" },
        responses = {
            @ApiResponse(responseCode = "200", description = "Successful operation", content = {
                @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReportReview.class)))
            }),
            @ApiResponse(responseCode = "400", description = "No reported reviews"),
            @ApiResponse(responseCode = "401", description = "No found user"),
            @ApiResponse(responseCode = "403", description = "permission denied - not admin")
        }
    )
    @RequestMapping(
        method = RequestMethod.GET,
        value = "/reported/reviews/{userId}",
        produces = { "application/json" }
    )
    default ResponseEntity<List<ReportReview>> reportedReviewsUserIdGet(
        @Parameter(name = "userId", description = "user that is trying to get reported reviews, check if he is admin", required = true, in = ParameterIn.PATH) @PathVariable("userId") Long userId
    ) {
        getRequest().ifPresent(request -> {
            for (MediaType mediaType: MediaType.parseMediaTypes(request.getHeader("Accept"))) {
                if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                    String exampleString = "[ { \"reason\" : \"offensive\", \"review\" : { \"commentList\" : [ 0, 0 ], \"pinned\" : false, \"rating\" : 4, \"title\" : \"wow\", \"userId\" : 10, \"upvote\" : 453, \"bookNotion\" : \"POSITIVE\", \"bookId\" : 1234, \"downvote\" : 345, \"spoiler\" : true, \"timeCreated\" : \"2013-10-24T00:00:00.000+00:00\", \"id\" : 10, \"text\" : \"nice book\", \"lastEditTime\" : \"2013-10-24T00:00:00.000+00:00\" } }, { \"reason\" : \"offensive\", \"review\" : { \"commentList\" : [ 0, 0 ], \"pinned\" : false, \"rating\" : 4, \"title\" : \"wow\", \"userId\" : 10, \"upvote\" : 453, \"bookNotion\" : \"POSITIVE\", \"bookId\" : 1234, \"downvote\" : 345, \"spoiler\" : true, \"timeCreated\" : \"2013-10-24T00:00:00.000+00:00\", \"id\" : 10, \"text\" : \"nice book\", \"lastEditTime\" : \"2013-10-24T00:00:00.000+00:00\" } } ]";
                    ApiUtil.setExampleResponse(request, "application/json", exampleString);
                    break;
                }
            }
        });
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);

    }

}
