package com.project.demo.rest.reportUser;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.reportUser.ReportUserService;
import com.project.demo.logic.entity.user.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("reports-user")
public class ReportUserRestController {

    @Autowired
    ReportUserService reportUserService;

    @GetMapping("/income-and-expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllIncomeAndExpenses(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        Map<String, Double> incomes = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        Map<String, Double> expenses = reportUserService.getMonthlyTotals(year, "gasto", user.getId());

        Map<String, Object> response = Map.of(
                "income", incomes,
                "expenses", expenses
        );

        return new GlobalResponseHandler().handleResponse(
                "Reporte de gastos e ingresos recuperado exitosamente",
                response,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/monthly-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllMonthlyCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        Map<String, Double> incomes = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        Map<String, Double> expenses = reportUserService.getMonthlyTotals(year, "gasto", user.getId());
        Map<String, Double> cashFlow = reportUserService.getCashFlow(incomes, expenses);

        Map<String, Object> response = Map.of(
                "income", incomes,
                "expenses", expenses,
                "cashFlow", cashFlow
        );

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja mensual recuperado exitosamente",
                response,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/trimester-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTrimesterCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        Map<String, Double> incomesMonthly = reportUserService.getMonthlyTotals(year, "ingreso", user.getId());
        Map<String, Double> expensesMonthly = reportUserService.getMonthlyTotals(year, "gasto", user.getId());

        Map<String, Double> incomeTrimester = reportUserService.getTrimesterTotals(incomesMonthly);
        Map<String, Double> expensesTrimester = reportUserService.getTrimesterTotals(expensesMonthly);
        Map<String, Double> cashFlow = reportUserService.getCashFlow(incomeTrimester, expensesTrimester);

        Map<String, Object> response = Map.of(
                "income", incomeTrimester,
                "expenses", expensesTrimester,
                "cashFlow", cashFlow
        );

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja trimestral recuperado exitosamente",
                response,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/top-expense-categories")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getTop5ExpenseCategories(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User user,
            HttpServletRequest request) {

        List<Map<String, Object>> categories = reportUserService.getTop5ExpenseCategories(year, user.getId());

        return new GlobalResponseHandler().handleResponse(
                "Top 5 categorías de gasto obtenidas exitosamente",
                categories,
                HttpStatus.OK,
                request);
    }
}