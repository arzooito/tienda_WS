/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dipalme.policia.bd.tienda.Administrador;
import org.dipalme.policia.bd.tienda.Categoria;
import org.dipalme.policia.bd.tienda.Marca;
import org.dipalme.policia.bd.tienda.Pedido;
import org.dipalme.policia.bd.tienda.PedidoProductos;
import org.dipalme.policia.bd.tienda.Producto;
import org.dipalme.policia.bd.tienda.Subcategoria;
import org.dipalme.policia.bd.tienda.Usuario;
import org.dipalme.policia.bd.tienda.UsuarioProductos;
import org.dipalme.policia.webbackend.servicios.Generico;
import static org.dipalme.policia.webbackend.servicios.Generico.cargar;
import static org.dipalme.policia.webbackend.servicios.Generico.getBd;

/**
 *
 * @author Almerimatik
 */
public class Modelo {
    
    public static void guardarPedido(Pedido pedido){
        
            Date date = new Date();
            pedido.setFecha(date);
            Generico.guardar(pedido);       
    }
    
    public static void guardarPedidoProductos(long idPedido, List<PedidoProductos> prods){
        
        for(PedidoProductos reg : prods){      
            reg.setIdPedido(idPedido);
            Generico.guardar(reg);
        }
    }
    
    public static List<Long> buscarProductosHistorial(long idUsuario){
               
        return cargar(getBd().buscaListado(UsuarioProductos.p.idProducto)
                .where(UsuarioProductos.p.idUsuario.eq(idUsuario))
        );         
    }
    
    public static Administrador cargarAdministrador(String nombre, String pass){
        
        return cargar(getBd().buscaUnico(Administrador.class)
               .where(Administrador.p.nombre.eq(nombre)
                       .and(Administrador.p.password.eq(pass))
               )
       );
    }
    
    public static Usuario cargarUsuario(String nombre, String pass){
        
        return cargar(getBd().buscaUnico(Usuario.class)
               .where(Usuario.p.nombre.eq(nombre)
                       .and(Usuario.p.password.eq(pass))
               )
       );
    }
    
    public static List<Pedido> buscarPedidosPendientes(){
               
        return cargar(getBd().buscaListado(Pedido.class)
                .where(Pedido.p.finalizado.isNull())
        );         
    }
    
    public static List<PedidoProductos> buscarListaPedidoProductos(long idPedido){
               
        return cargar(getBd().buscaListado(PedidoProductos.class)
                .where(PedidoProductos.p.idPedido.eq(idPedido))
        );         
    }
    
    public static Producto buscarProducto(long idProducto){
        
        return cargar(getBd().buscaUnico(Producto.class)
               .where(Producto.p.id.eq(idProducto))
       );
    }
    
    public static void finalizarPedido(long idPedido){
        
        Pedido pedido = cargar(getBd().buscaUnico(Pedido.class)
               .where(Pedido.p.id.eq(idPedido)));
        
        pedido.setFinalizado(new Date());
        Generico.guardar(pedido);
    }
    
    public static List<Marca> buscarListaMarcas(){
        
        return cargar(getBd().buscaListado(Marca.class));
    }
    
    public static List<Categoria> buscarListaCategorias(){
        
        return cargar(getBd().buscaListado(Categoria.class));
    }
    
    public static List<Subcategoria> buscarListaSubcategorias(){
        
        return cargar(getBd().buscaListado(Subcategoria.class));
    }
    
    public static List<Producto> buscarListaProductos(){
        
        return cargar(getBd().buscaListado(Producto.class));
    }
    
    public static List<Producto> buscarListaProductosEntran(Date ultimaActualizacion){
        
        return cargar(getBd().buscaListado(Producto.class)
                .where(Producto.p.actualizado.gt(ultimaActualizacion)
                .and(Producto.p.activo.eq(true)))
        );
    }
    
    public static List<Producto> buscarListaProductosSalen(Date ultimaActualizacion){
        
        return cargar(getBd().buscaListado(Producto.class)
                .where(Producto.p.actualizado.gt(ultimaActualizacion)
                .and(Producto.p.activo.eq(false)))
        );
    }
    
    public static List<Marca> buscarListaMarcasEntran(List<Long> ids){
        
        return cargar(getBd().buscaListado(Marca.class)
                .where(Marca.p.id.in(ids))
        );
    }
    
    public static List<Categoria> buscarListaCategoriasEntran(List<Long> ids){
        
        return cargar(getBd().buscaListado(Categoria.class)
                .where(Categoria.p.id.in(ids))
        );
    }
    
    public static List<Subcategoria> buscarListaSubcategoriasEntran(List<Long> ids){
        
        return cargar(getBd().buscaListado(Subcategoria.class)
                .where(Subcategoria.p.id.in(ids))
        );
    }
    
    public static List<Producto> buscarProductosEliminados(String fecha){
        
        String consulta = "select id from ProductosEliminados where fecha > \'"+fecha+"\'";
        List<Producto> productos = new ArrayList<>();
        List<BigInteger> result = cargar(consulta);
        
        for(BigInteger reg : result){
            long id = reg.longValue();
            Producto prod = new Producto();
            prod.setId(id);
            productos.add(prod);
        }
        return productos;
    }
    
}
