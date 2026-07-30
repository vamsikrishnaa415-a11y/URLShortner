package com.example.urlshortener.urlservice.mapper;

import org.springframework.stereotype.Component;
import com.example.urlshortener.urlservice.dto.ShortUrlResponseDto;
import com.example.urlshortener.urlservice.entity.ShortUrl;

@Component
public class ShortUrlMapper {

    public ShortUrlResponseDto toResponseDto(ShortUrl entity) {
        return new ShortUrlResponseDto(
                entity.getId(),
                entity.getOriginalUrl(),
                entity.getShortCode(),
                entity.getCustomAlias(),
                entity.getCreatedAt(),
                entity.getExpiryDate(),
                entity.getActive(),
                entity.getClickCount());
    }
}