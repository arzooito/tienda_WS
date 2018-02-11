/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.dipalme.policia.bd.tienda.Categoria;
import org.dipalme.policia.bd.tienda.Marca;
import org.dipalme.policia.bd.tienda.Pedido;
import org.dipalme.policia.bd.tienda.PedidoProductos;
import org.dipalme.policia.bd.tienda.Producto;
import org.dipalme.policia.bd.tienda.Subcategoria;
import org.dipalme.policia.bd.tienda.Usuario;
import org.dipalme.policia.webbackend.comun.Fechas;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Almerimatik
 */
public class XML {
    
    
    public static Document getDocumento(String xml) {
	if (!org.zkoss.lang.Strings.isBlank(xml)) {
            try {
                javax.xml.parsers.DocumentBuilder docBuilder = javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();

                docBuilder.setErrorHandler(new org.xml.sax.ErrorHandler() {
                        @Override
                        public void warning(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
                            java.util.logging.Logger.getLogger(XML.class.getName()).log(java.util.logging.Level.SEVERE, "Warning", exception);
                        }

                        @Override
                        public void error(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
                            java.util.logging.Logger.getLogger(XML.class.getName()).log(java.util.logging.Level.SEVERE, "Error", exception);
                        }

                        @Override
                        public void fatalError(org.xml.sax.SAXParseException exception) throws org.xml.sax.SAXException {
                            java.util.logging.Logger.getLogger(XML.class.getName()).log(java.util.logging.Level.SEVERE, "FatalError", exception);
                        }
                });

                return docBuilder.parse(new java.io.ByteArrayInputStream(xml.getBytes("UTF-8")));
            }
            catch(Exception ex) {
                java.util.logging.Logger.getLogger(XML.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
            }
        }
	return null;
    }
    
    public static List<PedidoProductos> getProductos(Document doc) throws DOMException,NumberFormatException{
       
        List<PedidoProductos> productos = new ArrayList<>();
        
        if(doc != null){   
            int i;
            PedidoProductos prod;
            
            NodeList nodosProducto = doc.getElementsByTagName("producto");
            Element elementoProducto = null;
            String id = null;
            String unidades = null;
            
            if(nodosProducto != null && nodosProducto.getLength()>0){
                for(i=0; i<nodosProducto.getLength();i++){
                    
                    prod = new PedidoProductos();
                    elementoProducto = (Element)nodosProducto.item(i);
                    
                    id = elementoProducto.getAttribute("id");
                    unidades = elementoProducto.getAttribute("unidades");
                     
                    prod.setIdProducto(Long.parseLong(id));
                    prod.setUnidades(Integer.parseInt(unidades));
                    
                    productos.add(prod);
                }  
            }
        }         
        
        return productos;
    }
    
    public static Pedido getPedido(Document doc) throws DOMException,NumberFormatException{
               
        Element elementoPedido = doc.getDocumentElement();
        Pedido pedido = new Pedido();
        String fechaRecogida = elementoPedido.getAttribute("fechaRecogida");
        String horaRecogida = elementoPedido.getAttribute("horaRecogida");
        String idUsuario = elementoPedido.getAttribute("idUsuario");

        pedido.setFechaRecogida(Fechas.Convertir(fechaRecogida));
        pedido.setHoraRecogida(Fechas.ConvertirHora(horaRecogida));
        pedido.setIdUsuario(Long.parseLong(idUsuario));
        
        return pedido;
    }
    
    public static String construirPedidos(){
        
        String xml = "<pedidos>";
        
        List<Pedido> pedidos = Modelo.buscarPedidosPendientes();
        for(Pedido reg : pedidos){
            String pedido;
            Usuario user = reg.getUsuario();
            String id = String.format("%d",reg.getId());
            String fecha = Fechas.FormatearFechaHora(reg.getFecha());
            String fechaRecogida = Fechas.FormatearFecha(reg.getFechaRecogida());
            String horaRecogida = Fechas.FormatearHora(reg.getHoraRecogida());
            String usuario = user.getNombre();
            
            pedido = "<pedido "
                    + "id=\'"+id+"\' "
                    + "fecha=\'"+fecha+"\' "
                    + "fechaRecogida=\'"+fechaRecogida+"\' "
                    + "horaRecogida=\'"+horaRecogida+"\' "
                    + "usuario=\'"+usuario+"\' "
                    + ">";
            
            pedido += construirProductosPedido(reg.getId());
            pedido += "</pedido>";
            
            xml += pedido;
        }
        
        xml += "</pedidos>";
        return xml;
    }
    
    public static String construirProductosPedido(long idPedido){
        String xml = "";
        
        List<PedidoProductos> productos = Modelo.buscarListaPedidoProductos(idPedido);
        for(PedidoProductos reg : productos){
            String producto;
            Producto prod = reg.getProducto();
            Subcategoria subcategoria = prod.getSubcategoria();
            Categoria categoria = subcategoria.getCategoria();
            
            String nombre = prod.getNombre();
            String formato = prod.getFormato();
            String precio = String.format("%.2f", prod.getPrecio());
            String foto = prod.getFoto();
            String unds = String.format("%d", reg.getUnidades());
            String marca = prod.getMarca().getNombre();
            String cat = categoria.getNombre();
            String sub = subcategoria.getNombre();
            
            producto = "<producto "
                            + "nombre=\'"+nombre+"\' "
                            + "marca=\'"+marca+"\' "
                            + "formato=\'"+formato+"\' "
                            + "precio=\'"+precio.replace(",", ".")+"\' "
                            + "foto=\'"+foto+"\' "
                            + "cat=\'"+cat+"\' "
                            + "sub=\'"+sub+"\' "
                            + "unds=\'"+unds+"\' "
                        +"/>"; 
            xml+=producto;
        }
        
        return xml;
    } 
    
    
    ////////////////////////////////////////////////////////////////////////////
    /////////////////////////     Actualizacion  ///////////////////////////////
    ////////////////////////////////////////////////////////////////////////////
    
    public static String construirMarcas(List<Long> ids){
        
        List<Marca> marcas = Modelo.buscarListaMarcasEntran(ids);
                
        String xml="";
        
        if(!marcas.isEmpty()){
            xml = "<marcas>";
            String marc;
            for(Marca reg : marcas){

                String id = String.format("%d", reg.getId());
                String nombre = reg.getNombre();

                marc = "<marca "
                        + "id=\'"+id+"\' "
                        + "nombre=\'"+nombre+"\'"
                        +"/>";
              xml+=marc;
            }
            xml+="</marcas>";
        }
            
        
        return xml;
    }
    
    public static String construirCategorias(List<Long> ids){
        
        List<Categoria> categorias = Modelo.buscarListaCategoriasEntran(ids);
                
        String xml = "";
        
        if(!categorias.isEmpty()){
            xml = "<categorias>";
            String categoria;
            for(Categoria reg : categorias){

                String id = String.format("%d", reg.getId());
                String nombre = reg.getNombre();

                categoria = "<categoria "
                        + "id=\'"+id+"\' "
                        + "nombre=\'"+nombre+"\'"
                        +"/>";
              xml+=categoria;
            }
            xml+="</categorias>";
        }
            
        
        return xml;
    }
    
    public static String construirSubcategorias(List<Long> ids){
        
        List<Subcategoria> subcategorias = Modelo.buscarListaSubcategoriasEntran(ids);
        
        String xml = "";
        
        if(!subcategorias.isEmpty()){
            xml = "<subcategorias>";
            String subcategoria;
            for(Subcategoria reg : subcategorias){

                String id = String.format("%d", reg.getId());
                String nombre = reg.getNombre();
                String idCategoria = String.format("%d", reg.getIdCategoria());

                subcategoria = "<subcategoria "
                        + "id=\'"+id+"\' "
                        + "nombre=\'"+nombre+"\' "
                        + "idCategoria=\'"+idCategoria+"\'"
                        +"/>";
              xml+=subcategoria;
            }
            xml+="</subcategorias>";
        }
               
        return xml;
    }
    
    public static String construirProductosEntran(List<Producto> productos){
        
        String xml = "<entran>";
        String producto;
        for(Producto reg : productos){

            String id = String.format("%d", reg.getId());
            String nombre = reg.getNombre();
            String formato = reg.getFormato();
            String precio = String.format("%.2f", reg.getId()).replace(",", ".");
            String foto = reg.getFoto();
            String idMarca = String.format("%d", reg.getIdMarca());
            String idSubcategoria = String.format("%d", reg.getIdSubcategoria());

            producto = "<producto "
                    + "id=\'"+id+"\' "
                    + "nombre=\'"+nombre+"\' "
                    + "formato=\'"+formato+"\' "
                    + "precio=\'"+precio+"\' "
                    + "foto=\'"+foto+"\' "
                    + "idMarca=\'"+idMarca+"\' "
                    + "idSubcategoria=\'"+idSubcategoria+"\'"
                    +"/>";
            xml+=producto;
        }
        xml+="</entran>";    
        
        return xml;
    } 
    
    public static String construirProductosSalen(List<Producto> productos){
        
        String xml = "<salen>";
        String producto;
        for(Producto reg : productos){

            String id = String.format("%d", reg.getId());
            producto = "<producto "
                    + "id=\'"+id+"\' "
                    +"/>";
            xml+=producto;
        }
        xml+="</salen>";    
        
        return xml;
    }
    
    public static String construirProductosActualizar(List<Producto> entran, List<Producto> salen){
        
        String xml = "<productos>";
        
        if(!entran.isEmpty()){
             xml+= construirProductosEntran(entran);
        }
       
        
        if(!salen.isEmpty()){
            xml+= construirProductosSalen(salen);
        }
             
        xml+="</productos>";    
        
        return xml;
    }
    
    public static String construirActualizacion(List<Producto> entran, List<Producto> salen){
        
        String xml = "<actualizacion>";
        
        if(!entran.isEmpty()){
            
            List<Long> idsMarca = idsMarca(entran);
            List<Long> idsSubcategoria = idsSubcategoria(entran);
            List<Long> idsCategoria = idsCategoria(entran);
            
            xml+= construirMarcas(idsMarca);
            xml+= construirCategorias(idsCategoria);
            xml+= construirSubcategorias(idsSubcategoria);
        }
        
        xml+= construirProductosActualizar(entran, salen);
        
        xml+="</actualizacion>";    
        
        return xml;
    }
    
    private static List<Long> idsMarca(List<Producto> productos){
        List<Long> ids = new ArrayList<>();       
        for(Producto reg : productos){
            ids.add(reg.getIdMarca());
        }     
        return ids;
    }
    
    private static List<Long> idsSubcategoria(List<Producto> productos){
        List<Long> ids = new ArrayList<>();     
        for(Producto reg : productos){
            ids.add(reg.getIdSubcategoria());
        }
        return ids;
    }
    
    private static List<Long> idsCategoria(List<Producto> productos){
        List<Long> ids = new ArrayList<>();      
        for(Producto reg : productos){
            ids.add(reg.getSubcategoria().getIdCategoria());
        }
        return ids;
    }
}
