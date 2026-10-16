package com.example.gestiondereservas.data.room.entidades


enum class Area(val nombre: String, val encargadoPorDefecto: String) {
    INFORMATICA("Informática", "Camila Rojas"),
    FOTOGRAFIA("Fotografía", "Paula Silva"),
    DISENO("Diseño", "Felipe Mora"),
    COMUNICACIONES("Comunicaciones", "Martín Lagos"),
    SALUD("Salud", "Andrea Pérez"),
    GASTRONOMIA("Gastronomía", "Marco Soto"),
    TURISMO("Turismo", "Isabel Vega"),
    CONSTRUCCION("Construcción", "Rodrigo Muñoz")
}