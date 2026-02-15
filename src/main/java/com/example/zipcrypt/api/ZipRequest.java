package com.example.zipcrypt.api;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ZipRequest(
        @NotBlank @Size(max = 10_000) String plainText,
        @NotBlank @Size(max = 200) String sourceFileName,
        @NotNull TargetOs targetOs,
        @NotNull TargetTool targetTool
) {
}
