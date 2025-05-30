package com.forexservice.service;

import com.forexservice.data.dto.BulkConversionResponseDto;
import org.springframework.web.multipart.MultipartFile;

public interface BulkConversionService {
    BulkConversionResponseDto processCsvFile(MultipartFile file);
}
