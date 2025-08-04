package com.project.demo.logic.entity.reportAdmin;

import com.project.demo.logic.entity.invoice.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportAdminInvoicesService {

    @Autowired
    InvoiceRepository invoiceRepository;

    public Map<Integer, Integer> getMonthlyVolumeInvoices(int year) {
        List<Object[]> rawData = invoiceRepository.getMonthlyInvoiceVolume(year);

        Map<Integer, Integer> rawDataMap = rawData.stream()
                .collect(Collectors.toMap(
                        row -> ((Number) row[0]).intValue(),
                        row -> ((Number) row[1]).intValue()
                ));

        for (int month = 1; month <= 12; month++) {
            rawDataMap.putIfAbsent(month, 0);
        }

        return rawDataMap;
    }

    public Map<String, Double> getTotalIncomeAndExpensesByYear(int year) {
        Double totalIngresos = invoiceRepository.getTotalIncomeByYear(year);
        Double totalEgresos = invoiceRepository.getTotalExpensesByYear(year);

        totalIngresos = totalIngresos != null ? totalIngresos : 0.0;
        totalEgresos = totalEgresos != null ? totalEgresos : 0.0;

        return Map.of(
                "totalIncomes", totalIngresos,
                "totalExpenses", totalEgresos
        );
    }

    public List<Map<String, Object>> getTop10UsersByInvoiceVolume() {
        List<Object[]> results = invoiceRepository.getTop10UsersByInvoiceVolume();

        return results.stream().map(row -> Map.of(
                "id", row[0],
                "name", row[1],
                "lastname", row[2],
                "totalCountInvoices", row[3],
                "totalIncome", row[4],
                "totalExpenses", row[5]
        )).collect(Collectors.toList());
    }
}