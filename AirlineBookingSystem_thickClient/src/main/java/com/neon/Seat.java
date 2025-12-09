package com.neon;

public class Seat {
    private int row;         // 排数
    private String num;      // 座位号（A/B/C）
    private String type;     // 类型：靠窗/过道/中间
    private boolean isEmpty; // 是否空座

    // 构造方法
    public Seat(int row, String num, String type, boolean isEmpty) {
        this.row = row;
        this.num = num;
        this.type = type;
        this.isEmpty = isEmpty;
    }

    // 获取座位类型（用于匹配偏好）
    public String getType() {
        return type;
    }

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean empty) {
        isEmpty = empty;
    }

    // 座位信息格式化（如：5A(靠窗)）
    @Override
    public String toString() {
        return row + num + "(" + type + ")";
    }
}