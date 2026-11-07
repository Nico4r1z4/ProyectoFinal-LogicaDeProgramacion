package com.example.proyecto_final;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class MainLayout extends AppLayout {

    public MainLayout() {

        H1 titulo = new H1("Alquiler de Vehiculos");
        titulo.getStyle().set("font-size", "1.4rem");

        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), titulo);
        header.setDefaultVerticalComponentAlignment(FlexComponent.Alignment.CENTER);
        addToNavbar(header);

        Button btnVehiculos = new Button("Vehiculos", e -> UI.getCurrent().navigate(VehiculosView.class));

        VerticalLayout menu = new VerticalLayout(btnVehiculos);
        addToDrawer(menu);
    }
}