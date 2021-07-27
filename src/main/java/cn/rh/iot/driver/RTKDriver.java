package cn.rh.iot.driver;

import cn.rh.iot.driver.base.FrameType;
import cn.rh.iot.driver.base.IDriver;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * @Program: IOT_Controller
 * @Description: RTK驱动
 * @Author: Y.Y
 * @Create: 2020-09-23 18:33
 **/
public class RTKDriver implements IDriver {

    private final byte[] Delimiter=new byte[]{0x0D,0x0A};  //回车+换行符 \r\n

    @Override
    public void InjectParams(HashMap<String, Object> params) {
    }

    @Override
    public byte[] encode(String jData) {
        return null;
    }

    @Override
    public String decode(byte[] data) {

        try{
            if(data.length<5){return null;}
            String s=new String(data, StandardCharsets.US_ASCII);
            String[] valueList=s.split(",");

            if(valueList[0].toUpperCase().equals("$GPGGA")) {

                double lat;
                double lon;
                double alt;
                int quality;

                String sV = valueList[2];
                lat = Integer.parseInt(sV.substring(0, 2)) + Double.parseDouble(sV.substring(2)) / 60;
                if (valueList[3].equals("S")) {
                    lat = -lat;
                }

                sV = valueList[4];
                lon = Integer.parseInt(sV.substring(0, 3)) + Double.parseDouble(sV.substring(3)) / 60;
                if (valueList[5].equals("W")) {
                    lon = -lon;
                }
                quality = Integer.parseInt(valueList[6]);
                alt = Double.parseDouble(valueList[9]);

                return "\"msgId\":" + 2 + "," + System.lineSeparator() +
                        "\"payload\":{" + System.lineSeparator() +
                        "\"subkind\":" + 1 + System.lineSeparator() +
                        "\"lon\":" + lon + "," + System.lineSeparator() +
                        "\"lat\":" + lat + "," + System.lineSeparator() +
                        "\"alt\":" + alt + "," + System.lineSeparator() +
                        "\"qos\":" + quality + System.lineSeparator() +
                        "}";

            }else if((valueList[0].toUpperCase().equals("$GPRMC") || valueList[0].toUpperCase().equals("$GNRMC"))
                    && valueList[2].toUpperCase().equals("A")){

                //拼接UTC时间字符串
                StringBuilder sb = new StringBuilder();
                sb.append("20").append(valueList[9].substring(4)).append("-").append(valueList[9].substring(2,4)).append("-").append(valueList[9].substring(0,2));
                sb.append(" ").append(valueList[1].substring(0,2)).append(":").append(valueList[1].substring(2,4)).append(":").append(valueList[1].substring(4));

                //将UTC时间转换为本地时间（北京时间）
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

                Date utcDate = null;
                utcDate = sdf.parse(sb.toString());
                sdf.setTimeZone(TimeZone.getDefault());
                String ts=sdf.format(utcDate.getTime());

                //输出json
                return "\"msgId\":" + 2 + "," + System.lineSeparator() +
                        "\"payload\":{" + System.lineSeparator() +
                        "\"subkind\":" + 3 + System.lineSeparator() +
                        "\"timestamp\":" + "\""+ ts +"\""+System.lineSeparator()+
                        "}";
            }else{
                return null;
            }
        }catch (Exception ex){
            return null;
        }
    }


    @Override
    public byte[] getAskMessage() {
        return null;
    }

    @Override
    public FrameType getType() {
        return FrameType.Delimiter;
    }

    @Override
    public int getMessageLength() {
        return 0;
    }

    @Override
    public byte[] getHeader() {
        return null;
    }

    @Override
    public byte[] getTrailer() {
        return Delimiter;
    }

    @Override
    public int getLengthFieldOffset() {
        return 0;
    }

    @Override
    public int getLengthFieldLength() {
        return 0;
    }

    @Override
    public int getLengthAdjustment() {
        return 0;
    }
}
