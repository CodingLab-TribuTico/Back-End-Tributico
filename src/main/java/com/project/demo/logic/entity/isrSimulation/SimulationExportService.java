package com.project.demo.logic.entity.isrSimulation;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

@Service
public class SimulationExportService {

    private static final DecimalFormat decimalFormat = createDecimalFormat();

    private static DecimalFormat createDecimalFormat() {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        return new DecimalFormat("#,##0.00", symbols);
    }

    private static String formatNumber(double number) {
        return decimalFormat.format(number);
    }

    public static String generateHtmlContent(String body) {
        return "<html>" +
                "  <head>" +
                "    <style>body { font-family: 'Arial', sans-serif; }</style>" +
                "  </head>" +
                "  <body>" + body + "</body>" +
                "</html>";
    }

    public String generateSimulationPdf(IsrSimulation sim) {
        StringBuilder body = new StringBuilder();

        body.append("<div style='font-family: Arial, sans-serif;'>");

        body.append("<h1 style='font-weight: 700; font-size: 24px; border-bottom: 2px solid #3d2b1f; padding-bottom: 8px;'>")
            .append("D-101 - Declaración Jurada del Impuesto sobre la Renta").append("</h1>");

        body.append("<div style='margin-top: 20px;'>")
            .append("<p>02 - Período: ").append(sim.getSimulationPeriod()).append("</p>")
            .append("<p>04 - Cédula: ").append(sim.getSimulationIdentification()).append("</p>")
            .append("<p>06 - Nombre: ").append(sim.getSimulationName()).append("</p>")
            .append("</div>");

        //I. Activos y pasivos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("I. Activos y pasivos").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>20 - Efectivo, bancos, inversiones transitorias, documentos y cuentas por cobrar </td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getCurrentAssets())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>21 - Acciones y aportes en sociedades</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getEquityInvestments())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>22 - Inventarios</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getInventory())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>23 - Activos fijos (descuente la depreciacion acumulada)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNetFixedAssets())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>24 - Total activo neto (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalNetAssets())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>25 - Total pasivo</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalLiabilities())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>26 - Capital neto (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNetEquity())).append("</td></tr>")
            .append("</table>");

        // II. Ingresos y gastos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("II. Ingresos y deducciones").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>27 - Venta de bienes y servicios, excepto los servicios profesionales</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getSalesRevenue())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>28 - Servicios profesionales y honorarios</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getProfessionalFees())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>29 - Comisiones</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getCommissions())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>30 - Intereses y rendimientos</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getInterestsAndYields())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>31 - Dividendos y participaciones</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getDividendsAndShares())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>32 - Alquileres</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getRents())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>33 - Otros ingresos diferentes a los anteriores</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getOtherIncome())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>34 - Ingresos no gravables incluidos dentro de los anteriores</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNonTaxableIncome())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>35 - Total de renta bruta (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getGrossIncomeTotal())).append("</td></tr>")
            .append("</table>");

        // III. Costos, gastos y deducciones
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("III. Costos, gastos y deducciones").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>36 - Inventario inicial</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getInitialInventory())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>37 - Compras</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getPurchases())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>38 - Inventario final</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getFinalInventory())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>39 - Costo de ventas</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getCostOfGoodsSold())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>40 - Intereses y gastos financieros</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getFinancialExpenses())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>41 - Gastos de ventas y administrativos</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getAdministrativeExpenses())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>42 - Depreciación, amortización y agotamiento</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getDepreciationAndAmortization())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>43 - Aportes de regimenes voluntarios de pensiones complementias (Max 10% renta bruta)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getPensionContributions())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>44 - Otros costos, gastos y deducciones permitidos por la ley</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getOtherAllowableDeductions())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>45 - Total de costos, gastos y deducciones permitidos por la ley (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalAllowableDeductions())).append("</td></tr>")
            .append("</table>");

        // IV. Base imponible
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("IV. Base imponible").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>46 - Renta neta (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNetTaxableIncome())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>46 (bis) - Monto no sujeto aplicado al impuesto al salario (acumulado anual)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNonTaxableSalaryAmount())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>47 - Impuesto sobre la renta (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getIncomeTax())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>51 - Exoneración de zona franca</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getFreeTradeZoneExemption())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>53 - Exoneración de otros conceptos</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getOtherExemptions())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>54 - Impuesto sobre la renta después de exoneraciones (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNetIncomeTaxAfterExemptions())).append("</td></tr>")
            .append("</table>");

        // V. Créditos
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("V. Créditos").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>58 - Crédito familiar (solo personas físicas)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getFamilyCredit())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>59 - Otros créditos</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getOtherCredits())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>60 - Impuesto del periodo (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getPeriodTax())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>61 - Retenciones 2%</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTwoPercentWithholdings())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>62 - Otras retenciones</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getOtherWithholdings())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>63 - Pagos parciales</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getPartialPayments())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>64 - Total impuesto neto (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalNetTax())).append("</td></tr>")
            .append("</table>");

        // VI. Liquidación deuda tributaria
        body.append("<h2 style='font-weight: 700; font-size: 18px; border-bottom: 2px solid black; padding-bottom: 4px; margin-bottom: 12px;'>")
            .append("VI. Liquidación deuda tributaria").append("</h2>")
            .append("<table style='width: 100%; border-collapse: collapse;'>")
            .append("<tr><td style='padding: 8px;'>82 - Intereses (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getInterests())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>83 - Total deuda tributaria (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalTaxDebt())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>84 - Solicito compensar con créditos a mi favor por el monto de</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getRequestedCompensation())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>85 - Total deuda por pagar (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalDebtToPay())).append("</td></tr>")
            .append("</table>");

        return body.toString();
    }

    public String generateSimulationCsv(IsrSimulation sim) {
        StringBuilder csv = new StringBuilder();

        csv.append("D-101 - Declaración Jurada del Impuesto sobre la Renta\n");
        csv.append("02 - Período,").append(sim.getSimulationPeriod()).append("\n");
        csv.append("04 - Cédula,").append(sim.getSimulationIdentification()).append("\n");
        csv.append("06 - Nombre,").append(escape(sim.getSimulationName())).append("\n");

        // I. Activos y Pasivos
        csv.append("I. Activos y pasivos\n");
        csv.append("20 - Efectivo, bancos, inversiones transitorias, documentos y cuentas por cobrar,").append(sim.getCurrentAssets()).append("\n");
        csv.append("21 - Acciones y aportes en sociedades,").append(sim.getEquityInvestments()).append("\n");
        csv.append("22 - Inventarios,").append(sim.getInventory()).append("\n");
        csv.append("23 - Activos fijos (descuente la depreciacion acumulada),").append(sim.getNetFixedAssets()).append("\n");
        csv.append("24 - Total activo neto (autocalculado),").append(sim.getTotalNetAssets()).append("\n");
        csv.append("25 - Total pasivo,").append(sim.getTotalLiabilities()).append("\n");
        csv.append("26 - Capital neto (autocalculado),").append(sim.getNetEquity()).append("\n");

        // II. Ingresos y deducciones
        csv.append("II. Ingresos y deducciones\n");
        csv.append("27 - Venta de bienes y servicios, excepto los servicios profesionales,").append(formatNumber(sim.getSalesRevenue())).append("\n");
        csv.append("28 - Servicios profesionales y honorarios,").append(formatNumber(sim.getProfessionalFees())).append("\n");
        csv.append("29 - Comisiones,").append(formatNumber(sim.getCommissions())).append("\n");
        csv.append("30 - Intereses y rendimientos,").append(formatNumber(sim.getInterestsAndYields())).append("\n");
        csv.append("31 - Dividendos y participaciones,").append(formatNumber(sim.getDividendsAndShares())).append("\n");
        csv.append("32 - Alquileres,").append(formatNumber(sim.getRents())).append("\n");
        csv.append("33 - Otros ingresos diferentes a los anteriores,").append(formatNumber(sim.getOtherIncome())).append("\n");
        csv.append("34 - Ingresos no gravables incluidos dentro de los anteriores,").append(formatNumber(sim.getNonTaxableIncome())).append("\n");
        csv.append("35 - Total de renta bruta (autocalculado),").append(formatNumber(sim.getGrossIncomeTotal())).append("\n");

        // III. Costos, gastos y deducciones
        csv.append("III. Costos, gastos y deducciones\n");
        csv.append("36 - Inventario inicial,").append(formatNumber(sim.getInitialInventory())).append("\n");
        csv.append("37 - Compras,").append(formatNumber(sim.getPurchases())).append("\n");
        csv.append("38 - Inventario final,").append(formatNumber(sim.getFinalInventory())).append("\n");
        csv.append("39 - Costo de ventas,").append(formatNumber(sim.getCostOfGoodsSold())).append("\n");
        csv.append("40 - Intereses y gastos financieros,").append(formatNumber(sim.getFinancialExpenses())).append("\n");
        csv.append("41 - Gastos de ventas y administrativos,").append(formatNumber(sim.getAdministrativeExpenses())).append("\n");
        csv.append("42 - Depreciación, amortización y agotamiento,").append(formatNumber(sim.getDepreciationAndAmortization())).append("\n");
        csv.append("43 - Aportes de regimenes voluntarios de pensiones complementias (Max 10% renta bruta),").append(formatNumber(sim.getPensionContributions())).append("\n");
        csv.append("44 - Otros costos, gastos y deducciones permitidos por la ley,").append(formatNumber(sim.getOtherAllowableDeductions())).append("\n");
        csv.append("45 - Total de costos, gastos y deducciones permitidos por la ley (autocalculado),").append(formatNumber(sim.getTotalAllowableDeductions())).append("\n");

        // IV. Base imponible
        csv.append("IV. Base imponible\n");
        csv.append("46 - Renta neta (autocalculado),").append(formatNumber(sim.getNetTaxableIncome())).append("\n");
        csv.append("46 (bis) - Monto no sujeto aplicado al impuesto al salario (acumulado anual),").append(formatNumber(sim.getNonTaxableSalaryAmount())).append("\n");
        csv.append("47 - Impuesto sobre la renta (autocalculado),").append(formatNumber(sim.getIncomeTax())).append("\n");
        csv.append("51 - Exoneración de zona franca,").append(formatNumber(sim.getFreeTradeZoneExemption())).append("\n");
        csv.append("53 - Exoneración de otros conceptos,").append(formatNumber(sim.getOtherExemptions())).append("\n");
        csv.append("54 - Impuesto sobre la renta después de exoneraciones (autocalculado),").append(formatNumber(sim.getNetIncomeTaxAfterExemptions())).append("\n");

        // V. Créditos
        csv.append("V. Créditos\n");
        csv.append("58 - Crédito familiar (solo personas físicas),").append(formatNumber(sim.getFamilyCredit())).append("\n");
        csv.append("59 - Otros créditos,").append(formatNumber(sim.getOtherCredits())).append("\n");
        csv.append("60 - Impuesto del periodo (autocalculado),").append(formatNumber(sim.getPeriodTax())).append("\n");
        csv.append("61 - Retenciones 2%,").append(formatNumber(sim.getTwoPercentWithholdings())).append("\n");
        csv.append("62 - Otras retenciones,").append(formatNumber(sim.getOtherWithholdings())).append("\n");
        csv.append("63 - Pagos parciales,").append(formatNumber(sim.getPartialPayments())).append("\n");
        csv.append("64 - Total impuesto neto (autocalculado),").append(formatNumber(sim.getTotalNetTax())).append("\n");

        // VI. Liquidación deuda tributaria
        csv.append("VI. Liquidación deuda tributaria\n");
        csv.append("82 - Intereses (autocalculado),").append(formatNumber(sim.getInterests())).append("\n");
        csv.append("83 - Total deuda tributaria (autocalculado),").append(formatNumber(sim.getTotalTaxDebt())).append("\n");
        csv.append("84 - Solicito compensar con créditos a mi favor por el monto de,").append(formatNumber(sim.getRequestedCompensation())).append("\n");
        csv.append("85 - Total deuda por pagar (autocalculado),").append(formatNumber(sim.getTotalDebtToPay())).append("\n");

        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }
}


