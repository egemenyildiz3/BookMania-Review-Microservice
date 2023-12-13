package nl.tudelft.sem.template.example.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import nl.tudelft.sem.template.model.Comment;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * ReportComment
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
public class ReportComment {

  private Comment comment;

  private String reason;

  public ReportComment comment(Comment comment) {
    this.comment = comment;
    return this;
  }

  /**
   * Get comment
   * @return comment
  */
  @Valid 
  @Schema(name = "comment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("comment")
  public Comment getComment() {
    return comment;
  }

  public void setComment(Comment comment) {
    this.comment = comment;
  }

  public ReportComment reason(String reason) {
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
    ReportComment reportComment = (ReportComment) o;
    return Objects.equals(this.comment, reportComment.comment) &&
        Objects.equals(this.reason, reportComment.reason);
  }

  @Override
  public int hashCode() {
    return Objects.hash(comment, reason);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class ReportComment {\n");
    sb.append("    comment: ").append(toIndentedString(comment)).append("\n");
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

