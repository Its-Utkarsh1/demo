package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;
import com.LabResourceUtilizationPlatform.Service.InstitutionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/institutions")
public class InstitutionController {

    private final InstitutionService institutionService;

    @PostMapping
    public ResponseEntity<InstitutionResponse> createInstitution(@Valid @RequestBody CreateInstitutionRequest request){
        InstitutionResponse response = institutionService.createInstitution(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{instituteCode}")
    public ResponseEntity<InstitutionResponse> getInstitutionByCode(
            @PathVariable String instituteCode) {
        return ResponseEntity.ok(institutionService.getInstitutionByCode(instituteCode));
    }

    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> getAllInstitutions() {

        return ResponseEntity.ok(institutionService.getAllInstitutions());
    }

    @PutMapping
    public ResponseEntity<InstitutionResponse> updateInstitution(
            @Valid @RequestBody UpdateInstitutionRequest request) {

        return ResponseEntity.ok(institutionService.updateInstitution(request));
    }

    @DeleteMapping("/{instituteCode}")
    public ResponseEntity<Void> deleteInstitution(
            @PathVariable String instituteCode) {

        institutionService.deleteInstitution(instituteCode);
        return ResponseEntity.noContent().build();
    }
}
