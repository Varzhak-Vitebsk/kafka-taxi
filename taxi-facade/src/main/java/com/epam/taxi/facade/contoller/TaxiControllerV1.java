package com.epam.taxi.facade.contoller;

import com.epam.taxi.facade.model.ErrorResponse;
import com.epam.taxi.facade.model.TaxiPositionRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RequestMapping(path = "/api/public/v1", produces = MediaType.APPLICATION_JSON_VALUE)
@Validated
public interface TaxiControllerV1 {

  @Operation(summary = "Acquire normalized list of cryptos in descending order for a period.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200"),
      @ApiResponse(responseCode = "400", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))
      }),
      @ApiResponse(responseCode = "500", content = {
          @Content(mediaType = "application/json",
              schema = @Schema(implementation = ErrorResponse.class))
      })
  })
  @PostMapping(value = "position")
  Mono<ResponseEntity<Void>> addTaxiPosition(@RequestBody @Valid TaxiPositionRequest request);

}
