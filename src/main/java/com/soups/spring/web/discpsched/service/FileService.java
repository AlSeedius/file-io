package com.soups.spring.web.discpsched.service;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.soups.spring.web.discpsched.DAO.CalendarRepository;
import com.soups.spring.web.discpsched.DAO.PersonRepository;
import com.soups.spring.web.discpsched.DAO.ScheduleRepository;
import com.soups.spring.web.discpsched.FileStorageException;
import com.soups.spring.web.discpsched.entitie.Calendar;
import com.soups.spring.web.discpsched.entitie.Person;
import com.soups.spring.web.discpsched.entitie.Schedule;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import static java.nio.charset.StandardCharsets.*;

@Service
public class FileService {
    @Autowired
    private PersonRepository personRepository;
    @Autowired
    private CalendarRepository calendarRepository;
    @Autowired
    private ScheduleRepository scheduleRepository;

    public FileService(PersonRepository personRepository,
                       CalendarRepository dateRepository,
                       ScheduleRepository scheduleRepository) {
        this.personRepository = personRepository;
        this.calendarRepository = dateRepository;
        this.scheduleRepository = scheduleRepository;
    }

    public List<Integer> newMonths;
    public List<ArrayList<String>> changesInSchedule;
    public Integer nRdu;


    @Value("${app.upload.dir:${user.home}}")
    public String uploadDir;

    public void uploadFile(MultipartFile file) {
        try {
            String path = new String(StringUtils.cleanPath(file.getOriginalFilename()).getBytes(),UTF_8);
            newMonths = new ArrayList<>();
            changesInSchedule = new ArrayList<>();
            Path copyLocation = Paths
                    .get(uploadDir + File.separator + path);
            Files.copy(file.getInputStream(), copyLocation, StandardCopyOption.REPLACE_EXISTING);
            startParsing(file);
        } catch (Exception e) {
            e.printStackTrace();
            throw new FileStorageException("Could not store file " + file.getOriginalFilename()
                    + ". Please try again!");
        }
    }

    private void startParsing(MultipartFile reapExcelDataFile) throws IOException {
        String fileName = reapExcelDataFile.getOriginalFilename();
        String expansion = fileName.substring(fileName.indexOf("."));
        if (expansion.equals(".xlsx")) {
            XSSFWorkbook workbook = new XSSFWorkbook(reapExcelDataFile.getInputStream());
            Integer n = parseNamesVRN(workbook.getSheetAt(0));
            parseScheduleVRN(workbook.getSheetAt(0), n);
        }
        else {
            HSSFWorkbook workbook = new HSSFWorkbook(reapExcelDataFile.getInputStream());
            int i = workbook.getNumberOfSheets() - 1;
            Integer n = parseNamesLPC(workbook.getSheetAt(i));
            parseScheduleLPC(workbook.getSheetAt(i),n);
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
    }*/
private Integer parseNamesVRN(XSSFSheet worksheet) {
    int k = 0;
    for (int i = 3; i < worksheet.getPhysicalNumberOfRows(); i ++) {
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
                        if (length>0) {
                            tempPerson.setLastName(textOfARow.substring(0, length - 2).trim());
                            tempPerson.setFirstName(textOfARow.substring(length - 1, length).trim());
                            if (textOfARow.substring(length + 1, length + 2).equals(" "))
                                tempPerson.setSecondName(textOfARow.substring(length + 2, length + 3).trim());
                            else
                                tempPerson.setSecondName(textOfARow.substring(length + 1, length + 2).trim());
                            k++;
                            tempPerson.setRduId(2);
                            if (personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(tempPerson.getLastName(), 2,
                                    tempPerson.getFirstName(),tempPerson.getSecondName()) == null)
                                if (!tempPerson.getLastName().isEmpty() | !tempPerson.getFirstName().isEmpty() | !tempPerson.getSecondName().isEmpty())
                                    personRepository.save(tempPerson);
                        }
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

/*    private void parseScheduleVRN(HSSFSheet worksheet, Integer n) {
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
private void parseScheduleVRN(XSSFSheet worksheet, Integer n) {
    String header = worksheet.getRow(0).getCell(5).toString();
    Integer monthNumber = monthNumber(header);
    Integer yearNumber = yearNumber(header);
    Integer dayCount = dayCount(monthNumber, yearNumber);
    for (int i = 4; i < dayCount + 3; i++) {
        for (int j = 3; j < 3+n; j ++) {
            XSSFRow row = worksheet.getRow(j);
            String readName = row.getCell(3).getStringCellValue();
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
                else if (t == 3) {
                    String cellValue = row.getCell(i).getStringCellValue();
                    if (cellValue.equals("Д")) {
                        type = "1";
                    } else if (cellValue.equals("Н") || cellValue.equals("4Н")) {
                        type = "2";
                    }
                    else if (cellValue.equals("О"))
                        type = "О";
                }
                else type="0";
/*                    row.getCell(i).getStringCellValue();
                    type = row.getCell(i).getStringCellValue();*/
                }
            correctSchedule(monthNumber, yearNumber, i, readName, type, 1);
            }
        }
    }
    private void parseScheduleLPC(HSSFSheet worksheet, Integer n) {
        String header = worksheet.getRow(2).getCell(1).toString();
        Integer monthNumber = monthNumber(header);
        Integer yearNumber = yearNumber(header);
        Integer dayCount = dayCount(monthNumber, yearNumber);
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
        tempDate = LocalDate.of(yearNumber, monthNumber, i - 2);
        int tempDateId = calendarRepository.findByDay(tempDate).getId()+1;
        int length = readName.indexOf('.');
        String fn = readName.substring(length - 1, length).trim();
        String ln = readName.substring(0, length - 2).trim();
        String sn;
        if (readName.substring(length + 1, length + 2).equals(" "))
            sn = readName.substring(length + 2, length + 3).trim();
        else
            sn=readName.substring(length + 1, length + 2).trim();
        tempPerson = personRepository.findByLastNameAndRduIdAndFirstNameAndSecondName(ln, i2,fn,sn);
        if (!scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).isEmpty()) {
            tempSchedule = scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).get(0);
            if (type != "0" & !type.equals("о") & !type.equals(tempSchedule.getType())) {
                tempSchedule.setType(type);
                ArrayList<String> changes = new ArrayList<>();
                changes.add(monthNumber.toString());
                changes.add(tempPerson.getLastName());
                if (!changesInSchedule.contains(changes))
                    changesInSchedule.add(changes);
            } else if (type == "0" | (type.equals("0")))
                scheduleRepository.delete(scheduleRepository.findByDateIdAndPersonId(tempDateId, tempPerson.getId()).get(0));
        } else if (type != "0" & !(type.equals("о"))) {
            if (!newMonths.contains(monthNumber))
            {
                newMonths.add(monthNumber);
                nRdu=i2;
            }
            tempSchedule = new Schedule(tempPerson.getId(), tempDateId, type);
            scheduleRepository.save(tempSchedule);
        }
    }

    public void addYear(Integer year) {
        LocalDate start = LocalDate.of(year, 1, 1);
        LocalDate end = LocalDate.of(year + 1, 1, 1);
        for (LocalDate date = start; date.isBefore(end); date = date.plusDays(1)) {
            Calendar tempDate = new Calendar(date);
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
