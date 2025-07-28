package com.project.demo.logic.entity.isrSimulation;

import org.springframework.stereotype.Service;

import java.text.DecimalFormat;

@Service
public class IsrSimulationService {

    private static final DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");

    private String formatNumber(double number) {
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

    public String generateSimulation(IsrSimulation sim) {
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
            .append("<tr><td style='padding: 8px;'>24 - Total pasivo</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getTotalLiabilities())).append("</td></tr>")
            .append("<tr><td style='padding: 8px;'>24 - Capital neto (autocalculado)</td><td style='text-align: right;'>₡ ").append(formatNumber(sim.getNetEquity())).append("</td></tr>")
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

    public String generateCsvContent(IsrSimulation sim) {
        StringBuilder csv = new StringBuilder();

        csv.append("SECCIÓN,CONCEPTO,VALOR\n");

        // Información general
        csv.append("Información General,Período,").append(sim.getSimulationPeriod()).append("\n");
        csv.append("Información General,Cédula,").append(sim.getSimulationIdentification()).append("\n");
        csv.append("Información General,Nombre,").append(escape(sim.getSimulationName())).append("\n");

        // I. Activos y Pasivos
        csv.append("I. Activos y Pasivos,Efectivo,").append(sim.getCurrentAssets()).append("\n");
        csv.append("I. Activos y Pasivos,Acciones y aportes,").append(sim.getEquityInvestments()).append("\n");
        csv.append("I. Activos y Pasivos,Inventarios,").append(sim.getInventory()).append("\n");
        csv.append("I. Activos y Pasivos,Activos fijos,").append(sim.getNetFixedAssets()).append("\n");
        csv.append("I. Activos y Pasivos,Total activo neto,").append(sim.getTotalNetAssets()).append("\n");
        csv.append("I. Activos y Pasivos,Total pasivo,").append(sim.getTotalLiabilities()).append("\n");
        csv.append("I. Activos y Pasivos,Capital neto,").append(sim.getNetEquity()).append("\n");

        // II. Ingresos y deducciones
        csv.append("II. Ingresos y Deducciones,Venta de bienes y servicios,").append(sim.getSalesRevenue()).append("\n");
        csv.append("II. Ingresos y Deducciones,Servicios profesionales y honorarios,").append(sim.getProfessionalFees()).append("\n");
        csv.append("II. Ingresos y Deducciones,Comisiones,").append(sim.getCommissions()).append("\n");
        csv.append("II. Ingresos y Deducciones,Intereses y rendimientos,").append(sim.getInterestsAndYields()).append("\n");
        csv.append("II. Ingresos y Deducciones,Dividendos y participaciones,").append(sim.getDividendsAndShares()).append("\n");
        csv.append("II. Ingresos y Deducciones,Alquileres,").append(sim.getRents()).append("\n");
        csv.append("II. Ingresos y Deducciones,Otros ingresos,").append(sim.getOtherIncome()).append("\n");
        csv.append("II. Ingresos y Deducciones,Ingresos no gravables,").append(sim.getNonTaxableIncome()).append("\n");
        csv.append("II. Ingresos y Deducciones,Total renta bruta,").append(sim.getGrossIncomeTotal()).append("\n");

        // III. Costos, gastos y deducciones
        csv.append("III. Costos y Gastos,Inventario inicial,").append(sim.getInitialInventory()).append("\n");
        csv.append("III. Costos y Gastos,Compras,").append(sim.getPurchases()).append("\n");
        csv.append("III. Costos y Gastos,Inventario final,").append(sim.getFinalInventory()).append("\n");
        csv.append("III. Costos y Gastos,Costo de ventas,").append(sim.getCostOfGoodsSold()).append("\n");
        csv.append("III. Costos y Gastos,Gastos financieros,").append(sim.getFinancialExpenses()).append("\n");
        csv.append("III. Costos y Gastos,Gastos administrativos,").append(sim.getAdministrativeExpenses()).append("\n");
        csv.append("III. Costos y Gastos,Depreciación y amortización,").append(sim.getDepreciationAndAmortization()).append("\n");
        csv.append("III. Costos y Gastos,Aportes a pensiones,").append(sim.getPensionContributions()).append("\n");
        csv.append("III. Costos y Gastos,Otros deducibles,").append(sim.getOtherAllowableDeductions()).append("\n");
        csv.append("III. Costos y Gastos,Total deducciones,").append(sim.getTotalAllowableDeductions()).append("\n");

        // IV. Base imponible
        csv.append("IV. Base Imponible,Renta neta,").append(sim.getNetTaxableIncome()).append("\n");
        csv.append("IV. Base Imponible,Monto no sujeto,").append(sim.getNonTaxableSalaryAmount()).append("\n");
        csv.append("IV. Base Imponible,Impuesto ISR,").append(sim.getIncomeTax()).append("\n");
        csv.append("IV. Base Imponible,Exoneración zona franca,").append(sim.getFreeTradeZoneExemption()).append("\n");
        csv.append("IV. Base Imponible,Otras exoneraciones,").append(sim.getOtherExemptions()).append("\n");
        csv.append("IV. Base Imponible,ISR después de exoneraciones,").append(sim.getNetIncomeTaxAfterExemptions()).append("\n");

        // V. Créditos
        csv.append("V. Créditos,Crédito familiar,").append(sim.getFamilyCredit()).append("\n");
        csv.append("V. Créditos,Otros créditos,").append(sim.getOtherCredits()).append("\n");
        csv.append("V. Créditos,Impuesto del periodo,").append(sim.getPeriodTax()).append("\n");
        csv.append("V. Créditos,Retenciones 2%,").append(sim.getTwoPercentWithholdings()).append("\n");
        csv.append("V. Créditos,Otras retenciones,").append(sim.getOtherWithholdings()).append("\n");
        csv.append("V. Créditos,Pagos parciales,").append(sim.getPartialPayments()).append("\n");
        csv.append("V. Créditos,Total impuesto neto,").append(sim.getTotalNetTax()).append("\n");

        // VI. Liquidación de deuda
        csv.append("VI. Deuda Tributaria,Intereses,").append(sim.getInterests()).append("\n");
        csv.append("VI. Deuda Tributaria,Total deuda tributaria,").append(sim.getTotalTaxDebt()).append("\n");
        csv.append("VI. Deuda Tributaria,Compensación solicitada,").append(sim.getRequestedCompensation()).append("\n");
        csv.append("VI. Deuda Tributaria,Total deuda por pagar,").append(sim.getTotalDebtToPay()).append("\n");

        return csv.toString();
    }

    private String escape(String value) {
        if (value == null) return "";
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }


}
