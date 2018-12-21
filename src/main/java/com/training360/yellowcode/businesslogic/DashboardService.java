package com.training360.yellowcode.businesslogic;

import com.training360.yellowcode.database.DashboardDao;
import com.training360.yellowcode.dbTables.Dashboard;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private DashboardDao dashboardDao;


    public DashboardService(DashboardDao dashboardDao) {
        this.dashboardDao = dashboardDao;
    }

    @PreAuthorize("hasRole('ADMIN')")
    public Dashboard createDashboard() {
        return dashboardDao.createDashboard();
    }
}
