package org.koreait.global.constants;

public enum Gender {
    FEMALE(0),
    MALE(1),
    OTHER(2);

    private final int num;

    Gender(int num) {
        this.num = num;
    }

    public int getNum() {
        return num;
    }
}
