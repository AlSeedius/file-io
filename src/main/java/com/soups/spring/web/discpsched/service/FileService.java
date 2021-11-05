package com.soups.spring.web.discpsched.service;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.RduRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.FileStorageException;
import com.soups.spring.web.discpsched.entitie.Calendar;
import com.soups.spring.web.discpsched.entitie.Person;
import com.soups.spring.web.discpsched.entitie.Rdu;
import com.soups.spring.web.discpsched.entitie.Schedule;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private RduRepository rduRepository;


    public FileService(PersonRepository personRepository,
                       CalendarRepository dateRepository,
                       ScheduleRepository scheduleRepository) {
        this.personRepository = personRepository;
        this.calendarRepository = dateRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<Integer> newMonths;
    public List<ArrayList<String>> changesInSchedule;
    public Rdu nRdu;


    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    public void uploadFile(MultipartFile file, Integer type) {
        try {
        //    String path = new String(StringUtils.cleanPath(file.getOriginalFilename()).getBytes(),UTF_8);
            newMonths = new ArrayList<>();
            changesInSchedule = new ArrayList<>();
         //   Path copyLocation = Paths.get(uploadDir + File.separator + path);
         //   Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            startParsing(file, type);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

    private void startParsing(MultipartFile reapExcelDataFile, Integer type) throws IOException {
        String fileName = reapExcelDataFile.getOriginalFilename();
        String expansion = fileName.substring(fileName.indexOf("."));
        if (expansion.equals(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
            String sName = workbook.getSheetAt(0).getSheetName();
            if (sName.equals("Новый год")) {
                for (int i = 1; i <= 12; i++) {
                    Integer n = parseNamesODUCDiop(workbook.getSheetAt(i));
                    parseScheduleODUCDiop(workbook.getSheetAt(i), n);
                }
            }
            else {
                Integer n = parseNamesRSP(workbook.getSheetAt(0), type);
                parseScheduleRSP(workbook.getSheetAt(0), n, type);
            }
        }
        else {
            HSSFWorkbook workbook = new HSSFWorkbook(reapExcelDataFile.getInputStream());
            String sName = workbook.getSheetAt(0).getSheetName();
            if (sName.equals("Новый год")) {
                for (int i = 1; i <= 12; i++) {
                    Integer n = parseNamesODUCDiop(workbook.getSheetAt(i));
                    parseScheduleODUCDiop(workbook.getSheetAt(i), n);
                }
            }
            else if (sName.equals("Лист1")){
                for (int i=1; i<=workbook.getNumberOfSheets()-1; i++){
                    Integer n = parseNamesODUCSopr(workbook.getSheetAt(i));
                    parseScheduleODUCSopr(workbook.getSheetAt(i),n);
                }
            }
            else {
                int i = workbook.getNumberOfSheets() - 1;
                Integer n = parseNamesLPC(workbook.getSheetAt(i));
                parseScheduleLPC(workbook.getSheetAt(i), n);
            }
        }
    }

    private Integer parseNamesRSP(XSSFSheet worksheet, Integer type) {
        int k = 0;
        for (int i = 3; i < worksheet.getPhysicalNumberOfRows(); i++) {
            XSSFRow row = worksheet.getRow(i);
            if (row.getCell(3) != null)
                if (!row.getCell(3).getStringCellValue().isEmpty()) {
                    String textOfARow = row.getCell(3).getStringCellValue();
                    if (textOfARow.length() > 0) {
                        Person tempPerson = new Person();
                        int length = textOfARow.indexOf('.');
                        tempPerson.setLastName(textOfARow.substring(0, length - 2).trim());
                        tempPerson.setFirstName(textOfARow.substring(length - 1, length).trim());
                        tempPerson.setSecondName(textOfARow.substring(length + 1, length + 2).trim());
                        tempPerson.setRduId(type);
                        k++;
                        if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), type,
                                tempPerson.getFirstName(), tempPerson.getSecondName()) == null)
                            if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                personRepository.save(tempPerson);
                    }
                }
        }
        return k;
    }
    private Integer parseNamesODUCDiop(HSSFSheet worksheet) {
        int k = 0;
        for (int i = 9; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            HSSFRow row = worksheet.getRow(i);
            if (row.getCell(1) != null && !row.getCell(1).toString().isEmpty()) {
                String nameCell = row.getCell(2).getStringCellValue();
                if (nameCell.length() > 0) {
                    Person tempPerson = new Person();
                    int length = nameCell.indexOf('.');
                    if (length > 0) {
                        tempPerson.setLastName(nameCell.substring(0, length - 2).trim());
                        tempPerson.setFirstName(nameCell.substring(length - 1, length).trim());
                        if (nameCell.substring(length + 1, length + 2).equals(" "))
                            tempPerson.setSecondName(nameCell.substring(length + 2, length + 3).trim());
                        else
                            tempPerson.setSecondName(nameCell.substring(length + 1, length + 2).trim());
                        k++;
                        tempPerson.setRduId(3);
                        if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 3,
                                tempPerson.getFirstName(), tempPerson.getSecondName()) == null)
                            if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                personRepository.save(tempPerson);
                    }
                }
            }
            else
                break;
        }
        return k;
    }
    private Integer parseNamesODUCDiop(XSSFSheet worksheet) {
        int k = 0;
        for (int i = 9; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            XSSFRow row = worksheet.getRow(i);
            if (row.getCell(1) != null && !row.getCell(1).toString().isEmpty()) {
                String nameCell = row.getCell(2).getStringCellValue();
                if (nameCell.length() > 0) {
                    Person tempPerson = new Person();
                    int length = nameCell.indexOf('.');
                    if (length > 0) {
                        tempPerson.setLastName(nameCell.substring(0, length - 2).trim());
                        tempPerson.setFirstName(nameCell.substring(length - 1, length).trim());
                        if (nameCell.substring(length + 1, length + 2).equals(" "))
                            tempPerson.setSecondName(nameCell.substring(length + 2, length + 3).trim());
                        else
                            tempPerson.setSecondName(nameCell.substring(length + 1, length + 2).trim());
                        k++;
                        tempPerson.setRduId(3);
                        if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 3,
                                tempPerson.getFirstName(), tempPerson.getSecondName()) == null)
                            if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                personRepository.save(tempPerson);
                    }
                }
            }
            else
                break;
        }
        return k;
    }
    private Integer parseNamesODUCSopr(HSSFSheet worksheet) {
        int k = 0;
        for (int i = 24; i < worksheet.getPhysicalNumberOfRows() - 1; i++) {
            HSSFRow row = worksheet.getRow(i);
            if (row.getCell(1) != null && !row.getCell(1).toString().isEmpty()) {
                String nameCell = row.getCell(1).getStringCellValue();
                if (nameCell.length() > 0) {
                    Person tempPerson = new Person();
                    int length = nameCell.indexOf('.');
                    if (length > 0) {
                        tempPerson.setLastName(nameCell.substring(0, length - 2).trim());
                        tempPerson.setFirstName(nameCell.substring(length - 1, length).trim());
                        if (nameCell.substring(length + 1, length + 2).equals(" "))
                            tempPerson.setSecondName(nameCell.substring(length + 2, length + 3).trim());
                        else
                            tempPerson.setSecondName(nameCell.substring(length + 1, length + 2).trim());
                        k++;
                        tempPerson.setRduId(4);
                        if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 4,
                                tempPerson.getFirstName(), tempPerson.getSecondName()) == null)
                            if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                personRepository.save(tempPerson);
                    }
                }
            }
            else
                break;
        }
        return k;
    }
    private Integer parseNamesLPC(HSSFSheet worksheet) {
        int k = 0;
        for (int i = 5; i < worksheet.getPhysicalNumberOfRows(); i = i + 2) {
            HSSFRow row = worksheet.getRow(i);
            if (row.getCell(2) != null)
                if (!row.getCell(2).getStringCellValue().isEmpty()) {
                    String textOfARow = row.getCell(2).getStringCellValue();
                    if (textOfARow.length() > 0) {
                        Person tempPerson = new Person();
                        int length = textOfARow.indexOf('.');
                        if (length > 0) {
                            tempPerson.setLastName(textOfARow.substring(0, length - 2).trim());
                            tempPerson.setFirstName(textOfARow.substring(length - 1, length).trim());
                            if (textOfARow.substring(length + 1, length + 2).equals(" "))
                                tempPerson.setSecondName(textOfARow.substring(length + 2, length + 3).trim());
                            else
                                tempPerson.setSecondName(textOfARow.substring(length + 1, length + 2).trim());
                            k++;
                            tempPerson.setRduId(2);
                            if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 2,
                                    tempPerson.getFirstName(), tempPerson.getSecondName()) == null)
                                if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                    personRepository.save(tempPerson);
                        }
                    }
                }
        }
        return k;
    }

    private void parseScheduleRSP(XSSFSheet worksheet, Integer n, Integer RDUn) {
        String header = "";
        int c = 0;
        while (header.isEmpty()) {
            c = worksheet.getRow(0).getFirstCellNum();
            header = worksheet.getRow(0).getCell(c).toString();
            c++;
        }
        Integer monthNumber = monthNumber(header);
        Integer yearNumber = yearNumber(header);
        int z = 0;
        for (Cell cell : worksheet.getRow(2)) {
            if (cell.getStringCellValue().trim().equals("Факт")) {
                z = cell.getColumnIndex();
                break;
            }
        }
        int k = 0;
        int day = 0;
        for (int i = 4; i < z; i++) {
            if (worksheet.getRow(2).getCell(i).getStringCellValue().length() < 1) {
                k++;
            } else {
                day++;
                for (int j = 3; j < 3 + n; j++) {
                    XSSFRow row = worksheet.getRow(j);
                    String readName = row.getCell(3).getStringCellValue();
                    String type = "0";
                    if (row.getCell(i) == null)
                        type = "0";
                    else {
                        String cellValue = row.getCell(i).toString().trim();
                        if (cellValue.length() == 0)
                            type = "0";
                        else if (cellValue.startsWith("Д")) {
                            type = "1";
                        } else if (cellValue.startsWith("Н") || cellValue.startsWith("4Н")) {
                            type = "2";
                        } else if (cellValue.equals("О")) {
                            type = "О";
                        } else if (cellValue.equals("8") || cellValue.equals("8.0")) {
                            type = "8";
                        } else if (cellValue.equals("04:00")) {
                            type = "4";
                        } else if (cellValue.equals("К")) {
                            type = "К";
                        } else if (cellValue.equals("Б")) {
                            type = "Б";
                        }
                    }
                    correctSchedule(monthNumber, yearNumber, day, readName, type, RDUn);
                }
            }
        }
    }

    private void parseScheduleODUCDiop(XSSFSheet worksheet, int n) {
        String header = worksheet.getRow(4).getCell(14).toString();
        int monthNumber = monthNumber(header);
        int yearNumber = yearNumber(header);
        int dayCount = dayCount(monthNumber, yearNumber);
        for (int i = 9; i < dayCount + 9; i++) {
            for (int j = 9; j < 9 + n; j++) {
                XSSFRow row = worksheet.getRow(j);
                String readName = row.getCell(2).getStringCellValue();
                String type = "0";
                if (row.getCell(i) == null || row.getCell(i).toString().isEmpty())
                    type = "0";
                else {
                    String val = row.getCell(i).toString().substring(0,1);
                    switch (val){
                        case("Д"):
                            type="1";
                            break;
                        case("Н"):
                            type="2";
                            break;
                        case("8"):
                        case("7"):
                            type="8";
                            break;
                        case("4"):
                            type="4";
                            break;
                        case("О"):
                            type="О";
                            break;
                    }
                }
                correctSchedule(monthNumber, yearNumber, i-8, readName, type, 3);
            }
        }
    }
    private void parseScheduleODUCDiop(HSSFSheet worksheet, int n) {
        String header = worksheet.getRow(4).getCell(14).toString();
        int monthNumber = monthNumber(header);
        int yearNumber = yearNumber(header);
        int dayCount = dayCount(monthNumber, yearNumber);
        for (int i = 9; i < dayCount + 9; i++) {
            for (int j = 9; j < 9 + n; j++) {
                HSSFRow row = worksheet.getRow(j);
                String readName = row.getCell(2).getStringCellValue();
                String type = "0";
                if (row.getCell(i) == null || row.getCell(i).toString().isEmpty())
                    type = "0";
                else {
                    String val = row.getCell(i).toString().substring(0,1);
                    switch (val){
                        case("Д"):
                            type="1";
                            break;
                        case("Н"):
                            type="2";
                            break;
                        case("8"):
                        case("7"):
                        case("4"):
                            type="8";
                            break;
                        case("О"):
                            type="О";
                            break;
                    }
                }
                correctSchedule(monthNumber, yearNumber, i-8, readName, type, 3);
            }
        }
    }
    private void parseScheduleODUCSopr(HSSFSheet worksheet, int n) {
        HSSFRow headerRow = worksheet.getRow(21);
        String header = headerRow.getCell(12).toString()+" "+headerRow.getCell(13).toString()+" "+headerRow.getCell(16).toString();
        int monthNumber = monthNumber(header);
        int yearNumber = yearNumber(header);
        int dayCount = dayCount(monthNumber, yearNumber);
        for (int i = 3; i < dayCount + 3; i++) {
            for (int j = 24; j < 24 + n; j++) {
                HSSFRow row = worksheet.getRow(j);
                String readName = row.getCell(1).getStringCellValue();
                String type = "0";
                if (row.getCell(i) == null || row.getCell(i).toString().isEmpty())
                    type = "0";
                else {
                    String val = row.getCell(i).toString().substring(0,1);
                    switch (val){
                        case("Д"):
                            type="1";
                            break;
                        case("Н"):
                            type="2";
                            break;
                        case("8"):
                        case("7"):
                        case("4"):
                            type="8";
                            break;
                        case("О"):
                            type="О";
                            break;
                    }
                }
                correctSchedule(monthNumber, yearNumber, i-2, readName, type, 4);
            }
        }
    }
    private void parseScheduleLPC(HSSFSheet worksheet, int n) {
        String header = worksheet.getRow(2).getCell(1).toString();
        int monthNumber = monthNumber(header);
        int yearNumber = yearNumber(header);
        int dayCount = dayCount(monthNumber, yearNumber);
        for (int i = 3; i < dayCount + 3; i++) {
            for (int j = 5; j < 5 + 2 * n; j = j + 2) {
                HSSFRow row = worksheet.getRow(j);
                String readName = row.getCell(2).getStringCellValue();
                String type = "0";
                if (row.getCell(i) == null)
                    type = "0";
                else {
                    int z = (int)row.getCell(i).getNumericCellValue();
                    if (z==12)
                        type="1";
                    else if (z==4) {
                        short fontIndex = row.getCell(i).getCellStyle().getFontIndex();
                        HSSFFont font = worksheet.getWorkbook().getFontAt(fontIndex);
                        short fontColor = font.getColor();
                        if (fontColor == IndexedColors.BLUE.index)
                            type = "2";
                        else if (fontColor == 32767)
                            type = "4";
                    }
                    else if (z==8){
                        short fontIndex = row.getCell(i).getCellStyle().getFontIndex();
                        HSSFFont font = worksheet.getWorkbook().getFontAt(fontIndex);
                        short fontColor = font.getColor();
                        if (fontColor == 32767)
                            type = "8";
                    }
                }
                correctSchedule(monthNumber, yearNumber, i, readName, type, 2);
            }
        }
    }

    private void correctSchedule(Integer monthNumber, Integer yearNumber, int i, String readName, String type, int i2) {
        LocalDate tempDate;
        Person tempPerson;
        Schedule tempSchedule;
        tempDate = LocalDate.of(yearNumber, monthNumber, i);
        int tempDateId = calendarRepository.findByDay(tempDate).getId()+1;
        int length = readName.indexOf('.');
        String fn = readName.substring(length - 1, length).trim();
        String ln = readName.substring(0, length - 2).trim();
        String sn;
        if (readName.substring(length + 1, length + 2).equals(" "))
            sn = readName.substring(length + 2, length + 3).trim();
        else
            sn=readName.substring(length + 1, length + 2).trim();
        tempPerson = personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(ln,i2,fn,sn);
        nRdu=rduRepository.findById(i2).get();
        if (!scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).isEmpty()) {
            tempSchedule = scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).get(0);
            if (!type.equals("0") & !type.equals(tempSchedule.getType())) {
                tempSchedule.setType(type);
                ArrayList<String> changes = new ArrayList<>();
                changes.add(monthNumber.toString());
                changes.add(tempPerson.getLastName());
                scheduleRepository.save(tempSchedule);
                if (!changesInSchedule.contains(changes))
                    changesInSchedule.add(changes);
            } else if (type.equals("0"))
                scheduleRepository.delete(scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).get(0));
        } else if (!type.equals("0")) {
            if (!newMonths.contains(monthNumber))
                newMonths.add(monthNumber);
            tempSchedule = new Schedule(tempPerson.getId(), tempDateId, type);
            scheduleRepository.save(tempSchedule);
        }
    }

    public void addYear(Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year + 1, 1, 5);
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            Calendar tempDate = new Calendar(date);
            if (calendarRepository.findByDay(date)==null)
                calendarRepository.save(tempDate);
        }
    }
    private Integer monthNumber(String header) {
        header=header.toLowerCase();
        if (header.contains("январь"))
            return  1;
        else if (header.contains("февраль"))
            return 2;
        else if (header.contains("март"))
            return 3;
        else if (header.contains("апрель"))
            return 4;
        else if (header.contains("май"))
            return 5;
        else if (header.contains("июнь"))
            return 6;
        else if (header.contains("июль"))
            return 7;
        else if (header.contains("август"))
            return 8;
        else if (header.contains("сентябрь"))
            return 9;
        else if (header.contains("октябрь"))
            return 10;
        else if (header.contains("ноябрь"))
            return 11;
        else if (header.contains("декабрь"))
            return 12;
        else
            return 0;
    }
    private Integer yearNumber(String header) {
        Pattern pat = Pattern.compile("[-]?[0-9]+(.[0-9]+)?");
        Matcher matcher = pat.matcher(header);
        while (matcher.find()) {
            String value = header.substring(matcher.start(), matcher.end());
            return Integer.parseInt(value);
        }
        return 0;
    }
    private Integer dayCount(Integer month, Integer year) {
        if (month == 1 || month == 3 || month == 5 || month == 7
                || month == 8 || month == 10 || month == 12) {
            return 31;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            return 30;
        } else {
            if (year % 4 == 0)
                return 29;
            else
                return 28;
        }
    }

}
    /*    private Integer parseNamesVRN(HSSFSheet worksheet) {
            int k = 0;
            for (int i = 7; i < worksheet.getPhysicalNumberOfRows(); i = i + 2) {
                HSSFRow row = worksheet.getRow(i);
                if (row.getCell(2) != null)
                    if (!row.getCell(2).getStringCellValue().isEmpty()) {
                        String textOfARow = row.getCell(2).getStringCellValue();
                        if (textOfARow.length() > 0) {
                            Person tempPerson = new Person();
                            int length = textOfARow.indexOf('.');
                            tempPerson.setLastName(textOfARow.substring(0, length - 2).trim());
                            tempPerson.setFirstName(textOfARow.substring(length - 1, length).trim());
                            tempPerson.setSecondName(textOfARow.substring(length + 1, length + 2).trim());
                            tempPerson.setRduId(1);
                            k++;
                            if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 1,
                                    tempPerson.getFirstName(),tempPerson.getSecondName()) == null)
                                if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                    personRepository.save(tempPerson);
                        }
                    }
            }
            return k;
        }

    private int rduN(HSSFWorkbook workbook){
        HSSFSheet worksheet = workbook.getSheetAt(0);
        String header;
        if (worksheet.getRow(3).getCell(3).toString().isEmpty())
            return 2;
        else
            return 1;
    }
    private void parseScheduleVRN(HSSFSheet worksheet, Integer n) {
        String header = worksheet.getRow(3).getCell(3).toString();
        Integer monthNumber = monthNumber(header);
        Integer yearNumber = yearNumber(header);
        Integer dayCount = dayCount(monthNumber, yearNumber);
        for (int i = 3; i < dayCount + 3; i++) {
            for (int j = 7; j < 7 + 2 * n; j = j + 2) {
                HSSFRow row = worksheet.getRow(j);
                String readName = row.getCell(2).getStringCellValue();
                String type = "0";
                if (row.getCell(i) == null)
                    type = "0";
                else {
                    int t = row.getCell(i).getCellType();
                    if (t == 1) {
                        type = row.getCell(i).getStringCellValue();
                        if (type.length() == 0)
                            type = "0";
                    } else if (t == 0)
                        type = String.valueOf((int) row.getCell(i).getNumericCellValue());
//                    else if (t == 3)
//                    {
//                        row.getCell(i).setCellType(1);
//                        type = row.getCell(i).getStringCellValue();
//                    }
                }
                correctSchedule(monthNumber, yearNumber, i, readName, type, 1);
            }
        }
    }*/