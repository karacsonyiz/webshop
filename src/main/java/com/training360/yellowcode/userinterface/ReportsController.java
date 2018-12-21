package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.OrdersService;
import com.training360.yellowcode.businesslogic.ReportsService;
import com.training360.yellowcode.dbTables.Orders;
import com.training360.yellowcode.dbTables.Reports;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ReportsController {

    private ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    @RequestMapping(value = "/api/reports/orders", method = RequestMethod.GET)
    public List<Reports> listReportsByDate() {
        return reportsService.listReportsByDate();
    }

    @RequestMapping(value = "/api/reports/products", method = RequestMethod.GET)
    public List<Reports> listReportsByProductAndDate(){
        return reportsService.listReportsByProductAndDate();
    }
}
