package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateInstitutionRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.InstitutionResponse;
import com.LabResourceUtilizationPlatform.Service.InstitutionService;
import com.LabResourceUtilizationPlatform.Service.ServiceImpl.InstitutionServiceImpl;
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

    private final InstitutionServiceImpl institutionService;


    @PostMapping
    public ResponseEntity<InstitutionResponse> createInstitution(@Valid @RequestBody CreateInstitutionRequest request){
        InstitutionResponse response = institutionService.createInstitution(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    public ResponseEntity<InstitutionResponse> getInstitutionById(
            @PathVariable Long id) {
        return ResponseEntity.ok(institutionService.getInstitutionById(id));
    }

    @GetMapping
    public ResponseEntity<List<InstitutionResponse>> getAllInstitutions() {

        return ResponseEntity.ok(institutionService.getAllInstitutions());
    }

    @PutMapping("/{id}")
    public ResponseEntity<InstitutionResponse> updateInstitution(
            @PathVariable Long id,
            @Valid @RequestBody UpdateInstitutionRequest request) {

        return ResponseEntity.ok(institutionService.updateInstitution(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstitution(
            @PathVariable Long id) {

        institutionService.deleteInstitution(id);
        return ResponseEntity.noContent().build();
    }
}
