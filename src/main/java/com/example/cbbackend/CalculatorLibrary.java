package com.example.cbbackend;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Platform;

public interface CalculatorLibrary extends Library {

    String LIB_NAME = "libcalculator";

    int sumar(int a, int b);

    void saludar(String nombre);

    static String resourcePath() {
        return "native/" + Platform.RESOURCE_PREFIX + "/" + System.mapLibraryName(LIB_NAME);
    }

    static CalculatorLibrary instance() {
        return Holder.require();
    }

    final class Holder {

        private static final String PATH = resourcePath();
        private static final CalculatorLibrary LIBRARY;
        private static final LinkageError FAILURE;

        static {
            CalculatorLibrary loaded = null;
            LinkageError failure = null;
            try {
                loaded = Native.load(PATH, CalculatorLibrary.class);
            } catch (LinkageError e) {
                failure = e;
            }
            LIBRARY = loaded;
            FAILURE = failure;
        }

        static CalculatorLibrary require() {
            if (FAILURE != null) {
                throw new UnsatisfiedLinkError(
                        "No se pudo cargar la libreria nativa '" + PATH + "' para la plataforma "
                                + Platform.RESOURCE_PREFIX + ". Compila " + LIB_NAME + " para esta "
                                + "plataforma y colocalo en src/main/resources/" + PATH + ". "
                                + "Causa original: " + FAILURE.getMessage());
            }
            return LIBRARY;
        }

        private Holder() {
        }
    }
}
