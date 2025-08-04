package com.project.demo.rest.reportAdmin;

import com.project.demo.logic.entity.http.GlobalResponseHandler;
import com.project.demo.logic.entity.reportAdmin.ReportAdminInvoicesService;
import com.project.demo.logic.entity.reportAdmin.ReportAdminUsersService;
import com.project.demo.logic.entity.user.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("reports-admin")
public class ReportAdminRestController {

    @Autowired
    private ReportAdminUsersService reportAdminUsersService;

    @Autowired
    private ReportAdminInvoicesService reportAdminInvoicesService;

    @GetMapping("/registered-users")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getRegisteredUsers(
            @RequestParam(defaultValue = "0") int year,
            HttpServletRequest request) {

        Map<Integer, Integer> registeredUsers = reportAdminUsersService.getMonthlyRegisteredUsers(year);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de usuarios registrados por mes recuperado exitosamente",
                registeredUsers,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/status-proportion")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getStatusProportion(HttpServletRequest request) {

        Map<String, Integer> proportionUsers = reportAdminUsersService.getProportionStatus();

        return new GlobalResponseHandler().handleResponse(
                "Reporte de proporción de estado de usuarios recuperado exitosamente",
                proportionUsers,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/volume-invoices")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getMonthlyVolumeInvoices(@RequestParam(defaultValue = "0") int year, HttpServletRequest request){
        Map<Integer, Integer> volumeInvoices = reportAdminInvoicesService.getMonthlyVolumeInvoices(year);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de volumen mensual de facturas procesadas recuperado exitosamente",
                volumeInvoices,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/proportion-income-expenses")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getTotalIncomeAndExpensesByYear(@RequestParam(defaultValue = "0") int year, HttpServletRequest request){

        Map<String, Double> totalIncomeAndExpenses = reportAdminInvoicesService.getTotalIncomeAndExpensesByYear(year);

        return new GlobalResponseHandler().handleResponse(
                "Reporte de volumen mensual de facturas procesadas recuperado exitosamente",
                totalIncomeAndExpenses,
                HttpStatus.OK,
                request);
    }

    @GetMapping("/top-users-invoice-volume")
    @PreAuthorize("hasAnyRole('SUPER_ADMIN')")
    public ResponseEntity<?> getTop10UsersByInvoiceVolume(HttpServletRequest request){

        List<Map<String, Object>> top10InvoicesVolume = reportAdminInvoicesService.getTop10UsersByInvoiceVolume();

        return new GlobalResponseHandler().handleResponse(
                "Reporte de volumen mensual de facturas procesadas recuperado exitosamente",
                top10InvoicesVolume,
                HttpStatus.OK,
                request);
    }
}
