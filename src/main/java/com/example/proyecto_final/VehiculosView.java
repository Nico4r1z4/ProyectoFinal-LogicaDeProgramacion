package com.example.proyecto_final;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Vehículos")
public class VehiculosView extends VerticalLayout {

    public Grid<Vehiculos> grid = new Grid<>();

    public VehiculosView() {
        H2 titulo = new H2("Vehículos");

        grid.addColumn(v -> v.placa).setHeader("Placa");
        grid.addColumn(v -> v.marca + " " + v.modelo).setHeader("Vehiculo");
        grid.addColumn(v -> v.anio).setHeader("Año");
        grid.addColumn(v -> v.tipo).setHeader("Tipo");
        grid.addColumn(v -> "$" + v.tarifa).setHeader("Tarifa/dia");
        grid.addColumn(v -> v.estado).setHeader("Estado");

        add(titulo, grid);
        cargarVehiculos();
    }

    public void cargarVehiculos() {
        grid.setItems(ArchivoUtil.leerVehiculos());
    }
}
