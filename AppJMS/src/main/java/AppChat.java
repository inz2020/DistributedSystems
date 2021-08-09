
import javafx.application.Application;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;

public class AppChat extends Application {
    private MessageProducer messageProducer;
    private Session session;
    private String codeUser;

    public static void main(String[] args) {
        Application.launch(AppChat.class);
    }

    @Override
    public void start(Stage stage) throws Exception {
        stage.setTitle("Application JMS For Chatting");
        //Creation du container principal
        //avec borderpane on subdive le container en 5 zones
        BorderPane borderPane = new BorderPane();
        //borderPane.setBackground(new Background(new BackgroundFill(Color.GRAY, CornerRadii.EMPTY, Insets.EMPTY)));
        HBox hBox = new HBox();
        hBox.setPadding(new Insets(10));
        hBox.setBackground(new Background(
                new BackgroundFill(Color.BROWN, CornerRadii.EMPTY, Insets.EMPTY)));
        //Espace entre les eleemnts
        hBox.setSpacing(10);

        Label labelCode = new Label("Code: ");

        TextField textFieldCode = new TextField();
        textFieldCode.setText("C1");


        Label labelHost = new Label("Host: ");
        TextField textFieldHost = new TextField();
        textFieldHost.setText("localhost");

        Label labelPort = new Label("Port: ");
        TextField textFieldPort = new TextField();
        textFieldPort.setText("61616");

        Button buttonConnecter = new Button("Connecter");
        hBox.getChildren().add(labelCode);
        hBox.getChildren().add(textFieldCode);
        hBox.getChildren().add(labelHost);
        hBox.getChildren().add(textFieldHost);
        hBox.getChildren().add(labelPort);
        hBox.getChildren().add(textFieldPort);
        hBox.getChildren().add(buttonConnecter);

        borderPane.setTop(hBox);

        //Creer le Vertical Box
        VBox vBox = new VBox();
        borderPane.setCenter(vBox);

        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(10);
        HBox hBox2 = new HBox();
        hBox2.setPadding(new Insets(15));
        vBox.getChildren().add(gridPane);
        vBox.getChildren().add(hBox2);

        //Creation des elements à mettre dans le gridpane

        Label labelTo = new Label("To");
        TextField textFieldTo = new TextField("C1");
        textFieldTo.setPrefWidth(150);

        Label labelMessage = new Label("Message");
        TextArea textAreaMessage = new TextArea();
        textAreaMessage.setPrefWidth(250);
        textAreaMessage.setPrefRowCount(1);

        Button buttonEnvoyer = new Button("Envoyer");
        Label labelImage = new Label("Image");

        //Recuperer la liste du fichier
        File file = new File("src/main/images");
        ObservableList<String> observableListImage = FXCollections.observableArrayList(file.list());
        ComboBox<String> comboBoxImages = new ComboBox<String>(observableListImage);
        comboBoxImages.setPrefWidth(200);
        //Selectionner la 1ere image
        comboBoxImages.getSelectionModel().select(0);

        Button buttonEnvoyerImage = new Button("Envoyer Image");

        gridPane.setPadding(new Insets(10));
        gridPane.add(labelTo, 0, 0);
        gridPane.add(labelMessage, 0, 1);
        gridPane.add(labelImage, 0, 2);
        gridPane.add(textFieldTo, 1, 0);
        gridPane.add(textAreaMessage, 1, 1);
        gridPane.add(comboBoxImages, 1, 2);
        gridPane.add(buttonEnvoyer, 2, 1);
        gridPane.add(buttonEnvoyerImage, 2, 2);

        ///Creer les elements à mettre dans hBox2
        //Creer l'observableList
        ObservableList<String> stringObservableList = FXCollections.observableArrayList();

        ListView<String> stringListView = new ListView<>(stringObservableList);
        File file2 = new File("src/main/images/" + comboBoxImages.getSelectionModel().getSelectedItem());
        Image image = new Image(file2.toURI().toString());
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(400);
        imageView.setFitHeight(300);
        hBox2.getChildren().add(stringListView);
        hBox2.getChildren().add(imageView);
        hBox2.setPadding(new Insets(10));
        hBox2.setSpacing(10);


        //Creation d'une scéne
        Scene scene = new Scene(borderPane, 800, 500);
        //Preciser a scene à utiliser
        stage.setScene(scene);
        stage.show();

        comboBoxImages.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            File file3 = new File("src/main/images/" + newValue);
            Image image2 = new Image(file3.toURI().toString());
            imageView.setImage(image2);
        });

        buttonEnvoyer.setOnAction(actionEvent -> {

            try {

                TextMessage textMessage = session.createTextMessage();
                textMessage.setText(textAreaMessage.getText());
                textMessage.setStringProperty("code", textFieldTo.getText());
                messageProducer.send(textMessage);
            } catch (JMSException e) {
                e.printStackTrace();
            }

        });

        buttonEnvoyerImage.setOnAction(actionEvent -> {
            try {
                StreamMessage streamMessage = session.createStreamMessage();
                streamMessage.setStringProperty("code", textFieldTo.getText());
                streamMessage.setStringProperty("from", codeUser);
                File file4 = new File("src/main/images/" + comboBoxImages.getSelectionModel().getSelectedItem());
                FileInputStream fileInputStream = new FileInputStream(file4);


                byte[] data = new byte[(int) file4.length()];
                //ecrire le nom de l'image et le recuperer
                fileInputStream.read(data);

                streamMessage.writeString(comboBoxImages.getSelectionModel().getSelectedItem());
                streamMessage.writeInt(data.length);
                streamMessage.writeBytes(data);
                //Envoi du fichier
                messageProducer.send(streamMessage);
            } catch (Exception e) {
                e.printStackTrace();
            }

        });

        buttonConnecter.setOnAction(actionEvent -> {
            //Recuperer les paramètres
            try {
                codeUser = textFieldCode.getText();
                String host = textFieldHost.getText();
                int port = Integer.parseInt(textFieldPort.getText());

                //Integrer JMS
                ConnectionFactory connectionFactory = new
                        ActiveMQConnectionFactory("tcp://" + host + ":" + port);
                Connection connection = connectionFactory.createConnection();
                connection.start();
                session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
                Destination destination = session.createTopic("ensaf.chat");
                MessageConsumer messageConsumer = session.createConsumer(destination, "code = '" + codeUser + "'");
                messageProducer = session.createProducer(destination);
                messageProducer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

                //Demarrer le listener
                messageConsumer.setMessageListener(message -> {

                    try {
                        if (message instanceof TextMessage) {
                            TextMessage textMessage = (TextMessage) message;
                            //System.out.println(textMessage.getText());
                            stringObservableList.add(textMessage.getText());
                            stringListView.getSelectionModel().select(textMessage.getText());
                        } else if (message instanceof StreamMessage) {
                            //Creation d'u  stream à partir d'un tableau d'octes

                            StreamMessage streamMessage = (StreamMessage) message;

                            String nomPhto = streamMessage.readString();
                            stringObservableList.add("Reception de la photo" + nomPhto);
                            int size = streamMessage.readInt();
                            byte[] data = new byte[size];
                            streamMessage.readBytes(data);
                            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                            Image image2 = new Image(byteArrayInputStream);
                            imageView.setImage(image2);
                            stringListView.getSelectionModel().select(streamMessage.readString());

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                });
                hBox.setDisable(true);

            } catch (JMSException e) {
                e.printStackTrace();
            }


        });
    }
}

