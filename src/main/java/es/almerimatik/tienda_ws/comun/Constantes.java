/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package es.almerimatik.tienda_ws.comun;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.dipalme.policia.webbackend.comun.Sesion;
import org.dipalme.policia.webbackend.servicios.Generico;

/**
 *
 * @author Almerimatik
 */
public class Constantes {
    
    private static String rutaBase;
    
    public static void init() throws IOException {
        
        //cargarPropiedades();
        Generico.setConfigFile(Thread.currentThread().getContextClassLoader().getResource("hibernateTienda.cfg.xml"));
    }
    
    public static void cargarPropiedades(){
        
        Properties prop = new Properties();
        
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("tiendaWS.properties");         
            prop.load(is);
            is.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Sesion.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        //Añadir aqui las propiedades a cargar.
        rutaBase = prop.getProperty("rutaBase");
        
        System.out.println("Propiedades cargadas con exito");
        prop.clear();
    }

    public static String getRutaBase() {
        return rutaBase;
    }

    public static void setRutaBase(String rutaBase) {
        Constantes.rutaBase = rutaBase;
    }
    
    
}
