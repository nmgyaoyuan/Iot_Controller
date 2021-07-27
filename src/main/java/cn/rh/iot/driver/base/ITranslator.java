package cn.rh.iot.driver.base;

/*
*@描述: 翻译器接口
*@作者: YaoYuan
*@创建日期: 2021/4/29
*@修改日期: 2021/4/29
*/
public interface ITranslator {

    public byte[] From(byte[] data);

    public byte[] To(byte[] data);

}
