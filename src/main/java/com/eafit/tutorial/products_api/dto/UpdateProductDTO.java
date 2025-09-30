package com.eafit.tutorial.products_api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * DTO para actualización de productos
 */
@Schema(description = "Datos permitidos para actualizar un producto")
public class UpdateProductDTO {

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Size(min = 2, max = 100, message = "El nombre debe tener entre 2 y 100 caracteres")
    @Schema(description = "Nombre del producto", example = "Smartphone Pro")
    private String name;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Schema(description = "Descripción del producto", example = "Smartphone de última generación con cámara de 108MP")
    private String description;

    @NotNull(message = "El precio es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "Formato de precio inválido")
    @Schema(description = "Precio del producto", example = "999.99")
    private BigDecimal price;

    @NotBlank(message = "La categoría es obligatoria")
    @Pattern(regexp = "^[A-Za-z\\s]+$", message = "La categoría solo puede contener letras y espacios")
    @Schema(description = "Categoría del producto", example = "Electrónicos")
    private String category;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Schema(description = "Cantidad en stock", example = "50")
    private Integer stock;

    // Getters y Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}

