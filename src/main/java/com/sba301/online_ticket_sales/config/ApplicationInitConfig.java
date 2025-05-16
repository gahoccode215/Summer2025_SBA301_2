package com.sba301.online_ticket_sales.config;

import com.sba301.online_ticket_sales.entity.Country;
import com.sba301.online_ticket_sales.entity.Genre;
import com.sba301.online_ticket_sales.repository.CountryRepository;
import com.sba301.online_ticket_sales.repository.GenreRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ApplicationInitConfig {

    CountryRepository countryRepository;
    GenreRepository genreRepository;

    @Bean
    ApplicationRunner applicationRunner(){
        log.info("Initializing application.....");
        return args -> {
            if(countryRepository.count() == 0){
                countryRepository.save(Country.builder().name("Việt Nam").build());
                countryRepository.save(Country.builder().name("Mỹ").build());
                countryRepository.save(Country.builder().name("Hàn Quốc").build());
                countryRepository.save(Country.builder().name("Nhật Bản").build());
                countryRepository.save(Country.builder().name("Trung Quốc").build());
            }
            if(genreRepository.count() == 0){
                genreRepository.save(Genre.builder().name("Hành động").build());
                genreRepository.save(Genre.builder().name("Kinh dị").build());
                genreRepository.save(Genre.builder().name("Tình cảm").build());
                genreRepository.save(Genre.builder().name("Hoạt hình").build());
                genreRepository.save(Genre.builder().name("Viễn tưởng").build());
                genreRepository.save(Genre.builder().name("Hài").build());
            }
            log.info("Application initialization completed .....");
        };
    }
}
