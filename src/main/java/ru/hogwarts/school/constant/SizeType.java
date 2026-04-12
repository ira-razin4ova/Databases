package ru.hogwarts.school.constant;

public enum SizeType {
    XS("XS", 1),
    S("S",2),
    M("M",3),
    L("L",4),
    XL("XL",5),
    XXL("2XL",6),
    XXXL("3XL",7);

    private final String label;

    private final int sortOrder;

    SizeType(String label, int sortOrder) {
        this.label = label;
        this.sortOrder = sortOrder;
    }

    public int getSortOrder() {
        return sortOrder;
    }

    public String getLabel() {
        return label;
    }

}
