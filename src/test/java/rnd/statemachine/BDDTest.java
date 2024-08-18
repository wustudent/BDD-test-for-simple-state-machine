package rnd.statemachine;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;
import org.junit.runner.RunWith;

/**
 * Test runner class for feature files.
 */
@RunWith(Cucumber.class)
@CucumberOptions(plugin = {"pretty", "html:build/cucumber.html"})
public class BDDTest {

}
