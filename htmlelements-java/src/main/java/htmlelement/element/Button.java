package htmlelement.element;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

public class Button extends TypifiedElement{
    /**
     * Specifies wrapped {@link WebElement}.
     *
     * @param wrappedElement {@code WebElement} to wrap.
     */
    protected Button(WebElement wrappedElement) {
        super(wrappedElement);
    }

    @Override
    public void setWebDriver(WebDriver driver) {
    }
}
