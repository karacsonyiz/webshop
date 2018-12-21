package com.training360.yellowcode.userinterface;

import com.training360.yellowcode.businesslogic.DashboardService;
import com.training360.yellowcode.dbTables.Dashboard;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DashboardController {

    private DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public Dashboard createDashboard() {
        return dashboardService.createDashboard();
    }
}
