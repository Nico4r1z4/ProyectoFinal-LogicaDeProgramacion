package com.example.proyecto_final;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route(value = "clientes", layout = MainLayout.class)
@PageTitle("Clientes")
public class ClientesView extends VerticalLayout {

    public Grid<Cliente> grid = new Grid<>();
    public TextField buscar = new TextField();

    public ClientesView() {
        H2 titulo = new H2("Clientes");

        grid.addColumn(c -> c.documento).setHeader("Documento");
        grid.addColumn(c -> c.nombre).setHeader("Nombre");
        grid.addColumn(c -> c.telefono).setHeader("Telefono");
        grid.addComponentColumn(c -> crearBotones(c)).setHeader("Acciones");

        buscar.setPlaceholder("Buscar por nombre o documento");
        buscar.setValueChangeMode(ValueChangeMode.EAGER);
        buscar.addValueChangeListener(e -> cargarClientes());

        Button btnNuevo = new Button("Nuevo cliente", e -> abrirFormulario(null));

        HorizontalLayout barra = new HorizontalLayout(buscar, btnNuevo);

        add(titulo, barra, grid);
        cargarClientes();
    }

    public void cargarClientes() {
        String texto = buscar.getValue().toLowerCase();
        List<Cliente> resultado = new ArrayList<>();

        for (Cliente c : ArchivoUtil.leerClientes()) {
            if (c.nombre.toLowerCase().contains(texto) || c.documento.contains(texto)) {
                resultado.add(c);
            }
        }
        grid.setItems(resultado);
    }

    public HorizontalLayout crearBotones(Cliente c) {
        Button btnEditar = new Button("Editar", e -> abrirFormulario(c));
        Button btnEliminar = new Button("Eliminar", e -> confirmarEliminar(c));
        return new HorizontalLayout(btnEditar, btnEliminar);
    }

    public void abrirFormulario(Cliente c) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("Cliente");

        TextField documento = new TextField("Documento");
        TextField nombre = new TextField("Nombre");
        TextField telefono = new TextField("Teléfono");

        if (c != null) {
            documento.setValue(c.documento);
            documento.setReadOnly(true); // el documento no se cambia
            nombre.setValue(c.nombre);
            telefono.setValue(c.telefono);
        }

        dialogo.add(new FormLayout(documento, nombre, telefono));

        Button btnGuardar = new Button("Guardar", e -> {
            if (documento.isEmpty() || nombre.isEmpty() || telefono.isEmpty()) {
                Notification.show("Llena todos los campos");
                return;
            }
            if (telefono.getValue().length() != 10) {
                Notification.show("El teléfono debe tener 10 números");
                return;
            }

            Cliente nuevo = new Cliente();
            nuevo.documento = documento.getValue();
            nuevo.nombre = nombre.getValue();
            nuevo.telefono = telefono.getValue();

            List<Cliente> clientes = ArchivoUtil.leerClientes();
            if (c == null) {
                for (Cliente otro : clientes) {
                    if (otro.documento.equals(nuevo.documento)) {
                        Notification.show("Ya existe un cliente con ese documento");
                        return;
                    }
                }
                clientes.add(nuevo);
            } else {
                for (int i = 0; i < clientes.size(); i++) {
                    if (clientes.get(i).documento.equals(nuevo.documento)) {
                        clientes.set(i, nuevo);
                    }
                }
            }
            ArchivoUtil.guardarClientes(clientes);
            Notification.show("Cliente guardado");
            dialogo.close();
            cargarClientes();
        });
        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());

        dialogo.getFooter().add(btnCancelar, btnGuardar);
        dialogo.open();
    }

    public void confirmarEliminar(Cliente c) {
        Dialog dialogo = new Dialog();
        dialogo.setHeaderTitle("¿Eliminar a " + c.nombre + "?");
        dialogo.add(new Paragraph("Esta accion no se puede deshacer."));

        Button btnCancelar = new Button("Cancelar", e -> dialogo.close());
        Button btnConfirmar = new Button("Si eliminar", e -> {
            List<Cliente> clientes = ArchivoUtil.leerClientes();
            for (int i = 0; i < clientes.size(); i++) {
                if (clientes.get(i).documento.equals(c.documento)) {
                    clientes.remove(i);
                    break;
                }
            }
            ArchivoUtil.guardarClientes(clientes);
            Notification.show("Cliente eliminado");
            dialogo.close();
            cargarClientes();
        });

        dialogo.getFooter().add(btnCancelar, btnConfirmar);
        dialogo.open();
    }
}