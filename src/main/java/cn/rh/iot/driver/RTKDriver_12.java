package cn.rh.iot.driver;

import cn.rh.iot.driver.base.FrameType;
import cn.rh.iot.driver.base.IDriver;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.TimeZone;


public class RTKDriver_12 implements IDriver {

    private final byte[] Delimiter=new byte[]{0x0D,0x0A};  //回车+换行符 \r\n

    @Override
    public void InjectParams(HashMap<String, Object> params) {
    }

    @Override
    public boolean Is2Me(byte[] data) {
        return true;
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

            String sHead=valueList[0].toUpperCase();

            if(sHead.equals("$GPGGA")){

                double lat;
                double lon;
                double alt;
                int quality;

                String sV=valueList[2];
                lat=Integer.parseInt(sV.substring(0,2))+Double.parseDouble(sV.substring(2))/60;
                if(valueList[3].equals("S")){
                    lat=-lat;
                }

                sV=valueList[4];
                lon=Integer.parseInt(sV.substring(0,3))+Double.parseDouble(sV.substring(3))/60;
                if(valueList[5].equals("W")){
                    lon=-lon;
                }
                quality=Integer.parseInt(valueList[6]);
                alt=Double.parseDouble(valueList[9]);

                return  "\"msgId\":" + 2 + "," + System.lineSeparator() +
                        "\"payload\":{" + System.lineSeparator() +
                        "\"subkind\":"+ 1 +"," + System.lineSeparator() +
                        "\"lon\":" + lon + "," + System.lineSeparator() +
                        "\"lat\":" + lat + "," + System.lineSeparator() +
                        "\"alt\":" + alt + "," + System.lineSeparator() +
                        "\"qos\":" + quality + System.lineSeparator() +
                        "}";

            }else if(sHead.equals("$GPVTG") || sHead.equals("$GNVTG")){

                double true_north_direction;
                double velocity;

                String sV=valueList[9];
                if(sV.equals("N")){                //代表数据无效
                    return null;
                }

                sV=valueList[1];
                true_north_direction=Integer.parseInt(sV.substring(0,2));

                sV=valueList[7];
                velocity=Integer.parseInt(sV.substring(0,2));

                return  "\"msgId\":" + 2 + "," + System.lineSeparator() +
                        "\"payload\":{" + System.lineSeparator() +
                        "\"subkind\":" + 2 + ","+ System.lineSeparator() +
                        "\"direction\":" + true_north_direction + "," + System.lineSeparator() +
                        "\"velocity\":" + velocity + System.lineSeparator() +
                        "}";

            }else if((sHead.equals("$GPRMC") || sHead.equals("$GNRMC")) && valueList[2].toUpperCase().equals("A")){

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
                        "\"subkind\":" + 3 + ","+System.lineSeparator() +
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
