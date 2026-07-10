package com.osb.panel.web;

import com.osb.panel.domain.Sanayici;
import com.osb.panel.service.SanayiciService;
import jakarta.annotation.PostConstruct;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.List;

@Named
@ViewScoped
public class SanayiciBean implements Serializable {

    @Autowired
    private transient SanayiciService service;

    @Getter private List<Sanayici> sanayiciler;
    @Getter @Setter private Sanayici selected;
    @Getter private boolean editMode;

    @Getter @Setter private String aramaKelimesi;

    @Getter @Setter private List<Sanayici> filteredSanayiciler;

    @PostConstruct
    public void init() {
        load();
    }

    public void load() {
        sanayiciler = service.findAll();
        aramaKelimesi = null; // Listeyi sıfırlayınca arama kutusu da temizlensin
    }


    public void ara() {
        sanayiciler = service.ara(aramaKelimesi);
    }

    public void prepareNew() {
        selected = new Sanayici();
        editMode = false;
    }

    public void prepareEdit(Sanayici s) {
        // Re-fetch from DB to get a managed/fresh entity and avoid detached entity issues
        selected = service.findById(s.getId()).orElse(null);
        editMode = true;
    }

    public void save() {
        try {
            service.save(selected);
            load();
            addMessage(FacesMessage.SEVERITY_INFO,
                    editMode ? "Record updated successfully." : "Record created successfully.");
            PrimeFaces.current().executeScript("PF('recordDialog').hide()");
            PrimeFaces.current().ajax().update("mainForm:dt");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error saving record: " + e.getMessage());
        }
    }

    public void delete(Sanayici s) {
        try {
            service.deleteById(s.getId());
            load();
            addMessage(FacesMessage.SEVERITY_WARN, "\"" + s.getCompanyName() + "\" has been deleted.");
        } catch (Exception e) {
            addMessage(FacesMessage.SEVERITY_ERROR, "Error deleting record: " + e.getMessage());
        }
    }

    public Sanayici.Durum[] getStatusValues() {
        return Sanayici.Durum.values();
    }

    private void addMessage(FacesMessage.Severity severity, String summary) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(severity, summary, null));
    }
}