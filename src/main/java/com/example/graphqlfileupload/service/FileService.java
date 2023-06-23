package com.example.graphqlfileupload.service;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.graphqlfileupload.model.CaptivePortal;
import com.example.graphqlfileupload.repository.CaptivePortalRepository;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {

  private static final Logger LOGGER = getLogger(FileService.class);


  private final CaptivePortalRepository captivePortalRepository;

  public FileService(final CaptivePortalRepository captivePortalRepository) {
    this.captivePortalRepository = captivePortalRepository;
  }

  public void saveFile(final MultipartFile file,
      final Long captivePortalId)
      throws IOException {
    final String type = file.getContentType();
    assert type != null;

    final CaptivePortal captivePortal = captivePortalRepository.findById(captivePortalId)
        .orElseThrow();
    final String name =
        "output/" + captivePortal.getExternalId() + "/" + file.getOriginalFilename();

    //TODO change this later, for now used to create simple files
    if (type.contains("png")) {
      final ByteArrayInputStream bis = new ByteArrayInputStream(file.getBytes());
      final BufferedImage bImage = ImageIO.read(bis);
      ImageIO.write(bImage, "PNG", new File(name));

      LOGGER.info("Created image {}", name);
    }

    if (type.contains("pdf") || type.contains("html")) {
      try (OutputStream out = new FileOutputStream(name)) {
        out.write(file.getBytes());
      }
      LOGGER.info("Created {} {}", file.getContentType(), name);
    }
  }
}
