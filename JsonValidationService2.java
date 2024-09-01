package com.example.jsonvalidation.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

@Service
public class JsonValidationService2 {

    private final JsonSchema jsonSchema;

    public JsonValidationService() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode schemaNode = mapper.readTree(new ClassPathResource("schema.json").getInputStream());
        JsonSchemaFactory schemaFactory = JsonSchemaFactory.getInstance();
        jsonSchema = schemaFactory.getSchema(schemaNode);
    }

    public Set<String> validateJson(String json) {
        Set<String> errors = new HashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode jsonNode = mapper.readTree(json);
            Set<ValidationMessage> schemaValidationMessages = jsonSchema.validate(jsonNode);
            
            // Collect validation errors from the schema validator
            schemaValidationMessages.forEach(v -> errors.add("Schema Validation Error: " + v.getMessage()));

            // Additional validation for regex patterns and possible values
            JsonNode columnsNode = jsonNode.path("Columns");
            if (columnsNode.isArray()) {
                for (JsonNode column : columnsNode) {
                    validateColumn(column, errors);
                }
            }

        } catch (IOException e) {
            errors.add("Failed to parse JSON: " + e.getMessage());
        }
        return errors;
    }

    private void validateColumn(JsonNode column, Set<String> errors) {
        String datatype = column.path("datatype").asText();
        JsonNode possibleValuesNode = column.path("possible_values");
        JsonNode conditionsNode = column.path("conditions");

        // Validate possible values
        if (possibleValuesNode.isArray()) {
            Iterator<JsonNode> possibleValues = possibleValuesNode.elements();
            while (possibleValues.hasNext()) {
                JsonNode valueNode = possibleValues.next();
                if (!validateDataType(valueNode.asText(), datatype)) {
                    errors.add("Value " + valueNode.asText() + " does not match datatype " + datatype);
                }
            }
        }

        // Validate regex pattern
        String pattern = conditionsNode.path("pattern").asText();
        if (!pattern.isEmpty()) {
            try {
                Pattern.compile(pattern);
            } catch (PatternSyntaxException e) {
                errors.add("Invalid regex pattern: " + pattern);
            }
        }
    }

    private boolean validateDataType(String value, String datatype) {
        try {
            switch (datatype) {
                case "integer":
                    Integer.parseInt(value);
                    break;
                case "string":
                    // Strings are always valid for the string datatype
                    break;
                // Add more cases as needed for other datatypes
                default:
                    return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
