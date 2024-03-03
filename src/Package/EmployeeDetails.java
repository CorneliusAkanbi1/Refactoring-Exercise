package Package;
/* * 
 * This is a menu driven system that will allow users to define a data structure representing a collection of 
 * records that can be displayed both by means of a dialog that can be scrolled through and by means of a table
 * to give an overall view of the collection contents.
 * 
 * */

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.miginfocom.swing.MigLayout;

public class EmployeeDetails extends JFrame implements ActionListener, ItemListener, DocumentListener, WindowListener {
	// decimal format for inactive currency text field
	private static final DecimalFormat format = new DecimalFormat("\u20ac ###,###,##0.00");
	// decimal format for active currency text field
	private static final DecimalFormat fieldFormat = new DecimalFormat("0.00");
	// hold object start position in file
	private long currentByteStart = 0;
	private RandomFile application = new RandomFile();
	// display files in File Chooser only with extension .dat
	private FileNameExtensionFilter datfilter = new FileNameExtensionFilter("dat files (*.dat)", "dat");
	// hold file name and path for current file in use
	private File file;
	// holds true or false if any changes are made for text fields
	private boolean change = false;
	// holds true or false if any changes are made for file content
	boolean changesMade = false;
	private JMenuItem open, save, saveAs, create, modify, delete, firstItem, lastItem, nextItem, prevItem, searchById,
			searchBySurname, listAll, closeApp;
	private JButton first, previous, next, last, add, edit, deleteButton, displayAll, searchId, searchSurname,
			saveChange, cancelChange;
	private JComboBox<String> genderCombo, departmentCombo, fullTimeCombo;
	private JTextField idField, ppsField, surnameField, firstNameField, salaryField;
	private static EmployeeDetails frame = new EmployeeDetails();
	// font for labels, text fields and combo boxes
	Font font1 = new Font("SansSerif", Font.BOLD, 16);
	// holds automatically generated file name
	String generatedFileName;
	// holds current Employee object
	Employee currentEmployee;
	JTextField searchByIdField, searchBySurnameField;
	// gender combo box values
	String[] gender = { "", "M", "F" };
	// department combo box values
	String[] department = { "", "Administration", "Production", "Transport", "Management" };
	// full time combo box values
	String[] fullTime = { "", "Yes", "No" };

	private JMenuBar menuBar() {
	    JMenuBar menuBar = new JMenuBar();
	    menuBar.add(createFileMenu());
	    menuBar.add(createRecordMenu());
	    menuBar.add(createNavigateMenu());
	    menuBar.add(createCloseMenu());
	    return menuBar;
	}

