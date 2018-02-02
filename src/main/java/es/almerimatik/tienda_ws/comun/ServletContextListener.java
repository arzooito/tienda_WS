package es.almerimatik.tienda_ws.comun;
        
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContextEvent;
/**
 * Inicializa el servlet que sirve las alertas dentro de la aplicaci�n.
 * 
 * @version 1.0
 * @since 28/03/16
 * @author Pablo Arqueros
 */
public class ServletContextListener extends org.dipalme.policia.webbackend.comun.ServletContextListener{

    /**
     * Inicia el contexto.
     * 
     * @param sce ServletContextEvent Evento a iniciar de tipo ServletContext.
     */
    @Override
    public void contextInitialized(ServletContextEvent sce)  {
        try {
            Constantes.init();
        } catch (IOException ex) {
            Logger.getLogger(ServletContextListener.class.getName()).log(Level.SEVERE, null, ex);
        }
        Logger.getLogger(ServletContextListener.class.getName()).log(Level.INFO, "Contexto tienda_WS Inicializado");
        super.contextInitialized(sce);     
    }

    /**
     * Destruye o finaliza el contexto.
     * @param arg0 Evento a finalizar de tipo ServletContext.
     */
    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
        super.contextDestroyed(arg0);
        Logger.getLogger(ServletContextListener.class.getName()).log(Level.INFO, "Contexto tienda_WS Finalizado");
    }
}