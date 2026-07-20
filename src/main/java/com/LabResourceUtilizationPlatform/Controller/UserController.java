package com.LabResourceUtilizationPlatform.Controller;

import com.LabResourceUtilizationPlatform.Dtos.Request.CreateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Request.UpdateUserRequest;
import com.LabResourceUtilizationPlatform.Dtos.Response.UserResponse;
import com.LabResourceUtilizationPlatform.Entity.User;
import com.LabResourceUtilizationPlatform.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    public final UserService userService;

    @PostMapping
    public ResponseEntity<UserResponse>createUser(@Valid @RequestBody CreateUserRequest request){
        UserResponse userResponse = userService.createUser(request);
        return new ResponseEntity<>(userResponse, HttpStatus.CREATED);
    }

    @GetMapping("/{email}")
    public ResponseEntity<UserResponse> getUserByEmail(@PathVariable String email) {

        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("institutionCode/{institutionCode}")
    public ResponseEntity<List<UserResponse>> getAllUserByInstitutionCode(@PathVariable String institutionCode) {

        return ResponseEntity.ok(userService.getAllUserByInstitutionCode(institutionCode));
    }

    @PutMapping
    public ResponseEntity<UserResponse> updateUser(
            @Valid @RequestBody UpdateUserRequest request) {

        return ResponseEntity.ok(userService.updateUser(request));
    }

    @GetMapping("/{institutionCode}/{departmentName}")
    public ResponseEntity<List<UserResponse>> getUsersByDepartment(
            @PathVariable String institutionCode,
            @PathVariable String departmentName) {

        return ResponseEntity.ok(
                userService.getUsersByDepartment(
                        institutionCode,
                        departmentName
                )
        );
    }

    @DeleteMapping("/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable String email) {

        userService.deleteUser(email);
        return ResponseEntity.ok("User Deleted Successfully");
    }

    @GetMapping("/lab-technicians")
    @PreAuthorize("hasRole('LAB_MANAGER')")
    public ResponseEntity<List<UserResponse>> getLabTechnicians() {

        return ResponseEntity.ok(
                userService.getLabTechniciansForManager()
        );
    }
}
