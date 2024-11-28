package database;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
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
     * Tìm kiếm dữ liệu từ bảng trong cơ sở dữ liệu.
     *
     * @param tableName Tên bảng trong cơ sở dữ liệu
     * @param condition Điều kiện WHERE, ví dụ: "id = ?"
     * @param parameters Tham số cho điều kiện WHERE
     * @param clazz Lớp đại diện cho kiểu dữ liệu (Class của T)
     * @return Danh sách các đối tượng kiểu T
     */
    public List<T> search(String tableName, String condition, Object[] parameters, Class<T> clazz) {
        List<T> resultList = new ArrayList<>();

        String query = "SELECT * FROM " + tableName + (condition.isEmpty() ? "" : " WHERE " + condition);

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            for (int i = 0; i < parameters.length; i++) {
                stmt.setObject(i + 1, parameters[i]);
            }

            ResultSet rs = stmt.executeQuery();
            Field[] fields = clazz.getDeclaredFields();

            while (rs.next()) {
                T instance = clazz.getDeclaredConstructor().newInstance();

                for (Field field : fields) {
                    field.setAccessible(true);
                    String columnName = ListBookController.Book.getColumnName(field.getName());


                    if (field.getType() == SimpleStringProperty.class) {
                        SimpleStringProperty property = (SimpleStringProperty) field.get(instance);
                        property.set(rs.getString(columnName));
                    } else if (field.getType() == SimpleBooleanProperty.class) {
                        SimpleBooleanProperty property = (SimpleBooleanProperty) field.get(instance);
                        property.set(rs.getBoolean(columnName));
                    } else {
                        try {
                            field.set(instance, rs.getObject(columnName));
                        } catch (IllegalArgumentException e) {
                            Logger.getLogger(GenericSearch.class.getName()).log(Level.WARNING,
                                    "Wrong search field " + field.getName(), e);
                        }
                    }
                }
                resultList.add(instance);
            }
        } catch (Exception e) {
            Logger.getLogger(GenericSearch.class.getName()).log(Level.SEVERE, "Error", e);
        }

        return resultList;
    }



}