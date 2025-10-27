package com.neon;

class Cellphone
{
    void charging(Voltage5V voltage5V)
    {
        int voltage = voltage5V.output5V();
        if (voltage == 5)
        {
            System.out.println("成功充电，电压为:" + voltage);
        }
        else
        {
            System.out.println("充电失败，电压为:" + voltage);
        }
    }
}

// 5V
interface Voltage5V {
    int output5V();
}

// 220V
class Voltage220V {
    // 输出220V电压
    public int output220V() {
        int voltage = 220;
        System.out.println("输出电压：" + voltage + "V");
        return voltage;
    }
}

//// 适配器
//class VoltageAdapter implements Voltage5V {
//    // 220V类实例
//    private Voltage220V voltage220V;
//
//    public VoltageAdapter(Voltage220V voltage220V) {
//        this.voltage220V = voltage220V;
//    }
//
//    @Override
//    public int output5V() {
//        int outVoltage = 0;
//        int inputVoltage = voltage220V.output220V();
//        outVoltage = inputVoltage / 44;
//        System.out.println("电压转换完成，输出：" + outVoltage + "V");
//        return outVoltage;
//    }
//}

interface VoltageSource {
    int getVoltage();
}


// 适配器类 ，依赖 VoltageSource 接口
class VoltageAdapter implements Voltage5V {
    private VoltageSource voltageSource;

    public VoltageAdapter(VoltageSource voltageSource) {
        this.voltageSource = voltageSource;
    }

    @Override
    public int output5V() {
        int inlVoltage = voltageSource.getVoltage();

        int outVoltage = inlVoltage / (inlVoltage / 5);
        System.out.println(inlVoltage + "V 转换为 " + outVoltage + "V");
        return outVoltage;
    }
}


