//******************************************************************************
//
// Copyright (c) 2016 Microsoft Corporation. All rights reserved.
//
// This code is licensed under the MIT License (MIT).
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
// THE SOFTWARE.
//
//******************************************************************************
package com.github.grossopa.playground.winappdriver.demo;

import io.appium.java_client.windows.WindowsDriver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Jack Yin
 * @since 1.0
 */
public class CalculatorTest {

    private static WindowsDriver<WebElement> calculatorSession = null;
    private static WebElement calculatorResult = null;

    @BeforeAll
    public static void setup() {
        try {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability("app", "Microsoft.WindowsCalculator_8wekyb3d8bbwe!App");
            calculatorSession = new WindowsDriver<>(new URL("http://127.0.0.1:4723"), capabilities);
            calculatorSession.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);

            calculatorResult = calculatorSession.findElementByAccessibilityId("CalculatorResults");
            assertNotNull(calculatorResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @BeforeEach
    public void Clear() {
        calculatorSession.findElementByName("Clear").click();
        assertEquals("0", _GetCalculatorResultText());
    }

    @AfterAll
    public static void TearDown() {
        calculatorResult = null;
        if (calculatorSession != null) {
            calculatorSession.quit();
        }
        calculatorSession = null;
    }

    @Test
    void Addition() {
        calculatorSession.findElementByName("One").click();
        calculatorSession.findElementByName("Plus").click();
        calculatorSession.findElementByName("Seven").click();
        calculatorSession.findElementByName("Equals").click();
        assertEquals("8", _GetCalculatorResultText());
    }

    @Test
    void Combination() {
        calculatorSession.findElementByName("Seven").click();
        calculatorSession.findElementByName("Multiply by").click();
        calculatorSession.findElementByName("Nine").click();
        calculatorSession.findElementByName("Plus").click();
        calculatorSession.findElementByName("One").click();
        calculatorSession.findElementByName("Equals").click();
        calculatorSession.findElementByName("Divide by").click();
        calculatorSession.findElementByName("Eight").click();
        calculatorSession.findElementByName("Equals").click();
        assertEquals("8", _GetCalculatorResultText());
    }

    @Test
    void Division() {
        calculatorSession.findElementByName("Eight").click();
        calculatorSession.findElementByName("Eight").click();
        calculatorSession.findElementByName("Divide by").click();
        calculatorSession.findElementByName("One").click();
        calculatorSession.findElementByName("One").click();
        calculatorSession.findElementByName("Equals").click();
        assertEquals("8", _GetCalculatorResultText());
    }

    @Test
    void Multiplication() {
        calculatorSession.findElementByName("Nine").click();
        calculatorSession.findElementByName("Multiply by").click();
        calculatorSession.findElementByName("Nine").click();
        calculatorSession.findElementByName("Equals").click();
        assertEquals("81", _GetCalculatorResultText());
    }

    @Test
    void Subtraction() {
        calculatorSession.findElementByName("Nine").click();
        calculatorSession.findElementByName("Minus").click();
        calculatorSession.findElementByName("One").click();
        calculatorSession.findElementByName("Equals").click();
        assertEquals("8", _GetCalculatorResultText());
    }

    protected String _GetCalculatorResultText() {
        // trim extra text and whitespace off of the display value
        return calculatorResult.getText().replace("Display is", "").trim();
    }

}
