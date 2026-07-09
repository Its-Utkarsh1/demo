package com.LabResourceUtilizationPlatform.Controller;
import com.LabResourceUtilizationPlatform.Dtos.Request.CreateEquipmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateEquipmentRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.EquipmentResponse;
import com.LabResourceUtilizationPlatform.Service.EquipmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/equipment")
public class EquipmentController {

    private final EquipmentService equipmentService;

    @PostMapping
    public ResponseEntity<EquipmentResponse> createEquipment(
            @Valid @RequestBody CreateEquipmentRequest request) {

        EquipmentResponse response = equipmentService.createEquipment(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{institutionCode}/{labCode}/{equipmentCode}")
    public ResponseEntity<EquipmentResponse> getEquipmentByCode(
            @PathVariable String institutionCode,
            @PathVariable String labCode,
            @PathVariable String equipmentCode) {

        return ResponseEntity.ok(
                equipmentService.getEquipmentByCode(
                        equipmentCode,
                        labCode,
                        institutionCode));
    }

    @GetMapping("/{institutionCode}/{labCode}")
    public ResponseEntity<List<EquipmentResponse>> getAllEquipment(
            @PathVariable String institutionCode,
            @PathVariable String labCode) {

        return ResponseEntity.ok(
                equipmentService.getAllEquipment(
                        labCode,
                        institutionCode));
    }

    @PutMapping
    public ResponseEntity<EquipmentResponse> updateEquipment(
            @Valid @RequestBody UpdateEquipmentRequest request) {

        return ResponseEntity.ok(
                equipmentService.updateEquipment(request));
    }

    @DeleteMapping("/{institutionCode}/{labCode}/{equipmentCode}")
    public ResponseEntity<String> deleteEquipment(
            @PathVariable String institutionCode,
            @PathVariable String labCode,
            @PathVariable String equipmentCode) {

        equipmentService.deleteEquipment(
                equipmentCode,
                labCode,
                institutionCode);

        return ResponseEntity.ok("Equipment deleted successfully.");
    }
}
