package com.app.NE.services.payroll;

import com.app.NE.dto.requests.ProcessPayrollDTO;
import com.app.NE.dto.responses.ApiResponse;
import com.app.NE.models.Deduction;
import com.app.NE.models.Employment;
import com.app.NE.models.Message;
import com.app.NE.models.PaySlip;

import java.math.BigDecimal;
import java.util.List;

public interface IPayrollService {
    ApiResponse processPayroll(ProcessPayrollDTO dto);
    PaySlip createPaySlip(Employment employment, List<Deduction> deductions, int month, int year);
    BigDecimal calculateTotalDeductions(BigDecimal baseSalary, List<Deduction> deductions);
    ApiResponse getAllPaySlips(int month, int year);

    ApiResponse getAllMyPaySlipsAsManager(int month, int year);

    ApiResponse getEmployeePaySlips(String employeeCode, int month, int year);
    ApiResponse approvePayroll(int month, int year);
    Message createPaymentMessage(PaySlip slip);
}
