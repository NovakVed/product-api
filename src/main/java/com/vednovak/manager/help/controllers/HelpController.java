package com.vednovak.manager.help.controllers;

import com.vednovak.manager.product.utils.SwaggerConstants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.vednovak.manager.product.utils.SwaggerConstants.CLOSE;

@Tag(name = "Help", description = "the Documentation API")
@RestController
@RequestMapping(HelpController.ENDPOINT)
public class HelpController {

        protected static final String ENDPOINT = "/v1/help";

        @Operation(summary = "Redirect to OpenAPI Documentation", description = "Redirects the user to the OpenAPI page containing the API documentation")
        @ApiResponses(value = {
                        @ApiResponse(responseCode = SwaggerConstants.OK, description = "Redirection successful"),
                        @ApiResponse(responseCode = SwaggerConstants.INTERNAL_SERVER_ERROR, description = "Internal server error")
        })
        @GetMapping
        public ResponseEntity<String> redirectToDocumentation() {
                return ResponseEntity
                                .status(HttpStatus.FOUND)
                                .header(HttpHeaders.LOCATION, "/swagger-ui/index.html")
                                .header(HttpHeaders.CONNECTION, CLOSE)
                                .build();
        }
}
