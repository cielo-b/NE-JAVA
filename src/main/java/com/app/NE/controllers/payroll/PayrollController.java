package com.app.NE.controllers.payroll;

import com.app.NE.dto.requests.ApprovePayRollDTO;
import com.app.NE.dto.requests.ProcessPayrollDTO;
import com.app.NE.dto.responses.ApiResponse;
import com.app.NE.services.payroll.IPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final IPayrollService payrollService;

    @PostMapping("/process")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> processPayroll(@RequestBody ProcessPayrollDTO request) {
        return ResponseEntity.ok(payrollService.processPayroll(request));
    }

    @GetMapping("/slips")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> getAllPaySlips(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.getAllPaySlips(month, year));
    }

    @GetMapping("/my-slips")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<ApiResponse> getAllMyPaySlipsAsManager(
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.getAllMyPaySlipsAsManager(month, year));
    }

    @GetMapping("/slips/{employeeCode}")
    @PreAuthorize("hasAnyRole('MANAGER', 'EMPLOYEE')")
    public ResponseEntity<ApiResponse> getEmployeePaySlips(
            @PathVariable String employeeCode,
            @RequestParam Integer month,
            @RequestParam Integer year) {
        return ResponseEntity.ok(payrollService.getEmployeePaySlips(employeeCode, month, year));
    }

    @PostMapping("/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse> approvePayroll(
            @RequestBody ApprovePayRollDTO dto) {

        return ResponseEntity.ok().body(payrollService.approvePayroll(dto.getMonth(), dto.getYear()));
    }
}