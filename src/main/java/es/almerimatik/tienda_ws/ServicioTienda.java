/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.util.List;
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
    
    /**
     * This is a sample web service operation
     */
    @WebMethod(operationName = "guardarPedido")
    public void guardarPedido(@WebParam(name = "pedido") String xmlPedido) {
        
       Document doc = XML.getDocumento(xmlPedido);
       
       System.out.println(xmlPedido);
       
       List<PedidoProductos> pProductos = XML.getProductos(doc);
       Pedido pedido = XML.getPedido(doc);
       float total = Tools.calcularTotal(pProductos);
       pedido.setPrecioTotal(total);
       Modelo.guardarPedido(pedido);
       Modelo.guardarPedidoProductos(pedido.getId(), pProductos);
       actualizarHistorial(pedido,pProductos);
    }
    
    @WebMethod(operationName = "cargarPedidos")
    public String cargarPedidos(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
        
        String xml = null;
        if(Tools.isAdmin(usuario, password)){
            xml = XML.construirPedidos();
        }
        return xml;
    }
    
    @WebMethod(operationName = "actualizar")
    public void actualizar(@WebParam(name = "fecha") String fecha) {
        
           
    }
    
    @WebMethod(operationName = "loginAdmin")
    public boolean loginAdmin(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
        
       return Tools.isAdmin(usuario, password);
    }
    
    @WebMethod(operationName = "login")
    public boolean login(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
        
       return Tools.isUser(usuario, password);
    }
    
    @WebMethod(operationName = "registrarUsuario")
    public void registrarUsuario(
            @WebParam(name = "nombre") String nombre, 
            @WebParam(name = "password")String password,
            @WebParam(name = "mail") String mail,
            @WebParam(name = "telefono") String telefono) {
        
        Usuario user = new Usuario();
        user.setNombre(nombre);
        user.setPassword(password);
        user.setEmail(mail);
        user.setTelefono(telefono);
        
        Generico.guardar(user);
    }
    
    @WebMethod(operationName = "finalizarPedido")
    public void finalizarPedido(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password, @WebParam(name = "idPedido")long idPedido) {
        
       if( Tools.isAdmin(usuario, password)){
           Modelo.finalizarPedido(idPedido);
       }
    }
    
    
    /*
    ============================================================================
    ============================================================================
    ============================================================================
    */
    
    
    
    private static void actualizarHistorial(Pedido pedido,List<PedidoProductos> pProductos){
        
        long idUsuario = pedido.getIdUsuario();
        List<Long> idsProductoHistorial = Modelo.buscarProductosHistorial(idUsuario);
        UsuarioProductos uProducto;
        
        for(PedidoProductos reg : pProductos){
            
            if(!idsProductoHistorial.contains(reg.getIdProducto())){
                uProducto = new UsuarioProductos();
                uProducto.setIdUsuario(idUsuario);
                uProducto.setIdProducto(reg.getIdProducto());
                Generico.guardar(uProducto);
            }
        }
    }
}
