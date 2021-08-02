package cn.rh.iot;

import cn.rh.iot.driver.RTKDriver;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class IotApplicationTests {

    @Test
    void contextLoads() {
    }


    @Test
    void TestGPRMC(){
        String sValue="$GPRMC,024813.640,A,3158.4608,N,11848.3737,E,10.05,324.27,150706,,,A*50";

        RTKDriver dr=new RTKDriver();

        System.out.println(dr.decode(sValue.getBytes()));
    }

    @Test
    void TestHexString(){

        int stateNumber=0x000F;

        String s=Integer.toString(stateNumber);

        System.out.println(s);

        stateNumber=0x2280;

        s=Integer.toString(stateNumber);

        System.out.println(s);


    }

}
