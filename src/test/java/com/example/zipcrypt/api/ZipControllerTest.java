package com.example.zipcrypt.api;

import com.example.zipcrypt.domain.TargetOs;
import com.example.zipcrypt.domain.TargetTool;
import com.example.zipcrypt.service.ArchiveResult;
import com.example.zipcrypt.service.ZipArchiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ZipController.class)
class ZipControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ZipArchiveService zipArchiveService;

    @Test
    void shouldReturnCreatedZipBinary() throws Exception {
        byte[] zipBytes = {0x50, 0x4B, 0x03, 0x04};
        var request = new ZipRequest("sample", "sample.txt", TargetOs.WINDOWS, TargetTool.NATIVE);
        when(zipArchiveService.createEncryptedArchive(any()))
                .thenReturn(new ArchiveResult("id-123", "id-123.zip", zipBytes));

        mockMvc.perform(post("/api/v1/archives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/zip"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"id-123.zip\""))
                .andExpect(header().string("X-Archive-Id", "id-123"))
                .andExpect(content().bytes(zipBytes));
    }
}
