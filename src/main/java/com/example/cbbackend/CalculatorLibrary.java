package com.example.cbbackend;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CalculatorLibrary extends Library {

    String libName = System.mapLibraryName("libcalculator"); // libcalculator.so / libcalculator.dll
    String path = "native/" + Platform.RESOURCE_PREFIX + "/" + libName;
    CalculatorLibrary INSTANCE = Native.load(path, CalculatorLibrary.class);

    int sumar(int a, int b);

    void saludar(String nombre);
}
