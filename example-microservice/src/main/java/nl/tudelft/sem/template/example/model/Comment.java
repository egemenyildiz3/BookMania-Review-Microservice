package nl.tudelft.sem.template.example.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Comment
 */
@Entity
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
public class Comment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long reviewId;

  private Long userId;

  private String text;

  private Long upvote;

  private Long downvote;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate timeCreated;

  public Comment id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Comment reviewId(Long reviewId) {
    this.reviewId = reviewId;
    return this;
  }

  /**
   * Get reviewId
   * @return reviewId
  */
  
  @Schema(name = "reviewId", example = "3", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("reviewId")
  public Long getReviewId() {
    return reviewId;
  }

  public void setReviewId(Long reviewId) {
    this.reviewId = reviewId;
  }

  public Comment userId(Long userId) {
    this.userId = userId;
    return this;
  }

  /**
   * Get userId
   * @return userId
  */
  
  @Schema(name = "userId", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("userId")
  public Long getUserId() {
    return userId;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }

  public Comment text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  
  @Schema(name = "text", example = "bad review", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Comment upvote(Long upvote) {
    this.upvote = upvote;
    return this;
  }

  /**
   * Get upvote
   * @return upvote
  */
  
  @Schema(name = "upvote", example = "453", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("upvote")
  public Long getUpvote() {
    return upvote;
  }

  public void setUpvote(Long upvote) {
    this.upvote = upvote;
  }

  public Comment downvote(Long downvote) {
    this.downvote = downvote;
    return this;
  }

  /**
   * Get downvote
   * @return downvote
  */
  
  @Schema(name = "downvote", example = "345", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("downvote")
  public Long getDownvote() {
    return downvote;
  }

  public void setDownvote(Long downvote) {
    this.downvote = downvote;
  }

  public Comment timeCreated(LocalDate timeCreated) {
    this.timeCreated = timeCreated;
    return this;
  }

  /**
   * Get timeCreated
   * @return timeCreated
  */
  @Valid 
  @Schema(name = "timeCreated", example = "Thu Oct 24 02:00:00 CEST 2013", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("timeCreated")
  public LocalDate getTimeCreated() {
    return timeCreated;
  }

  public void setTimeCreated(LocalDate timeCreated) {
    this.timeCreated = timeCreated;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Comment comment = (Comment) o;
    return Objects.equals(this.id, comment.id) &&
        Objects.equals(this.reviewId, comment.reviewId) &&
        Objects.equals(this.userId, comment.userId) &&
        Objects.equals(this.text, comment.text) &&
        Objects.equals(this.upvote, comment.upvote) &&
        Objects.equals(this.downvote, comment.downvote) &&
        Objects.equals(this.timeCreated, comment.timeCreated);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, reviewId, userId, text, upvote, downvote, timeCreated);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Comment {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    reviewId: ").append(toIndentedString(reviewId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    upvote: ").append(toIndentedString(upvote)).append("\n");
    sb.append("    downvote: ").append(toIndentedString(downvote)).append("\n");
    sb.append("    timeCreated: ").append(toIndentedString(timeCreated)).append("\n");
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

