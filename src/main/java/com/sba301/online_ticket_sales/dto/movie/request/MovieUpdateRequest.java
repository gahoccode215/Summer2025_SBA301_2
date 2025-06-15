package com.sba301.online_ticket_sales.dto.movie.request;

import com.sba301.online_ticket_sales.enums.AgeRestriction;
import com.sba301.online_ticket_sales.enums.MovieFormat;
import com.sba301.online_ticket_sales.enums.MovieStatus;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.validator.constraints.URL;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class MovieUpdateRequest {

  @Size(min = 1, max = 255, message = "Tiêu đề phim phải từ 1-255 ký tự")
  String title;

  @Size(max = 2000, message = "Mô tả phim không được vượt quá 2000 ký tự")
  String description;

  @Positive(message = "Thời lượng phim phải là số dương")
  @Min(value = 1, message = "Thời lượng phim phải ít nhất 1 phút")
  @Max(value = 600, message = "Thời lượng phim không được vượt quá 600 phút")
  Integer duration;

  @PastOrPresent(message = "Ngày phát hành không được là ngày tương lai")
  LocalDate releaseDate;

  @FutureOrPresent(message = "Ngày công chiếu phải là ngày hiện tại hoặc tương lai")
  LocalDate premiereDate;

  @FutureOrPresent(message = "Ngày kết thúc chiếu phải là ngày hiện tại hoặc tương lai")
  LocalDate endDate;

  @URL(message = "URL thumbnail không hợp lệ")
  String thumbnailUrl;

  @URL(message = "URL trailer không hợp lệ")
  String trailerUrl;

  MovieStatus movieStatus;

  AgeRestriction ageRestriction;

  @Size(max = 10, message = "Phim không được có quá 10 định dạng chiếu")
  List<MovieFormat> availableFormats;

  @Positive(message = "ID quốc gia phải là số dương")
  Integer countryId;

  @Size(max = 10, message = "Phim không được có quá 10 thể loại")
  List<@Positive(message = "ID thể loại phải là số dương") Integer> genreIds;

  @Size(max = 5, message = "Phim không được có quá 5 đạo diễn")
  List<@Positive(message = "ID đạo diễn phải là số dương") Integer> directorIds;

  @Size(max = 20, message = "Phim không được có quá 20 diễn viên")
  List<@Positive(message = "ID diễn viên phải là số dương") Integer> actorIds;

  Boolean isDeleted;

  Boolean isPublished;

  // Custom validation methods for update
  @AssertTrue(message = "Ngày công chiếu phải sau hoặc bằng ngày phát hành")
  private boolean isPremiereDateValidForUpdate() {
    if (releaseDate == null || premiereDate == null) {
      return true;
    }
    return !premiereDate.isBefore(releaseDate);
  }

  @AssertTrue(message = "Ngày kết thúc chiếu phải sau ngày công chiếu")
  private boolean isEndDateValidForUpdate() {
    if (premiereDate == null || endDate == null) {
      return true;
    }
    return !endDate.isBefore(premiereDate);
  }

  @AssertTrue(message = "Phim IMAX phải có định dạng IMAX hoặc IMAX_3D")
  private boolean isImaxFormatValidForUpdate() {
    if (movieStatus != MovieStatus.IMAX) {
      return true;
    }
    if (availableFormats == null || availableFormats.isEmpty()) {
      return true;
    }
    return availableFormats.contains(MovieFormat.IMAX)
        || availableFormats.contains(MovieFormat.IMAX_3D);
  }

  @AssertTrue(message = "Không thể xóa phim đang có lịch chiếu")
  private boolean isDeletionValid() {
    if (isDeleted == null || !isDeleted) {
      return true;
    }
    return true;
  }

  @AssertTrue(message = "Không thể hủy xuất bản phim đang có vé đã bán")
  private boolean isPublishStatusValid() {
    if (isPublished == null || isPublished) {
      return true;
    }
    return true;
  }

  public boolean hasUpdates() {
    return title != null
        || description != null
        || duration != null
        || releaseDate != null
        || premiereDate != null
        || endDate != null
        || thumbnailUrl != null
        || trailerUrl != null
        || movieStatus != null
        || ageRestriction != null
        || availableFormats != null
        || countryId != null
        || genreIds != null
        || directorIds != null
        || actorIds != null
        || isDeleted != null
        || isPublished != null;
  }

  public boolean hasCriticalUpdates() {
    return movieStatus != null || isDeleted != null || isPublished != null;
  }
}
