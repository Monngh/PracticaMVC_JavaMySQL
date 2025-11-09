
package mvc_ayd;

import Controlador.ControladorUsuario;
import Modelo.MDB;
import Vista.usuarios;

/**
 *
 * @author gaelo
 */
public class MVC_AYD {

    public static void main(String[] args) {
        
        usuarios objV=new usuarios();
        MDB objM=new MDB();
        objV.setVisible(true);
        
        ControladorUsuario objControlador=new ControladorUsuario(objV, objM);
        
    }
    
}
