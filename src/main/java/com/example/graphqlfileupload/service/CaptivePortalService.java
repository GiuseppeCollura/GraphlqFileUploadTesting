package com.example.graphqlfileupload.service;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.graphqlfileupload.model.CaptivePortal;
import com.example.graphqlfileupload.repository.CaptivePortalRepository;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class CaptivePortalService {

  private static final Logger LOGGER = getLogger(CaptivePortalService.class);

  private final CaptivePortalRepository captivePortalRepository;

  public CaptivePortalService(final CaptivePortalRepository captivePortalRepository) {
    this.captivePortalRepository = captivePortalRepository;
  }

  public CaptivePortal createCaptivePortal(final CaptivePortal cp) {
    final CaptivePortal save = captivePortalRepository.save(cp);
    final File f = new File("output/" + save.getExternalId());

    if (f.mkdir()) {
      LOGGER.info("Directory has been created successfully");
    } else {
      LOGGER.warn("Directory cannot be created");
    }

    return save;
  }

  public List<com.example.graphqlfileupload.model.File> getWorkspace(final Long captivePortalId)
      throws IOException {
    final CaptivePortal captivePortal = captivePortalRepository.findById(captivePortalId)
        .orElseThrow();

    final String path = "output/" + captivePortal.getExternalId();

    final File f = new File(path);
    final File[] files = f.listFiles();

    final ArrayList<com.example.graphqlfileupload.model.File> requestedFiles = new ArrayList<>();

    for (final File file : files) {
      final String mimeType = Files.probeContentType(file.toPath());

      requestedFiles.add(new com.example.graphqlfileupload.model.File().toBuilder()
          .name(file.getName())
          .size(file.length())
          .type(mimeType)
          .build());
    }

    return requestedFiles;
  }
}
