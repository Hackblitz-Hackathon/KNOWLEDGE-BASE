package com.example.jsonvalidation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SchemaValidators;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Set;

@Service
public class JsonValidationService {

    private final JsonSchema jsonSchema;

    public JsonValidationService() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(new ClassPathResource("schema.json").getInputStream());
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance();
        jsonSchema = schemaFactory.getSchema(schemaNode);
    }

    public Set<ValidationMessage> validateJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            return jsonSchema.validate(jsonNode);
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse JSON", e);
        }
    }
}
