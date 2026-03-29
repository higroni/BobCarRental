package com.bobcarrental.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderTemplateResponse {
    private Long id;
    private String templateName;
    private String templateContent;
    private String description;
    private Boolean active;
    private Boolean isDefault;
    private String templateType;
    private Integer lineCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

// Made with Bob
