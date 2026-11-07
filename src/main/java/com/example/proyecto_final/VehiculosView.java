package com.example.proyecto_final;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Vehículos")
public class VehiculosView extends VerticalLayout {

    public Grid<Vehiculos> grid = new Grid<>();
    public TextField buscar = new TextField();
    public ComboBox<String> filtroTipo = new ComboBox<>();

    public VehiculosView() {
        H2 titulo = new H2("Vehículos");

        grid.addColumn(v -> v.placa).setHeader("Placa");
        grid.addColumn(v -> v.marca + " " + v.modelo).setHeader("Vehiculo");
        grid.addColumn(v -> v.anio).setHeader("Año");
        grid.addColumn(v -> v.tipo).setHeader("Tipo");
        grid.addColumn(v -> "$" + v.tarifa).setHeader("Tarifa/dia");
        grid.addColumn(v -> v.estado).setHeader("Estado");

        buscar.setPlaceholder("Buscar por placa o marca");
        buscar.setValueChangeMode(ValueChangeMode.EAGER);
        buscar.addValueChangeListener(e -> cargarVehiculos());

        filtroTipo.setItems("Todos", "Automovil", "Camioneta");
        filtroTipo.setValue("Todos");
        filtroTipo.addValueChangeListener(e -> cargarVehiculos());

        HorizontalLayout barra = new HorizontalLayout(buscar, filtroTipo);

        add(titulo, barra, grid);

        add(titulo, grid);
        cargarVehiculos();
    }

    public void cargarVehiculos() {
        String texto = buscar.getValue().toLowerCase();
        String tipo = filtroTipo.getValue();
        List<Vehiculos> resultado = new ArrayList<>();

        for (Vehiculos v : ArchivoUtil.leerVehiculos()) {
            boolean coincideTexto = v.placa.toLowerCase().contains(texto)
                    || v.marca.toLowerCase().contains(texto);
            boolean coincideTipo = tipo == null || tipo.equals("Todos") || v.tipo.equals(tipo);

            if (coincideTexto && coincideTipo) {
                resultado.add(v);
            }
        }
        grid.setItems(resultado);
    }
}
