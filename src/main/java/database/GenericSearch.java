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

    private String getColumnName(String fieldName, Class<?> clazz) {
        if (clazz == Book.class) {
            return Book.getColumnName(fieldName);
        } else if (clazz == Thesis.class) {
            return Thesis.getColumnName(fieldName);
        } else if (clazz == Paper.class) {
            return Paper.getColumnName(fieldName);
        }
        // For other Document subclasses (if added in the future), you might need to implement a specific mapping
        // For now, just return the fieldName as a fallback, but you should adjust this based on your actual needs
        return fieldName;
    }
}