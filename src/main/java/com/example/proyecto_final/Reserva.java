package com.example.proyecto_final;

import java.time.LocalDate;

public class Reserva {
    public String id;
    public String documento; // documento del cliente
    public String placa;
    public LocalDate inicio;
    public LocalDate fin;
    public String extras; // por ejemplo "Seguro,GPS"
    public double costo;
    public String estado; // Activa, Devuelta o Cancelada
    public double multa;
}
