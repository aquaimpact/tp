package seedu.tassist.logic;

import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.logging.Logger;

import javafx.collections.ObservableList;
import seedu.tassist.commons.core.GuiSettings;
import seedu.tassist.commons.core.LogsCenter;
import seedu.tassist.commons.exceptions.DataLoadingException;
import seedu.tassist.logic.commands.Command;
import seedu.tassist.logic.commands.CommandResult;
import seedu.tassist.logic.commands.exceptions.CommandException;
import seedu.tassist.logic.parser.AddressBookParser;
import seedu.tassist.logic.parser.exceptions.ParseException;
import seedu.tassist.model.AddressBook;
import seedu.tassist.model.Model;
import seedu.tassist.model.Operations;
import seedu.tassist.model.ReadOnlyAddressBook;
import seedu.tassist.model.person.Person;
import seedu.tassist.storage.Storage;

/**
 * The main LogicManager of the app.
 */
public class LogicManager implements Logic {
    public static final String FILE_OPS_ERROR_FORMAT =
            "Could not save data due to the following error: %s";

    public static final String FILE_OPS_PERMISSION_ERROR_FORMAT =
            "Could not save data to file %s due to insufficient permissions "
            + "to write to the file or the folder.";

    private final Logger logger = LogsCenter.getLogger(LogicManager.class);

    private final Model model;
    private final Storage storage;
    private final AddressBookParser addressBookParser;

    /**
     * Constructs a {@code LogicManager} with the given {@code Model} and {@code Storage}.
     */
    public LogicManager(Model model, Storage storage) {
        this.model = model;
        this.storage = storage;
        addressBookParser = new AddressBookParser();
    }

    @Override
    public CommandResult execute(String commandText) throws CommandException, ParseException {
        logger.info("----------------[USER COMMAND][" + commandText + "]");

        Operations.update(model);
        CommandResult commandResult;
        Command command = addressBookParser.parseCommand(commandText);
        commandResult = command.execute(model);

        try {
            storage.saveAddressBook(model.getAddressBook());
        } catch (AccessDeniedException e) {
            throw new CommandException(String.format(
                    FILE_OPS_PERMISSION_ERROR_FORMAT, e.getMessage()), e);
        } catch (IOException ioe) {
            throw new CommandException(String.format(
                    FILE_OPS_ERROR_FORMAT, ioe.getMessage()), ioe);
        }

        return commandResult;
    }

    /**
     * Loads an AddressBook from a CSV file and updates the model.
     *
     * @param filePath The path to the CSV file to be loaded.
     * @throws IOException If an error occurs while reading the CSV file.
     */
    @Override
    public void loadCsv(Path filePath) throws IOException {
        try {
            Optional<ReadOnlyAddressBook> addressBookOptional = storage
                    .readAddressBookFromCsv(filePath);
            if (addressBookOptional.isPresent()) {
                model.setAddressBook(new AddressBook(addressBookOptional.get()));
            } else {
                model.setAddressBook(new AddressBook());
            }
        } catch (DataLoadingException e) {
            throw new IOException("Failed to load CSV data from: " + filePath, e);
        }
    }

    @Override
    public void saveCsv(Path filePath) throws IOException {
        storage.saveAddressBookToCsv(model.getAddressBook(), filePath);
    }

    //@@author xuxuezhou
    /**
     * Loads an AddressBook from a Json file and updates the model.
     *
     * @param filePath The path to the Json file to be loaded.
     * @throws IOException If an error occurs while reading the CSV file.
     */
    @Override
    public void loadJson(Path filePath) throws IOException {
        try {
            Optional<ReadOnlyAddressBook> addressBookOptional = storage
                    .readAddressBookFromCsv(filePath);
            if (addressBookOptional.isPresent()) {
                model.setAddressBook(new AddressBook(addressBookOptional.get()));
            } else {
                model.setAddressBook(new AddressBook());
            }
        } catch (DataLoadingException e) {
            throw new IOException("Failed to load CSV data from: " + filePath, e);
        }
    }

    //@@author xuxuezhou
    @Override
    public void saveJson(Path filePath) throws IOException {
        storage.saveAddressBook(model.getAddressBook(), filePath);
    }


    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return model.getAddressBook();
    }

    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return model.getFilteredPersonList();
    }

    @Override
    public Path getAddressBookFilePath() {
        return model.getAddressBookFilePath();
    }

    @Override
    public GuiSettings getGuiSettings() {
        return model.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        model.setGuiSettings(guiSettings);
    }
}
