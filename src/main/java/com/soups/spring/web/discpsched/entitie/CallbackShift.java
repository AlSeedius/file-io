package com.soups.spring.web.discpsched.entitie;

import java.time.LocalDateTime;

public class CallbackShift {

    private String colleagueName;
    private String prevShift;
    private String nextShift;
    private LocalDateTime startShift;
    private boolean shiftFound;

    public boolean isShiftFound() {
        return shiftFound;
    }

    public void setShiftFound(boolean shiftFound) {
        this.shiftFound = shiftFound;
    }

    public String getColleagueName() {
        return colleagueName;
    }

    public void setColleagueName(String colleagueName) {
        this.colleagueName = colleagueName;
    }

    public String getPrevShift() {
        return prevShift;
    }

    public void setPrevShift(String prevShift) {
        this.prevShift = prevShift;
    }

    public String getNextShift() {
        return nextShift;
    }

    public void setNextShift(String nextShift) {
        this.nextShift = nextShift;
    }

    public LocalDateTime getStartShift() {
        return startShift;
    }

    public void setStartShift(LocalDateTime startShift) {
        this.startShift = startShift;
    }

    public CallbackShift(String colleagueName, String prevShift, String nextShift, LocalDateTime startShift, boolean shiftFound) {
        this.colleagueName = colleagueName;
        this.prevShift = prevShift;
        this.nextShift = nextShift;
        this.startShift = startShift;
        this.shiftFound = shiftFound;
    }
    public CallbackShift(){}

}
