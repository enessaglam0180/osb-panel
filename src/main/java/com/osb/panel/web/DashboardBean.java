package com.osb.panel.web;

import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import java.io.Serializable;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Getter private long totalCount = 0;
    @Getter private long activeCount = 0;
    @Getter private long passiveCount = 0;
    @Getter private long suspendedCount = 0;
    @Getter private double activePlotM2 = 0;

    @Getter private String statusChartJson = "{}";
    @Getter private String sectorPlotChartJson = "{}";

    @PostConstruct
    public void init() {
        // Mock veri veya servis çağrıları buraya gelecek
        totalCount = 120;
        activeCount = 85;
        passiveCount = 25;
        suspendedCount = 10;
        activePlotM2 = 450000;
    }
}