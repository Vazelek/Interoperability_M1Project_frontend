package com.example.demo;

import hr.algebra.valentinherve.productapp.model.Product;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private Label welcomeText;

    @FXML
    private TableView<Product> productTable;

    @FXML
    private TableColumn<Product, Long> idColumn;

    @FXML
    private TableColumn<Product, String> nameColumn;

    @FXML
    private TableColumn<Product, String> priceColumn;

    @FXML
    private TableColumn<Product, String> stockColumn;

    @FXML
    private TextField newProductName;
    @FXML
    private TextField newProductPrice;
    @FXML
    private TextField newProductStock;
    @FXML
    private TextField updateProductName;
    @FXML
    private TextField updateProductPrice;
    @FXML
    private TextField updateProductStock;

    private Product selectedItem = null;

    private final ObservableList<Product> products = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        stockColumn.setCellValueFactory(new PropertyValueFactory<>("stock"));

        productTable.setItems(products);

        addDeleteButtonToTable();
        addSelectButtonToTable();
    }

    private void addDeleteButtonToTable() {
        TableColumn<Product, Void> colBtn = new TableColumn<>("Action");

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Delete");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Product product = getTableView().getItems().get(getIndex());
                            deleteProduct(product.getId());
                            products.remove(product);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        productTable.getColumns().add(colBtn);
    }

    private void addSelectButtonToTable() {
        TableColumn<Product, Void> colBtn = new TableColumn<>("Select");

        Callback<TableColumn<Product, Void>, TableCell<Product, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<Product, Void> call(final TableColumn<Product, Void> param) {
                final TableCell<Product, Void> cell = new TableCell<>() {

                    private final Button btn = new Button("Select");

                    {
                        btn.setOnAction((ActionEvent event) -> {
                            Product product = getTableView().getItems().get(getIndex());
                            selectItem(product);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(btn);
                        }
                    }
                };
                return cell;
            }
        };

        colBtn.setCellFactory(cellFactory);
        productTable.getColumns().add(colBtn);
    }

    private void selectItem(Product product) {
        updateProductName.setText(product.getName());
        updateProductPrice.setText(product.getPrice().toString());
        updateProductStock.setText(String.valueOf(product.getStock()));
        welcomeText.setText("Selected Product ID: " + product.getId());
        selectedItem = product;
    }

    @FXML
    protected void onGetAllButtonClick() {
        welcomeText.setText("Fetching products...");

        RestTemplate restTemplate = new RestTemplate();
        configureRestTemplate(restTemplate);

        String restEndpointUrl = "http://localhost:8081/product";

        ResponseEntity<Product[]> productArrayResponse = restTemplate.getForEntity(restEndpointUrl, Product[].class);

        ObservableList<Product> productList = FXCollections.observableArrayList();
        Product[] productArray = productArrayResponse.getBody();
        if (productArray != null) {
            productList.addAll(Arrays.asList(productArray));
        }

        productTable.setItems(productList);
        welcomeText.setText("Products fetched successfully!");
    }

    @FXML
    private void onAddProductButtonClick() {
        String name = newProductName.getText();
        BigDecimal price = new BigDecimal(newProductPrice.getText());
        long stock = Long.parseLong(newProductStock.getText());

        Product product = new Product(name, stock, price);

        RestTemplate restTemplate = new RestTemplate();
        configureRestTemplate(restTemplate);

        String restEndpointUrl = "http://localhost:8081/product";

        try {
            ResponseEntity<Product> response = restTemplate.postForEntity(restEndpointUrl, product, Product.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                Product createdProduct = response.getBody();
                products.add(createdProduct);
                clearNewProductFields();
                onGetAllButtonClick();
            }
        } catch (HttpClientErrorException e) {
            welcomeText.setText("Error adding product: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateProductButtonClick() {
        if (selectedItem != null) {
            selectedItem.setName(updateProductName.getText());
            selectedItem.setPrice(new BigDecimal(updateProductPrice.getText()));
            selectedItem.setStock(Long.parseLong(updateProductStock.getText()));

            RestTemplate restTemplate = new RestTemplate();
            configureRestTemplate(restTemplate);

            String restEndpointUrl = "http://localhost:8081/product/" + selectedItem.getId();

            try {
                ResponseEntity<Product> response = restTemplate.exchange(restEndpointUrl, HttpMethod.PUT, new HttpEntity<>(selectedItem), Product.class);
                if (response.getStatusCode() == HttpStatus.OK) {
                    Product updatedProduct = response.getBody();
                    productTable.refresh();
                    onGetAllButtonClick();
                }
            } catch (HttpClientErrorException e) {
                welcomeText.setText("Error updating product: " + e.getMessage());
            }
        }
    }

    private void deleteProduct(Long productId) {
        RestTemplate restTemplate = new RestTemplate();
        configureRestTemplate(restTemplate);

        String restEndpointUrl = "http://localhost:8081/product/" + productId;

        try {
            restTemplate.delete(restEndpointUrl);
            onGetAllButtonClick();
        } catch (HttpClientErrorException e) {
            welcomeText.setText("Error deleting product: " + e.getMessage());
        }
    }

    private void configureRestTemplate(RestTemplate restTemplate) {
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        List<MediaType> mediaTypes = new ArrayList<>();
        mediaTypes.add(MediaType.APPLICATION_JSON);
        converter.setSupportedMediaTypes(mediaTypes);
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
    }

    private void clearNewProductFields() {
        newProductName.clear();
        newProductPrice.clear();
        newProductStock.clear();
    }
}
