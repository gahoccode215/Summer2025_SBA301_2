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
public class MovieCreationRequest {

  @NotBlank(message = "Tiêu đề phim không được để trống")
  @Size(min = 1, max = 255, message = "Tiêu đề phim phải từ 1-255 ký tự")
  String title;

  @NotBlank(message = "Mô tả phim không được để trống")
  @Size(max = 2000, message = "Mô tả phim không được vượt quá 2000 ký tự")
  String description;

  @NotNull(message = "Thời lượng phim không được để trống")
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

  @NotNull(message = "Trạng thái phim không được để trống")
  MovieStatus movieStatus;

  @NotNull(message = "Độ tuổi giới hạn không được để trống")
  AgeRestriction ageRestriction;

  @NotEmpty(message = "Phim phải có ít nhất một định dạng chiếu")
  @Size(max = 10, message = "Phim không được có quá 10 định dạng chiếu")
  List<MovieFormat> availableFormats;

  @NotNull(message = "Quốc gia không được để trống")
  @Positive(message = "ID quốc gia phải là số dương")
  Integer countryId;

  @NotEmpty(message = "Phim phải có ít nhất một thể loại")
  @Size(max = 10, message = "Phim không được có quá 10 thể loại")
  List<@Positive(message = "ID thể loại phải là số dương") Integer> genreIds;

  @NotEmpty(message = "Phim phải có ít nhất một đạo diễn")
  @Size(max = 5, message = "Phim không được có quá 5 đạo diễn")
  List<@Positive(message = "ID đạo diễn phải là số dương") Integer> directorIds;

  @NotEmpty(message = "Phim phải có ít nhất một diễn viên")
  @Size(max = 20, message = "Phim không được có quá 20 diễn viên")
  List<@Positive(message = "ID diễn viên phải là số dương") Integer> actorIds;

  @AssertTrue(message = "Ngày công chiếu phải sau hoặc bằng ngày phát hành")
  private boolean isPremiereDateValid() {
    if (releaseDate == null || premiereDate == null) {
      return true;
    }
    return !premiereDate.isBefore(releaseDate);
  }

  @AssertTrue(message = "Ngày kết thúc chiếu phải sau ngày công chiếu")
  private boolean isEndDateValid() {
    if (premiereDate == null || endDate == null) {
      return true;
    }
    return !endDate.isBefore(premiereDate);
  }

  @AssertTrue(message = "Phim IMAX phải có định dạng IMAX hoặc IMAX_3D")
  private boolean isImaxFormatValid() {
    if (movieStatus != MovieStatus.IMAX) {
      return true;
    }
    if (availableFormats == null || availableFormats.isEmpty()) {
      return false;
    }
    return availableFormats.contains(MovieFormat.IMAX)
        || availableFormats.contains(MovieFormat.IMAX_3D);
  }
}
