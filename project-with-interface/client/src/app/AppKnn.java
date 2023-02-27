package app;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

/**
 * classe che lancia l'interfaccia definita nel file appKnn.xml.
 * @author Kristi Dashaj.
 * @author Giuseppe Grisolia.
 */
public class AppKnn extends Application{

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("appKnn.fxml"));
		VBox root = (VBox) loader.load();
		AppKnnController controller = (AppKnnController) loader.getController();
        
		Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        
        //mostra la scena
        primaryStage.show();
    }

    /**
     * metodo main.
     * @param args
     */
    public static void main(String[] args) {
        launch(args);
    } 
}