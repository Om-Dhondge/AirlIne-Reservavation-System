package com.application;

import com.flight.*;
import com.manager.*;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AirlineReservationSystemFX extends Application {
    
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    private static Date currentSystemDate = null;
    private static boolean isDateSet = false;

    private List<Flight> flights = new ArrayList<>();
    private Manager manager = new Manager("John Doe", "manager123");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Airline Reservation System");
        showMainMenu(primaryStage);
    }

    private void showMainMenu(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Are you a Manager or a Customer?");
        if (isDateSet) {
            Label dateLabel = new Label("Current System Date: " + dateFormat.format(currentSystemDate));
            layout.getChildren().add(dateLabel);
        }



        Button managerButton = new Button("Manager");
        Button customerButton = new Button("Customer");
        Button exitButton = new Button("Exit");

        managerButton.setOnAction(e -> showManagerLogin(stage));
        customerButton.setOnAction(e -> showCustomerMenu(stage));
        exitButton.setOnAction(e -> stage.close());

        layout.getChildren().addAll(label, managerButton, customerButton, exitButton);
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.show();
    }



    private void showManagerLogin(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Enter Manager Password:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("Login");
        Button backButton = new Button("Back");

        loginButton.setOnAction(e -> {
            if (manager.validatePassword(passwordField.getText())) {
                if (!isDateSet) {
                    showDateSetupDialog(stage);
                } else {
                    showManagerMenu(stage);
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Invalid password. Access denied.");
            }
        });

        backButton.setOnAction(e -> showMainMenu(stage));

        layout.getChildren().addAll(label, passwordField, loginButton, backButton);
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
    }

    private void showDateSetupDialog(Stage stage) {
        Stage dateStage = new Stage();
        dateStage.setTitle("Set System Date");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Enter Current System Date (dd/MM/yyyy):");
        TextField dateField = new TextField();
        Button setButton = new Button("Set Date");

        setButton.setOnAction(e -> {
            try {
                Date date = dateFormat.parse(dateField.getText());
                currentSystemDate = date;
                isDateSet = true;
                dateStage.close();
                showManagerMenu(stage);
            } catch (ParseException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid date format. Please use dd/MM/yyyy");
            }
        });

        layout.getChildren().addAll(label, dateField, setButton);
        Scene scene = new Scene(layout, 300, 150);
        dateStage.setScene(scene);
        dateStage.show();
    }

    private void showManagerMenu(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Manager Menu:");
        Button addFlightButton = new Button("Add Flight");
        Button reportButton = new Button("Generate Flight Report");
        Button statisticsButton = new Button("View Statistics"); // New button for statistics menu
        Button logoutButton = new Button("Log Out");

        addFlightButton.setOnAction(e -> showAddFlightForm(stage));
        reportButton.setOnAction(e -> generateReport());
        statisticsButton.setOnAction(e -> showStatisticsMenu(stage)); // Show statistics menu
        logoutButton.setOnAction(e -> showMainMenu(stage));

        layout.getChildren().addAll(label, addFlightButton, reportButton, statisticsButton, logoutButton);
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
    }

    private void showAddFlightForm(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField flightIdField = new TextField();
        TextField originField = new TextField();
        TextField destinationField = new TextField();
        ComboBox<Boolean> internationalBox = new ComboBox<>();
        internationalBox.getItems().addAll(true, false);
        TextField seatsField = new TextField();
        TextField fareField = new TextField();
        TextField dateField = new TextField();

        Label currentDateLabel = new Label("Current System Date: " + dateFormat.format(currentSystemDate));
        Button addButton = new Button("Add Flight");
        Button backButton = new Button("Back");

        layout.getChildren().addAll(
                new Label("Flight ID:"), flightIdField,
                new Label("Origin:"), originField,
                new Label("Destination:"), destinationField,
                new Label("Is International:"), internationalBox,
                new Label("Total Seats:"), seatsField,
                new Label("Base Fare:"), fareField,
                new Label("Flight Date (dd/MM/yyyy):"), dateField,
                addButton, backButton
        );

        addButton.setOnAction(e -> {
            try {
                Date flightDate = dateFormat.parse(dateField.getText());
                if (!isValidFlightDate(flightDate)) {
                    showAlert(Alert.AlertType.ERROR, "Flight date cannot be before current system date.");
                    return;
                }

                manager.addFlight(flights, flightIdField.getText(), originField.getText(),
                        destinationField.getText(), internationalBox.getValue(),
                        Integer.parseInt(seatsField.getText()), Double.parseDouble(fareField.getText()), dateField.getText());
                showAlert(Alert.AlertType.INFORMATION, "Flight added successfully!");
            } catch (NumberFormatException | ParseException ex) {
                showAlert(Alert.AlertType.ERROR, "Invalid input for seats, fare, or date.");
            }
        });

        backButton.setOnAction(e -> showManagerMenu(stage));

        Scene scene = new Scene(layout, 400, 600);
        stage.setScene(scene);
    }

    private void showCustomerMenu(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Customer Menu:");
        Label dateLabel = new Label("Current System Date: " + dateFormat.format(currentSystemDate));
        Button searchFlightsButton = new Button("Search Flights");
        Button bookFlightButton = new Button("Book Flight");
        Button logoutButton = new Button("Log Out");

        searchFlightsButton.setOnAction(e -> showSearchFlightsForm(stage));
        bookFlightButton.setOnAction(e -> showBookFlightForm(stage));
        logoutButton.setOnAction(e -> showMainMenu(stage));

        layout.getChildren().addAll(label,dateLabel, searchFlightsButton, bookFlightButton, logoutButton);
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
    }

    public static Date getCurrentSystemDate() {
        if (!isDateSet) {
            throw new IllegalStateException("System date has not been set by manager.");
        }
        return currentSystemDate;
    }

    private boolean isValidFlightDate(Date flightDate) {
        return !flightDate.before(currentSystemDate);
    }



    private void generateReport() {
        // Create a new stage for the report
        Stage reportStage = new Stage();
        reportStage.setTitle("Flight Report");

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        StringBuilder report = new StringBuilder();
        report.append(String.format("%-12s %-15s %-15s %-15s %-12s %-12s %-10s %-12s\n",
                "Flight ID", "Origin", "Destination", "International",
                "Seats Booked", "Base Fare", "Seats Left", "Flight Date"));
        report.append("---------------------------------------------------------------------------------------------------------\n");

        for (Flight flight : flights) {
            int seatsBooked = flight.getTotalSeats() - flight.getAvailableSeats();
            report.append(String.format("%-12s %-15s %-15s %-15s %-12d $%-11.2f %-10d %-12s\n",
                    flight.getFlightId(), flight.getOrigin(), flight.getDestination(),
                    flight.isInternational(), seatsBooked, flight.getBaseFare(),
                    flight.getAvailableSeats(), flight.getFlightDate()));
        }

        // Create TextArea for report display
        TextArea reportArea = new TextArea(report.toString());
        reportArea.setEditable(false);
        reportArea.setPrefRowCount(20);
        reportArea.setPrefColumnCount(80);

        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> reportStage.close());

        layout.getChildren().addAll(reportArea, closeButton);

        Scene scene = new Scene(layout, 800, 500);
        reportStage.setScene(scene);
        reportStage.show();
    }

       private void showSearchFlightsForm(Stage stage) {
       VBox layout = new VBox(10);
       layout.setPadding(new Insets(20));

       Label currentDateLabel = new Label("Current System Date: " + dateFormat.format(currentSystemDate));
       TextField originField = new TextField();
       TextField destinationField = new TextField();
       Button searchButton = new Button("Search");
       Button backButton = new Button("Back");

       // Use TableView instead of TextArea for better formatting
       TableView<Flight> resultsTable = new TableView<>();

       // Define columns
       TableColumn<Flight, String> flightIdCol = new TableColumn<>("Flight ID");
       flightIdCol.setCellValueFactory(new PropertyValueFactory<>("flightId"));

       TableColumn<Flight, String> dateCol = new TableColumn<>("Date");
       dateCol.setCellValueFactory(cellData ->
               new SimpleStringProperty(cellData.getValue().getFlightDate()));

       TableColumn<Flight, Integer> seatsCol = new TableColumn<>("Available Seats");
       seatsCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));

       TableColumn<Flight, String> baseFareCol = new TableColumn<>("Base Fare");
       baseFareCol.setCellValueFactory(cellData ->
               new SimpleStringProperty(String.format("$%.2f", cellData.getValue().getBaseFare())));

       TableColumn<Flight, String> dynamicFareCol = new TableColumn<>("Current Fare");
       dynamicFareCol.setCellValueFactory(cellData ->
               new SimpleStringProperty(String.format("$%.2f", cellData.getValue().calculateDynamicFare())));

       // Add a column for price breakdown
       TableColumn<Flight, String> detailsCol = new TableColumn<>("Price Details");
       detailsCol.setCellValueFactory(cellData -> new SimpleStringProperty("View Details"));
       detailsCol.setCellFactory(tc -> new TableCell<Flight, String>() {
           final Button btn = new Button("View Details");

           @Override
           protected void updateItem(String item, boolean empty) {
               super.updateItem(item, empty);
               if (empty) {
                   setGraphic(null);
               } else {
                   setGraphic(btn);
                   btn.setOnAction(event -> {
                       Flight flight = getTableView().getItems().get(getIndex());
                       Alert alert = new Alert(Alert.AlertType.INFORMATION);
                       alert.setTitle("Price Breakdown");
                       alert.setHeaderText("Flight " + flight.getFlightId() + " Price Details");
                       alert.setContentText(flight.getPriceBreakdown());
                       alert.showAndWait();
                   });
               }
           }
       });

       resultsTable.getColumns().addAll(flightIdCol, dateCol, seatsCol,
               baseFareCol, dynamicFareCol, detailsCol);

       searchButton.setOnAction(e -> {
           resultsTable.getItems().clear();
           String origin = originField.getText();
           String destination = destinationField.getText();

           List<Flight> matchingFlights = flights.stream()
                   .filter(flight -> flight.getOrigin().equalsIgnoreCase(origin) &&
                           flight.getDestination().equalsIgnoreCase(destination) &&
                           !flight.isFullCapacity())
                   .collect(Collectors.toList());

           if (matchingFlights.isEmpty()) {
               showAlert(Alert.AlertType.INFORMATION, "No flights available for this route.");
           } else {
               resultsTable.getItems().addAll(matchingFlights);
           }
       });

       backButton.setOnAction(e -> showCustomerMenu(stage));

       layout.getChildren().addAll(
               currentDateLabel,
               new Label("Origin:"), originField,
               new Label("Destination:"), destinationField,
               searchButton,
               resultsTable,
               backButton
       );

       Scene scene = new Scene(layout, 800, 600);
       stage.setScene(scene);
   }
    private void showBookFlightForm(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        TextField flightIdField = new TextField();
        TextField seatsField = new TextField();
        Button bookButton = new Button("Book");
        Button backButton = new Button("Back");

        bookButton.setOnAction(e -> {
            String flightId = flightIdField.getText();
            int seatsToBook;
            try {
                seatsToBook = Integer.parseInt(seatsField.getText());
                Flight selectedFlight = flights.stream()
                        .filter(flight -> flight.getFlightId().equals(flightId))
                        .findFirst().orElse(null);

                if (selectedFlight == null) {
                    showAlert(Alert.AlertType.ERROR, "Flight ID not found.");
                    return;
                }

                if (selectedFlight.isFullCapacity() || seatsToBook > selectedFlight.getAvailableSeats()) {
                    showAlert(Alert.AlertType.ERROR, "Not enough seats available.");
                    return;
                }

                selectedFlight.bookSeats(seatsToBook);

                // Calculate total fare and display the invoice
                double totalFare = seatsToBook * selectedFlight.calculateDynamicFare(); // Use dynamic fare
                String invoice = String.format(
                        "Booking successful!\n\nInvoice:\nFlight ID: %s\nOrigin: %s\nDestination: %s\n" +
                                "Seats Booked: %d\nTotal Fare: $%.2f",
                        selectedFlight.getFlightId(),
                        selectedFlight.getOrigin(),
                        selectedFlight.getDestination(),
                        seatsToBook,
                        totalFare
                );

                showAlert(Alert.AlertType.INFORMATION, invoice);

            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Please enter a valid number of seats.");
            }
        });

        backButton.setOnAction(e -> showCustomerMenu(stage));

        layout.getChildren().addAll(
                new Label("Flight ID:"), flightIdField,
                new Label("Seats to Book:"), seatsField,
                bookButton, backButton
        );

        Scene scene = new Scene(layout, 300, 250);
        stage.setScene(scene);
    }
    private void showAlert(Alert.AlertType alertType, String message) {
        Alert alert = new Alert(alertType);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showStatisticsMenu(Stage stage) {
        VBox layout = new VBox(10);
        layout.setPadding(new Insets(20));

        Label label = new Label("Flight Statistics:");

        // Get statistics from the FlightStatistics class
        Map<String, Long> flightsByOrigin = FlightStatistics.countFlightsByOrigin(flights);
        double averageFare = FlightStatistics.calculateAverageFare(flights);
        int totalAvailableSeats = FlightStatistics.countAvailableSeats(flights);
        long totalFlightsBooked = FlightStatistics.countBookedFlights(flights);  // Get total flights booked

        // Display each statistic
        StringBuilder statsText = new StringBuilder("Flights by Origin:\n");
        flightsByOrigin.forEach((origin, count) ->
                statsText.append(String.format("%s: %d flights\n", origin, count))
        );

        statsText.append(String.format("\nAverage Fare: $%.2f\n", averageFare));
        statsText.append(String.format("Total Available Seats: %d\n", totalAvailableSeats));
        statsText.append(String.format("Total Flights Booked: %d\n", totalFlightsBooked));  // Display flights booked

        TextArea statsArea = new TextArea(statsText.toString());
        statsArea.setEditable(false);
        statsArea.setPrefRowCount(15);  // Show more rows
        statsArea.setPrefColumnCount(40);  // Show more columns

        Button backButton = new Button("Back");
        backButton.setOnAction(e -> showManagerMenu(stage));

        layout.getChildren().addAll(label, statsArea, backButton);

        // Increased window size for better visibility
        Scene scene = new Scene(layout, 600, 500);
        stage.setScene(scene);
    }
}




