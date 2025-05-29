package com.app.NE.controllers.employee;

import com.app.NE.dto.requests.LoginDTO;
import com.app.NE.dto.requests.RegisterDTO;
import com.app.NE.dto.requests.RegisterEmployeeDTO;
import com.app.NE.dto.requests.UpdateEmployeeDTO;
import com.app.NE.dto.responses.ApiResponse;
import com.app.NE.services.auth.IAuthService;
import com.app.NE.services.employee.IEmployeeService;
import com.app.NE.utils.Constants;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employee")
@RequiredArgsConstructor
public class EmployeeController {
    private final IAuthService authService;
    private final IEmployeeService employeeService;

    @PreAuthorize("hasAnyRole('MANAGER')")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> register(@Valid @RequestBody RegisterEmployeeDTO dto) {
        ApiResponse response = authService.registerEmployee(dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('MANAGER')")
    @GetMapping("/get-my-employees")
    public ResponseEntity<ApiResponse> getMyEmployees() {
        ApiResponse response = employeeService.getMyEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN')")
    @GetMapping("/all-paginated")
    public ResponseEntity<ApiResponse> getAllPaginated(@RequestParam(value = "page", defaultValue = Constants.DEFAULT_PAGE_NUMBER) int page,
                                                       @RequestParam(value = "limit", defaultValue = Constants.DEFAULT_PAGE_SIZE) int limit) {

        Pageable pageable = PageRequest.of(page, limit);
        return ResponseEntity.ok().body(employeeService.getAllEmployees(pageable));
    }


    @GetMapping("/by-code/{code}")
    public ResponseEntity<ApiResponse> getEmployeeByCode(@PathVariable String code) {
        ApiResponse response = employeeService.getEmployeeByCode(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/update/{code}")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable String code, @Valid @RequestBody RegisterEmployeeDTO dto) {
        ApiResponse response = employeeService.updateEmployee(code, dto);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/disable/{code}")
    public ResponseEntity<ApiResponse> disableEmployee(@PathVariable String code) {
        ApiResponse response = employeeService.disableEmployee(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PutMapping("/enable/{code}")
    public ResponseEntity<ApiResponse> enableEmployee(@PathVariable String code) {
        ApiResponse response = employeeService.enableEmployee(code);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}

