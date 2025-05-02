---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# TAssist Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project was adapted from [AB3](https://se-education.org/addressbook-level3/) (source code provided [here](https://github.com/nus-cs2103-AY2425S2/tp)).

This project adapts the matriculation number checksum from [here](https://nusmodifications.github.io/nus-matriculation-number-calculator/) (source code provided [here](https://github.com/nusmodifications/nus-matriculation-number-calculator/blob/gh-pages/matric.js)).

Xuezhou utilized AI tools (e.g., ChatGPT-4o, Copilot) to assist with code autocompletion and method refinement for more than half of the implementation.

Online images are used for the icons of the attendance tags:
* [Check Icon](https://www.iconsdb.com/white-icons/checkmark-icon.html)
* [Cross Icon](https://www.iconsdb.com/white-icons/x-mark-icon.html)
* [Ban Icon](https://www.iconsdb.com/white-icons/ban-icon.html)

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/Main.java) and [`MainApp`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `del -i 1`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("del -i 1,3,5")` API call as an example.

<puml src="diagrams/DeleteMultipleIndexesSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `del -i 1,3,5` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a student).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml"/>


The `Model` component,

* stores TAssist data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user's preferences. This is exposed to the outside as a `ReadOnlyUserPref` object.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml"/>

</box>

### LoadDataCommand

The `LoadDataCommand` allows TAssist to import student data from external files in `.csv` or `.json` format.

- It is parsed by `LoadDataCommandParser`, which validates the file name and extension.
- Supported extensions: `.csv` and `.json`.
- Upon execution, the command passes control to the `Storage` component, which attempts to read the file and parse its contents.
- The parsed students are added into the existing TAssist model. Duplicate and malformed entries are filtered with user-facing error messages.
- If the data file is missing, corrupted, or contains entries violating the schema, the command raises a `CommandException` with detailed context.

<puml src="diagrams/LoadDataSequenceDiagram.puml" alt="Sequence diagram for LoadDataCommand" />

This feature streamlines bulk data import and is useful for onboarding existing records into TAssist with minimal manual effort.

### Storage component

**API** : [`Storage.java`](https://github.com/AY2425S2-CS2103-F15-4/tp/blob/master/src/main/java/seedu/tassist/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both taassist data and user preference data in JSON and CSV format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.tassist.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented.

### Undo and redo command
When a command is entered, the class `AddressBookParser` will call the different parsers to parse the command string
based on the command word provided. Within the different switch statements of `AddressBookParser`, a function call to
the class `Operations` is also made to record the string and current state (i.e. the `personList` of the address book) 
and saved to a list (`pastStates`). 
The combination of string, type and `personList` are saved as an object called `Snapshot`.

* If the command is invalid, the `Snapshot` object will be removed from the list.
* If the command is valid, the `Snapshot` object remains.

The list will grow with every command added until an `Undo` command is executed.

When an `Undo` command is executed, it will remove the last added `Snapshot` from the list (`pastStates`) and 
load the `personList` as the current list. The `Snapshot` object is then saved in another list (`futureStates`) 
for the `Redo` command.

Similarly, when a `Redo` command is executed, it will remove the last added `Snapshot` object from 
the list (`futureStates`), load the `personList` as the current list, before adding the `Snapshot` object back to the
list (`pastStates`).

Note:
* An exception is thrown when the required list is empty.
  * For `Undo`, it is `pastStates`.
  * For `Redo`, it is `futureStates`.
* When a valid command is executed after the `Undo` command runs, 
the `futureStates` list is cleared.
* If any other valid command apart from `redo` is executed after `undo`, the `featureStates` list will be cleared.

--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Requirements**

### Product scope

**Target user profile**:

* CS2106 Teaching Assistants (TAs)
* Manage multiple tutorial groups per semester
* Need access to student information to track participation, assignments, and communicate with students
* May want to identify and support weaker students who require extra attention
* Has a need to manage a significant number of contacts
* Prefer desktop apps over other types
* Can type fast
* Prefers typing instead of mouse interactions
* Is reasonably comfortable using CLI apps

**Value proposition**: CS2106 TAs struggle to efficiently track progress and access student information with multiple tutorial groups and many students in each. The lack of a centralized system makes management and follow-ups tedious. TAssist provides a keyboard-driven platform that streamlines student organization into meaningful groups, enabling access to key details anytime, anywhere.


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​        | I want to …​                                                             | So that I can…​                                                                                                                                                                                         |
|----------|----------------|--------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `*`      | new user       | access a walkthrough on basic app functionalities                        | learn how to use the app proficiently without struggling to figure it out on my own.                                                                                                                    |
| `*`      | new user       | access basic help documentation                                          | assess if the application will be useful for my needs.                                                                                                                                                  |
| `*`      | new user       | see an introductory video of the app                                     | learn about key features and basic usage of the application.                                                                                                                                            |
| `* * *`  | user           | add a student's record                                                   | note their particulars and begin keeping track of their attendance and/or lab scores.                                                                                                                   |
| `* * *`  | user           | update a student's tutorial attendance                                   | maintain accurate administrative records.                                                                                                                                                               |
| `* * *`  | user           | update a student's grades for labs                                       | identify if they are struggling with course content and may require additional guidance. This also helps me keep track of graded assignments without rummaging through all the scripts in my workspace. |
| `*`      | user           | label students with tags                                                 | keep track of additional information such as students who may require additional attention.                                                                                                             |
| `* * *`  | user           | delete a student record                                                  | remove students who dropped the class, incorrect entries, or outdated records.                                                                                                                          |
| `* *`    | user           | edit a students record                                                   | fix inaccurate information within records.                                                                                                                                                              |
| `* * *`  | user           | view a list of all students                                              | access all my students' information.                                                                                                                                                                    |
| `*`      | user           | filter/search students by name                                           | locate a specific student.                                                                                                                                                                              |
| `*`      | user           | filter/search students by tutorial / lab group                           | find all students in a class.                                                                                                                                                                           |
| `*`      | user           | filter/search students by home faculty                                   | see the distribution of students across faculties.                                                                                                                                                      |
| `*`      | user           | filter/search students by year                                           | see the distribution of students by cohort.                                                                                                                                                             |
| `*`      | user           | filter/search students by performance                                    | identify students who may need help.                                                                                                                                                                    |
| `*`      | user           | export my data to JSON                                                   | store it in a structured, parseable format.                                                                                                                                                             |
| `*`      | user           | export my data to CSV                                                    | store it in an easily readable format.                                                                                                                                                                  |
| `*`      | user           | receive prompts when typing in commands                                  | be reminded of missing fields.                                                                                                                                                                          |
| `*`      | user           | receive a changelog of updates                                           | stay informed about new features.                                                                                                                                                                       |
| `*`      | lazy user      | perform batch operations for updating attendance                         | N.A.                                                                                                                                                                                                    |
| `*`      | lazy user      | perform batch operations for updating participation                      | N.A.                                                                                                                                                                                                    |
| `*`      | lazy user      | perform batch operations for updating grades                             | N.A.                                                                                                                                                                                                    |
| `*`      | lazy user      | perform batch operations for deleting records by their logical groupings | remove all relevant records.                                                                                                                                                                            |
| `*`      | lazy user      | perform batch operations for deleting all records                        | start afresh with a new user profile.                                                                                                                                                                   |
| `*`      | lazy user      | import data from a CSV file                                              | quickly load data from existing records without manually establishing a new database for it.                                                                                                            |
| `*`      | lazy user      | abbreviations for commands                                               | be more proficient upon familiarizing with the application.                                                                                                                                             |
| `*`      | lazy user      | add keyboard shortcuts                                                   | customize the application to the user's preferences for higher efficiency.                                                                                                                              |
| `*`      | careless user  | automate backups                                                         | rollback on royal mess ups.                                                                                                                                                                             |
| `*`      | careless user  | undo and redo what i just did                                            | revert when i make mistakes.                                                                                                                                                                            |
| `*`      | careless user  | receive warning messages before deleting multiple records                | avoid destroying the whole database from a messed-up command.                                                                                                                                           |
| `*`      | organized user | hide details of a student record that I do not need by default           | avoid cluttering the user interface.                                                                                                                                                                    |

### Use cases

For all use cases below, the **System** is the `TAssist` and the **Actor** is the `user`, unless specified otherwise

**Use case: UC01 - View all students**

**MSS**

1.  TAssist displays all students.

    Use case ends.

**Use case: UC02 - Exit System**

**MSS**

1. User requests to exit TAssist.
2. TAssist saves current data.
3. TAssist exits.

    Use case ends.

**Use case: UC03 - Display Help Message**

**MSS**

1. User requests to display a help message.
2. TAssist displays a help message.

   Use case ends.

**Use case: UC04 - Toggle Contact List View**

**MSS**

1. User requests to toggle the UI view between compact and detailed view of student record.
2. TAssist updates the UI to reflect the selected view mode.

   Use case ends.

**Use case: UC05 - Add a student**

**MSS**

1. User requests to add a student into the list.
2. TAssist validates the input data.
3. TAssist adds the student to the database.
4. TAssist displays the confirmation message.

    Use case ends.

**Extensions**

* 2a. TAssist detects only mandatory arguments provided for a student record within user input.

    Use case resumes at step 3.

* 2b. TAssist detects missing mandatory arguments required for a student record within user input.

    * 2b1. TAssist shows an error message, requesting for provision of missing arguments.

    * 2b2. User enters missing arguments.

    Steps 2b1 and 2b2 are repeated until all mandatory arguments are valid.

    Use case resumes at step 3.

* 2c. TAssist detects invalid/incorrect format arguments in user input.

    * 2c1. TAssist shows an error message, either specifying format requirements or a list of valid arguments.

    * 2c2. User corrects input.

    Steps 2c1 and 2c2 are repeated until the input provided is valid.

    Use case resumes at step 3.

* 2d. TAssist detects duplicate student entry based on user input.

    * 2d1. TAssist shows an error message.

    * 2d2. User enters new data.

    Steps 2d1 and 2d2 are repeated until the input is valid.

    Use case resumes at step 3.

* *a. At any time, user clears input.

    Use case ends.

**Use case: UC06 - Delete a student**

**MSS**

1.  User requests to delete a specific student
2.  TAssist deletes the student

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to delete a student.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides an index that is out of range.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1d. User requests to delete a student that is not in the database.

    * 1d1. TAssist shows an error message, saying the student has already been deleted.

    Use case ends.


**Use case: UC07 - Mark attendance for a student**

**MSS**

1.  User requests to mark attendance for a student in the list, for some week
2.  TAssist marks the student as attended for the indicated week

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to mark a student's attendance. For example, the week number
  is not provided.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides an index or week number that is out of range.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1d. The student that the user wants to mark attendance for does not have
  a valid tutorial group.

    * 1d1. TAssist shows an error message, informing the user that the student
    has no tutorial group, and therefore cannot be marked attendance for tutorials.
    
    * 1d2. User enters new data.

  Steps 1d1 and 1d2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1e. The student that the user wants to mark attendance for currently has no tutorial.

    * 1e1. TAssist shows an error message, informing the user that the student has no tutorial.
  It does not make sense to mark an individual student with a cancelled tutorial as attended.
  To mark the student as any other attendance status for that week, the user would need to mark
  the whole tutorial group as some other status like "Not Attended" first.

    * 1e2. User enters new data.

  Steps 1e1 and 1e2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1f. User requests that the student be marked as not attended.

    * 1f1. TAssist marks the student as not attended for the indicated week.

  Use case ends.

* 1g. User requests that the student be marked as on MC.

    * 1g1. TAssist marks the student as on MC for the indicated week.

  Use case ends.


* *a. At any time, user clears input.

  Use case ends.


**Use case: UC08 - Mark attendance for multiple students**

**MSS**

1.  User requests to mark attendance for a range of students in the list, for some week
2.  TAssist marks the student's as attended for the indicated week

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to mark the students' attendance. For example, the week number
is not provided.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, within the range of indexes provided, there is at least
  one index that is of an invalid format (like a negative index). Or, the range may be of an
  invalid format, not using the required syntax of commas and hyphens, or is descending in order.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1d. Within the range of indexes provided, there is at least one student that has valid no tutorial group.

    * 1d1. TAssist shows an error message, informing the user that at least one student
      has no tutorial group, and therefore cannot be marked attendance for tutorials.
  All students in the provided range of students must have a valid tutorial group.

    * 1d2. User enters new data.

  Steps 1d1 and 1d2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1e. Within the range of indexes provided, there is at least one student that currently has no tutorial.

    * 1e1. TAssist shows an error message, informing the user that at least one student has no tutorial.
      It does not make sense to mark an individual student with a cancelled tutorial as attended.
      To mark the student as any other attendance status for that week, the user would need to mark
      the whole tutorial group as some other status like "Not Attended" first.
  
      All students in the provided range of students must have an attendance status that is not "No Tutorial".
  
    * 1e2. User enters new data.

  Steps 1e1 and 1e2 are repeated until the data entered are correct.

  Use case resumes at step 2.


* 1f. User requests that the students be marked as not attended.

    * 1f1. TAssist marks all the students as not attended for the indicated week.

  Use case ends.


* 1g. User requests that the students be marked as on MC.

    * 1g1. TAssist marks all the students as on MC for the indicated week.

  Use case ends.


* *a. At any time, user clears input.

  Use case ends.

**Use case: UC09 - Mark attendance for a tutorial group**

**MSS**

1.  User requests to mark attendance for a tutorial group in the list, for some week
2.  TAssist marks the tutorial group as attended for the indicated week

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to mark a tutorial group's attendance. For instance,
he does not provide the week number required.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides a tutorial group that is not a valid tutorial group
of any student in the list, or a week number that is out of range.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.


* 1d. User requests that the tutorial group be marked as not attended.

    * 1d1. TAssist marks everyone in the tutorial group as not attended for the indicated week.

    Use case ends.


* 1e. User requests that the tutorial group be marked as on MC.

    * 1e1. TAssist marks everyone in the tutorial group as on MC for the indicated week.

  Use case ends.


* 1f. User requests that the tutorial group be marked as no tutorial.
For example, the tutorial is cancelled due to a public holiday.

    * 1f1. TAssist marks everyone in the tutorial group as no tutorial for the indicated week.

  Use case ends.


* *a. At any time, user clears input.

  Use case ends.


**Use case: UC10 - Mark attendance for multiple tutorial groups**

**MSS**

1.  User requests to mark attendance for a range of tutorial groups in the list, for some week
2.  TAssist marks the tutorial groups as attended for the indicated week

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to mark the tutorial groups' attendance. For instance,
  he does not provide the week number required.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, within the range of tutorial groups provided, there is at least
one tutorial group that is of an invalid format, or there is at least one tutorial group
that is not a tutorial group of any student in the list. Or, the range may be of an
invalid format, not using the required syntax of commas and hyphens, or is descending in order.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.


* 1d. User requests that the tutorial groups be marked as not attended.

    * 1d1. TAssist marks everyone in the tutorial groups as not attended for the indicated week.

    Use case ends.


* 1e. User requests that the tutorial groups be marked as on MC.

    * 1e1. TAssist marks everyone in the tutorial groups as on MC for the indicated week.

  Use case ends.


* 1f. User requests that the tutorial groups be marked as no tutorial.
  For example, the tutorial is cancelled due to a public holiday.

    * 1f1. TAssist marks everyone in the tutorial groups as no tutorial for the indicated week.

  Use case ends.


* *a. At any time, user clears input.

  Use case ends.


**Use case: UC11 - Update lab score for a student**

**MSS**

1.  User requests to update lab score for a student in the list, for some lab session
2.  TAssist updates the lab score for the student for the indicated lab session

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to update a student's lab score.

    * 1b1. TAssist shows an error message, requesting for missing arguments.

    * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides an index that is out of range, or an invalid lab session name.

    * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

    * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.

**Use case: UC12 - Update max lab score for a lab**

**MSS**

1.  User requests to update maximum lab score for a lab.
2.  TAssist updates the maximum lab score for all the student for the indicated lab session

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to update the maximum lab score.

  * 1b1. TAssist shows an error message, requesting for missing arguments.

  * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides a lab session that is out of range, or an invalid maximum lab score.

  * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

  * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.


**Use case: UC13 - Update lab score for a student and maximum lab score for a lab**

**MSS**

1.  User requests to update lab score for a student in the list, for some lab session. 
    At the same time, update the maximum lab score for the specified lab.
2.  TAssist updates the lab score for the student for the indicated lab session, and 
    update the maximum lab score for the indicated lab.

    Use case ends.

**Extensions**

* 1a. User provides only the mandatory arguments in the
  correct format, or provides additional arguments on top
  of the mandatory arguments, all in the correct format.

  Use case resumes at step 2.

* 1b. User does not provide at least one mandatory argument
  required to update a student's lab score.

  * 1b1. TAssist shows an error message, requesting for missing arguments.

  * 1b2. User enters new data.

  Steps 1b1 and 1b2 are repeated until the data entered are correct.

  Use case resumes at step 2.

* 1c. User provides at least one argument that is invalid, or in incorrect format.
  For instance, user provides an index that is out of range, 
  or the given maximum score for the lab is lower than the student's score.

  * 1c1. TAssist shows an error message, requesting for valid arguments in correct format.

  * 1c2. User enters new data.

  Steps 1c1 and 1c2 are repeated until the data entered are correct.

  Use case resumes at step 2.


**Use case: UC14 - Load data from file**

**MSS**

1.  User requests to load data by specifying a file location.
2.  TAssist loads data from the specified file location.
3.  TAssist displays all students and their information, as loaded from file.

    Use case ends.

**Extensions**

* 2a. File at specified location is missing.

    * 2a1. TAssist displays an error message.
    
    * 2a2. TAssist displays no student data.

    Use case ends.

* 2b. File at specified location cannot be parsed, due to corrupted data in file.

    * 2b1. TAssist displays an error message for non-parsable data records.

    * 2b2. TAssist displays the remaining students that are parsable.

  Use case ends.

* 2c. File at specified location contains duplicate entries.

    * 2c1. TAssist displays an error message for duplicate entries.

  Use case resumes at Step 3.

* 2d. File at specified location contains entries exceeding the maximum number of entries allowed.

    * 2d1. TAssist displays an error message that the file contains too many entries.

    * 2d2. Starting from the first entry, TA shows up to the maximum number of entries, and omits
      all the entries after that.

  Use case ends.

* 2e. File at specified location contains entries nearing the maximum number of entries allowed.

    * 2e1. TAssist displays an error message, with the number of entries left that the user
      can add, before it reaches the maximum number of entries.

  Use case resumes at Step 3.

**Use case: UC15 - Save data to file**

**MSS**

1.  User closes the app.
2.  TAssist saves students' data into a file, at a specified location.

  Use case ends.

**Use case: UC16 - Undo the last command**

**MSS**

1.  User requests to undo the last command.
2.  TAssist undoes the last command.

    Use case ends.

**Extensions**

* 1a. There is a command available to undo

  Use case resumes at step 2.
* 1b. No command has been run before.

  * 1b1. TAssist displays an error message.

  Use case ends.
* 1c. No available command to undo.
  * 1c1. TAssist displays an error message.

  Use case ends.

**Use case: UC17 - Redo the last command**

**MSS**

1.  User requests to redo the last command.
2.  TAssist redoes the last command.

    Use case ends.

**Extensions**

* 1a. There is a command available to redo
  
  Use case resumes at step 2.
* 1b. No command has been run before.

  * 1b1. TAssist displays an error message.

  Use case ends.
* 1c. No available command to redo.
  * 1c1. TAssist displays an error message.

  Use case ends.


### Non-Functional Requirements

1. A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
2. Ideally entirely keyboard driven, with minimal mouse clicks required.
2. Should work on any _mainstream OS_ as long as it has Java `17` or above installed.
3. Should respond to user input relatively quickly (under 1s).
3. Should be able to hold up to 1000 students without a noticeable sluggishness in performance for typical usage.
4. Data should be stored locally and should be in a human editable text file, not involving the use of a Database Management System.
5. Should save backups occasionally.
6. Should follow Object-oriented paradigm primarily.
7. Should not depend on a personal remote server and function completely offline.
8. Should work without requiring an installer.
9. Should work well for standard screen resolutions 1920x1080 and higher and of screen scales 100% and 125%.
10. Should be functional for screen resolutions 1280x720 and higher and of screen scales 150%.
11. Product should be under 100MB, while documents should be under 15MB/file.
12. Should have PDF-friendly developer guides and user guides.

### Glossary
* **Command**: An action that the user calls to run a specified function.
It may return a result and it may also take in a specified number of arguments.
* **Component**: A [Name] component has its API in a [Name].java interface and implements its functionality
using the [Name]Manager.java class following the [Name] interface.
* **Database**: The save file of this program.
* **Database entry**: An item saved in the database. Commonly linked to a contact. One contact is one database entry.
* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Sequence diagram**: a diagram that shows the flow of the program.
* **TA**: Teaching Assistant
* **MC**: Medical Certificate (Proof of absence as certified by a medical professional, like a doctor)

--------------------------------------------------------------------------------------------------------------------

## **Appendix: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Copy the file to the folder you want to use as the _home folder_ for TAssist.

   1. Open a command terminal, `cd` into the folder you put the jar file in, and use the `java -jar TAssist.jar` command to run the application.<br>
      A GUI similar to the below should appear in a few seconds. Note how the app contains some sample data.<br>
      ![Ui](images/Ui.png)

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by using the `java -jar TAssist.jar` command.<br>
       Expected: The most recent window size and location is retained.

1. To close the application, use either the `exit` command or click the `x` at the top right corner.

### Toggling student contact details
1. Toggling student contact details within the UI. Performed within one session.

    1. Prerequisites: Full contact details are seen on the UI (default setting upon startup).

    1. Test case: `toggle`<br>
       Expected: Message shown in the status message. Some contact details are hidden.

    1. Test case: `toggle`<br>
       Expected: Message shown in the status message. All contact details are shown.

### Listing all contacts
1. Listing all contacts within the UI.
    1. Prerequisites: Show only a subset of contacts using the `search` command.

    1. Test case: `list`<br>
       Expected: All contacts will be shown in the UI.

### Searching for particular contacts

1. Searching for a particular contact.

    1. Prerequisites: Contact with name `Alex Yeoh` exists.

    1. Test case: `search -n ye`<br>
       Expected: Contact of `Alex Yeoh` is shown on the UI. Status message updated.

### Redo command: redo
1. Redo a command that has data changes (`lab`, `att` etc.)
    1. Test case (redo lab command `lab -ln 1 -msc 25`): `redo`<br>
       Expected: Successfully redo lab score command. <br>
       Command was: lab -ln 1 -msc 25
1. Redo a command that has no data changes (`list` etc.)
    1. Test case (redo command list): `redo`<br>
       Expected: list command was the last command, no changes has occurred
1. Hit limit of redo (no more things to redo)
    1. Test case: `redo`<br>
       Expected: You have reached the limit of redo

### Undo command: undo
1. Undo a command that has data changes (`lab`, `att` etc.)
    1. Test case (undo `lab command lab -ln 1 -msc 25`): `undo`<br>
       Expected: Successfully undo lab score command. <br>
       Command was: lab -ln 1 -msc 25

1. Undo a command that has no data changes (`list` etc.)
    1. Test case (undo command `list`): `undo`<br>
       Expected: list command was the last command, no changes has occurred

1. Hit limit of undo (no more things to undo)
    1. Test case: `undo`<br>
       Expected: You have reached the limit of undo

### Adding a student
1. Adding contacts in one session.
    1. Test case: `add -n stresson -p 94309214 -t T01 -m A0243421 -e e05941325@u.nus.edu`<br>
       Expected: Adds a new contact.

    1. Test case: `add -n stresson -p 94309214 -t T01 -m A0243421 -e e05941325@u.nus.edu`<br>
       Expected: Duplicate is detected. Error details shown in the status message.

    1. Test case: `add -n stresson -p 94309214 -t T01 -m A0312456 -e e05941325@u.nus.edu`<br>
       Expected: Adds a new contact. Only matriculation number defines a unique contact.

    1. Test case: `add -n name with -t flag in their name -p 94309214 -t T01 -m A8569364 -e e05941325@u.nus.edu`<br>
       Expected: Duplicate flag is detected. Error details shown in the status message.

    1. Test case: `add -n "name with -t flag in their name" -p 94309214 -t T01 -m A8569364 -e e05941325@u.nus.edu`<br>
       Expected: Adds a new contact.

### Editing a student
1. Editing a student while all students are being shown

   1. Prerequisites: List all students using the `list` command. Multiple students in the list.

   1. Test Case: `edit -i 1 -n JohnDoe`<br>
      Expected: Edits the name of the 1st student to JohnDoe.

   2. Test Case: `edit -i 1 -m A0000030U`<br>
      Expected: This student already exists in TAssist.

   3. Test Case: `edit -i 1 -p`<br>
      Expected: You cannot remove the Phone Number!

   4. Test Case: `edit -i 2 -t`<br>
      Expected: You cannot remove the Tutorial Group!

### Deleting a student

1. Deleting a student while all students are being shown

   1. Prerequisites: List all students using the `list` command. Multiple students in the list.

   1. Test case: `del -i 1`<br>
      Expected: First person in the list is deleted. Confirmation message is shown with their details.

   1. Test case: `del -i 0`<br>
      Expected: Error message shown: "Invalid index. Index must be a non-zero positive integer and within the range of listed records.” No deletion occurs.

   1. Test case: `del -i -1`<br>
   Expected: Error message shown: "Invalid input.” Possible issues shown (invalid range input or non-zero integer). No deletion occurs.

   1. Test case: `del -i 999` (where 999 > number of students shown)<br>
   Expected: Error message shown: "Invalid index (out of range)! You currently have [number] records!” No deletion occurs.

   1. Test case: `del -i 1-3` (range input)<br>
   Expected: Persons at index 1, 2, and 3 are deleted. Confirmation message lists all three.

   1. Test case: `del -i 2,4` (comma-separated input)<br>
   Expected: Persons at index 2 and 4 are deleted. Confirmation message shows both.

   1. Test case: `del -i 3, 6-7, 9` (mixed comma and range input)<br>
   Expected: All specified persons are deleted. Duplicates are ignored. Confirmation message lists all unique deletions.

   1. Test case: `del -i 3-1`<br>
   Expected: Error message shown: "Invalid index range! Ensure that start <= end and all values are positive integers. Expected format: start-end (e.g., 2-4).”

   1. Test case: `del -i 1 -i 2`<br>
   Expected: Error message shown: "Multiple values specified for the following single-valued field(s): -i”

   1. Test case: `del`<br>
   Expected: Error message: "Missing arguments! Requires -i <index>.” Delete usage message displayed.

   1. Test case: `del -i `<br>
   Expected: Error message: "Missing arguments! Requires -i <index>..” Delete usage message displayed.

   1. Test case: `del -i one`<br>
   Expected: Error message: "Invalid index. Only digits, commas and dashes are allowed."

   1. Test case: `del -i 1a`<br>
   Expected: Error message: "Invalid index. Only digits, commas and dashes are allowed."

   1. Test case: `del -i 1,,,2`<br>
   Expected: Error message: "Invalid index format! Please input each index separated by a comma. Expected format: index, index,... (e.g., 2,4)"


### Tagging a student
1. Tagging a student while all students are being shown

   1. Prerequisites: List all student using the `list` command. Multiple students in the list.

   2. Test Case: `tag -a -i 1`<br>
      Expected: You need to provide a tag using the flag (-tag)

   3. Test Case: `tag -a -i 1 -tag testTag`<br>
      Expected: Successfully added a tag

   4. Test Case: `tag -m -i 1 -tag testTag -tag newTestTag`<br>
      Expected: Successfully edited a tag

   5. Test Case: `tag -d -i 1 -tag newTestTag`<br>
      Expected: Successfully deleted a tag

### Marking attendance

1. Marking the attendance of a student while all students are being shown.

   1. Prerequisites: List all student using the `list` command. Multiple students in the list.

   1. Test case: `att -i 1 -w 5`<br>
      Expected: First contact is marked as attended for week 5. (Provided he satisfies the restrictions of the mark attendance command)

   1. Test case: `att -i 1 -w 5 -u`<br>
      Expected: First contact is marked as not attended for week 5. (Provided he satisfies the restrictions of the mark attendance command)

   1. Test case: `att -t T01 -w 5`<br>
      Expected: All students in the tutorial group T01 are marked as attended for week 5. (Provided the restrictions of the mark attendance command are satisfied)

   1. Test case: `att -i T01-T02 -w 5`<br>
      Expected: All students in the tutorial groups T01 and T02 are marked as attended for week 5. (Provided the restrictions of the mark attendance command are satisfied)

   1. Other incorrect delete commands to try: `att`, `att -i x`, `...` (where x is larger than the list size)<br>
      Expected: Error messages describing the error.

### Lab Score

1. Updating lab score for specified student

    1. Test case: `lab -i 1 -ln 1 -sc 20`<br>
       Expected: Update lab 1 score for student 1 as 20/25

    1. Test case: `lab -i 1 -ln 1 -sc 40`<br>
       Expected: The updated max score cannot be lesser than the current score for the lab.Your input: 5. The current score for this lab: 25. <br>
       Note: In this case, the score for lab 1 was set to 25.

    1. Test case: `lab -i 1 -ln 10 -sc 20`<br>
       Expected: Lab number must be between 1 and 4

1. Updating max lab score for a specific lab

    1. Test case: `lab -ln 1 -msc 30`<br>
       Expected: Update lab 1 max score to be 30

    1. Test case: `lab -ln 1 -msc 5`<br>
      Expected: Person 1 has score higher than the max lab score (5) that you wish to set. <br>
      Note: In this case, the maximum score was set to be 25
       

1. Updating both lab score and max lab score
    1. Test case: `lab -i 1 -ln 1 -sc 20 -msc 35`<br>
       Expected: Update lab 1 max score to be 35 and lab 1 score for student 1 as 20/35

    1. Test case: `lab -ln 1 -sc 20 -msc 35`<br>
       Expected: Invalid index. Index must be a non-zero positive integer and within the range of listed records.

    1. Test case: `lab -i 5 -ln 1 -sc 20 -msc 35`<br>
       Expected: This index does not exist. It exceeds the maximum number of contacts

### Loading data
1. Loading CSV data into the application
   1. Prerequisites: You need to have a file at ./data/userdata.csv
   2. Test Case: `load -f userdata -ext csv`
      Expected: Loaded data from file: userdata.csv

2. Loading JSON data into the application
   1. Prerequisites: You need to have a file at ./data/addressbook.json
   2. Test Case: `load -f addressbook -ext json`
      Expected: Loaded data from file: addressbook.json

### Exporting data
1. Export data as CSV format
   2. Test Case: `export -f ./data/test.csv`
      Expected: Exported data to file: ./data/test.csv
1. Export data as JSON format
   2. Test Case: `export -f ./data/test.json`
      Expected: Exported data to file: ./data/test.csv

### Saving data

1. Saving of data is automatically done, there is no command to manually save the data.

## **Appendix: Planned Enhancements**
Team size: 6

1. This pertains to the `NAME`, `FACULTY`, and `REMARK` of a contact.
   * To allow for overzealous input validation, the application allows for most unicode characters to be provided. 
   * As such, one can have names such as `恵凛`, `まさひろ`, or even `Nguyễn Thị Minh Hằng`. 
   * However, due to the extensive nature of such characters, we have not properly ensured that all characters can be displayed by the UI, and may appear as `▯` instead. 
   * Considering the fact that this application is targeted for English typists, we strongly recommend only alphanumerical characters be provided to the application instead.

1. Bulk marking of attendance for several weeks, or for all students, at one go.
    * We understand that some users may want to mark attendance for several weeks, or for all students, using one command.
    * Currently, our MarkAttendanceCommand only supports marking attendance for one week at a time, on multiple students
      given a range of indexes, or a range of tutorial groups.
    * We will consider extending support for marking attendance over a range of weeks, as well as
      marking attendance for all students in the list, in the future.

1. Matching of attendance records to an existing tutorial group.
   * We understand that when a student gets newly added to some existing tutorial group, 
   OR the tutorial group of an existing student gets edited to some existing tutorial group, 
   it will only make sense that the weeks of "No Tutorial" from this existing tutorial group 
   get copied over to the attendance list of the new student.
   * For instance, if a student is transferred from `T01` (with all weeks having tutorial) to `T02`
     (with week 7 having no tutorial, due to a public holiday), then the attendance status for week 7
     should be set to "No Tutorial", consistent with other students in tutorial group `T02`.
   * Similarly, if a new student is added to `T02`, his attendance status for week 7 should be set to "No Tutorial"
   by default, consistent with other students in tutorial group `T02`.
   * We will consider extending support for cross-checking a student's new tutorial group, against the
   attendance lists of other students from the same tutorial group, such as to match the weeks of "No Tutorial"
   with the new tutorial group, in the future.

1. Lab score section will only be shown for those in a lab group.
    * It is a known error that currently, the lab score section will always be shown for all student regardless of
    the student being in a lab group or not. This is due to the lab score section always being added to a student when
    adding a new student to the list.
    * We plan to check if a student is in a lab group before adding the lab scores to the student 
    in a future implementation.

1. De-selection of a person contact.
    * When a contact is selected in the UI, there is currently no means to de-select it.
    * This means that under the compact view achieved through the `toggle` command, one contact will always be expanded.
    * The current fix for this will be to run a `search` command that shows nothing, before running `list` again.
    * Considering the fact that TAssist is primarily a keyboard driven application, we do not expect most users to be using mouse options that heavily, and will thus be implemented in a future date.

1. More comprehensive error messages for invalid commands 
    * Error messages for invalid commands currently remind the user about the full, expected usage of the command.
    * Some error messages may not manage to identify the problematic input within the given command. For example, when an invalid flag is provided after the index input (like `lab -i 1 -ln25`), the current implementation will prompt the user that the index given is invalid. This is because the app recognises this invalid flag (-ln25) as part of the preceding input to the preceding flag (`-i`). The app therefore treats this index input (`1 -ln25`) as invalid.
    * This feature flaw is prevalent across all of our commands, due to our implementation of recognising flags and inputs. 
    * We understand that users may want better informed error messages to invalid commands. To fix this feature flaw, we will propose to alter our implementation of recognising inaccurate flags and inputs, to better meet users' needs. 
    * In the meantime, we would appreciate that users could kindly refer to our User Guide, which contains comprehensive information about the flags and inputs expected for each command. Thank you very much!

## **Appendix: Effort**

### Difficulty level

We rate the Team Project (TP) a 8/10.

### Challenges faced

- Navigating through the large code base proved to be a significant challenge, especially at the start. It was hard to understand what went wrong and debugging took a significant amount of time and effort.

- Creation of test cases was also difficult, especially when thinking of creative ways to break the code or handle unexpected input.

- Trying to establish a balance when restricting user inputs was also difficult. While flexibility allowed for overzealous inputs, it also creates more opportunities for different user combinations to potentially generate crashes which has to be separately tested.

- JavaFX was new to everyone in the team. As such, minor improvements to the UI came at the cost of extensive debugging and researching for solutions online to circumvent issues.

### Effort required

- A lot of effort was required by many to ensure that deadlines were met. Work schedules had to be rearranged to ensure that PR's from all members are vetted and accepted before submission deadlines.

### Achievements

- Despite the seemingly minute additions to the MVP in terms of features, our goals were achieved.

- A deeper understanding of software development and the tools available (e.g. Git).

