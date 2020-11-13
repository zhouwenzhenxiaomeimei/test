package com.ceshiren.web;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @author 羊菇菇
 * @date 2020/11/11 20:27
 */
public class WeiXinTest {

    public static WebDriver driver;

    @BeforeAll
    static void testinitData() {
        driver = new ChromeDriver();
        System.setProperty("webdriver.chrome.driver", "D:\\chromedriver_win32\\chromedriver.exe");
    }


    @Order(1)
   // @Test
    void writeCookie() {
        try {
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            driver.get("https://work.weixin.qq.com/wework_admin/loginpage_wx");
            Thread.sleep(15000);
            //把cookies写到文件里面，2019老板让我写个自动化脚本，爬虫几个网站的用户投诉，那个时候就是登录绕不过去，技术老大也是恨铁不成刚
            Set<Cookie> cookies = driver.manage().getCookies();
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            mapper.writeValue(new File("cookie.yaml"), cookies);
            System.out.println(cookies);
            driver.quit();
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    @Order(2)
    @Test
    void readCookieAndLogin() {
        try {
            driver.get("https://work.weixin.qq.com/wework_admin/loginpage_wx");
            driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
            ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
            //本来想 封装成Map，只能封装成list,因为cookie.jaml是数组格式的
            //不能泛化成Cookie类型，这里考虑了安全性，特别有意思
            // TODO ObjectMapper,TypeReference 都不熟，周六直播课之前要补一下
            TypeReference<List<HashMap<String, Object>>> typeReference = new TypeReference<List<HashMap<String, Object>>>() {
            };
            List<HashMap<String, Object>> mapperCookies = mapper.readValue(new File("cookie.yaml"), typeReference);

            //jdk8.0阿姆达
            mapperCookies.forEach(cookieMap->{
                driver.manage().addCookie(new Cookie(cookieMap.get("name").toString(),cookieMap.get("value").toString()));
                System.out.println(cookieMap.get("name").toString()+cookieMap.get("value").toString());
            });
               driver.navigate().refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


       @Order(3)
       @Test
       void addMember(){
        driver.findElement(By.xpath("//*[@node-type='addmember']")).click();
        driver.findElement(By.xpath("//*[@id='username']")).sendKeys("曹建明");
        driver.findElement(By.xpath("//*[@id='memberAdd_acctid']")).sendKeys("caojianming");
        driver.findElement(By.xpath("//*[@id='memberAdd_phone']")).sendKeys("13570379286");
        driver.findElement(By.xpath("//*[@class='qui_btn ww_btn js_btn_save']")).click();
 }




   // @AfterAll
    static void After() {
        driver.quit();
    }

}
