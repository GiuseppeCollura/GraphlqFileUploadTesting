package com.example.graphqlfileupload.util;

import java.net.URI;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.springframework.graphql.ExecutionGraphQlRequest;
import org.springframework.graphql.server.WebGraphQlRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;

//TODO not sure if this should be in the util directory.
public class MultipartGraphQlRequest extends WebGraphQlRequest implements ExecutionGraphQlRequest {

  private final String document;
  private final String operationName;
  private final Map<String, Object> variables;
  private final Map<String, Object> extensions;


  public MultipartGraphQlRequest(
      final String query,
      final String operationName,
      final Map<String, Object> variables,
      final Map<String, Object> extensions,
      final URI uri,
      final HttpHeaders headers,
      final String id,
      @Nullable final Locale locale) {

    super(uri, headers, fakeBody(query), id, locale);

    this.document = query;
    this.operationName = operationName;
    this.variables = variables;
    this.extensions = extensions;
  }

  private static Map<String, Object> fakeBody(final String query) {
    final Map<String, Object> fakeBody = new HashMap<>();
    fakeBody.put("query", query);
    return fakeBody;
  }

  @Override
  public String getDocument() {
    return document;
  }

  @Override
  public Map<String, Object> getExtensions() {
    return extensions;
  }

  @Override
  public String getOperationName() {
    return operationName;
  }

  @Override
  public Map<String, Object> getVariables() {
    return variables;
  }
}
