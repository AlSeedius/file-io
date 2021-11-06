package com.soups.spring.web.discpsched.entitie;

import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class FileOutput {

    public byte type;

    private List<Correction> newList;
    private List<Correction> deleteList;
    private List<Correction> correctionList;
    public String row1;
    public String row2;

    public FileOutput() {
        this.newList = new ArrayList<>();
        this.deleteList = new ArrayList<>();
        this.correctionList = new ArrayList<>();
        type = 0;
    }

    public void addNew(Correction correction) {
        this.newList.add(correction);
    }

    public void addDelete(Correction correction) {
        this.deleteList.add(correction);
    }

    public void addCorrection(Correction correction) {
        this.correctionList.add(correction);
    }

    private void setType() {
        if (this.deleteList.size() == 0 && this.correctionList.size() == 0 && this.newList.size() != 0)
            type = 1;  //новый месяц
        else if (this.deleteList.size() != 0 && this.correctionList.size() == 0 && this.newList.size() == 0)
            type = 2;  //удаление
        else if (this.deleteList.size()==0 && this.correctionList.size() ==0 && this.newList.size() ==0 )
            type = 4;
        else
            type = 3; //исправление
    }

    public String[] getOutput() {
        setType();
        if (type == 1) {
            List<String> months = enumerateMonths(this.newList);
            if (months.size() == 1)
                row1 = "Добавлен месяц ";
            else
                row1 = "Добавлены месяцы ";
            for (String s : months)
                row1 += s + ", ";
            row1 = row1.substring(0, row1.length() - 2) + ".";
            row2 = "Добавлено выходов на работу: " + this.newList.size()+".";
        } else if (type == 2) {
            List<String> months = enumerateMonths(this.deleteList);
            if (months.size() == 1)
                row1 = "Изменен месяц ";
            else
                row1 = "Изменены месяцы ";
            for (String s : months)
                row1 += s + ", ";
            row1 = row1.substring(0, row1.length() - 2) + ".";
            row2 = "Удалено выходов на работу: " + this.deleteList.size()+". Затронутый персонал: ";
            for (String s : enumeratePeople(deleteList))
                row2+= s +", ";
            row2 = row2.substring(0, row2.length() - 2);
        }
        else if (type == 3){
            List<String> months = combineArrays(enumerateMonths(this.correctionList),
                    enumerateMonths(this.deleteList), enumerateMonths(this.newList));
            if (months.size() == 1)
                row1 = "Изменен месяц ";
            else
                row1 = "Изменены месяцы ";
            for (String s : months)
                row1 += s + ", ";
            row1 = row1.substring(0, row1.length() - 2) + ".";
            row2="";
            if (this.newList.size()!=0)
                row2+="Добавлено выходов на работу: " + this.newList.size() + ". ";
            if (this.correctionList.size()!=0)
                row2+="Изменено выходов на работу: " + this.correctionList.size() + ". ";
            if (this.deleteList.size()!=0)
                row2+="Удалено выходов на работу: " + this.deleteList.size() + ". Затронутый персонал: ";
            for (String s : combineArrays(enumeratePeople(correctionList), enumeratePeople(newList), enumeratePeople(deleteList)))
                row2+= s +", ";
            row2 = row2.substring(0, row2.length() - 2);
        }
        else if (type ==4){
            row2 = "";
            row1 = "Не обнаружено изменений по сравнению с существующим графиком.";
        }
        else {
            row2 = "";
            row1 = "Произошла ошибка при оценке изменений.";
        }
        return new String[] {row1, row2};
    }

    private List<String> enumerateMonths(List<Correction> list){
        ArrayList<String> months = new ArrayList<>();
        for (Correction c : list){
            if (!months.contains(c.date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru"))))
                months.add(c.date.getMonth().getDisplayName(TextStyle.FULL_STANDALONE, new Locale("ru")));
        }
        return months;
    }

    private List<String> enumeratePeople(List<Correction> list){
        ArrayList<String> people = new ArrayList<>();
        for (Correction c: list){
            if (!people.contains(c.name))
                people.add(c.name);
        }
        return people;
    }

    private List<String> combineArrays(List<String> l1, List<String> l2, List<String> l3){
        List<String> output = l1;
        for (String s : l2){
            if (!output.contains(s))
                output.add(s);
        }
        for (String s : l3){
            if (!output.contains(s))
                output.add(s);
        }
        return output;
    }
}
