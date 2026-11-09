package com.example.proyecto_final;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.NumberField;

import java.util.ArrayList;
import java.util.List;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Vehículos")
public class VehiculosView extends VerticalLayout {

    public Grid<Vehiculos> grid = new Grid<>();
    public TextField buscar = new TextField();
    public ComboBox<String> filtroTipo = new ComboBox<>();

    public VehiculosView() {
        H2 titulo = new H2("Vehiculos");

        grid.addColumn(v -> v.placa).setHeader("Placa");
        grid.addColumn(v -> v.marca + " " + v.modelo).setHeader("Vehiculo");
        grid.addColumn(v -> v.anio).setHeader("Año");
        grid.addColumn(v -> v.tipo).setHeader("Tipo");
        grid.addColumn(v -> "$" + v.tarifa).setHeader("Tarifa/dia");
        grid.addColumn(v -> v.estado).setHeader("Estado");
        grid.addComponentColumn(v -> new Button("Editar", e -> abrirFormulario(v))).setHeader("Acciones");

        buscar.setPlaceholder("Buscar por placa o marca");
        buscar.setValueChangeMode(ValueChangeMode.EAGER);
        buscar.addValueChangeListener(e -> cargarVehiculos());

        filtroTipo.setItems("Todos", "Automovil", "Camioneta");
        filtroTipo.setValue("Todos");
        filtroTipo.addValueChangeListener(e -> cargarVehiculos());

        Button btnNuevo = new Button("Nuevo vehiculo", e -> abrirFormulario(null));

        HorizontalLayout barra = new HorizontalLayout(buscar, filtroTipo, btnNuevo);

        add(titulo, barra, grid);
        cargarVehiculos();
    }

    public void cargarVehiculos() {
        String texto = buscar.getValue().toLowerCase();
        String tipo = filtroTipo.getValue();
        List<Vehiculos> resultado = new ArrayList<>();

        for (Vehiculos v : ArchivoUtil.leerVehiculos()) {
            boolean coincideTexto = v.placa.toLowerCase().contains(texto) || v.marca.toLowerCase().contains(texto);
            boolean coincideTipo = tipo == null || tipo.equals("Todos") || v.tipo.equals(tipo);

            if (coincideTexto && coincideTipo) {
                resultado.add(v);
            }
        }
        grid.setItems(resultado);
    }

    public void abrirFormulario(Vehiculos v) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("Vehículo");

        TextField placa = new TextField("Placa");
        TextField marca = new TextField("Marca");
        TextField modelo = new TextField("Modelo");
        IntegerField anio = new IntegerField("Año");
        ComboBox<String> tipo = new ComboBox<>("Tipo");
        tipo.setItems("Automovil", "Camioneta");
        NumberField tarifa = new NumberField("Tarifa por dia");
        ComboBox<String> estado = new ComboBox<>("Estado");
        estado.setItems("Disponible", "Alquilado", "Mantenimiento");
        estado.setValue("Disponible");
        TextField foto = new TextField("URL de la foto");

        if (v != null) {
            placa.setValue(v.placa);
            placa.setReadOnly(true);
            marca.setValue(v.marca);
            modelo.setValue(v.modelo);
            anio.setValue(v.anio);
            tipo.setValue(v.tipo);
            tarifa.setValue(v.tarifa);
            estado.setValue(v.estado);
            foto.setValue(v.foto);
        }

        dialogo.add(new FormLayout(placa, marca, modelo, anio, tipo, tarifa, estado, foto));

        Button btnGuardar = new Button("Guardar", e -> {

            if (placa.isEmpty() || marca.isEmpty() || modelo.isEmpty() || anio.isEmpty()
                    || tipo.isEmpty() || tarifa.isEmpty()) {
                Notification.show("Llena todos los campos");
                return;
            }
            if (placa.getValue().length() != 6) {
                Notification.show("La placa debe tener 6 caracteres, por ejemplo ABC123");
                return;
            }
            if (anio.getValue() < 1990 || anio.getValue() > 2027) {
                Notification.show("El año debe estar entre 1990 y 2027");
                return;
            }
            if (tarifa.getValue() <= 0) {
                Notification.show("La tarifa debe ser mayor que 0");
                return;
            }

            String urlFoto = foto.getValue();
            if (urlFoto.isEmpty()) {
                urlFoto = "https://placehold.co/300x180?text=" + marca.getValue();
            }

            Vehiculos nuevo = new Vehiculos();
            nuevo.placa = placa.getValue().toUpperCase();
            nuevo.marca = marca.getValue();
            nuevo.modelo = modelo.getValue();
            nuevo.anio = anio.getValue();
            nuevo.tipo = tipo.getValue();
            nuevo.tarifa = tarifa.getValue();
            nuevo.estado = estado.getValue();
            nuevo.foto = urlFoto;

            List<Vehiculos> vehiculos = ArchivoUtil.leerVehiculos();
            if (v == null) {
                for (Vehiculos otro : vehiculos) {
                    if (otro.placa.equals(nuevo.placa)) {
                        Notification.show("Ya existe un vehiculo con esa placa");
                        return;
                    }
                }
                vehiculos.add(nuevo);
            } else {
                for (int i = 0; i < vehiculos.size(); i++) {
                    if (vehiculos.get(i).placa.equals(nuevo.placa)) {
                        vehiculos.set(i, nuevo);
                    }
                }
            }
            ArchivoUtil.guardarVehiculos(vehiculos);
            Notification.show("Vehiculo guardado");
            dialogo.close();
            cargarVehiculos();
        });
        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());

        dialogo.getFooter().add(btnCancelar, btnGuardar);
        dialogo.open();
    }
}
