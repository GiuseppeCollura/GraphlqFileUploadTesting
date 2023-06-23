package com.example.graphqlfileupload.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.graphqlfileupload.service.FileService;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class FileController {

  private static final Logger LOGGER = getLogger(FileController.class);

  private final FileService fileService;

  public FileController(final FileService fileService) {
    this.fileService = fileService;
  }

  @MutationMapping(name = "fileUpload")
  public Boolean uploadFile(@Argument final List<MultipartFile> files,
      @Argument("captivePortalId") final Long captivePortalId) throws IOException {
    for (final MultipartFile file : files) {
      LOGGER.info("Retrieved file: name={}", file.getOriginalFilename());

      fileService.saveFile(file, captivePortalId);
    }
    return true;
  }
}
