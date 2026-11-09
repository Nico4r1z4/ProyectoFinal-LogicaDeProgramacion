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
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.CheckboxGroup;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import java.util.ArrayList;
import java.util.List;

@Route(value = "reservas", layout = MainLayout.class)
@PageTitle("Reservas")
public class ReservasView extends VerticalLayout {

    public Grid<Reserva> grid = new Grid<>();
    public TextField buscar = new TextField();
    public ComboBox<String> filtroEstado = new ComboBox<>();
    public static final double SEGURO = 20000;
    public static final double GPS = 10000;
    public static final double SILLA = 8000;
    public ComboBox<String> cliente;
    public ComboBox<String> vehiculo;
    public DatePicker inicio;
    public DatePicker fin;
    public CheckboxGroup<String> extras;
    public H3 textoCosto;

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

        Button btnNueva = new Button("Nueva reserva", e -> abrirFormulario());

        HorizontalLayout barra = new HorizontalLayout(buscar, filtroEstado, btnNueva);

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

    public int contarDias(LocalDate desde, LocalDate hasta) {
        return (int) ChronoUnit.DAYS.between(desde, hasta) + 1;
    }

    public double buscarTarifa(String placa) {
        for (Vehiculos v : ArchivoUtil.leerVehiculos()) {
            if (v.placa.equals(placa)) {
                return v.tarifa;
            }
        }
        return 0;
    }

    public double calcularCosto() {
        if (vehiculo.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
            return 0;
        }
        if (fin.getValue().isBefore(inicio.getValue())) {
            return 0;
        }
        int dias = contarDias(inicio.getValue(), fin.getValue());
        double tarifa = buscarTarifa(vehiculo.getValue());

        double valorExtras = 0;
        for (String extra : extras.getValue()) {
            if (extra.equals("Seguro")) {
                valorExtras = valorExtras + SEGURO;
            } else if (extra.equals("GPS")) {
                valorExtras = valorExtras + GPS;
            } else {
                valorExtras = valorExtras + SILLA;
            }
        }
        return dias * tarifa + dias * valorExtras;
    }

    public void actualizarCosto() {
        textoCosto.setText("Costo: " + calcularCosto());
    }

    public void abrirFormulario() {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("Nueva reserva");

        cliente = new ComboBox<>("Cliente");
        List<String> listaClientes = new ArrayList<>();
        for (Cliente c : ArchivoUtil.leerClientes()) {
            listaClientes.add(c.documento + " - " + c.nombre);
        }
        cliente.setItems(listaClientes);

        vehiculo = new ComboBox<>("Vehículo (placa)");
        List<String> placas = new ArrayList<>();
        for (Vehiculos v : ArchivoUtil.leerVehiculos()) {
            if (!v.estado.equals("Mantenimiento")) {
                placas.add(v.placa);
            }
        }
        vehiculo.setItems(placas);

        inicio = new DatePicker("Fecha de inicio");
        fin = new DatePicker("Fecha de fin");
        inicio.setMin(LocalDate.now());

        extras = new CheckboxGroup<>("Extras (valor por día)");
        extras.setItems("Seguro", "GPS", "Silla de bebé");

        textoCosto = new H3("Costo: $0");

        vehiculo.addValueChangeListener(e -> actualizarCosto());
        inicio.addValueChangeListener(e -> actualizarCosto());
        fin.addValueChangeListener(e -> actualizarCosto());
        extras.addValueChangeListener(e -> actualizarCosto());

        dialogo.add(new FormLayout(cliente, vehiculo, inicio, fin), extras,
                new Paragraph("Seguro $20000 · GPS $10000 · Silla de bebe $8000 (por dia)"), textoCosto);

        Button btnGuardar = new Button("Guardar", e -> {
            if (cliente.isEmpty() || vehiculo.isEmpty() || inicio.isEmpty() || fin.isEmpty()) {
                Notification.show("Llena el cliente, el vehículo y las fechas");
                return;
            }
            if (fin.getValue().isBefore(inicio.getValue())) {
                Notification.show("La fecha de fin no puede ser antes de la de inicio");
                return;
            }

            String documento = cliente.getValue().split(" - ")[0];

            Reserva nueva = new Reserva();
            nueva.id = UUID.randomUUID().toString().substring(0, 8);
            nueva.documento = documento;
            nueva.placa = vehiculo.getValue();
            nueva.inicio = inicio.getValue();
            nueva.fin = fin.getValue();
            nueva.extras = textoExtras();
            nueva.costo = calcularCosto();
            nueva.estado = "Activa";
            nueva.multa = 0;

            List<Reserva> reservas = ArchivoUtil.leerReservas();
            reservas.add(nueva);
            ArchivoUtil.guardarReservas(reservas);

            Notification.show("Reserva guardada. Total: " + nueva.costo);
            dialogo.close();
            cargarReservas();
        });
        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());

        dialogo.getFooter().add(btnCancelar, btnGuardar);
        dialogo.open();
    }

    public String textoExtras() {
        String texto = "";
        for (String extra : extras.getValue()) {
            if (texto.isEmpty()) {
                texto = extra;
            } else {
                texto = texto + "," + extra;
            }
        }
        return texto;
    }

}