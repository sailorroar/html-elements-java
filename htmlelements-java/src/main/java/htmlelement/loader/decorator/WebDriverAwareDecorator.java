package htmlelement.loader.decorator;


import htmlelement.element.HtmlElement;
import htmlelement.element.webDriverInterface.WebDriverAware;
import htmlelement.pagefactory.CustomElementLocatorFactory;
import org.openqa.selenium.WebDriver;

import java.lang.reflect.Field;
import java.util.List;

public class WebDriverAwareDecorator extends HtmlElementDecorator {
        private WebDriver driver;

        public WebDriverAwareDecorator(CustomElementLocatorFactory factory, WebDriver driver) {
            super(factory);

            this.driver = driver;
        }

        @Override
        protected <T extends HtmlElement> T decorateHtmlElement(ClassLoader loader, Field field) {
            T element = super.decorateHtmlElement(loader, field);

            if (element instanceof WebDriverAware) {
                ((WebDriverAware)element).setWebDriver(driver);
            }

            return element;
        }

        @Override
        protected <T extends HtmlElement> List<T> decorateHtmlElementList(ClassLoader loader, Field field) {
            List<T> elements = super.decorateHtmlElementList(loader, field);

            if (elements.size() > 0 && elements.get(0) instanceof WebDriverAware) {
                for (T element : elements) {
                    ((WebDriverAware)element).setWebDriver(driver);
                }
            }

            return elements;
        }
}
