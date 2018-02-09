/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.util.List;
import org.dipalme.policia.bd.tienda.Administrador;
import org.dipalme.policia.bd.tienda.PedidoProductos;
import org.dipalme.policia.bd.tienda.Producto;
import org.dipalme.policia.bd.tienda.Usuario;

/**
 *
 * @author Almerimatik
 */
public class Tools {
    
    public static float calcularTotal(List<PedidoProductos> productos){  
        
        float total = 0;
        int unds;
        float precio;
        
        for(PedidoProductos reg : productos){
            
            Producto prod = Modelo.buscarProducto(reg.getIdProducto());
            unds = reg.getUnidades();
            precio = prod.getPrecio();
            total+= unds * precio;
        }
        
        return total;
    }
    
    public static boolean isAdmin(String usuario, String password) {
        
        Administrador admin = Modelo.cargarAdministrador(usuario, password);
        return admin != null;
    }
    
    public static boolean isUser(String usuario, String password) {
        
        Usuario user = Modelo.cargarUsuario(usuario, password);
        return user != null;
    }
    
}
