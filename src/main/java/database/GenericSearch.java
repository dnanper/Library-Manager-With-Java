package database;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import model.Book;
import model.Document;
import model.Paper;
import model.Thesis;
import ui.listbook.ListBookController;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GenericSearch<T> {
    private final Connection connection;

    public GenericSearch(Connection connection) {
        this.connection = connection;
    }

    /**
     * Performs a search operation on the specified database table based on the given condition and parameters.
     * It retrieves the results from the database and creates instances of the specified class (`T`) using reflection.
     *
     * @param tableName The name of the database table on which the search should be performed.
     * @param condition The WHERE condition for the SQL query (can be an empty string if no specific condition is needed).
     * @param parameters An array of objects representing the parameters to be set in the SQL queries WHERE clause.
     * @param clazz The class object representing the type of objects to be created from the database results.
     * @return A list of objects of type `T` representing the search results. If an error occurs during the search or object creation process,
     *         an empty list may be returned and the error will be logged.
     */
    public List<T> search(String tableName, String condition, Object[] parameters, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();

        String query = "SELECT * FROM " + tableName + (condition.isEmpty()? "" : " WHERE " + condition);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }

            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                T instance;
                if (Document.class.isAssignableFrom(clazz)) {
                    if (clazz == Book.class) {
                        instance = (T) createBookInstance(rs);
                    } else if (clazz == Thesis.class) {
                        instance = (T) createThesisInstance(rs);
                    } else if (clazz == Paper.class) {
                        instance = (T) createPaperInstance(rs);
                    } else {
                        instance = createDocumentInstance(rs, clazz);
                    }
                } else {
                    instance = createGenericInstance(rs, clazz);
                }
                resultList.add(instance);
            }
        } catch (Exception e) {
            Logger.getLogger(GenericSearch.class.getName()).log(Level.SEVERE, "Error", e);
        }

        return resultList;
    }

    /**
     * Creates an instance of the `Book` class based on the data retrieved from the `ResultSet`.
     * It fetches the relevant columns from the result set and uses them to initialize a new `Book` object.
     *
     * @param rs The `ResultSet` containing the database query results related to a book record.
     * @return A new `Book` object populated with the data from the result set. In case of an error while fetching data or creating the object,
     *         an exception will be thrown.
     * @throws Exception If there is an issue accessing the result set columns or creating the `Book` object.
     */
    private Book createBookInstance(ResultSet rs) throws Exception {
        String title = rs.getString(Book.getColumnName("title"));
        String author = rs.getString(Book.getColumnName("author"));
        String id = rs.getString(Book.getColumnName("id"));
        String genre = rs.getString(Book.getColumnName("genre"));
        String publisher = rs.getString(Book.getColumnName("publisher"));
        boolean isAvail = rs.getBoolean(Book.getColumnName("availability"));
        String url = rs.getString(Book.getColumnName("url"));
        String urlCoverImage = rs.getString(Book.getColumnName("urlCoverImage"));
        String description = rs.getString(Book.getColumnName("description"));

        return new Book(title, author, id, genre, publisher, isAvail, url, urlCoverImage, description);
    }

    /**
     * Creates an instance of the `Thesis` class based on the data retrieved from the `ResultSet`.
     * It fetches the relevant columns from the result set and uses them to initialize a new `Thesis` object.
     *
     * @param rs The `ResultSet` containing the database query results related to a thesis record.
     * @return A new `Thesis` object populated with the data from the result set. In case of an error while fetching data or creating the object,
     *         an exception will be thrown.
     * @throws Exception If there is an issue accessing the result set columns or creating the `Thesis` object.
     */
    private Thesis createThesisInstance(ResultSet rs) throws Exception {
        String title = rs.getString(Thesis.getColumnName("title"));
        String author = rs.getString(Thesis.getColumnName("author"));
        String id = rs.getString(Thesis.getColumnName("id"));
        String genre = rs.getString(Thesis.getColumnName("genre"));
        String university = rs.getString(Thesis.getColumnName("university"));
        String department = rs.getString(Thesis.getColumnName("department"));
        boolean isAvail = rs.getBoolean(Thesis.getColumnName("availability"));

        return new Thesis(title, author, id, genre, university, department, isAvail);
    }

    /**
     * Creates an instance of the `Paper` class based on the data retrieved from the `ResultSet`.
     * It fetches the relevant columns from the result set and uses them to initialize a new `Paper` object.
     *
     * @param rs The `ResultSet` containing the database query results related to a paper record.
     * @return A new `Paper` object populated with the data from the result set. In case of an error while fetching data or creating the object,
     *         an exception will be thrown.
     * @throws Exception If there is an issue accessing the result set columns or creating the `Paper` object.
     */
    private Paper createPaperInstance(ResultSet rs) throws Exception {
        String title = rs.getString(Paper.getColumnName("title"));
        String author = rs.getString(Paper.getColumnName("author"));
        String id = rs.getString(Paper.getColumnName("id"));
        String genre = rs.getString(Paper.getColumnName("genre"));
        String conference = rs.getString(Paper.getColumnName("conference"));
        String year = rs.getString(Paper.getColumnName("year"));
        boolean isAvail = rs.getBoolean(Paper.getColumnName("availability"));

        return new Paper(title, author, id, genre, conference, year, isAvail);
    }

    /**
     * Creates an instance of a class that implements the `Document` interface based on the data retrieved from the `ResultSet`.
     * It uses reflection to set the properties of the object based on the columns in the result set.
     *
     * @param rs The `ResultSet` containing the database query results.
     * @param clazz The class object representing the type of `Document`-implementing class to be created.
     * @return A new instance of the specified `Document`-implementing class populated with the data from the result set.
     *         In case of an error while using reflection or accessing the result set columns, an exception will be thrown.
     * @throws Exception If there is an issue creating the object or setting its properties using reflection.
     */
    private T createDocumentInstance(ResultSet rs, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = getColumnName(field.getName(), clazz);
            setPropertyFromResultSet(rs, instance, field, columnName);
        }
        return instance;
    }

    /**
     * Creates an instance of a generic class based on the data retrieved from the `ResultSet`.
     * It uses reflection to set the properties of the object based on the columns in the result set.
     *
     * @param rs The `ResultSet` containing the database query results.
     * @param clazz The class object representing the type of generic class to be created.
     * @return A new instance of the specified generic class populated with the data from the result set.
     *         In case of an error while using reflection or accessing the result set columns, an exception will be thrown.
     * @throws Exception If there is an issue creating the object or setting its properties using reflection.
     */
    private T createGenericInstance(ResultSet rs, Class<T> clazz) throws Exception {
        T instance = clazz.getDeclaredConstructor().newInstance();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            String columnName = field.getName();
            setPropertyFromResultSet(rs, instance, field, columnName);
        }
        return instance;
    }

    /**
     * Sets the value of a property in an object based on the data from the `ResultSet`.
     * It determines the type of the property (e.g., `SimpleStringProperty` or `SimpleBooleanProperty`) and sets its value accordingly.
     *
     * @param rs The `ResultSet` containing the database query results.
     * @param instance The object whose property should be set.
     * @param field The `Field` object representing the property to be set.
     * @param columnName The name of the column in the result set corresponding to the property.
     */
    private void setPropertyFromResultSet(ResultSet rs, Object instance, Field field, String columnName) {
        try {
            if (field.getType() == SimpleStringProperty.class) {
                SimpleStringProperty property = (SimpleStringProperty) field.get(instance);
                property.set(rs.getString(columnName));
            } else if (field.getType() == SimpleBooleanProperty.class) {
                SimpleBooleanProperty property = (SimpleBooleanProperty) field.get(instance);
                property.set(rs.getBoolean(columnName));
            } else {
                field.set(instance, rs.getObject(columnName));
            }
        } catch (Exception e) {
            Logger.getLogger(GenericSearch.class.getName()).log(Level.WARNING,
                    "Error setting property for field: " + field.getName(), e);
        }
    }

    /**
     * Gets the column name in the database table corresponding to a given field name for specific entity classes.
     * If the class is `Book`, `Thesis`, or `Paper`, it uses the respective class's method to get the column name.
     * Otherwise, it returns the field name itself assuming it matches the column name in the table.
     *
     * @param fieldName The name of the field in the entity class.
     * @param clazz The class object representing the entity class.
     * @return The column name in the database table corresponding to the given field name.
     */
    private String getColumnName(String fieldName, Class<?> clazz) {
        if (clazz == Book.class) {
            return Book.getColumnName(fieldName);
        } else if (clazz == Thesis.class) {
            return Thesis.getColumnName(fieldName);
        } else if (clazz == Paper.class) {
            return Paper.getColumnName(fieldName);
        }
        return fieldName;
    }
}