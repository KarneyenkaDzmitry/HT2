package app;

import util.DBWorker;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class Phonebook {

    // Хранилище записей о людях.
    private HashMap<String, Person> persons = new HashMap<String, Person>();

    // Объект для работы с БД.
    private DBWorker db = DBWorker.getInstance();

    // Указатель на экземпляр класса.
    private static Phonebook instance = null;

    // Метод для получения экземпляра класса (реализован Singleton).
    public static Phonebook getInstance() throws ClassNotFoundException, SQLException {
        if (instance == null) {
            instance = new Phonebook();
        }
        return instance;
    }

    // При создании экземпляра класса из БД извлекаются все записи.
    protected Phonebook() throws ClassNotFoundException, SQLException {
        ResultSet db_data = this.db.getDBData("SELECT * FROM `person` ORDER BY `surname` ASC");
        while (db_data.next()) {
            this.persons.put(db_data.getString("id"), new Person(db_data.getString("id"), db_data.getString("name"), db_data.getString("surname"), db_data.getString("middlename")));
        }
    }

    // Добавление записи о человеке.
    public boolean addPerson(Person person) {
        ResultSet db_result;
        String query;

        // У человека может не быть отчества.
        if (!person.getSurname().equals("")) {
            query = "INSERT INTO `person` (`name`, `surname`, `middlename`) VALUES ('" + person.getName() + "', '" + person.getSurname() + "', '" + person.getMiddlename() + "')";
        } else {
            query = "INSERT INTO `person` (`name`, `surname`) VALUES ('" + person.getName() + "', '" + person.getSurname() + "')";
        }

        Integer affected_rows = this.db.changeDBData(query);

        // Если добавление прошло успешно...
        if (affected_rows > 0) {
            person.setId(this.db.getLastInsertId().toString());

            // Добавляем запись о человеке в общий список.
            this.persons.put(person.getId(), person);
            return true;
        } else {
            return false;
        }
    }

    // Обновление записи о человеке.
    public boolean updatePerson(String id, Person person) {
        Integer id_filtered = Integer.parseInt(person.getId());
        String query = "";

        // У человека может не быть отчества.
        if (!person.getSurname().equals("")) {
            query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname() + "', `middlename` = '" + person.getMiddlename() + "' WHERE `id` = " + id_filtered;
        } else {
            query = "UPDATE `person` SET `name` = '" + person.getName() + "', `surname` = '" + person.getSurname() + "' WHERE `id` = " + id_filtered;
        }
        Integer affected_rows = this.db.changeDBData(query);
        // Если обновление прошло успешно...
        if (affected_rows > 0) {
            // Обновляем запись о человеке в общем списке.
            this.persons.put(person.getId(), person);
            return true;
        } else {
            return false;
        }
    }

    public boolean addNumber(Person person, String number) {
        Integer id_filtered = Integer.parseInt(person.getId());
        String query = "";

        // У человека может не быть отчества.
        query = "INSERT INTO `phone` (`owner`, `number`) VALUES (" + id_filtered + ", '" + number + "')";

        Integer affected_rows = this.db.changeDBData(query);

        // Если добавление прошло успешно...
        if (affected_rows > 0) {
            String phone_id = this.db.getLastInsertId().toString();

            // Обновляем запись о человеке в общем списке.
            person.getPhones().put(phone_id, number);
            this.persons.put(person.getId(), person);
            return true;
        } else {
            return false;
        }
    }

    // Обновление записи о человеке.
    public boolean updateNumber(String id, String phone_id, String number) {
        if ((phone_id != null) && (!phone_id.equals("null"))) {
            int filtered_id = Integer.parseInt(phone_id);
            Integer affected_rows = this.db.changeDBData("UPDATE `phone` SET `number` = '" + number + "' WHERE `id`=" + filtered_id);

            if (affected_rows > 0) {
                // Обновляем запись о телефоне из общего списка.
                if ((id != null) && (!id.equals("null"))) {
                    this.persons.get(id).getPhones().put(phone_id, number);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    // Удаление записи о человеке.
    public boolean deletePerson(String id) {
        if ((id != null) && (!id.equals("null"))) {
            int filtered_id = Integer.parseInt(id);

            Integer affected_rows = this.db.changeDBData("DELETE FROM `person` WHERE `id`=" + filtered_id);

            // Если удаление прошло успешно...
            if (affected_rows > 0) {
                // Удаляем запись о человеке из общего списка.
                this.persons.remove(id);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean deleteNumber(String id, String phone_id) {
        if ((phone_id != null) && (!phone_id.equals("null"))) {
            int filtered_id = Integer.parseInt(phone_id);
            Integer affected_rows = this.db.changeDBData("DELETE FROM `phone` WHERE `id`=" + filtered_id);
            if (affected_rows > 0) {
                // Удаляем запись о человеке из общего списка.
                if ((id != null) && (!id.equals("null"))) {
                    this.persons.get(id).getPhones().remove(phone_id);
                }
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    // +++++++++++++++++++++++++++++++++++++++++
    // Геттеры и сеттеры
    public HashMap<String, Person> getContents() {
        return persons;
    }

    public Person getPerson(String id) {
        return this.persons.get(id);
    }
    // Геттеры и сеттеры
    // -----------------------------------------

}
