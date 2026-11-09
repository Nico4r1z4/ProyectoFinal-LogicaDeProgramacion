package com.example.proyecto_final;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class ArchivoUtil {
    public static final String CARPETA = "src/main/resources/data";
    public static final String ARCHIVO_VEHICULOS = CARPETA + "/vehiculos.txt";
    public static final String ARCHIVO_CLIENTES = CARPETA + "/clientes.txt";
    public static final String ARCHIVO_RESERVAS = CARPETA + "/reservas.txt";

    public static List<String> leerLineas(String ruta) {
        List<String> lineas = new ArrayList<>();
        File archivo = new File(ruta);
        if (!archivo.exists()) {
            crearDatosEjemplo();
        }
        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.trim().isEmpty()) {
                    lineas.add(linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer " + ruta + ": " + e.getMessage());
        }
        return lineas;
    }

    public static void escribirLineas(String ruta, List<String> lineas) {
        File carpeta = new File(CARPETA);
        if (!carpeta.exists()) {
            carpeta.mkdirs();
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ruta))) {
            for (String linea : lineas) {
                bw.write(linea);
                bw.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error al escribir " + ruta + ": " + e.getMessage());
        }
    }

    public static void crearDatosEjemplo() {
        LocalDate hoy = LocalDate.now();

        if (!new File(ARCHIVO_VEHICULOS).exists()) {
            List<String> v = new ArrayList<>();
            v.add("ABC123|Mazda|3|2026|Automovil|150000.0|Alquilado|https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQcraVpoz4QQVdsi1G0PjEvKmqawTkPjgUPkaszLKFAo0HhkbXOMC9VzGtg&s=10");
            v.add("DEF456|Renault|Logan|2020|Automovil|110000.0|Disponible|https://cdn.group.renault.com/ren/co/vehicles/logan/design/exterior/logan-lateral-exterior.jpg.ximg.small.webp/210c18118f.webp");
            v.add("GHI789|Chevrolet|Tracker|2026|Camioneta|190000.0|Disponible|https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRJ5M-5BS1d3hFxTkkL4iTEggjU0e_leXTkYa0aWZ3vFg&s=10");
            v.add("JKL012|Toyota|Hilux|2021|Camioneta|250000.0|Disponible|https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTPbDcpUNhaLUYqptmfogk7eG1tbMVrOH2QYwsMSyVhsoVCSiaBx1Ruc9RG&s=10");
            v.add("MNO345|Kia|Picanto|2024|Automovil|95000.0|Disponible|https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGXE2ETx9Ek-Y_Jruz8KVP7neriMJJX1wUIwbsfGqFdg&s=10");
            v.add("PQR678|Renault|Duster|2022|Camioneta|170000.0|Mantenimiento|https://www.renaultnorteysur.com/wp-content/uploads/2024/08/renault-duster-portada.webp");
            escribirLineas(ARCHIVO_VEHICULOS, v);
        }

        if (!new File(ARCHIVO_CLIENTES).exists()) {
            List<String> c = new ArrayList<>();
            c.add("1020304050|Laura Gomez|3001234567");
            c.add("1098765432|Andres Ramirez|3109876543");
            c.add("43567890|Maria Ruiz|3204567890");
            c.add("71234567|Carlos Mejia|3152223344");
            escribirLineas(ARCHIVO_CLIENTES, c);
        }

        if (!new File(ARCHIVO_RESERVAS).exists()) {

            List<String> r = new ArrayList<>();
            r.add("R1|1020304050|ABC123|" + hoy.minusMonths(2) + "|" + hoy.minusMonths(2).plusDays(2)
                    + "|Seguro|510000.0|Devuelta|0.0");
            r.add("R2|1098765432|DEF456|" + hoy.minusMonths(1) + "|" + hoy.minusMonths(1).plusDays(1)
                    + "||220000.0|Devuelta|165000.0");
            r.add("R3|43567890|ABC123|" + hoy.minusMonths(1).plusDays(5) + "|" + hoy.minusMonths(1).plusDays(6)
                    + "|GPS|320000.0|Devuelta|0.0");
            r.add("R4|71234567|ABC123|" + hoy.minusDays(1) + "|" + hoy.plusDays(2) + "|GPS|640000.0|Activa|0.0");
            r.add("R5|1020304050|JKL012|" + hoy.plusDays(3) + "|" + hoy.plusDays(5)
                    + "|Seguro,GPS|840000.0|Activa|0.0");
            escribirLineas(ARCHIVO_RESERVAS, r);
        }
    }

    // Para los Vehiculos:
    public static List<Vehiculos> leerVehiculos() {
        List<Vehiculos> vehiculos = new ArrayList<>();
        for (String linea : leerLineas(ARCHIVO_VEHICULOS)) {
            try {
                String[] d = linea.split("\\|");
                Vehiculos v = new Vehiculos();
                v.placa = d[0];
                v.marca = d[1];
                v.modelo = d[2];
                v.anio = Integer.parseInt(d[3]);
                v.tipo = d[4];
                v.tarifa = Double.parseDouble(d[5]);
                v.estado = d[6];
                v.foto = d[7];
                vehiculos.add(v);
            } catch (Exception e) {
                System.out.println("Linea mala en vehiculos.txt: " + linea);
            }
        }
        return vehiculos;
    }

    public static void guardarVehiculos(List<Vehiculos> vehiculos) {
        List<String> lineas = new ArrayList<>();
        for (Vehiculos v : vehiculos) {
            lineas.add(v.placa + "|" + v.marca + "|" + v.modelo + "|" + v.anio + "|" + v.tipo + "|"
                    + v.tarifa + "|" + v.estado + "|" + v.foto);
        }
        escribirLineas(ARCHIVO_VEHICULOS, lineas);
    }

    public static List<Cliente> leerClientes() {
        List<Cliente> clientes = new ArrayList<>();
        for (String linea : leerLineas(ARCHIVO_CLIENTES)) {
            try {
                String[] d = linea.split("\\|");
                Cliente c = new Cliente();
                c.documento = d[0];
                c.nombre = d[1];
                c.telefono = d[2];
                clientes.add(c);
            } catch (Exception e) {
                System.out.println("Línea mala en clientes.txt: " + linea);
            }
        }
        return clientes;
    }

    public static void guardarClientes(List<Cliente> clientes) {
        List<String> lineas = new ArrayList<>();
        for (Cliente c : clientes) {
            lineas.add(c.documento + "|" + c.nombre + "|" + c.telefono);
        }
        escribirLineas(ARCHIVO_CLIENTES, lineas);
    }

    public static List<Reserva> leerReservas() {
        List<Reserva> reservas = new ArrayList<>();
        for (String linea : leerLineas(ARCHIVO_RESERVAS)) {
            try {
                String[] d = linea.split("\\|");
                Reserva r = new Reserva();
                r.id = d[0];
                r.documento = d[1];
                r.placa = d[2];
                r.inicio = LocalDate.parse(d[3]);
                r.fin = LocalDate.parse(d[4]);
                r.extras = d[5];
                r.costo = Double.parseDouble(d[6]);
                r.estado = d[7];
                r.multa = Double.parseDouble(d[8]);
                reservas.add(r);
            } catch (Exception e) {
                System.out.println("Línea mala en reservas.txt: " + linea);
            }
        }
        return reservas;
    }

    public static void guardarReservas(List<Reserva> reservas) {
        List<String> lineas = new ArrayList<>();
        for (Reserva r : reservas) {
            lineas.add(r.id + "|" + r.documento + "|" + r.placa + "|" + r.inicio + "|" + r.fin + "|"
                    + r.extras + "|" + r.costo + "|" + r.estado + "|" + r.multa);
        }
        escribirLineas(ARCHIVO_RESERVAS, lineas);
    }
}
