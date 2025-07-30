package com.project.demo.rest.reportUser;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
import com.project.demo.logic.entity.reportUser.ReportUserService;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("reports-user")
public class ReportUserRestController {

    @Autowired
    ReportUserService reportUserService;

    @Autowired
    InvoiceRepository invoiceRepository;

    @GetMapping("/income-and-expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllIncomeAndExpenses(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        List<Double> incomes = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        List<Double> expenses = reportUserService.getMonthlyTotals(year, "gasto", user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Reporte de gastos e ingresos recuperado exitosamente",
                List.of(incomes, expenses),
                HttpStatus.OK,
                request);
    }

    @GetMapping("/monthly-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllMonthlyCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        List<Double> incomes = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        List<Double> expenses = reportUserService.getMonthlyTotals(year, "gasto", user.getId());
        List<Double> cashFlow = reportUserService.getCashFlow(incomes, expenses);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja mensual recuperado exitosamente",
                List.of(incomes, expenses, cashFlow),
                HttpStatus.OK,
                request);
    }

    @GetMapping("/trimester-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTrimesterCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        List<Double> incomesMonthly = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        List<Double> expensesMonthly = reportUserService.getMonthlyTotals(year, "gasto", user.getId());
        List<Double> incomeTrimester = reportUserService.getTrimesterTotals(incomesMonthly);
        List<Double> expensesTrimester = reportUserService.getTrimesterTotals(expensesMonthly);
        List<Double> cashFlow = reportUserService.getCashFlow(incomeTrimester, expensesTrimester);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja trimestral recuperado exitosamente",
                List.of(incomeTrimester, expensesTrimester, cashFlow),
                HttpStatus.OK,
                request);
    }

    @GetMapping("/top-expense-categories")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getTop5ExpenseCategories(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        List<Object[]> results = invoiceRepository.getTop5ExpenseCategoriesByYear(year, user.getId());

        List<Map<String, Object>> categories = results.stream().map(row -> Map.of(
                "category", row[0],
                "total", row[1]
        )).collect(Collectors.toList());

        return new GlobalResponseHandler().handleResponse(
                "Top 5 categorías de gasto obtenidas exitosamente",
                categories,
                HttpStatus.OK,
                request);
    }
}
