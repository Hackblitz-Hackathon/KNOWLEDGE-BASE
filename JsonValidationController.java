package com.example.jsonvalidation.controller;

import com.example.jsonvalidation.service.JsonValidationService;
import com.networknt.schema.ValidationMessage;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/api/validate")
public class JsonValidationController {

    private final JsonValidationService validationService;

    public JsonValidationController(JsonValidationService validationService) {
        this.validationService = validationService;
    }

    @PostMapping
    public Set<ValidationMessage> validateJson(@RequestBody String json) {
        return validationService.validateJson(json);
    }
}
