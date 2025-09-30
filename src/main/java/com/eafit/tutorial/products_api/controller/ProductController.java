package com.eafit.tutorial.products_api.controller;

import com.eafit.tutorial.products_api.dto.CreateProductDTO;
import com.eafit.tutorial.products_api.dto.ProductDTO;
import com.eafit.tutorial.products_api.dto.UpdateProductDTO;
import com.eafit.tutorial.products_api.dto.PagedResponse;
import com.eafit.tutorial.products_api.dto.ApiResponse;
import com.eafit.tutorial.products_api.exception.ProductAlreadyExistsException;
import com.eafit.tutorial.products_api.exception.ProductNotFoundException;
import com.eafit.tutorial.products_api.model.Product;
import com.eafit.tutorial.products_api.service.ProductService;
import com.eafit.tutorial.products_api.util.ProductMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controlador REST para productos
 *
 * Maneja todas las operaciones CRUD y búsquedas relacionadas con productos.
 */
@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "Products", description = "API para gestión de productos")
@Validated
@CrossOrigin(origins = "*", maxAge = 3600)
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductMapper productMapper;

    /**
     * Obtiene todos los productos con paginación opcional
     */
    @Operation(
        summary = "Obtener productos",
        description = "Obtiene todos los productos activos con paginación opcional y ordenamiento"
    )
    @ApiResponses(value = {
        @SwaggerApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @SwaggerApiResponse(responseCode = "400", description = "Parámetros de consulta inválidos"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllProducts(
        @Parameter(description = "Número de página (base 0)", example = "0")
        @RequestParam(value = "page", defaultValue = "0") @Min(0) int page,

        @Parameter(description = "Tamaño de página", example = "20")
        @RequestParam(value = "size", defaultValue = "20") @Min(1) int size,

        @Parameter(description = "Campo de ordenamiento", example = "name")
        @RequestParam(value = "sort", defaultValue = "id") String sortField,

        @Parameter(description = "Dirección de ordenamiento", example = "asc")
        @RequestParam(value = "direction", defaultValue = "asc") String sortDirection,

        @Parameter(description = "Si es true, retorna lista simple sin paginación")
        @RequestParam(value = "unpaged", defaultValue = "false") boolean unpaged
    ) {
        logger.debug("GET /api/v1/products - page: {}, size: {}, sort: {}, direction: {}, unpaged: {}",
            page, size, sortField, sortDirection, unpaged);
        try {
            if (unpaged) {
                List<Product> products = productService.getAllProducts();
                List<ProductDTO> productDTOs = productMapper.toDTOList(products);
                return ResponseEntity.ok(ApiResponse.success(productDTOs, "Productos obtenidos exitosamente"));
            } else {
                Sort.Direction direction = sortDirection.equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortField));
                Page<Product> productPage = productService.getAllProducts(pageable);
                Page<ProductDTO> productDTOPage = productPage.map(productMapper::toDTO);
                PagedResponse<ProductDTO> pagedResponse = PagedResponse.of(productDTOPage);
                return ResponseEntity.ok(ApiResponse.success(pagedResponse, "Productos paginados obtenidos exitosamente"));
            }
        } catch (Exception e) {
            logger.error("Error al obtener productos", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Obtiene un producto por ID
     */
    @Operation(
        summary = "Obtener producto por ID",
        description = "Obtiene un producto específico por su identificador único"
    )
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Producto encontrado", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @SwaggerApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> getProductById(
        @Parameter(description = "ID del producto", example = "1", required = true)
        @PathVariable @Min(1) Long id) {
        logger.debug("GET /api/v1/products/{}", id);
        try {
            Optional<Product> product = productService.getProductById(id);
            return product.map(value -> ResponseEntity.ok(ApiResponse.success(productMapper.toDTO(value), "Producto encontrado exitosamente")))
                          .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                              .body(ApiResponse.error("Producto no encontrado con ID: " + id, 404)));
        } catch (Exception e) {
            logger.error("Error al obtener producto con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Crea un nuevo producto
     */
    @Operation(
        summary = "Crear producto",
        description = "Crea un nuevo producto en el sistema"
    )
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "201", description = "Producto creado exitosamente"),
        @SwaggerApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @SwaggerApiResponse(responseCode = "409", description = "El producto ya existe"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ProductDTO>> createProduct(
        @Parameter(description = "Datos del nuevo producto", required = true)
        @Valid @RequestBody CreateProductDTO createProductDTO) {
        logger.debug("POST /api/v1/products - name: {}", createProductDTO.getName());
        try {
            Product product = productMapper.toEntity(createProductDTO);
            Product savedProduct = productService.createProduct(product);
            ProductDTO productDTO = productMapper.toDTO(savedProduct);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(productDTO, "Producto creado exitosamente"));
        } catch (Exception e) {
            logger.error("Error al crear producto", e);
            if (e.getMessage() != null && e.getMessage().contains("Ya existe")) {
                return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(ApiResponse.error(e.getMessage(), 409));
            }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Actualiza un producto existente
     */
    @Operation(
        summary = "Actualizar producto",
        description = "Actualiza los datos de un producto existente por su ID"
    )
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Producto actualizado exitosamente", content = @Content(schema = @Schema(implementation = ProductDTO.class))),
        @SwaggerApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
        @SwaggerApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @SwaggerApiResponse(responseCode = "409", description = "El nombre de producto ya existe"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductDTO>> updateProduct(
        @Parameter(description = "ID del producto a actualizar", example = "1", required = true)
        @PathVariable @Min(1) Long id,
        @Parameter(description = "Datos para actualizar el producto", required = true)
        @Valid @RequestBody UpdateProductDTO updateProductDTO) {
        logger.debug("PUT /api/v1/products/{} - name: {}", id, updateProductDTO.getName());
        try {
            Product updated = productService.updateProduct(id, updateProductDTO);
            ProductDTO productDTO = productMapper.toDTO(updated);
            return ResponseEntity.ok(ApiResponse.success(productDTO, "Producto actualizado exitosamente"));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), 404));
        } catch (ProductAlreadyExistsException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(e.getMessage(), 409));
        } catch (Exception e) {
            logger.error("Error al actualizar producto con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Elimina un producto (soft delete)
     */
    @Operation(
        summary = "Eliminar producto",
        description = "Elimina lógicamente un producto marcándolo como inactivo"
    )
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Producto eliminado exitosamente"),
        @SwaggerApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(
        @Parameter(description = "ID del producto a eliminar", example = "1", required = true)
        @PathVariable @Min(1) Long id) {
        logger.debug("DELETE /api/v1/products/{}", id);
        try {
            productService.deleteProduct(id);
            return ResponseEntity.ok(ApiResponse.success(null, "Producto eliminado exitosamente"));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.error(e.getMessage(), 404));
        } catch (Exception e) {
            logger.error("Error al eliminar producto con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }

    /**
     * Busca productos por categoría
     */
    @Operation(
        summary = "Buscar productos por categoría",
        description = "Obtiene una lista de productos que pertenecen a una categoría específica"
    )
    @ApiResponses({
        @SwaggerApiResponse(responseCode = "200", description = "Búsqueda por categoría exitosa"),
        @SwaggerApiResponse(responseCode = "400", description = "Categoría inválida"),
        @SwaggerApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<ProductDTO>>> getProductsByCategory(
        @Parameter(description = "Nombre de la categoría a buscar", example = "Electrónicos", required = true)
        @PathVariable @NotBlank String category) {
        logger.debug("GET /api/v1/products/category/{}", category);
        try {
            List<Product> products = productService.getProductsByCategory(category);
            List<ProductDTO> productDTOs = productMapper.toDTOList(products);
            return ResponseEntity.ok(ApiResponse.success(productDTOs, "Búsqueda por categoría exitosa"));
        } catch (Exception e) {
            logger.error("Error al buscar productos por categoría: {}", category, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error("Error interno del servidor"));
        }
    }
}
