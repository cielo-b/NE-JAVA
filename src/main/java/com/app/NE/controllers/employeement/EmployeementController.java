package com.app.NE.controllers.employeement;

import com.app.NE.dto.requests.CreateEmployeementDTO;
import com.app.NE.dto.requests.RegisterEmployeeDTO;
import com.app.NE.dto.responses.ApiResponse;
import com.app.NE.services.auth.IAuthService;
import com.app.NE.services.employeement.IEmployeementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/employement")
@RequiredArgsConstructor
public class EmployeementController {
    private final IEmployeementService employeementService;

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> create(@Valid @RequestBody CreateEmployeementDTO dto) {
        ApiResponse response = employeementService.createEmployeement(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/by-id/{id}")
    public ResponseEntity<ApiResponse> getById(@PathVariable UUID id) {
        ApiResponse response = employeementService.getEmployeementById(id);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/by-employee-code/{code}")
    public ResponseEntity<ApiResponse> getByEmployeeCode(@PathVariable String code) {
        ApiResponse response = employeementService.getEmployeementByEmployeeCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/update/{code}")
    public ResponseEntity<ApiResponse> updateEmployeement(@PathVariable String code, @Valid @RequestBody CreateEmployeementDTO dto) {
        ApiResponse response = employeementService.updateEmployeement(code, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/activate/{code}")
    public ResponseEntity<ApiResponse> activate(@PathVariable String code) {
        ApiResponse response = employeementService.activateEmployeement(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/deactivate/{code}")
    public ResponseEntity<ApiResponse> deactivate(@PathVariable String code) {
        ApiResponse response = employeementService.deactivateEmployeement(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

}
