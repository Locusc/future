package cn.locusc.future.java.design.pattern.proxy.statiz;

public class Test {

    public static void main(String[] args) {
        IRentingHouse rentingHouse = new RentingHouseImpl();
        // 自己要租用一个一室一厅的房子
        // rentingHouse.rentHouse();

        RentingHouseProxy rentingHouseProxy = new RentingHouseProxy(rentingHouse);
        rentingHouseProxy.rentHouse();
    }
}
