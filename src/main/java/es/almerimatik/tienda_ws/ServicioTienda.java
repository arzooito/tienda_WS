/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import es.almerimatik.tienda_ws.comun.Constantes;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.dipalme.policia.bd.tienda.Pedido;
import org.dipalme.policia.bd.tienda.PedidoProductos;
import org.dipalme.policia.bd.tienda.Usuario;
import org.dipalme.policia.bd.tienda.UsuarioProductos;
import org.dipalme.policia.webbackend.servicios.Generico;
import org.w3c.dom.Document;

/**
 *
 * @author Almerimatik
 */
@WebService(serviceName = "ServicioTienda")
public class ServicioTienda {
   
    /*
    ============================================================================
    ============================================================================
                                LLAMADAS AL WS
    ============================================================================
    ============================================================================
    */
    
    @WebMethod(operationName = "loginAdmin")
    public boolean loginAdmin(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
        
       return Tools.isAdmin(usuario, password);
    }
       
    @WebMethod(operationName = "cargarPedidos")
    public String cargarPedidos(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
        
        String xml = null;
        if(Tools.isAdmin(usuario, password)){
            xml = XML.construirPedidos();
        }
        return xml;
    }
      
    @WebMethod(operationName = "finalizarPedido")
    public void finalizarPedido(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password, @WebParam(name = "idPedido")long idPedido) {
        
       if( Tools.isAdmin(usuario, password)){
           Modelo.finalizarPedido(idPedido);
       }
    }
    
    @WebMethod(operationName = "getFoto")
    public byte[] getFoto(@WebParam(name = "ruta") String ruta) { 
        
        byte[] foto = null;
        try {
            foto = recuperarRecurso(Constantes.getRutaBase() + ruta);
        } catch (IOException ex) {
            Logger.getLogger(ServicioTienda.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return foto;
    }
    
    /*
    ============================================================================
    ============================================================================
    ============================================================================
    */
    
    private byte[] recuperarRecurso(String ruta) throws FileNotFoundException, IOException{
        
        File recurso = new File(ruta);
        byte[] data = new byte[(int)recurso.length()];
        FileInputStream fis = new FileInputStream(recurso);
        fis.read(data);
        fis.close();
        
        return data;
    }
}
