package db_lab.data;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class Product {

    public final int code;
    public final String name;
    public final String description;
    public final Map<Material, Float> composition;

    public Product(final int code, final String name, final String description, final Map<Material, Float> composition) {
        this.code = code;
        this.name = name;
        this.description = description == null ? "" : description;
        this.composition = composition == null ? Map.of() : Collections.unmodifiableMap(new HashMap<>(composition));
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof Product) {
            final var p = (Product) other;
            return (
                p.code == this.code &&
                p.name.equals(this.name) &&
                p.description.equals(this.description) &&
                p.composition.equals(this.composition)
            );
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.name, this.description, this.composition);
    }

    @Override
    public String toString() {
        return Printer.stringify(
            "Product",
            List.of(
                Printer.field("code", this.code),
                Printer.field("name", this.name),
                Printer.field("description", this.description),
                Printer.field("composition", this.composition)
            )
        );
    }

    public final class DAO {

        public static Optional<Product> find(final Connection connection, final int productId) {
            Optional<Product> temp = Optional.empty();
            try (final var statement = DAOUtils.prepare(connection, Queries.FIND_PRODUCT, productId);
                    final var resultSet = statement.executeQuery();) {
                while (resultSet.next()) {
                    final var code = resultSet.getInt("p.code");
                    final var name = resultSet.getString("p.name");
                    final var description = resultSet.getString("p.description");
                    final var composition = Material.DAO.forProduct(connection, productId);
                    temp = Optional.of(new Product(code, name, description, composition));  
                }
                return temp;
            } catch (final Exception e) {
                throw new DAOException(e);
            }
        }
    }
}
