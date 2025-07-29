package com.project.demo.rest.reportUser;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.invoice.InvoiceRepository;
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
    InvoiceRepository invoiceRepository;

    @GetMapping("/income-and-expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllIncomeAndExpenses(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        List<Object[]> rawIncome = invoiceRepository.getMonthlyInvoiceTotals(year, "ingreso", userPrincipal.getId());
        List<Object[]> rawExpenses = invoiceRepository.getMonthlyInvoiceTotals(year, "gasto", userPrincipal.getId());

        List<Double> monthlyTotalsIncome = new ArrayList<>(Collections.nCopies(12, 0.0));
        List<Double> monthlyTotalsExpenses = new ArrayList<>(Collections.nCopies(12, 0.0));

        for (Object[] row : rawIncome) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            monthlyTotalsIncome.set(month - 1, total);
        }

        for (Object[] row : rawExpenses) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            monthlyTotalsExpenses.set(month - 1, total);
        }

        List<List<Double>> totals = List.of(monthlyTotalsIncome, monthlyTotalsExpenses);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de gastos e ingresos recuperado exitosamente",
                totals,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/monthly-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllMonthlyCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        List<Object[]> rawIncome = invoiceRepository.getMonthlyInvoiceTotals(year, "ingreso", userPrincipal.getId());
        List<Object[]> rawExpenses = invoiceRepository.getMonthlyInvoiceTotals(year, "gasto", userPrincipal.getId());

        List<Double> monthlyTotalsIncome = new ArrayList<>(Collections.nCopies(12, 0.0));
        List<Double> monthlyTotalsExpenses = new ArrayList<>(Collections.nCopies(12, 0.0));
        List<Double> monthlyCashFlow =  new ArrayList<>(Collections.nCopies(12, 0.0));

        for (Object[] row : rawIncome) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            monthlyTotalsIncome.set(month - 1, total);
        }

        for (Object[] row : rawExpenses) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            monthlyTotalsExpenses.set(month - 1, total);
        }

        for (int i = 0; i < 12; i++) {
            double income = monthlyTotalsIncome.get(i);
            double expense = monthlyTotalsExpenses.get(i);
            monthlyCashFlow.set(i, income - expense);
        }

        List<List<Double>> totals = List.of(monthlyTotalsIncome, monthlyTotalsExpenses, monthlyCashFlow);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja mensual recuperado exitosamente",
                totals,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/trimester-cash-flow")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getAllTrimesterCashFlow(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        List<Object[]> rawIncome = invoiceRepository.getMonthlyInvoiceTotals(year, "ingreso", userPrincipal.getId());
        List<Object[]> rawExpenses = invoiceRepository.getMonthlyInvoiceTotals(year, "gasto", userPrincipal.getId());

        List<Double> trimesterTotalsIncome = new ArrayList<>(Collections.nCopies(4, 0.0));
        List<Double> trimesterTotalsExpenses = new ArrayList<>(Collections.nCopies(4, 0.0));
        List<Double> trimesterCashFlow = new ArrayList<>(Collections.nCopies(4, 0.0));

        for (Object[] row : rawIncome) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            int trimester = (month - 1) / 3;
            trimesterTotalsIncome.set(trimester, trimesterTotalsIncome.get(trimester) + total);
        }

        for (Object[] row : rawExpenses) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            int trimester = (month - 1) / 3;
            trimesterTotalsExpenses.set(trimester, trimesterTotalsExpenses.get(trimester) + total);
        }

        for (int i = 0; i < 4; i++) {
            double income = trimesterTotalsIncome.get(i);
            double expense = trimesterTotalsExpenses.get(i);
            trimesterCashFlow.set(i, income - expense);
        }

        List<List<Double>> totals = List.of(trimesterTotalsIncome, trimesterTotalsExpenses, trimesterCashFlow);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de flujo de caja trimestral recuperado exitosamente",
                totals,
                HttpStatus.OK,
                request
        );
    }

    @GetMapping("/top-expense-categories")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN', 'USER')")
    public ResponseEntity<?> getTop5ExpenseCategories(
            @RequestParam(defaultValue = "0") int year,
            @AuthenticationPrincipal User userPrincipal,
            HttpServletRequest request) {

        List<Object[]> results = invoiceRepository.getTop5ExpenseCategoriesByYear(year, userPrincipal.getId());

        List<Map<String, Object>> categories = results.stream().map(row -> {
            Map<String, Object> map = new HashMap<>();
            map.put("category", row[0]);
            map.put("total", row[1]);
            return map;
        }).collect(Collectors.toList());

        return new GlobalResponseHandler().handleResponse(
                "Top 5 categorías de gasto obtenidas exitosamente",
                categories,
                HttpStatus.OK,
                request);
    }
}
