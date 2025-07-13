package com.sba301.online_ticket_sales.service.impl;

import com.sba301.online_ticket_sales.dto.person.request.PersonCreationRequest;
import com.sba301.online_ticket_sales.dto.person.request.PersonUpdateRequest;
import com.sba301.online_ticket_sales.dto.person.response.PersonResponse;
import com.sba301.online_ticket_sales.entity.Person;
import com.sba301.online_ticket_sales.enums.ErrorCode;
import com.sba301.online_ticket_sales.enums.Occupation;
import com.sba301.online_ticket_sales.exception.AppException;
import com.sba301.online_ticket_sales.mapper.PersonMapper;
import com.sba301.online_ticket_sales.repository.PersonRepository;
import com.sba301.online_ticket_sales.service.CloudinaryService;
import com.sba301.online_ticket_sales.service.PersonService;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PersonServiceImpl implements PersonService {

  PersonRepository personRepository;
  PersonMapper personMapper;
  CloudinaryService cloudinaryService;

  @Override
  public PersonResponse createPerson(PersonCreationRequest request, MultipartFile[] imageFiles) {
    Person person = personMapper.toPerson(request);

    List<String> imageUrls = null;
    if (imageFiles != null && imageFiles.length > 0) {
      imageUrls = uploadPersonImages(imageFiles);
      person.setImages(imageUrls);
    }

    person.setDeleted(false);
    Person savedPerson = personRepository.save(person);
    return personMapper.toPersonResponse(savedPerson);
  }

  @Override
  public PersonResponse updatePerson(
      Integer id, PersonUpdateRequest request, MultipartFile[] imageFiles) {
    Person person =
        personRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
    if (person.isDeleted()) {
      throw new AppException(ErrorCode.PERSON_NOT_FOUND);
    }
    personMapper.updatePersonFromRequest(request, person);
    // Upload new images if provided
    if (imageFiles != null && imageFiles.length > 0) {
      List<String> newImageUrls = uploadPersonImages(imageFiles);
      person.setImages(newImageUrls);
    }
    Person updatedPerson = personRepository.save(person);
    return personMapper.toPersonResponse(updatedPerson);
  }

  @Override
  public void deletePerson(Integer id) {
    Person person =
        personRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
    if (person.isDeleted()) {
      throw new AppException(ErrorCode.PERSON_NOT_FOUND);
    }
    // Xóa liên kết với Movie (bảng movie_directors và movie_actors)
    person.getDirectedMovies().clear();
    person.getActedMovies().clear();
    // Đặt country thành null
    person.setCountry(null);
    person.setDeleted(true);
    personRepository.save(person);
  }

  @Override
  public Page<PersonResponse> getAllPersons(
      Pageable pageable, String keyword, Occupation occupation) {
    Specification<Person> spec =
        (root, query, cb) -> {
          List<Predicate> predicates = new ArrayList<>();
          predicates.add(cb.equal(root.get("isDeleted"), false));
          if (keyword != null && !keyword.isBlank()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
          }
          if (occupation != null) {
            predicates.add(cb.equal(root.get("occupation"), occupation));
          }
          return cb.and(predicates.toArray(new Predicate[0]));
        };
    Page<Person> persons = personRepository.findAll(spec, pageable);
    return persons.map(personMapper::toPersonResponse);
  }

  private List<String> uploadPersonImages(MultipartFile[] imageFiles) {
    List<String> imageUrls = new ArrayList<>();

    for (MultipartFile imageFile : imageFiles) {
      if (imageFile != null && !imageFile.isEmpty()) {
        String imageUrl = uploadSinglePersonImage(imageFile);
        imageUrls.add(imageUrl);
      }
    }

    return imageUrls;
  }

  private String uploadSinglePersonImage(MultipartFile imageFile) {
    try {
      // Validate file
      validateImageFile(imageFile);

      // Convert to byte array
      byte[] imageBytes = imageFile.getBytes();

      // Upload to Cloudinary with person-specific folder
      Map<String, String> uploadResult =
          cloudinaryService.uploadImage(imageBytes, "persons/images");

      String secureUrl = uploadResult.get("secure_url");

      log.info("Successfully uploaded person image: {}", secureUrl);
      return secureUrl;

    } catch (Exception e) {
      log.error("Failed to upload person image: {}", imageFile.getOriginalFilename(), e);
      throw new AppException(ErrorCode.IMAGE_UPLOAD_FAILED);
    }
  }

  private void validateImageFile(MultipartFile file) {
    // Kiểm tra file không null và không empty
    if (file == null || file.isEmpty()) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FILE);
    }

    // Kiểm tra định dạng file
    String contentType = file.getContentType();
    if (contentType == null || !contentType.startsWith("image/")) {
      throw new AppException(ErrorCode.INVALID_IMAGE_FORMAT);
    }

    // Kiểm tra kích thước file (max 5MB)
    if (file.getSize() > 5 * 1024 * 1024) {
      throw new AppException(ErrorCode.FILE_TOO_LARGE);
    }

    // Kiểm tra định dạng cụ thể
    List<String> allowedTypes = List.of("image/jpeg", "image/png", "image/webp");
    if (!allowedTypes.contains(contentType)) {
      throw new AppException(ErrorCode.UNSUPPORTED_IMAGE_FORMAT);
    }
  }

  @Override
  public PersonResponse getPersonDetail(Integer id) {
    Person person =
        personRepository
            .findById(id)
            .orElseThrow(() -> new AppException(ErrorCode.PERSON_NOT_FOUND));
    if (person.isDeleted()) {
      throw new AppException(ErrorCode.PERSON_NOT_FOUND);
    }
    return personMapper.toPersonResponse(person);
  }
}
