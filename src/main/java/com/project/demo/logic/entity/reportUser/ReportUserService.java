package com.project.demo.logic.entity.reportUser;

import com.project.demo.logic.entity.invoice.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportUserService {

    @Autowired
    InvoiceRepository invoiceRepository;

    public Map<String, Double> getMonthlyTotals(int year, String type, Long userId) {
        List<Object[]> rawData = invoiceRepository.getMonthlyInvoiceTotals(year, type, userId);

        Map<Integer, Double> totalsMap = rawData.stream().collect(Collectors.toMap(
                row -> ((Number) row[0]).intValue(),
                row -> ((Number) row[1]).doubleValue()
        ));

        Map<String, Double> result = new LinkedHashMap<>();
        for (int month = 1; month <= 12; month++) {
            result.put(String.valueOf(month), totalsMap.getOrDefault(month, 0.0));
        }

        return result;
    }

    public Map<String, Double> getTrimesterTotals(Map<String, Double> monthlyTotals) {
        Map<String, Double> result = new LinkedHashMap<>();

        for (int i = 0; i < 4; i++) {
            double sum = 0.0;
            for (int j = i * 3 + 1; j <= (i + 1) * 3; j++) {
                sum += monthlyTotals.getOrDefault(String.valueOf(j), 0.0);
            }
            result.put(String.valueOf(i + 1), sum);
        }

        return result;
    }

    public Map<String, Double> getCashFlow(Map<String, Double> income, Map<String, Double> expenses) {
        Map<String, Double> result = new LinkedHashMap<>();

        for (int i = 1; i <= income.size(); i++) {
            String key = String.valueOf(i);
            double inflow = income.getOrDefault(key, 0.0);
            double outflow = expenses.getOrDefault(key, 0.0);
            result.put(key, inflow - outflow);
        }

        return result;
    }

    public List<Map<String, Object>> getTop5ExpenseCategories(int year, Long userId) {
        List<Object[]> results = invoiceRepository.getTop5ExpenseCategoriesByYear(year, userId);

        return results.stream().map(row -> Map.of(
                "category", row[0],
                "total", row[1]
        )).collect(Collectors.toList());
    }
}