package pizzas;


import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class MyImage {

    Image image;
    String imPath;
    StackPane sp;



    public MyImage(String path, StackPane s) {
        try{
            //this.image = new Image(MainLogo.class.getResource(path).toString());
            image = new Image(path);
            this.imPath=path;
            if (s==null) {
                this.sp=new StackPane();
                sp.setMinSize(200,200);
                ImageView img = new ImageView();
                img.setImage(image);
                sp.getChildren().add(img);
            }
            else sp=s;
        }
        catch(Exception e)
        {
            System.out.println("Error creating image with path "+path);
            System.out.println(e);
        }
    }

    public StackPane getSp() {
        return sp;
    }


    public String getImPathh(){
        return imPath;
    }

    /**
     * Superposition d'une autre image sur l'image courante
     *
     * @param imageAAjouter
     *            designe le chemin ou trouver l'image a ajouter au dessus de
     *            l'image courante stockee dans this
     */
    public void superposer(String imageAAjouter) {
        // on charge l'image
        image = new Image(imageAAjouter);
        ImageView img = new ImageView();
        img.setImage(image);
        sp.getChildren().add(img);
    }

}
