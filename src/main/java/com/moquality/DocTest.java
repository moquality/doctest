package com.moquality;


import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.Rectangle;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.appium.java_client.AppiumDriver;

class Step {
    public String screenshot;
    public String desc;
    public By selector;

    public Step(String desc, By selector) {
        this.desc = desc;
        this.selector = selector;
    }

    public Step(String desc, By selector, String screenshot) {
        this(desc, selector);
        this.screenshot = screenshot;
    }
}


public class DocTest {
    AppiumDriver driver;
    int timeout = 1000;

    private String sessionName;
    List<Step> steps;
    private String outputDir = "output";
    private String prefix = "/screenshots/";
    private String imageWidth = "250px";

    private int stepCount;


    public DocTest(AppiumDriver driver) {
        this.driver = driver;
        new File(outputDir).mkdirs();
        this.stepCount = 0;
    }

    public void startSession(String sessionName) throws Exception {
        this.sessionName = sessionName;
        Thread.sleep(timeout);
        clear();
    }

    public void click(String desc, By selector) throws Exception {
        String image = this.screenshot();
        this.steps.add(new Step(desc, selector, image));
        WebElement elem = driver.findElement(selector);
        elem.click();
        highlight(image, elem.getRect());
        Thread.sleep(timeout);
    }

    private void highlight(String image, Rectangle rect) {

    }

    public void sendKeys(String desc, By selector, String text) throws Exception {
        WebElement elem = driver.findElement(selector);
        elem.clear();
        elem.sendKeys(text);
        this.steps.add(new Step(desc, selector, this.screenshot()));
        Thread.sleep(timeout);
    }

    public String screenshot() throws IOException {
        File srcFile=driver.getScreenshotAs(OutputType.FILE);
        String filename= prefix + UUID.randomUUID().toString() + ".jpg";
        File targetFile=new File(outputDir + filename);
        FileUtils.copyFile(srcFile,targetFile);
        return filename;
    }

    public void endSession() throws Exception {
        this.steps.add(new Step("End", null, this.screenshot()));

        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd");
        String date = ft.format(new Date());
        String fileName = date + "-" + sessionName.replaceAll("[^a-zA-Z0-9.-]", "-") + ".md";
        File file = new File(fileName);
        file.createNewFile();

        StringBuilder sb = new StringBuilder();

        sb.append(
                "---\n" +
                        "title: '"+sessionName+"'\n" +
                        "layout: nil\n" +
                        "order: "+(++stepCount)+"\n" +
                        "---\n\n");

        for (Step step:this.steps) {
            String stepString = String.format("## %s\n<img src=\"%s\" width=\"%s\">\n\n", step.desc, step.screenshot, this.imageWidth);
            sb.append(stepString);
        }
        FileUtils.writeStringToFile(file, sb.toString());
    }

    public void clear() {
        this.steps = new ArrayList<>();
    }
}
