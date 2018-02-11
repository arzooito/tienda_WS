/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.util.Date;
import java.util.List;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import org.dipalme.policia.bd.tienda.Pedido;
import org.dipalme.policia.bd.tienda.PedidoProductos;
import org.dipalme.policia.bd.tienda.Producto;
import org.dipalme.policia.bd.tienda.Usuario;
import org.dipalme.policia.bd.tienda.UsuarioProductos;
import org.dipalme.policia.webbackend.comun.Fechas;
import org.dipalme.policia.webbackend.servicios.Generico;
import org.w3c.dom.Document;


/**
 *
 * @author Almerimatik
 */
@WebService(serviceName = "ServicioAppTienda")
public class ServicioAppTienda {
    
/*
    ============================================================================
    ============================================================================
                                LLAMADAS AL WS
    ============================================================================
    ============================================================================
*/
    
    @WebMethod(operationName = "login")
    public long login(@WebParam(name = "nombre") String usuario, @WebParam(name = "password")String password) {
       
        Usuario user = Modelo.cargarUsuario(usuario, password);
        if(user != null){
            return user.getId();
        }
        else{
            return -1l;
        }
    }
    
    
    @WebMethod(operationName = "registrarUsuario")
    public long registrarUsuario(
            @WebParam(name = "nombre") String nombre, 
            @WebParam(name = "password")String password,
            @WebParam(name = "mail") String mail,
            @WebParam(name = "telefono") String telefono) {
        
        if(!Tools.isUser(nombre, password)){
      
            Usuario user = new Usuario();
            user.setNombre(nombre);
            user.setPassword(password);
            user.setEmail(mail);
            user.setTelefono(telefono);
            
            Generico.guardar(user);

            return user.getId();  
        }
        
        return -1l;
    }
    
    
    @WebMethod(operationName = "guardarPedido")
    public boolean guardarPedido(@WebParam(name = "pedido") String xmlPedido) {
       
       boolean guardado = false;
       Document doc = XML.getDocumento(xmlPedido);
       
       System.out.println(xmlPedido);
       
       List<PedidoProductos> pProductos = XML.getProductos(doc);
       Pedido pedido = XML.getPedido(doc);
       float total = Tools.calcularTotal(pProductos);
       pedido.setPrecioTotal(total);
       Generico.beginTransaction();
       try{
            Modelo.guardarPedido(pedido);
            Modelo.guardarPedidoProductos(pedido.getId(), pProductos);
            actualizarHistorial(pedido,pProductos);
            Generico.commit();
            guardado = true;
       }catch(Exception e){
            Generico.rollback();
       }
       return guardado;
    }
    
    
    @WebMethod(operationName = "actualizar")
    public String actualizar(@WebParam(name = "fecha") String fecha) {
        
        Date ultimaActualizacion = Fechas.Convertir(fecha);
        List<Producto> entran = Modelo.buscarListaProductosEntran(ultimaActualizacion);
        List<Producto> salen = Modelo.buscarListaProductosSalen(ultimaActualizacion);
        List<Producto> eliminados = Modelo.buscarProductosEliminados(fecha);
        salen.addAll(eliminados);
        String result = XML.construirActualizacion(entran, salen);
        return result;
        
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
