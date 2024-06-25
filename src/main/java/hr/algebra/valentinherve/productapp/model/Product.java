package hr.algebra.valentinherve.productapp.model;

import java.math.BigDecimal;
import java.util.Objects;

public class Product {

    private Long id;
    private String name;
    private long stock;
    private BigDecimal price;

    public Product() {
    }

    public Product(String name, long stock, BigDecimal price) {
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public Product(Long id, String name, long stock, BigDecimal price) {
        this.id = id;
        this.name = name;
        this.stock = stock;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStock() {
        return stock;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setStock(long stock) {
        this.stock = stock;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return stock == product.stock && Objects.equals(id, product.id) && Objects.equals(name, product.name)&& Objects.equals(price, product.price);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, stock, price);
    }

    @Override
    public String toString() {
        return name + " - " + stock;
    }
}
