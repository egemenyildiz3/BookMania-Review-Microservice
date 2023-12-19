package nl.tudelft.sem.template.example.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * Review
 */

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
@Entity
public class Review {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long bookId;

  private Long userId;

  private String title;

  private String text;

  private Long rating;

  @ElementCollection
  @Valid
  private List<Long> commentList;

  private Long upvote;

  private Long downvote;

  private Boolean spoiler;

  /**
   * Gets or Sets bookNotion
   */
  public enum BookNotionEnum {
    POSITIVE("POSITIVE"),
    
    NEGATIVE("NEGATIVE"),
    
    NEUTRAL("NEUTRAL");

    private String value;

    BookNotionEnum(String value) {
      this.value = value;
    }

    @JsonValue
    public String getValue() {
      return value;
    }

    @Override
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static BookNotionEnum fromValue(String value) {
      for (BookNotionEnum b : BookNotionEnum.values()) {
        if (b.value.equals(value)) {
          return b;
        }
      }
      throw new IllegalArgumentException("Unexpected value '" + value + "'");
    }
  }

  private BookNotionEnum bookNotion;

  private Boolean pinned;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate timeCreated;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDate lastEditTime;

  public Review id(Long id) {
    this.id = id;
    return this;
  }

  /**
   * Get id
   * @return id
  */
  
  @Schema(name = "id", example = "10", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("id")
  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Review bookId(Long bookId) {
    this.bookId = bookId;
    return this;
  }

  /**
   * Get bookId
   * @return bookId
  */
  
  @Schema(name = "bookId", example = "1234", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookId")
  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public Review userId(Long userId) {
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

  public Review title(String title) {
    this.title = title;
    return this;
  }

  /**
   * Get title
   * @return title
  */
  
  @Schema(name = "title", example = "wow", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("title")
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public Review text(String text) {
    this.text = text;
    return this;
  }

  /**
   * Get text
   * @return text
  */
  
  @Schema(name = "text", example = "nice book", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("text")
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Review rating(Long rating) {
    this.rating = rating;
    return this;
  }

  /**
   * Get rating
   * @return rating
  */
  
  @Schema(name = "rating", example = "4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("rating")
  public Long getRating() {
    return rating;
  }

  public void setRating(Long rating) {
    this.rating = rating;
  }

  public Review commentList(List<Long> commentList) {
    this.commentList = commentList;
    return this;
  }

  public Review addCommentListItem(Long commentListItem) {
    if (this.commentList == null) {
      this.commentList = new ArrayList<>();
    }
    this.commentList.add(commentListItem);
    return this;
  }

  /**
   * Get commentList
   * @return commentList
  */
  
  @Schema(name = "commentList", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("commentList")
  public List<Long> getCommentList() {
    return commentList;
  }

  public void setCommentList(List<Long> commentList) {
    this.commentList = commentList;
  }

  public Review upvote(Long upvote) {
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

  public Review downvote(Long downvote) {
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

  public Review spoiler(Boolean spoiler) {
    this.spoiler = spoiler;
    return this;
  }

  /**
   * Get spoiler
   * @return spoiler
  */
  
  @Schema(name = "spoiler", example = "true", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("spoiler")
  public Boolean getSpoiler() {
    return spoiler;
  }

  public void setSpoiler(Boolean spoiler) {
    this.spoiler = spoiler;
  }

  public Review bookNotion(BookNotionEnum bookNotion) {
    this.bookNotion = bookNotion;
    return this;
  }

  /**
   * Get bookNotion
   * @return bookNotion
  */
  
  @Schema(name = "bookNotion", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookNotion")
  public BookNotionEnum getBookNotion() {
    return bookNotion;
  }

  public void setBookNotion(BookNotionEnum bookNotion) {
    this.bookNotion = bookNotion;
  }

  public Review pinned(Boolean pinned) {
    this.pinned = pinned;
    return this;
  }

  /**
   * Get pinned
   * @return pinned
  */
  
  @Schema(name = "pinned", example = "false", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("pinned")
  public Boolean getPinned() {
    return pinned;
  }

  public void setPinned(Boolean pinned) {
    this.pinned = pinned;
  }

  public Review timeCreated(LocalDate timeCreated) {
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

  public Review lastEditTime(LocalDate lastEditTime) {
    this.lastEditTime = lastEditTime;
    return this;
  }

  /**
   * Get lastEditTime
   * @return lastEditTime
  */
  @Valid 
  @Schema(name = "lastEditTime", example = "Thu Oct 24 02:00:00 CEST 2013", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("lastEditTime")
  public LocalDate getLastEditTime() {
    return lastEditTime;
  }

  public void setLastEditTime(LocalDate lastEditTime) {
    this.lastEditTime = lastEditTime;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Review review = (Review) o;
    return Objects.equals(this.id, review.id) &&
        Objects.equals(this.bookId, review.bookId) &&
        Objects.equals(this.userId, review.userId) &&
        Objects.equals(this.title, review.title) &&
        Objects.equals(this.text, review.text) &&
        Objects.equals(this.rating, review.rating) &&
        Objects.equals(this.commentList, review.commentList) &&
        Objects.equals(this.upvote, review.upvote) &&
        Objects.equals(this.downvote, review.downvote) &&
        Objects.equals(this.spoiler, review.spoiler) &&
        Objects.equals(this.bookNotion, review.bookNotion) &&
        Objects.equals(this.pinned, review.pinned) &&
        Objects.equals(this.timeCreated, review.timeCreated) &&
        Objects.equals(this.lastEditTime, review.lastEditTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, bookId, userId, title, text, rating, commentList, upvote, downvote, spoiler, bookNotion, pinned, timeCreated, lastEditTime);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Review {\n");
    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    bookId: ").append(toIndentedString(bookId)).append("\n");
    sb.append("    userId: ").append(toIndentedString(userId)).append("\n");
    sb.append("    title: ").append(toIndentedString(title)).append("\n");
    sb.append("    text: ").append(toIndentedString(text)).append("\n");
    sb.append("    rating: ").append(toIndentedString(rating)).append("\n");
    sb.append("    commentList: ").append(toIndentedString(commentList)).append("\n");
    sb.append("    upvote: ").append(toIndentedString(upvote)).append("\n");
    sb.append("    downvote: ").append(toIndentedString(downvote)).append("\n");
    sb.append("    spoiler: ").append(toIndentedString(spoiler)).append("\n");
    sb.append("    bookNotion: ").append(toIndentedString(bookNotion)).append("\n");
    sb.append("    pinned: ").append(toIndentedString(pinned)).append("\n");
    sb.append("    timeCreated: ").append(toIndentedString(timeCreated)).append("\n");
    sb.append("    lastEditTime: ").append(toIndentedString(lastEditTime)).append("\n");
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

