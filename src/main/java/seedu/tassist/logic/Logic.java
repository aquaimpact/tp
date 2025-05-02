package seedu.tassist.logic;

import java.io.IOException;
import java.nio.file.Path;

import javafx.collections.ObservableList;
import seedu.tassist.commons.core.GuiSettings;
import seedu.tassist.logic.commands.CommandResult;
import seedu.tassist.logic.commands.exceptions.CommandException;
import seedu.tassist.logic.parser.exceptions.ParseException;
import seedu.tassist.model.ReadOnlyAddressBook;
import seedu.tassist.model.person.Person;

/**
 * API of the Logic component.
 */
public interface Logic {
    /**
     * Executes the command and returns the result.
     * @param commandText The command as entered by the user.
     * @return the result of the command execution.
     * @throws CommandException If an error occurs during command execution.
     * @throws ParseException If an error occurs during parsing.
     */
    CommandResult execute(String commandText) throws CommandException, ParseException;

    /**
     * Returns the AddressBook.
     *
     * @see seedu.tassist.model.Model#getAddressBook()
     */
    ReadOnlyAddressBook getAddressBook();

    /** Returns an unmodifiable view of the filtered list of persons. */
    ObservableList<Person> getFilteredPersonList();

    /**
     * Returns the user prefs' address book file path.
     */
    Path getAddressBookFilePath();

    /**
     * Returns the user prefs' GUI settings.
     */
    GuiSettings getGuiSettings();

    /**
     * Set the user prefs' GUI settings.
     */
    void setGuiSettings(GuiSettings guiSettings);

    /**
     * Loads AddressBook data from a CSV file located at {@code filePath}.
     *
     * @param filePath The path to the CSV file to be loaded.
     * @throws IOException If an I/O error occurs, such as when the file does not exist.
     */
    void loadCsv(Path filePath) throws IOException;

    /**
     * Saves current AddressBook data to a CSV file at {@code filePath}.
     *
     * @param filePath The path where the CSV file should be saved.
     * @throws IOException If an I/O error occurs, such as when writing to the file fails.
     */
    void saveCsv(Path filePath) throws IOException;

    //@@author xuxuezhou
    /**
     * Loads AddressBook data from a JSON file located at {@code filePath}.
     *
     * @param filePath The path to the JSON file to be loaded.
     * @throws IOException If an I/O error occurs, such as when the file does not exist.
     */
    void loadJson(Path filePath) throws IOException;

    //@@author xuxuezhou
    /**
     * Saves current AddressBook data to a JSON file at {@code filePath}.
     *
     * @param filePath The path where the JSON file should be saved.
     * @throws IOException If an I/O error occurs, such as when writing to the file fails.
     */
    void saveJson(Path filePath) throws IOException;
}
