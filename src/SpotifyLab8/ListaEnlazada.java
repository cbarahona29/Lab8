/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
*/
package SpotifyLab8;
/**
 *
 * @author danilos
 */
public class ListaEnlazada {
    private Nodo cabeza;
    private int tamaño;
    
    public ListaEnlazada() {
        cabeza = null;
        tamaño = 0;
    }
    
    public void agregarCancion(Cancion cancion) {
        Nodo nuevoNodo = new Nodo(cancion);
        
        if (cabeza == null) {
            cabeza = nuevoNodo;
        } else {
            Nodo actual = cabeza;
            while (actual.siguiente != null) {
                actual = actual.siguiente;
            }
            actual.siguiente = nuevoNodo;
        }
        
        tamaño++;
    }
    
    public boolean eliminarCancion(int indice) {
        if (cabeza == null || indice < 0 || indice >= tamaño) {
            return false;
        }
        
        if (indice == 0) {
            cabeza = cabeza.siguiente;
            tamaño--;
            return true;
        }
        
        Nodo actual = cabeza;
        for (int i = 0; i < indice - 1; i++) {
            actual = actual.siguiente;
        }
        
        actual.siguiente = actual.siguiente.siguiente;
        tamaño--;
        return true;
    }
    
    public Cancion obtenerCancion(int indice) {
        if (cabeza == null || indice < 0 || indice >= tamaño) {
            return null;
        }
        
        Nodo actual = cabeza;
        for (int i = 0; i < indice; i++) {
            actual = actual.siguiente;
        }
        
        return actual.cancion;
    }
    
    public int getTamaño() {
        return tamaño;
    }
    
    public String[] obtenerArrayCanciones() {
        String[] canciones = new String[tamaño];
        
        Nodo actual = cabeza;
        for (int i = 0; i < tamaño; i++) {
            canciones[i] = actual.cancion.toString();
            actual = actual.siguiente;
        }       
        return canciones;
    }
}