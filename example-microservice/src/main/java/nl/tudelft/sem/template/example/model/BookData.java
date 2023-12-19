package nl.tudelft.sem.template.example.model;

import java.net.URI;
import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.openapitools.jackson.nullable.JsonNullable;
import java.time.OffsetDateTime;
import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.*;
import io.swagger.v3.oas.annotations.media.Schema;


import java.util.*;
import javax.annotation.Generated;

/**
 * BookData
 */
@Entity
@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2023-12-05T16:23:47.330644900+01:00[Europe/Amsterdam]")
public class BookData {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookId;

  private Long avrRating;

  private Long mostUpvotedReview;

  private Long mostUpvotedComment;

  private Integer positiveRev;

  private Integer negativeRev;

  private Integer neutralRev;

  public BookData bookId(Long bookId) {
    this.bookId = bookId;
    return this;
  }

  /**
   * Get bookId
   * @return bookId
  */
  
  @Schema(name = "bookId", example = "23", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("bookId")
  public Long getBookId() {
    return bookId;
  }

  public void setBookId(Long bookId) {
    this.bookId = bookId;
  }

  public BookData avrRating(Long avrRating) {
    this.avrRating = avrRating;
    return this;
  }

  /**
   * Get avrRating
   * @return avrRating
  */
  
  @Schema(name = "avrRating", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("avrRating")
  public Long getAvrRating() {
    return avrRating;
  }

  public void setAvrRating(Long avrRating) {
    this.avrRating = avrRating;
  }

  public BookData mostUpvotedReview(Long mostUpvotedReview) {
    this.mostUpvotedReview = mostUpvotedReview;
    return this;
  }

  /**
   * Get mostUpvotedReview
   * @return mostUpvotedReview
  */
  
  @Schema(name = "mostUpvotedReview", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mostUpvotedReview")
  public Long getMostUpvotedReview() {
    return mostUpvotedReview;
  }

  public void setMostUpvotedReview(Long mostUpvotedReview) {
    this.mostUpvotedReview = mostUpvotedReview;
  }

  public BookData mostUpvotedComment(Long mostUpvotedComment) {
    this.mostUpvotedComment = mostUpvotedComment;
    return this;
  }

  /**
   * Get mostUpvotedComment
   * @return mostUpvotedComment
  */
  
  @Schema(name = "mostUpvotedComment", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("mostUpvotedComment")
  public Long getMostUpvotedComment() {
    return mostUpvotedComment;
  }

  public void setMostUpvotedComment(Long mostUpvotedComment) {
    this.mostUpvotedComment = mostUpvotedComment;
  }

  public BookData positiveRev(Integer positiveRev) {
    this.positiveRev = positiveRev;
    return this;
  }

  /**
   * Get positiveRev
   * @return positiveRev
  */
  
  @Schema(name = "positiveRev", example = "20", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("positiveRev")
  public Integer getPositiveRev() {
    return positiveRev;
  }

  public void setPositiveRev(Integer positiveRev) {
    this.positiveRev = positiveRev;
  }

  public BookData negativeRev(Integer negativeRev) {
    this.negativeRev = negativeRev;
    return this;
  }

  /**
   * Get negativeRev
   * @return negativeRev
  */
  
  @Schema(name = "negativeRev", example = "15", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("negativeRev")
  public Integer getNegativeRev() {
    return negativeRev;
  }

  public void setNegativeRev(Integer negativeRev) {
    this.negativeRev = negativeRev;
  }

  public BookData neutralRev(Integer neutralRev) {
    this.neutralRev = neutralRev;
    return this;
  }

  /**
   * Get neutralRev
   * @return neutralRev
  */
  
  @Schema(name = "neutralRev", example = "4", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
  @JsonProperty("neutralRev")
  public Integer getNeutralRev() {
    return neutralRev;
  }

  public void setNeutralRev(Integer neutralRev) {
    this.neutralRev = neutralRev;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    BookData bookData = (BookData) o;
    return Objects.equals(this.bookId, bookData.bookId) &&
        Objects.equals(this.avrRating, bookData.avrRating) &&
        Objects.equals(this.mostUpvotedReview, bookData.mostUpvotedReview) &&
        Objects.equals(this.mostUpvotedComment, bookData.mostUpvotedComment) &&
        Objects.equals(this.positiveRev, bookData.positiveRev) &&
        Objects.equals(this.negativeRev, bookData.negativeRev) &&
        Objects.equals(this.neutralRev, bookData.neutralRev);
  }

  @Override
  public int hashCode() {
    return Objects.hash(bookId, avrRating, mostUpvotedReview, mostUpvotedComment, positiveRev, negativeRev, neutralRev);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class BookData {\n");
    sb.append("    bookId: ").append(toIndentedString(bookId)).append("\n");
    sb.append("    avrRating: ").append(toIndentedString(avrRating)).append("\n");
    sb.append("    mostUpvotedReview: ").append(toIndentedString(mostUpvotedReview)).append("\n");
    sb.append("    mostUpvotedComment: ").append(toIndentedString(mostUpvotedComment)).append("\n");
    sb.append("    positiveRev: ").append(toIndentedString(positiveRev)).append("\n");
    sb.append("    negativeRev: ").append(toIndentedString(negativeRev)).append("\n");
    sb.append("    neutralRev: ").append(toIndentedString(neutralRev)).append("\n");
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

