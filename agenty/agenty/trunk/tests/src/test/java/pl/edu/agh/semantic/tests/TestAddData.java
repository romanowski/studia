/*
 * Copyright (c) 2011,2012, Krzysztof Styrc and Tomasz Zdybał
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the project nor the names of its contributors
 *    may be used to endorse or promote products derived from this software
 *    without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE PROJECT AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE PROJECT OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 */

package pl.edu.agh.semantic.tests;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.internal.seleniumemulation.WaitForPageToLoad;
import org.openqa.selenium.support.ui.Select;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static org.testng.Assert.assertTrue;

public class TestAddData {
	private WebDriver driver;
	private String baseUrl;
	private String file1;
	private String file2;
	private String file3;

	@BeforeClass
	public void setUp() throws Exception {
		driver = new FirefoxDriver();
		baseUrl = "http://localhost:8080";
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		file1 = getFileContents("data1.ttl");
		file2 = getFileContents("data2.ttl");
		file3 = getFileContents("data3.ttl");

	}

	private String getFileContents(String file) throws IOException {
		return FileUtils.readFileToString(new File(getClass().getClassLoader().getResource(file).getFile())).replaceAll(
				"\t", "    ");
	}

	@Test
	public void testAddData() throws Exception {
		driver.get(baseUrl + "/storage-node1/");
		driver.findElement(By.id("data")).clear();
		driver.findElement(By.id("data")).sendKeys(file1);
		driver.findElement(By.id("submit")).click();
		driver.get(baseUrl + "/storage-node2/");
		driver.findElement(By.id("data")).clear();
		driver.findElement(By.id("data")).sendKeys(file2);
		driver.findElement(By.id("submit")).click();
		driver.get(baseUrl + "/query-node1/query-service.html");
		driver.findElement(By.id("submit")).click();
		String result = driver.findElement(By.className("result")).getText();
		assertTrue(result.contains("Cay Horstmann"));
		assertTrue(result.contains("Gary Cornell"));
		assertTrue(result.contains("William Shakespeare"));
		assertTrue(result.contains("William Blake"));
	}

	@Test
	public void testAddDataToNamedGraph() throws Exception {
		driver.get(baseUrl + "/storage-node1/");
		driver.findElement(By.id("data")).clear();
		driver.findElement(By.id("data")).sendKeys(file3);
		new Select(driver.findElement(By.id("graph"))).selectByValue("named");
		driver.findElement(By.id("graph_name")).clear();
		driver.findElement(By.id("graph_name")).sendKeys("test_graph");
		driver.findElement(By.id("submit")).click();
		new WaitForPageToLoad();
		driver.get(baseUrl + "/query-node1/query-service.html");
		driver.findElement(By.id("submit")).click();
		String result = driver.findElement(By.className("result")).getText();
		assertTrue(result.contains("Donald Knuth"));
		assertTrue(result.contains("Stieg Larsson"));
	}

	@AfterClass
	public void tearDown() throws Exception {
		driver.quit();
	}
}
