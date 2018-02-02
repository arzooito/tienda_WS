/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws;

import java.util.ArrayList;
import java.util.List;
import org.dipalme.policia.bd.tienda.Categoria;
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
                            + "marca=\'"+marca+"\'"
                            + "formato=\'"+formato+"\' "
                            + "precio=\'"+precio+"\' "
                            + "foto=\'"+foto+"\' "
                            + "cat=\'"+cat+"\' "
                            + "sub=\'"+sub+"\' "
                            + "unds|'"+unds+"\' "
                        +"/>"; 
            xml+=producto;
        }
        
        return xml;
    }           
}
