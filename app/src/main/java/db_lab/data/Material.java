package db_lab.data;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class Material {

    public final int code;
    public final String description;

    public Material(final int code, final String description) {
        this.code = code;
        this.description = description == null ? "" : description;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof Material) {
            final var m = (Material) other;
            return (m.code == this.code && m.description.equals(this.description));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.code, this.description);
    }

    @Override
    public String toString() {
        return Printer.stringify(
                "Material",
                List.of(Printer.field("code", this.code), Printer.field("description", this.description)));
    }

    public final class DAO {

        public static Map<Material, Float> forProduct(final Connection connection, final int productId) {
            // Iterating through a resultSet:
            // https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
            try (final var statement = DAOUtils.prepare(connection, Queries.PRODUCT_COMPOSITION, productId);
                    final var resultSet = statement.executeQuery();) {
                final var compositions = new HashMap<Material, Float>();
                while (resultSet.next()) {
                    final var code = resultSet.getInt("m.code");
                    final var composition = resultSet.getFloat("c.percent");
                    final var material = new Material(code, resultSet.getString("m.description"));
                    compositions.put(material, composition);
                }
                return compositions;
            } catch (final Exception e) {
                throw new DAOException(e);
            }
        }
    }
}
