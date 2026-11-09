package com.example.proyecto_final;

import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "reservas", layout = MainLayout.class)
@PageTitle("Reservas")
public class ReservasView extends VerticalLayout {

    public Grid<Reserva> grid = new Grid<>();
    public TextField buscar = new TextField();
    public ComboBox<String> filtroEstado = new ComboBox<>();

    public ReservasView() {
        H2 titulo = new H2("Reservas");

        grid.addColumn(r -> r.id).setHeader("Id");
        grid.addColumn(r -> r.documento).setHeader("Cliente");
        grid.addColumn(r -> r.placa).setHeader("Placa");
        grid.addColumn(r -> r.inicio).setHeader("Inicio");
        grid.addColumn(r -> r.fin).setHeader("Fin");
        grid.addColumn(r -> r.extras).setHeader("Extras");
        grid.addColumn(r -> "$" + r.costo).setHeader("Costo");
        grid.addColumn(r -> "$" + r.multa).setHeader("Multa");
        grid.addColumn(r -> r.estado).setHeader("Estado");

        buscar.setPlaceholder("Buscar por placa o documento");
        buscar.setValueChangeMode(ValueChangeMode.EAGER);
        buscar.addValueChangeListener(e -> cargarReservas());

        filtroEstado.setItems("Todas", "Activa", "Devuelta", "Cancelada");
        filtroEstado.setValue("Todas");
        filtroEstado.addValueChangeListener(e -> cargarReservas());

        HorizontalLayout barra = new HorizontalLayout(buscar, filtroEstado);

        add(titulo, barra, grid);
        cargarReservas();
    }

    public void cargarReservas() {
        String texto = buscar.getValue().toLowerCase();
        String estado = filtroEstado.getValue();
        List<Reserva> resultado = new ArrayList<>();

        for (Reserva r : ArchivoUtil.leerReservas()) {
            boolean coincideTexto = r.placa.toLowerCase().contains(texto) || r.documento.contains(texto);
            boolean coincideEstado = estado == null || estado.equals("Todas") || r.estado.equals(estado);

            if (coincideTexto && coincideEstado) {
                resultado.add(r);
            }
        }
        grid.setItems(resultado);
    }
}