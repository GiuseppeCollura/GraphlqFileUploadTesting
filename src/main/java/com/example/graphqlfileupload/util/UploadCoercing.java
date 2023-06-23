package com.example.graphqlfileupload.util;


import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;
import org.springframework.web.multipart.MultipartFile;

//TODO not sure if this should be in the util directory.
public class UploadCoercing implements Coercing<MultipartFile, MultipartFile> {

  @Override
  public MultipartFile parseLiteral(final Object input) throws CoercingParseLiteralException {
    throw new CoercingParseLiteralException("Parsing literal of 'MultipartFile' is not supported");
  }

  @Override
  public MultipartFile parseValue(final Object input) throws CoercingParseValueException {
    if (input instanceof MultipartFile) {
      return (MultipartFile) input;
    }
    throw new CoercingParseValueException(
        String.format("Expected a 'MultipartFile' like object but was '%s'.",
            input != null ? input.getClass() : null)
    );
  }

  @Override
  public MultipartFile serialize(final Object dataFetcherResult) throws CoercingSerializeException {
    throw new CoercingSerializeException("Upload is an input-only type");
  }
}
