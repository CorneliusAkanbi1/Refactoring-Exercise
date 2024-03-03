package Package;

import java.io.IOException;
import java.io.RandomAccessFile;

public class RandomAccessEmployeeRecord extends Employee {
    public static final int RECORD_SIZE = 175; // Size of each RandomAccessEmployeeRecord object
	public static final long SIZE = 0;

    public RandomAccessEmployeeRecord() {
        super();
    }

    public RandomAccessEmployeeRecord(int employeeId, String pps, String surname, String firstName, char gender,
            String department, double salary, boolean fullTime) {
        super(employeeId, pps, surname, firstName, gender, department, salary, fullTime);
    }

    public void read(RandomAccessFile file) throws IOException {
        setEmployeeId(file.readInt());
        setPps(readString(file, 20));
        setSurname(readString(file, 20));
        setFirstName(readString(file, 20));
        setGender(file.readChar());
        setDepartment(readString(file, 20));
        setSalary(file.readDouble());
        setFullTime(file.readBoolean());
    }

    public void write(RandomAccessFile file) throws IOException {
        file.writeInt(getEmployeeId());
        writeString(file, getPps(), 20);
        writeString(file, getSurname(), 20);
        writeString(file, getFirstName(), 20);
        file.writeChar(getGender());
        writeString(file, getDepartment(), 20);
        file.writeDouble(getSalary());
        file.writeBoolean(getFullTime());
    }

    private String readString(RandomAccessFile file, int length) throws IOException {
        byte[] bytes = new byte[length * 2]; // Each char is 2 bytes
        file.readFully(bytes);
        return new String(bytes, "UTF-16").trim();
    }

    private void writeString(RandomAccessFile file, String value, int length) throws IOException {
        StringBuffer buffer = new StringBuffer(value);
        buffer.setLength(length);
        file.writeChars(buffer.toString());
    }
}
