package funny;

/**
 * 以两点的经纬度查询两点的不太精确距离
 * 把两点想象成一个梯形的左上与右下点
 * 梯形的对角线作为弦长求弧长即可
 */
//单位km
public class Distance {
    private double R_MAX = 6378.137;//赤道半径//长轴
    private double R_MIN = 6356.752;//短轴

    //抽象梯形边长
    private double L1;
    private double L2;
    private double L3;
    private double L4;

    //经纬度点1
    private double m1;
    private double n1;
    //经纬度点2
    private double m2;
    private double n2;

    public double calc_d_square(){
        calcL();
        return (L1 * L2 + square(L3));
    }

    public double distance(){
        double r1 = calcRn(n1);
        double r2 = calcRn(n2);
        return (r1+r2)/2 * Math.acos(cosB());
    }

    //夹角
    public double cosB(){
        double r1 = calcRn(n1);
        double r2 = calcRn(n2);
        return (square(r1) + square(r2) - calc_d_square())/(2*r1*r2);
    }

    public void calcL(){
        L1 = calcLn(n1);
        L2 = calcLn(n2);
        L3 = calcLm();
        L4 = L3;
    }

    //纬度圆等腰三角底边
    public double calcLn(double n){
        return sqrt(2*square(calcRn_self(n)) * (1 - cos(Math.abs(m2-m1))));
    }

    //经度圆三角底边
    public double calcLm(){
        double r1 = calcRn(n1);
        double r2 = calcRn(n2);
        return sqrt(square(r1) + square(r2) -2*r1*r2*cos(Math.abs(n2-n1)));
    }

    //获取纬度到地心半径
    //param angle 纬度
    public double calcRn(double angle){
        return sqrt(square(R_MAX) * square(R_MIN)/(square(R_MAX)*square(sin(angle)) + square(R_MIN)*square(cos(angle))));
    }

    //纬度自圆半径
    public double calcRn_self(double angle){
        return calcRn(angle)*cos(angle);
    }

    private double square(double num){
        return num*num;
    }

    private double sqrt(double num){
        return Math.sqrt(num);
    }

    private double cos(double num){
        return Math.cos(Math.toRadians(num));
    }

    private double sin(double num){
        return Math.sin(Math.toRadians(num));
    }

    public static void main(String[] args){
        Distance distance = new Distance();
        distance.m1 = 106.33;
        distance.n1 = 29.35;
        distance.m2 = 116.20;
        distance.n2 = 39.56;
        System.out.println(distance.distance());
    }
}
