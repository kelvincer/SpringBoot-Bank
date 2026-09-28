package com.example.cbbackend;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface CalculatorLibrary extends Library {

    CalculatorLibrary INSTANCE = Native.load("native/libcalculator", CalculatorLibrary.class);

    int sumar(int a, int b);

    void saludar(String nombre);
}
