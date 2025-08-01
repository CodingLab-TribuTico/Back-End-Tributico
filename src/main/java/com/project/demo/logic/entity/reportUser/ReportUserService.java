package com.project.demo.logic.entity.reportUser;

import com.project.demo.logic.entity.invoice.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReportUserService {

    @Autowired
    InvoiceRepository invoiceRepository;

    public List<Double> getMonthlyTotals(int year, String type, Long userId) {
        List<Object[]> rawData = invoiceRepository.getMonthlyInvoiceTotals(year, type, userId);
        List<Double> monthlyTotals = new ArrayList<>(Collections.nCopies(12, 0.0));
        for (Object[] row : rawData) {
            int month = ((Number) row[0]).intValue();
            double total = ((Number) row[1]).doubleValue();
            monthlyTotals.set(month - 1, total);
        }
        return monthlyTotals;
    }

    public List<Double> getTrimesterTotals(List<Double> monthlyTotals) {
        List<Double> trimesterTotals = new ArrayList<>(Collections.nCopies(4, 0.0));
        for (int i = 0; i < 12; i++) {
            int trimester = i / 3;
            trimesterTotals.set(trimester, trimesterTotals.get(trimester) + monthlyTotals.get(i));
        }
        return trimesterTotals;
    }

    public List<Double> getCashFlow(List<Double> income, List<Double> expenses) {
        List<Double> result = new ArrayList<>(Collections.nCopies(income.size(), 0.0));
        for (int i = 0; i < income.size(); i++) {
            result.set(i, income.get(i) - expenses.get(i));
        }
        return result;
    }
}
