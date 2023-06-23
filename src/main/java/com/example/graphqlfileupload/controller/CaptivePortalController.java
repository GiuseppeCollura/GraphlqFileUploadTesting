package com.example.graphqlfileupload.controller;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.graphqlfileupload.model.CaptivePortal;
import com.example.graphqlfileupload.model.File;
import com.example.graphqlfileupload.service.CaptivePortalService;
import java.io.IOException;
import java.util.List;
import org.slf4j.Logger;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class CaptivePortalController {

  private static final Logger LOGGER = getLogger(CaptivePortalController.class);

  private final CaptivePortalService captivePortalService;

  public CaptivePortalController(final CaptivePortalService captivePortalService) {
    this.captivePortalService = captivePortalService;
  }

  @MutationMapping
  public CaptivePortal createCaptivePortal(@Argument("cp") final CaptivePortal cp) {
    LOGGER.info("Creating captive portal");
    return captivePortalService.createCaptivePortal(cp);
  }

  @QueryMapping
  public List<File> workspace(@Argument("captivePortalId") final Long captivePortalId)
      throws IOException {
    LOGGER.info("Collecting captive portal workspace");
    return captivePortalService.getWorkspace(captivePortalId);
  }
}
