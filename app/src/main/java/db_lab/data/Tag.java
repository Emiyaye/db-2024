package db_lab.data;

import java.sql.Connection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class Tag {

    public final String name;

    public Tag(final String name) {
        this.name = name == null ? "" : name;
    }

    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        } else if (other == null) {
            return false;
        } else if (other instanceof Tag) {
            final var t = (Tag) other;
            return t.name.equals(this.name);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return Printer.stringify("Tag", List.of(Printer.field("name", this.name)));
    }

    public final class DAO {

        public static Set<Tag> ofProduct(final Connection connection, final int productId) {
            // Iterating through a resultSet:
            // https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html
            try (
                    final var statement = DAOUtils.prepare(connection, Queries.TAGS_FOR_PRODUCT, productId);
                    final var resultSet = statement.executeQuery();) {
                final var tags = new HashSet<Tag>();
                while (resultSet.next()) {
                    final var tag_name = resultSet.getString("t.tag_name");
                    final var tag = new Tag(tag_name);
                    tags.add(tag);
                }
                return tags;
            } catch (Exception e) {
                throw new DAOException(e);
            }
        }
    }
}
