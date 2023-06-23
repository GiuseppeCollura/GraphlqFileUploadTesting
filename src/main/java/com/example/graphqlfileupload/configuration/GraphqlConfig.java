package com.example.graphqlfileupload.configuration;

import static com.example.graphqlfileupload.handler.GraphqlMultipartHandler.SUPPORTED_RESPONSE_MEDIA_TYPES;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;

import com.example.graphqlfileupload.handler.GraphqlMultipartHandler;
import com.example.graphqlfileupload.util.UploadCoercing;
import com.fasterxml.jackson.databind.ObjectMapper;
import graphql.schema.GraphQLScalarType;
import org.springframework.boot.autoconfigure.graphql.GraphQlProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.graphql.execution.RuntimeWiringConfigurer;
import org.springframework.graphql.server.WebGraphQlHandler;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
public class GraphqlConfig {

  @Bean
  @Order(1)
  public RouterFunction<ServerResponse> graphQlMultipartRouterFunction(
      final GraphQlProperties properties,
      final WebGraphQlHandler webGraphQlHandler,
      final ObjectMapper objectMapper
  ) {
    final String path = properties.getPath();
    RouterFunctions.Builder builder = RouterFunctions.route();
    final GraphqlMultipartHandler graphqlMultipartHandler = new GraphqlMultipartHandler(
        webGraphQlHandler, objectMapper);
    builder = builder.POST(path, RequestPredicates.contentType(MULTIPART_FORM_DATA)
            .and(RequestPredicates.accept(SUPPORTED_RESPONSE_MEDIA_TYPES.toArray(MediaType[]::new))),
        graphqlMultipartHandler::handleRequest);
    return builder.build();
  }

  @Bean
  public RuntimeWiringConfigurer runtimeWiringConfigurerUpload() {

    final GraphQLScalarType uploadScalar = GraphQLScalarType.newScalar()
        .name("Upload")
        .coercing(new UploadCoercing())
        .build();

    return wiringBuilder -> wiringBuilder.scalar(uploadScalar);
  }
}
