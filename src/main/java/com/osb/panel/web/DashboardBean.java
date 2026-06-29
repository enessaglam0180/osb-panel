package com.osb.panel.web;

import com.osb.panel.domain.Sanayici.Durum;
import com.osb.panel.service.SanayiciService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;

@Named
@ViewScoped
public class DashboardBean implements Serializable {

    @Autowired
    private SanayiciService service;

    @Getter private String statusChartJson;
    @Getter private String sectorPlotChartJson;

    @Getter private long totalCount;
    @Getter private long activeCount;
    @Getter private long passiveCount;
    @Getter private long suspendedCount;
    @Getter private long activePlotM2;

    @PostConstruct
    public void init() {
        totalCount     = service.countAll();
        activeCount    = service.countByStatus(Durum.ACTIVE);
        passiveCount   = service.countByStatus(Durum.PASSIVE);
        suspendedCount = service.countByStatus(Durum.SUSPENDED);
        activePlotM2   = service.sumPlotSizeByStatus(Durum.ACTIVE);

        buildStatusChart();
        buildSectorChart();
    }

    private void buildStatusChart() {
        statusChartJson = """
            {
              "type": "doughnut",
              "data": {
                "labels": ["Active", "Passive", "Suspended"],
                "datasets": [{
                  "data": [%d, %d, %d],
                  "backgroundColor": ["#22c55e", "#94a3b8", "#f59e0b"],
                  "borderColor": ["#16a34a", "#64748b", "#d97706"],
                  "borderWidth": 2
                }]
              },
              "options": {
                "responsive": true,
                "maintainAspectRatio": false
              }
            }
            """.formatted(activeCount, passiveCount, suspendedCount);
    }

    private void buildSectorChart() {
        long activePlot = service.sumPlotSizeByStatus(Durum.ACTIVE);
        long passivePlot = service.sumPlotSizeByStatus(Durum.PASSIVE);
        long suspendedPlot = service.sumPlotSizeByStatus(Durum.SUSPENDED);

        sectorPlotChartJson = """
            {
              "type": "bar",
              "data": {
                "labels": ["Active", "Passive", "Suspended"],
                "datasets": [{
                  "label": "Plot Size (m²)",
                  "data": [%d, %d, %d],
                  "backgroundColor": ["rgba(34,197,94,0.7)", "rgba(148,163,184,0.7)", "rgba(245,158,11,0.7)"],
                  "borderColor": ["#16a34a", "#64748b", "#d97706"],
                  "borderWidth": 1
                }]
              },
              "options": {
                "responsive": true,
                "maintainAspectRatio": false,
                "plugins": {
                  "title": {
                    "display": true,
                    "text": "Total Plot Area by Status (m²)"
                  }
                }
              }
            }
            """.formatted(activePlot, passivePlot, suspendedPlot);
    }
}
