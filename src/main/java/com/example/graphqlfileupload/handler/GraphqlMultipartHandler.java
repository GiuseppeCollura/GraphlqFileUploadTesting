package com.example.graphqlfileupload.handler;

import static org.slf4j.LoggerFactory.getLogger;

import com.example.graphqlfileupload.mapper.MultipartVariableMapper;
import com.example.graphqlfileupload.util.MultipartGraphQlRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.http.MediaType;
import org.springframework.util.AlternativeJdkIdGenerator;
import org.springframework.util.Assert;
import org.springframework.util.IdGenerator;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;
import reactor.core.publisher.Mono;

public class GraphqlMultipartHandler {

  public static final List<MediaType> SUPPORTED_RESPONSE_MEDIA_TYPES =
      Arrays.asList(MediaType.APPLICATION_GRAPHQL_RESPONSE, MediaType.APPLICATION_JSON);

  private static final Logger LOGGER = getLogger(GraphqlMultipartHandler.class);

  private final WebGraphQlHandler graphQlHandler;
  private final ObjectMapper objectMapper;
  private final IdGenerator idGenerator = new AlternativeJdkIdGenerator();

  public GraphqlMultipartHandler(final WebGraphQlHandler graphQlHandler,
      final ObjectMapper objectMapper) {
    Assert.notNull(graphQlHandler, "WebGraphQlHandler is required");
    Assert.notNull(objectMapper, "ObjectMapper is required");
    this.graphQlHandler = graphQlHandler;
    this.objectMapper = objectMapper;
  }

  private static Map<String, MultipartFile> readMultipartBody(final ServerRequest request) {
    try {
      final AbstractMultipartHttpServletRequest abstractMultipartHttpServletRequest = (AbstractMultipartHttpServletRequest) request.servletRequest();
      return abstractMultipartHttpServletRequest.getFileMap();
    } catch (final RuntimeException ex) {
      throw new ServerWebInputException("Error while reading request parts", null, ex);
    }
  }

  private static MediaType selectResponseMediaType(final ServerRequest serverRequest) {
    for (final MediaType accepted : serverRequest.headers().accept()) {
      if (SUPPORTED_RESPONSE_MEDIA_TYPES.contains(accepted)) {
        return accepted;
      }
    }
    return MediaType.APPLICATION_JSON;
  }

  public ServerResponse handleRequest(final ServerRequest serverRequest) {
    final Optional<String> operation = serverRequest.param("operations");
    final Optional<String> mapParam = serverRequest.param("map");
    final Map<String, Object> inputQuery = readJson(operation, new TypeReference<>() {
    });
    final Map<String, Object> queryVariables;
    if (inputQuery.containsKey("variables")) {
      queryVariables = (Map<String, Object>) inputQuery.get("variables");
    } else {
      queryVariables = new HashMap<>();
    }
    Map<String, Object> extensions = new HashMap<>();
    if (inputQuery.containsKey("extensions")) {
      extensions = (Map<String, Object>) inputQuery.get("extensions");
    }

    final Map<String, MultipartFile> fileParams = readMultipartBody(serverRequest);
    final Map<String, List<String>> fileMapInput = readJson(mapParam, new TypeReference<>() {
    });
    fileMapInput.forEach((final String fileKey, final List<String> objectPaths) -> {
      final MultipartFile file = fileParams.get(fileKey);
      if (file != null) {
        objectPaths.forEach((final String objectPath) -> {
          MultipartVariableMapper.mapVariable(
              objectPath,
              queryVariables,
              file
          );
        });
      }
    });

    final String query = (String) inputQuery.get("query");
    final String opName = (String) inputQuery.get("operationName");

    final WebGraphQlRequest graphQlRequest = new MultipartGraphQlRequest(
        query,
        opName,
        queryVariables,
        extensions,
        serverRequest.uri(), serverRequest.headers().asHttpHeaders(),
        this.idGenerator.generateId().toString(), LocaleContextHolder.getLocale());

    if (LOGGER.isDebugEnabled()) {
      LOGGER.debug("Executing: " + graphQlRequest);
    }

    final Mono<ServerResponse> responseMono = this.graphQlHandler.handleRequest(graphQlRequest)
        .map(response -> {
          if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Execution complete");
          }
          final ServerResponse.BodyBuilder builder = ServerResponse.ok();
          builder.headers(headers -> headers.putAll(response.getResponseHeaders()));
          builder.contentType(selectResponseMediaType(serverRequest));
          return builder.body(response.toMap());
        });

    return ServerResponse.async(responseMono);
  }

  private <T> T readJson(final Optional<String> string,
      final TypeReference<T> t) {
    final Map<String, Object> map = new HashMap<>();
    if (string.isPresent()) {
      try {
        return objectMapper.readValue(string.get(), t);
      } catch (final JsonProcessingException e) {
        throw new RuntimeException(e);
      }
    }
    return (T) map;
  }


}