	private JMenu createFileMenu() {
	    JMenu fileMenu = new JMenu("File");
	    fileMenu.setMnemonic(KeyEvent.VK_F);
	    fileMenu.add(createMenuItem("Open", KeyEvent.VK_O, ActionEvent.CTRL_MASK));
	    fileMenu.add(createMenuItem("Save", KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	    fileMenu.add(createMenuItem("Save As", KeyEvent.VK_F2, ActionEvent.CTRL_MASK));
	    return fileMenu;
	}

	private JMenu createRecordMenu() {
	    JMenu recordMenu = new JMenu("Records");
	    recordMenu.setMnemonic(KeyEvent.VK_R);
	    recordMenu.add(createMenuItem("Create new Record", KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	    recordMenu.add(createMenuItem("Modify Record", KeyEvent.VK_E, ActionEvent.CTRL_MASK));
	    recordMenu.add(createMenuItem("Delete Record", KeyEvent.VK_DELETE, 0));
	    return recordMenu;
	}

	private JMenu createNavigateMenu() {
	    JMenu navigateMenu = new JMenu("Navigate");
	    navigateMenu.setMnemonic(KeyEvent.VK_N);
	    navigateMenu.add(createMenuItem("First", KeyEvent.VK_F, ActionEvent.CTRL_MASK));
	    navigateMenu.add(createMenuItem("Previous", KeyEvent.VK_P, ActionEvent.CTRL_MASK));
	    navigateMenu.add(createMenuItem("Next", KeyEvent.VK_N, ActionEvent.CTRL_MASK));
	    navigateMenu.add(createMenuItem("Last", KeyEvent.VK_L, ActionEvent.CTRL_MASK));
	    navigateMenu.addSeparator();
	    navigateMenu.add(createMenuItem("Search by ID", KeyEvent.VK_I, ActionEvent.CTRL_MASK));
	    navigateMenu.add(createMenuItem("Search by Surname", KeyEvent.VK_S, ActionEvent.CTRL_MASK));
	    navigateMenu.add(createMenuItem("List all Records", KeyEvent.VK_L, ActionEvent.CTRL_MASK));
	    return navigateMenu;
	}

	private JMenu createCloseMenu() {
	    JMenu closeMenu = new JMenu("Exit");
	    closeMenu.setMnemonic(KeyEvent.VK_E);
	    closeMenu.add(createMenuItem("Close", KeyEvent.VK_F4, ActionEvent.CTRL_MASK));
	    return closeMenu;
	}

	private JMenuItem createMenuItem(String text, int mnemonic, int accelerator) {
	    JMenuItem menuItem = new JMenuItem(text);
	    menuItem.setMnemonic(mnemonic);
	    menuItem.setAccelerator(KeyStroke.getKeyStroke(mnemonic, accelerator));
	    menuItem.addActionListener(this);
	    return menuItem;
	}


	private JPanel searchPanel() {
	    JPanel searchPanel = new JPanel(new MigLayout());
	    searchPanel.setBorder(BorderFactory.createTitledBorder("Search"));

	    searchPanel.add(new JLabel("Search by ID:"), "growx, pushx");
	    searchByIdField = createTextField(20);
	    searchPanel.add(searchByIdField, "width 200:200:200, growx, pushx");
	    searchPanel.add(createButton("Go", "Search Employee By ID"), "width 35:35:35, height 20:20:20, growx, pushx, wrap");

	    searchPanel.add(new JLabel("Search by Surname:"), "growx, pushx");
	    searchBySurnameField = createTextField(20);
	    searchPanel.add(searchBySurnameField, "width 200:200:200, growx, pushx");
	    searchPanel.add(createButton("Go", "Search Employee By Surname"), "width 35:35:35, height 20:20:20, growx, pushx, wrap");

	    return searchPanel;
	}

	private JTextField createTextField(int columns) {
	    JTextField textField = new JTextField(columns);
	    textField.addActionListener(this);
	    textField.setDocument(new JTextFieldLimit(20));
	    return textField;
	}

	private JButton createButton(String text, String toolTipText) {
	    JButton button = new JButton(text);
	    button.addActionListener(this);
	    button.setToolTipText(toolTipText);
	    return button;
	}


	// initialize navigation panel
	private JPanel navigPanel() {
	    JPanel navigPanel = new JPanel();

	    navigPanel.setBorder(BorderFactory.createTitledBorder("Navigate"));

	    navigPanel.add(createButton(first, "first.png", "Display first Record"));
	    navigPanel.add(createButton(previous, "prev.png", "Display next Record"));
	    navigPanel.add(createButton(next, "next.png", "Display previous Record"));
	    navigPanel.add(createButton(last, "last.png", "Display last Record"));

	    return navigPanel;
	}

	private JButton createButton(JButton button, String iconName, String toolTipText) {
	    button = new JButton(new ImageIcon(new ImageIcon(iconName).getImage()
	            .getScaledInstance(17, 17, java.awt.Image.SCALE_SMOOTH)));
	    button.setPreferredSize(new Dimension(17, 17));
	    button.addActionListener(this);
	    button.setToolTipText(toolTipText);
	    return button;
	}


	private JPanel buttonPanel() {
		JPanel buttonPanel = new JPanel();

		buttonPanel.add(add = new JButton("Add Record"), "growx, pushx");
		add.addActionListener(this);
		add.setToolTipText("Add new Employee Record");
		buttonPanel.add(edit = new JButton("Edit Record"), "growx, pushx");
		edit.addActionListener(this);
		edit.setToolTipText("Edit current Employee");
		buttonPanel.add(deleteButton = new JButton("Delete Record"), "growx, pushx, wrap");
		deleteButton.addActionListener(this);
		deleteButton.setToolTipText("Delete current Employee");
		buttonPanel.add(displayAll = new JButton("List all Records"), "growx, pushx");
		displayAll.addActionListener(this);
		displayAll.setToolTipText("List all Registered Employees");

		return buttonPanel;
	}
	private JPanel detailsPanel() {
	    JPanel empDetails = createEmployeeDetailsPanel();
	    setupListenersAndFormatting(empDetails);
	    return empDetails;
	}

	private JPanel createEmployeeDetailsPanel() {
	    JPanel empDetails = new JPanel(new MigLayout());
	    empDetails.setBorder(BorderFactory.createTitledBorder("Employee Details"));

	    addField(empDetails, "ID:", idField = new JTextField(20), false);
	    addField(empDetails, "PPS Number:", ppsField = new JTextField(20), true);
	    addField(empDetails, "Surname:", surnameField = new JTextField(20), true);
	    addField(empDetails, "First Name:", firstNameField = new JTextField(20), true);
	    addComboBox(empDetails, "Gender:", genderCombo = new JComboBox<>(gender), false);
	    addComboBox(empDetails, "Department:", departmentCombo = new JComboBox<>(department), false);
	    addField(empDetails, "Salary:", salaryField = new JTextField(20), true);
	    addComboBox(empDetails, "Full Time:", fullTimeCombo = new JComboBox<>(fullTime), false);

	    empDetails.add(createButtonPanel(), "span 2,growx, pushx,wrap");
	    return empDetails;
	}

	private void addField(JPanel panel, String label, JTextField field, boolean editable) {
	    panel.add(new JLabel(label), "growx, pushx");
	    field.setEditable(editable);
	    field.setDocument(new JTextFieldLimit(editable ? 20 : 9));
	    field.getDocument().addDocumentListener(this);
	    panel.add(field, "growx, pushx, wrap");
	}

	private void addComboBox(JPanel panel, String label, JComboBox<String> comboBox, boolean editable) {
	    panel.add(new JLabel(label), "growx, pushx");
	    comboBox.setBackground(Color.WHITE);
	    comboBox.setEnabled(editable);
	    comboBox.addItemListener(this);
	    comboBox.setRenderer(new DefaultListCellRenderer() {
	        public void paint(Graphics g) {
	            setForeground(new Color(65, 65, 65));
	            super.paint(g);
	        }
	    });
	    panel.add(comboBox, "growx, pushx, wrap");
	}

	private JPanel createButtonPanel() {
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.add(saveChange = new JButton("Save"));
	    saveChange.addActionListener(this);
	    saveChange.setVisible(false);
	    saveChange.setToolTipText("Save changes");
	    buttonPanel.add(cancelChange = new JButton("Cancel"));
	    cancelChange.addActionListener(this);
	    cancelChange.setVisible(false);
	    cancelChange.setToolTipText("Cancel edit");
	    return buttonPanel;
	}

	private void setupListenersAndFormatting(JPanel panel) {
	    for (Component component : panel.getComponents()) {
	        if (component instanceof JTextField) {
	            JTextField textField = (JTextField) component;
	            textField.setFont(font1);
	            textField.setBackground(Color.WHITE);
	        } else if (component instanceof JComboBox) {
	            JComboBox<?> comboBox = (JComboBox<?>) component;
	            comboBox.setFont(font1);
	            comboBox.setBackground(Color.WHITE);
	        }
	    }
	}


	// display current Employee details
	public void displayRecords(Employee thisEmployee) {
		int countGender = 0;
		int countDep = 0;
		boolean found = false;

		searchByIdField.setText("");
		searchBySurnameField.setText("");
		// if Employee is null or ID is 0 do nothing else display Employee
		// details
		if (thisEmployee == null) {
		} else if (thisEmployee.getEmployeeId() == 0) {
		} else {
			// find corresponding gender combo box value to current employee
			while (!found && countGender < gender.length - 1) {
				if (Character.toString(thisEmployee.getGender()).equalsIgnoreCase(gender[countGender]))
					found = true;
				else
					countGender++;
			} // end while
			found = false;
			// find corresponding department combo box value to current employee
			while (!found && countDep < department.length - 1) {
				if (thisEmployee.getDepartment().trim().equalsIgnoreCase(department[countDep]))
					found = true;
				else
					countDep++;
			} // end while
			idField.setText(Integer.toString(thisEmployee.getEmployeeId()));
			ppsField.setText(thisEmployee.getPps().trim());
			surnameField.setText(thisEmployee.getSurname().trim());
			firstNameField.setText(thisEmployee.getFirstName());
			genderCombo.setSelectedIndex(countGender);
			departmentCombo.setSelectedIndex(countDep);
			salaryField.setText(format.format(thisEmployee.getSalary()));
			// set corresponding full time combo box value to current employee
			if (thisEmployee.getFullTime() == true)
				fullTimeCombo.setSelectedIndex(1);
			else
				fullTimeCombo.setSelectedIndex(2);
		}
		change = false;
	}// end display records

	// display Employee summary dialog
	private void displayEmployeeSummaryDialog() {
		// display Employee summary dialog if these is someone to display
		if (isSomeoneToDisplay())
			new EmployeeSummaryDialog(getAllEmloyees());
	}// end displaySummaryDialog

	// display search by ID dialog
	private void displaySearchByIdDialog() {
		if (isSomeoneToDisplay())
			new SearchByIdDialog(EmployeeDetails.this);
	}// end displaySearchByIdDialog

	// display search by surname dialog
	private void displaySearchBySurnameDialog() {
		if (isSomeoneToDisplay())
			new SearchBySurnameDialog(EmployeeDetails.this);
	}// end displaySearchBySurnameDialog

	// find byte start in file for first active record
	private void firstRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for first record
			currentByteStart = application.getFirst();
			// assign current Employee to first record in file
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if first record is inactive look for next record
			if (currentEmployee.getEmployeeId() == 0)
				nextRecord();// look for next record
		} // end if
	}// end firstRecord

	// find byte start in file for previous active record
	private void previousRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for previous record
			currentByteStart = application.getPrevious(currentByteStart);
			// assign current Employee to previous record in file
			currentEmployee = application.readRecords(currentByteStart);
			// loop to previous record until Employee is active - ID is not 0
			while (currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for previous record
				currentByteStart = application.getPrevious(currentByteStart);
				// assign current Employee to previous record in file
				currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		}
	}// end previousRecord

	// find byte start in file for next active record
	private void nextRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for next record
			currentByteStart = application.getNext(currentByteStart);
			// assign current Employee to record in file
			currentEmployee = application.readRecords(currentByteStart);
			// loop to previous next until Employee is active - ID is not 0
			while (currentEmployee.getEmployeeId() == 0) {
				// get byte start in file for next record
				currentByteStart = application.getNext(currentByteStart);
				// assign current Employee to next record in file
				currentEmployee = application.readRecords(currentByteStart);
			} // end while
			application.closeReadFile();// close file for reading
		} // end if
	}// end nextRecord

	// find byte start in file for last active record
	private void lastRecord() {
		// if any active record in file look for first record
		if (isSomeoneToDisplay()) {
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			// get byte start in file for last record
			currentByteStart = application.getLast();
			// assign current Employee to first record in file
			currentEmployee = application.readRecords(currentByteStart);
			application.closeReadFile();// close file for reading
			// if last record is inactive look for previous record
			if (currentEmployee.getEmployeeId() == 0)
				previousRecord();// look for previous record
		} // end if
	}// end lastRecord

	public void searchEmployeeById() {
	    if (!isSomeoneToDisplay()) {
	        return; // If there are no records to display, exit the method
	    }

	    String searchId = searchByIdField.getText().trim();
	    if (searchId.isEmpty()) {
	        JOptionPane.showMessageDialog(null, "Please enter an ID to search.");
	        return;
	    }

	    try {
	        int idToSearch = Integer.parseInt(searchId);
	        boolean found = false;
	        Employee originalEmployee = currentEmployee;

	        firstRecord();
	        while (currentEmployee != null) {
	            if (currentEmployee.getEmployeeId() == idToSearch) {
	                displayRecords(currentEmployee);
	                found = true;
	                break;
	            }
	            nextRecord();
	        }

	        if (!found) {
	            JOptionPane.showMessageDialog(null, "Employee not found!");
	        }
	    } catch (NumberFormatException e) {
	        searchByIdField.setBackground(new Color(255, 150, 150));
	        JOptionPane.showMessageDialog(null, "Wrong ID format!");
	    } finally {
	        searchByIdField.setBackground(Color.WHITE);
	        searchByIdField.setText("");
	    }
	}


	private Employee getChangedDetails() {
	    boolean fullTime = fullTimeCombo.getSelectedItem().equals("Yes");
	    int employeeId = Integer.parseInt(idField.getText());
	    String ppsNumber = ppsField.getText().toUpperCase();
	    String surname = surnameField.getText().toUpperCase();
	    String firstName = firstNameField.getText().toUpperCase();
	    char gender = genderCombo.getSelectedItem().toString().charAt(0);
	    String department = departmentCombo.getSelectedItem().toString();
	    double salary = Double.parseDouble(salaryField.getText());

	    return new Employee(employeeId, ppsNumber, surname, firstName, gender, department, salary, fullTime);
	}


	// add Employee object to fail
	public void addRecord(Employee newEmployee) {
		// open file for writing
		application.openWriteFile(file.getAbsolutePath());
		// write into a file
		currentByteStart = application.addRecords(newEmployee);
		application.closeWriteFile();// close file for writing
	}// end addRecord

	// delete (make inactive - empty) record from file
	private void deleteRecord() {
		if (isSomeoneToDisplay()) {// if any active record in file display
									// message and delete record
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to delete record?", "Delete",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if answer yes delete (make inactive - empty) record
			if (returnVal == JOptionPane.YES_OPTION) {
				// open file for writing
				application.openWriteFile(file.getAbsolutePath());
				// delete (make inactive - empty) record in file proper position
				application.deleteRecords(currentByteStart);
				application.closeWriteFile();// close file for writing
				// if any active record in file display next record
				if (isSomeoneToDisplay()) {
					nextRecord();// look for next record
					displayRecords(currentEmployee);
				} // end if
			} // end if
		} // end if
	}// end deleteDecord

	// create vector of vectors with all Employee details
	private Vector<Object> getAllEmloyees() {
		// vector of Employee objects
		Vector<Object> allEmployee = new Vector<Object>();
		Vector<Object> empDetails;// vector of each employee details
		long byteStart = currentByteStart;
		int firstId;

		firstRecord();// look for first record
		firstId = currentEmployee.getEmployeeId();
		// loop until all Employees are added to vector
		do {
			empDetails = new Vector<Object>();
			empDetails.addElement(new Integer(currentEmployee.getEmployeeId()));
			empDetails.addElement(currentEmployee.getPps());
			empDetails.addElement(currentEmployee.getSurname());
			empDetails.addElement(currentEmployee.getFirstName());
			empDetails.addElement(new Character(currentEmployee.getGender()));
			empDetails.addElement(currentEmployee.getDepartment());
			empDetails.addElement(new Double(currentEmployee.getSalary()));
			empDetails.addElement(new Boolean(currentEmployee.getFullTime()));

			allEmployee.addElement(empDetails);
			nextRecord();// look for next record
		} while (firstId != currentEmployee.getEmployeeId());// end do - while
		currentByteStart = byteStart;

		return allEmployee;
	}// end getAllEmployees

	// activate field for editing
	private void editDetails() {
		// activate field for editing if there is records to display
		if (isSomeoneToDisplay()) {
			// remove euro sign from salary text field
			salaryField.setText(fieldFormat.format(currentEmployee.getSalary()));
			change = false;
			setEnabled(true);// enable text fields for editing
		} // end if
	}// end editDetails

	// ignore changes and set text field unenabled
	private void cancelChange() {
		setEnabled(false);
		displayRecords(currentEmployee);
	}// end cancelChange

	// check if any of records in file is active - ID is not 0
	private boolean isSomeoneToDisplay() {
		boolean someoneToDisplay = false;
		// open file for reading
		application.openReadFile(file.getAbsolutePath());
		// check if any of records in file is active - ID is not 0
		someoneToDisplay = application.isSomeoneToDisplay();
		application.closeReadFile();// close file for reading
		// if no records found clear all text fields and display message
		if (!someoneToDisplay) {
			currentEmployee = null;
			idField.setText("");
			ppsField.setText("");
			surnameField.setText("");
			firstNameField.setText("");
			salaryField.setText("");
			genderCombo.setSelectedIndex(0);
			departmentCombo.setSelectedIndex(0);
			fullTimeCombo.setSelectedIndex(0);
			JOptionPane.showMessageDialog(null, "No Employees registered!");
		}
		return someoneToDisplay;
	}// end isSomeoneToDisplay

	// check for correct PPS format and look if PPS already in use
	public boolean correctPps(String pps, long currentByte) {
		boolean ppsExist = false;
		// check for correct PPS format based on assignment description
		if (pps.length() == 8 || pps.length() == 9) {
			if (Character.isDigit(pps.charAt(0)) && Character.isDigit(pps.charAt(1))
					&& Character.isDigit(pps.charAt(2))	&& Character.isDigit(pps.charAt(3)) 
					&& Character.isDigit(pps.charAt(4))	&& Character.isDigit(pps.charAt(5)) 
					&& Character.isDigit(pps.charAt(6))	&& Character.isLetter(pps.charAt(7))
					&& (pps.length() == 8 || Character.isLetter(pps.charAt(8)))) {
				// open file for reading
				application.openReadFile(file.getAbsolutePath());
				// look in file is PPS already in use
				ppsExist = application.isPpsExist(pps, currentByte);
				application.closeReadFile();// close file for reading
			} // end if
			else
				ppsExist = true;
		} // end if
		else
			ppsExist = true;

		return ppsExist;
	}// end correctPPS

	// check if file name has extension .dat
	private boolean checkFileName(File fileName) {
		boolean checkFile = false;
		int length = fileName.toString().length();

		// check if last characters in file name is .dat
		if (fileName.toString().charAt(length - 4) == '.' && fileName.toString().charAt(length - 3) == 'd'
				&& fileName.toString().charAt(length - 2) == 'a' && fileName.toString().charAt(length - 1) == 't')
			checkFile = true;
		return checkFile;
	}// end checkFileName

	// check if any changes text field where made
	private boolean checkForChanges() {
		boolean anyChanges = false;
		// if changes where made, allow user to save there changes
		if (change) {
			saveChanges();// save changes
			anyChanges = true;
		} // end if
			// if no changes made, set text fields as unenabled and display
			// current Employee
		else {
			setEnabled(false);
			displayRecords(currentEmployee);
		} // end else

		return anyChanges;
	}// end checkForChanges

	// check for input in text fields
	private boolean checkInput() {
		boolean valid = true;
		// if any of inputs are in wrong format, colour text field and display
		// message
		if (ppsField.isEditable() && ppsField.getText().trim().isEmpty()) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (ppsField.isEditable() && correctPps(ppsField.getText().trim(), currentByteStart)) {
			ppsField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (surnameField.isEditable() && surnameField.getText().trim().isEmpty()) {
			surnameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (firstNameField.isEditable() && firstNameField.getText().trim().isEmpty()) {
			firstNameField.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (genderCombo.getSelectedIndex() == 0 && genderCombo.isEnabled()) {
			genderCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		if (departmentCombo.getSelectedIndex() == 0 && departmentCombo.isEnabled()) {
			departmentCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
		try {// try to get values from text field
			Double.parseDouble(salaryField.getText());
			// check if salary is greater than 0
			if (Double.parseDouble(salaryField.getText()) < 0) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end try
		catch (NumberFormatException num) {
			if (salaryField.isEditable()) {
				salaryField.setBackground(new Color(255, 150, 150));
				valid = false;
			} // end if
		} // end catch
		if (fullTimeCombo.getSelectedIndex() == 0 && fullTimeCombo.isEnabled()) {
			fullTimeCombo.setBackground(new Color(255, 150, 150));
			valid = false;
		} // end if
			// display message if any input or format is wrong
		if (!valid)
			JOptionPane.showMessageDialog(null, "Wrong values or format! Please check!");
		// set text field to white colour if text fields are editable
		if (ppsField.isEditable())
			setToWhite(); 

		return valid;
	}

	// set text field background colour to white
	private void setToWhite() {
		ppsField.setBackground(UIManager.getColor("TextField.background"));
		surnameField.setBackground(UIManager.getColor("TextField.background"));
		firstNameField.setBackground(UIManager.getColor("TextField.background"));
		salaryField.setBackground(UIManager.getColor("TextField.background"));
		genderCombo.setBackground(UIManager.getColor("TextField.background"));
		departmentCombo.setBackground(UIManager.getColor("TextField.background"));
		fullTimeCombo.setBackground(UIManager.getColor("TextField.background"));
	}// end setToWhite

	// enable text fields for editing
	public void setEnabled(boolean booleanValue) {
		boolean search;
		if (booleanValue)
			search = false;
		else
			search = true;
		ppsField.setEditable(booleanValue);
		surnameField.setEditable(booleanValue);
		firstNameField.setEditable(booleanValue);
		genderCombo.setEnabled(booleanValue);
		departmentCombo.setEnabled(booleanValue);
		salaryField.setEditable(booleanValue);
		fullTimeCombo.setEnabled(booleanValue);
		saveChange.setVisible(booleanValue);
		cancelChange.setVisible(booleanValue);
		searchByIdField.setEnabled(search);
		searchBySurnameField.setEnabled(search);
		searchId.setEnabled(search);
		searchSurname.setEnabled(search);
	}// end setEnabled

	// open file
	private void openFile() {
		final JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Open");
		// display files in File Chooser only with extension .dat
		fc.setFileFilter(datfilter);
		File newFile; // holds opened file name and path
		// if old file is not empty or changes has been made, offer user to save
		// old file
		if (file.length() != 0 || change) {
			int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
					JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
			// if user wants to save file, save it
			if (returnVal == JOptionPane.YES_OPTION) {
				saveFile();// save file
			} // end if
		} // end if

		int returnVal = fc.showOpenDialog(EmployeeDetails.this);
		// if file been chosen, open it
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			newFile = fc.getSelectedFile();
			// if old file wasn't saved and its name is generated file name,
			// delete this file
			if (file.getName().equals(generatedFileName))
				file.delete();// delete file
			file = newFile;// assign opened file to file
			// open file for reading
			application.openReadFile(file.getAbsolutePath());
			firstRecord();// look for first record
			displayRecords(currentEmployee);
			application.closeReadFile();// close file for reading
		} // end if
	}// end openFile

	// save file
	private void saveFile() {
		// if file name is generated file name, save file as 'save as' else save
		// changes to file
		if (file.getName().equals(generatedFileName))
			saveFileAs();// save file as 'save as'
		else {
			// if changes has been made to text field offer user to save these
			// changes
			if (change) {
				int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
						JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
				// save changes if user choose this option
				if (returnVal == JOptionPane.YES_OPTION) {
					// save changes if ID field is not empty
					if (!idField.getText().equals("")) {
						// open file for writing
						application.openWriteFile(file.getAbsolutePath());
						// get changes for current Employee
						currentEmployee = getChangedDetails();
						// write changes to file for corresponding Employee
						// record
						application.changeRecords(currentEmployee, currentByteStart);
						application.closeWriteFile();// close file for writing
					} // end if
				} // end if
			} // end if

			displayRecords(currentEmployee);
			setEnabled(false);
		} // end else
	}// end saveFile

	// save changes to current Employee
	private void saveChanges() {
		int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes to current Employee?", "Save",
				JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
		// if user choose to save changes, save changes
		if (returnVal == JOptionPane.YES_OPTION) {
			// open file for writing
			application.openWriteFile(file.getAbsolutePath());
			// get changes for current Employee
			currentEmployee = getChangedDetails();
			// write changes to file for corresponding Employee record
			application.changeRecords(currentEmployee, currentByteStart);
			application.closeWriteFile();// close file for writing
			changesMade = false;// state that all changes has bee saved
		} // end if
		displayRecords(currentEmployee);
		setEnabled(false);
	}// end saveChanges

	// save file as 'save as'
	private void saveFileAs() {
	    final JFileChooser fc = new JFileChooser();
	    fc.setDialogTitle("Save As");
	    fc.setFileFilter(datfilter);
	    fc.setApproveButtonText("Save");
	    fc.setSelectedFile(new File("new_Employee.dat"));

	    int returnVal = fc.showSaveDialog(EmployeeDetails.this);
	    if (returnVal == JFileChooser.APPROVE_OPTION) {
	        File newFile = fc.getSelectedFile();
	        if (!checkFileName(newFile)) {
	            newFile = new File(newFile.getAbsolutePath() + ".dat");
	            application.createFile(newFile.getAbsolutePath());
	        } else {
	            application.createFile(newFile.getAbsolutePath());
	        }

	        try {
	            Files.copy(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	            if (file.getName().equals(generatedFileName)) {
	                file.delete();
	            }
	            file = newFile;
	        } catch (IOException e) {
	            // Handle IOException, if needed
	            e.printStackTrace();
	        }
	    }
	    changesMade = false;
	}


	// allow to save changes to file when exiting the application
	private void exitApp() {
	    if (changesMade || file.getName().equals(generatedFileName)) {
	        int returnVal = JOptionPane.showOptionDialog(frame, "Do you want to save changes?", "Save",
	                JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, null, null, null);
	        if (returnVal == JOptionPane.YES_OPTION) {
	            saveFile();
	        } else if (returnVal == JOptionPane.CANCEL_OPTION) {
	            return;
	        }
	    }
	    if (file.getName().equals(generatedFileName)) {
	        file.delete();
	    }
	    System.exit(0);
	}


	// generate 20 character long file name
	private String getFileName() {
		String fileNameChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890_-";
		StringBuilder fileName = new StringBuilder();
		Random rnd = new Random();
		// loop until 20 character long file name is generated
		while (fileName.length() < 20) {
			int index = (int) (rnd.nextFloat() * fileNameChars.length());
			fileName.append(fileNameChars.charAt(index));
		}
		String generatedfileName = fileName.toString();
		return generatedfileName;
	}// end getFileName

	// create file with generated file name when application is opened
	private void createRandomFile() {
		generatedFileName = getFileName() + ".dat";
		// assign generated file name to file
		file = new File(generatedFileName);
		// create file
		application.createFile(file.getName());
	}// end createRandomFile

	// action listener for buttons, text field and menu items
	public void actionPerformed(ActionEvent e) {

		if (e.getSource() == closeApp) {
			if (checkInput() && !checkForChanges())
				exitApp();
		} else if (e.getSource() == open) {
			if (checkInput() && !checkForChanges())
				openFile();
		} else if (e.getSource() == save) {
			if (checkInput() && !checkForChanges())
				saveFile();
			change = false;
		} else if (e.getSource() == saveAs) {
			if (checkInput() && !checkForChanges())
				saveFileAs();
			change = false;
		} else if (e.getSource() == searchById) {
			if (checkInput() && !checkForChanges())
				displaySearchByIdDialog();
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges())
				displaySearchBySurnameDialog();
		} else if (e.getSource() == searchId || e.getSource() == searchByIdField)
			searchEmployeeById();
		else if (e.getSource() == searchSurname || e.getSource() == searchBySurnameField)
			searchEmployeeById();
		else if (e.getSource() == saveChange) {
			if (checkInput() && !checkForChanges())
				;
		} else if (e.getSource() == cancelChange)
			cancelChange();
		else if (e.getSource() == firstItem || e.getSource() == first) {
			if (checkInput() && !checkForChanges()) {
				firstRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == prevItem || e.getSource() == previous) {
			if (checkInput() && !checkForChanges()) {
				previousRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == nextItem || e.getSource() == next) {
			if (checkInput() && !checkForChanges()) {
				nextRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == lastItem || e.getSource() == last) {
			if (checkInput() && !checkForChanges()) {
				lastRecord();
				displayRecords(currentEmployee);
			}
		} else if (e.getSource() == listAll || e.getSource() == displayAll) {
			if (checkInput() && !checkForChanges())
				if (isSomeoneToDisplay())
					displayEmployeeSummaryDialog();
		} else if (e.getSource() == create || e.getSource() == add) {
			if (checkInput() && !checkForChanges())
				new AddRecordDialog(EmployeeDetails.this);
		} else if (e.getSource() == modify || e.getSource() == edit) {
			if (checkInput() && !checkForChanges())
				editDetails();
		} else if (e.getSource() == delete || e.getSource() == deleteButton) {
			if (checkInput() && !checkForChanges())
				deleteRecord();
		} else if (e.getSource() == searchBySurname) {
			if (checkInput() && !checkForChanges())
				new SearchBySurnameDialog(EmployeeDetails.this);
		}
	}// end actionPerformed

	// content pane for main dialog
	private void createContentPane() {
		setTitle("Employee Details");
		createRandomFile();// create random file name
		JPanel dialog = new JPanel(new MigLayout());

		setJMenuBar(menuBar());// add menu bar to frame
		// add search panel to frame
		dialog.add(searchPanel(), "width 400:400:400, growx, pushx");
		// add navigation panel to frame
		dialog.add(navigPanel(), "width 150:150:150, wrap");
		// add button panel to frame
		dialog.add(buttonPanel(), "growx, pushx, span 2,wrap");
		// add details panel to frame
		dialog.add(detailsPanel(), "gap top 30, gap left 150, center");

		JScrollPane scrollPane = new JScrollPane(dialog);
		getContentPane().add(scrollPane, BorderLayout.CENTER);
		addWindowListener(this);
	}// end createContentPane

	// create and show main dialog
	private static void createAndShowGUI() {

		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.createContentPane();// add content pane to frame
		frame.setSize(760, 600);
		frame.setLocation(250, 200);
		frame.setVisible(true);
	}// end createAndShowGUI

	// main method
	public static void main(String args[]) {
		javax.swing.SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				createAndShowGUI();
			}
		});
	}// end main

	// DocumentListener methods
	public void changedUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void insertUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	public void removeUpdate(DocumentEvent d) {
		change = true;
		new JTextFieldLimit(20);
	}

	// ItemListener method
	public void itemStateChanged(ItemEvent e) {
		change = true;
	}

	// WindowsListener methods
	public void windowClosing(WindowEvent e) {
		// exit application
		exitApp();
	}

	public void windowActivated(WindowEvent e) {
	}

	public void windowClosed(WindowEvent e) {
	}

	public void windowDeactivated(WindowEvent e) {
	}

	public void windowDeiconified(WindowEvent e) {
	}

	public void windowIconified(WindowEvent e) {
	}

	public void windowOpened(WindowEvent e) {
	}

	public int getNextFreeId() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void searchEmployeeBySurname() {
		// TODO Auto-generated method stub
		
	}
}// end class EmployeeDetails
