package cn.locusc.future.java.design.pattern.proxy.statiz;

public class RentingHouseProxy implements IRentingHouse {

    private IRentingHouse rentingHouse;

    public RentingHouseProxy(IRentingHouse rentingHouse) {
        this.rentingHouse = rentingHouse;
    }

    @Override
    public void rentHouse() {
        System.out.println("中介（代理）收取服务费3000元");
        rentingHouse.rentHouse();
        System.out.println("客户信息卖了3毛钱");
    }
}