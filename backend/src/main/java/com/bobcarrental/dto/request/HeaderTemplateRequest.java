package com.bobcarrental.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeaderTemplateRequest {
    @NotBlank(message = "Template name is required")
    @Size(max = 50)
    private String templateName;
    
    @NotBlank(message = "Template content is required")
    private String templateContent;
    
    @Size(max = 200)
    private String description;
    
    @Builder.Default
    private Boolean active = true;
    
    @Builder.Default
    private Boolean isDefault = false;
    
    private String templateType;
}

// Made with Bob
