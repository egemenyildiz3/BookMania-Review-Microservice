package nl.tudelft.sem.template.example.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import nl.tudelft.sem.template.example.model.Review;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReportReview
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
public class ReportReview {

  private Review review;

  private String reason;

  public ReportReview review(Review review) {
    this.review = review;
    return this;
  }

  /**
   * Get review
   * @return review
  */
  @Valid 
  @Schema(name = "review", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("review")
  public Review getReview() {
    return review;
  }

  public void setReview(Review review) {
    this.review = review;
  }

  public ReportReview reason(String reason) {
    this.reason = reason;
    return this;
  }

  /**
   * Get reason
   * @return reason
  */
  
  @Schema(name = "reason", example = "offensive", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reason")
  public String getReason() {
    return reason;
  }

  public void setReason(String reason) {
    this.reason = reason;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    ReportReview reportReview = (ReportReview) o;
    return Objects.equals(this.review, reportReview.review) &&
        Objects.equals(this.reason, reportReview.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(review, reason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportReview {\n");
    sb.append("    review: ").append(toIndentedString(review)).append("\n");
    sb.append("    reason: ").append(toIndentedString(reason)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

