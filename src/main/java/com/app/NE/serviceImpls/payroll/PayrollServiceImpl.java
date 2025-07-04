package com.app.NE.serviceImpls.payroll;

import com.app.NE.dto.requests.ProcessPayrollDTO;
import com.app.NE.dto.responses.ApiResponse;
import com.app.NE.enums.EDeductionName;
import com.app.NE.enums.EEmployementStatus;
import com.app.NE.enums.EPaySlipStatus;
import com.app.NE.exceptions.BadRequestException;
import com.app.NE.models.*;
import com.app.NE.repositories.*;
import com.app.NE.serviceImpls.auth.AuthServiceImpl;
import com.app.NE.serviceImpls.mail.MailServiceImpl;
import com.app.NE.services.auth.IAuthService;
import com.app.NE.services.payroll.IPayrollService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollServiceImpl implements IPayrollService {
    private final IEmployementRepository employmentRepository;
    private final IDeductionRepository deductionRepository;
    private final IPaySlipRepository paySlipRepository;
    private final IEmployeeRepository employeeRepository;
    private final IMessageRepository messageRepository;
    private final IAuthService authServiceImpl;
    private final IPayrollRepository payrollRepository;
    private final TaskExecutor taskExecutor;
    private final MailServiceImpl mailService;

    @Override
    @Transactional
    public ApiResponse processPayroll(ProcessPayrollDTO dto) {
        if(payrollRepository.existsByMonthAndYear(dto.getMonth(), dto.getYear())){
            throw new BadRequestException(String.format("Payroll month %s and year %s already exists", dto.getMonth(),dto.getYear()));
        }
                List<Employment> activeEmployments = employmentRepository.findByStatusAndEmployee_Institution(EEmployementStatus.ACTIVE, authServiceImpl.getPrincipal().getInstitution());
        List<Deduction> deductions = deductionRepository.findAll();

        ApiResponse apiResponse = new ApiResponse();
        List<PaySlip> slips = activeEmployments.stream()
                .map(employment -> createPaySlip(employment, deductions, dto.getMonth(), dto.getYear()))
                .collect(Collectors.toList());

        // create the payroll
        PayRoll payRoll = new PayRoll();
        payRoll.setSlips(slips);
        payRoll.setYear(dto.getYear());
        payRoll.setMonth(dto.getMonth());
        payrollRepository.save(payRoll);
        return new ApiResponse(slips, "Payroll processed successfully");
    }

    @Override
    public PaySlip createPaySlip(Employment employment, List<Deduction> deductions, int month, int year) {
                BigDecimal baseSalary = employment.getBaseSalary();
        BigDecimal totalDeductionAmount = calculateTotalDeductions(baseSalary, deductions);
        PaySlip slip = new PaySlip();
        slip.setMonth(month);
        slip.setYear(year);
        Deduction house = deductionRepository.findByDeductionName(EDeductionName.HOUSE);
        Deduction tr = deductionRepository.findByDeductionName(EDeductionName.TRANSPORT);
        BigDecimal percentage = house.getPercentage(); // assuming this is BigDecimal
        BigDecimal hundred = new BigDecimal(100);
        BigDecimal fraction = percentage.divide(hundred); // divide to get the fractional percentage
        BigDecimal h = baseSalary.multiply(fraction);
        BigDecimal p2 = tr.getPercentage();
        BigDecimal fr = p2.divide(hundred);
        BigDecimal t = baseSalary.multiply(fr);

        slip.setGrossSalary(baseSalary.add(t.add(h)));
        BigDecimal netSalary = baseSalary.add(t.add(h)).subtract(totalDeductionAmount);
        slip.setStatus(EPaySlipStatus.PENDING);
        slip.setNetSalary(netSalary);
        slip.setEmployee(employment.getEmployee());

        paySlipRepository.save(slip);

        return slip;
    }

    @Override
    public BigDecimal calculateTotalDeductions(BigDecimal baseSalary, List<Deduction> deductions) {
                BigDecimal in = null;
                for(Deduction deduction : deductions) {
                    in = baseSalary.add(deduction.getPercentage());
                }
                
                return in.divide(new BigDecimal(100)).multiply(baseSalary);
    }


    @Override
    public ApiResponse getAllPaySlips(int month, int year) {
        List<PaySlip> slips = paySlipRepository.findByMonthAndYear(month, year);
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(slips);
        return apiResponse;
    }


    @Override
    public ApiResponse getAllMyPaySlipsAsManager(int month, int year) {
        List<PaySlip> slips = paySlipRepository.findByMonthAndYearAndEmployee_Institution(month, year, authServiceImpl.getPrincipal().getInstitution());
        ApiResponse apiResponse = new ApiResponse();
        apiResponse.setData(slips);
        return apiResponse;
    }

    @Override
    public ApiResponse getEmployeePaySlips(String employeeCode, int month, int year) {
                Employee employee = employeeRepository.findByCode(employeeCode)
                .orElseThrow(() -> new RuntimeException("Employee not found"));
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setData(paySlipRepository.findByEmployeeAndMonthAndYear(employee, month, year));
        return apiResponse;
    }

    @Override
    @Transactional
    public ApiResponse approvePayroll(int month, int year) {
                List<PaySlip> paySlips = paySlipRepository.findByMonthAndYear(month, year);
        paySlips.forEach(paySlip -> {
            if(paySlip.getStatus() == EPaySlipStatus.PAID) {
                throw new BadRequestException(String.format("Payroll month %s and year %s already approved", month,year));
            }
            paySlip.setStatus(EPaySlipStatus.PAID);
            // get the base
        Message message = createPaymentMessage(paySlip);
        taskExecutor.execute(() -> {
            try{
                // send the email
                mailService.sendMail(message);
            }catch(Exception e){
                log.error("Failed to send verification email", e);
            }
        });
        });
        paySlipRepository.saveAll(paySlips);


        return new ApiResponse(true, "Payroll approved successfully");
    }

    @Override
    public Message createPaymentMessage(PaySlip slip) {
        System.out.println(slip.getNetSalary());
        Message message = new Message();
        message.setMessage("Dear " + slip.getEmployee().getFirstName() + ", your salary payment for " +
                slip.getMonth() + "/" + slip.getYear() + " has been processed from " + slip.getEmployee().getFirstName() +"." + " amount" + slip.getNetSalary());
        message.setYear(slip.getYear());
        message.setMonth(slip.getMonth());
        message.setEmployee(slip.getEmployee());
        message.setSentAt(LocalDateTime.now());
        message.setSent(false);
        return messageRepository.save(message);
    }
}
