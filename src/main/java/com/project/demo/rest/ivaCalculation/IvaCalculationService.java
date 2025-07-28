package com.project.demo.rest.ivaCalculation;

import com.project.demo.logic.entity.ivacalculation.IvaCalculation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;


@Service
public class IvaCalculationService {

    @Autowired
    private com.project.demo.rest.ivaCalculation.IvaCalculationRepository ivaCalculationRepository;

   
    public boolean validateIvaAmounts(IvaCalculation calculation) {
        return calculation.getIvaVentasBienes().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaVentasServicios().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaExportaciones().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaActividadesAgropecuarias().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaComprasBienes().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaComprasServicios().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaImportaciones().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaGastosGenerales().compareTo(BigDecimal.ZERO) >= 0 &&
               calculation.getIvaActivosFijos().compareTo(BigDecimal.ZERO) >= 0;
    }

   
    public BigDecimal calculateEffectiveIvaRate(IvaCalculation calculation) {
        if (calculation.getTotalIvaDebito().compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        
        return calculation.getTotalIvaCredito()
                .divide(calculation.getTotalIvaDebito(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal("100"));
    }

    
    public IvaCalculation createBlankSimulation(Long userId, int year, int month) {
        IvaCalculation calculation = new IvaCalculation();
        calculation.setYear(year);
        calculation.setMonth(month);
        
        calculation.calculateTotals();
        
        return calculation;
    }

    
    public IvaCalculation processSimulation(IvaCalculation calculation) {
        if (!validateIvaAmounts(calculation)) {
            throw new IllegalArgumentException("Los montos de IVA no pueden ser negativos");
        }
        
        calculation.calculateTotals();
        
        return calculation;
    }

    
    public IvaCalculationSummary generateCalculationSummary(IvaCalculation calculation) {
        return new IvaCalculationSummary(calculation);
    }

    
    public static class IvaCalculationSummary {
        private final IvaCalculation calculation;

        public IvaCalculationSummary(IvaCalculation calculation) {
            this.calculation = calculation;
        }

        public String getDetailedReport() {
            StringBuilder report = new StringBuilder();
            report.append("=== SIMULACIÓN DE IVA ===\n");
            report.append("Período: ").append(calculation.getMonth()).append("/").append(calculation.getYear()).append("\n");
            report.append("Usuario: ").append(calculation.getUser().getName()).append("\n\n");
            
            report.append("INGRESOS (IVA Débito):\n");
            report.append("- Ventas de Bienes: ₡").append(calculation.getIvaVentasBienes()).append("\n");
            report.append("- Ventas de Servicios: ₡").append(calculation.getIvaVentasServicios()).append("\n");
            report.append("- Exportaciones: ₡").append(calculation.getIvaExportaciones()).append("\n");
            report.append("- Actividades Agropecuarias: ₡").append(calculation.getIvaActividadesAgropecuarias()).append("\n");
            report.append("TOTAL IVA DÉBITO: ₡").append(calculation.getTotalIvaDebito()).append("\n\n");
            
            report.append("EGRESOS (IVA Crédito):\n");
            report.append("- Compras de Bienes: ₡").append(calculation.getIvaComprasBienes()).append("\n");
            report.append("- Compras de Servicios: ₡").append(calculation.getIvaComprasServicios()).append("\n");
            report.append("- Importaciones: ₡").append(calculation.getIvaImportaciones()).append("\n");
            report.append("- Gastos Generales: ₡").append(calculation.getIvaGastosGenerales()).append("\n");
            report.append("- Activos Fijos: ₡").append(calculation.getIvaActivosFijos()).append("\n");
            report.append("TOTAL IVA CRÉDITO: ₡").append(calculation.getTotalIvaCredito()).append("\n\n");
            
            report.append("RESULTADO:\n");
            if (calculation.getIvaNetoPorPagar().compareTo(BigDecimal.ZERO) > 0) {
                report.append("IVA POR PAGAR: ₡").append(calculation.getIvaNetoPorPagar()).append("\n");
            } else {
                report.append("IVA A FAVOR: ₡").append(calculation.getIvaAFavor()).append("\n");
            }
            
            return report.toString();
        }
    }
}
