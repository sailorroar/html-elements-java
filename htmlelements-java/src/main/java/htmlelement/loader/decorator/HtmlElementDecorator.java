package htmlelement.loader.decorator;

import htmlelement.element.TypifiedElement;
import htmlelement.loader.decorator.proxyhandlers.HtmlElementListNamedProxyHandler;
import htmlelement.loader.decorator.proxyhandlers.TypifiedElementListNamedProxyHandler;
import htmlelement.loader.decorator.proxyhandlers.WebElementListNamedProxyHandler;
import htmlelement.loader.decorator.proxyhandlers.WebElementNamedProxyHandler;
import htmlelement.pagefactory.CustomElementLocatorFactory;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.pagefactory.ElementLocator;
import org.openqa.selenium.support.pagefactory.ElementLocatorFactory;
import org.openqa.selenium.support.pagefactory.FieldDecorator;
import htmlelement.element.HtmlElement;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.util.List;

import static htmlelement.loader.HtmlElementLoader.createHtmlElement;
import static htmlelement.loader.HtmlElementLoader.createTypifiedElement;
import static htmlelement.loader.decorator.ProxyFactory.*;
import static htmlelement.utils.HtmlElementUtils.*;

/**
 * Decorator which is used to decorate fields of blocks and page objects.
 * <p/>
 * Will decorate the following fields with lazy proxies:
 * <p/>
 * <ul>
 * <li>{@code WebElement}</li>
 * <li>{@code List<WebElement>}</li>
 * <li>Block of elements ({@link HtmlElement} and its successors)</li>
 * <li>List of blocks</li>
 * <li>Typified element ({@link TypifiedElement} successors)</li>
 * <li>List of typified elements</li>
 * </ul>
 * <p/>
 * {@code WebElements}, lists of {@code WebElements}, typified elements and lists of typified elements
 * have to be marked with {@link org.openqa.selenium.support.FindBy}, {@link org.openqa.selenium.support.FindBys}
 * or {@link org.openqa.selenium.support.FindAll} annotation.
 *
 * @author Alexander Tolmachev starlight@yandex-team.ru
 *         Date: 13.08.12
 */
public class HtmlElementDecorator implements FieldDecorator {
    protected ElementLocatorFactory factory;

    public HtmlElementDecorator(CustomElementLocatorFactory factory) {
        this.factory = factory;
    }

    public Object decorate(ClassLoader loader, Field field) {
        try {
            if (isTypifiedElement(field)) {
                return decorateTypifiedElement(loader, field);
            }
            if (isHtmlElement(field)) {
                return decorateHtmlElement(loader, field);
            }
            if (isWebElement(field) && !field.getName().equals("wrappedElement")) {
                return decorateWebElement(loader, field);
            }
            if (isTypifiedElementList(field)) {
                return decorateTypifiedElementList(loader, field);
            }
            if (isHtmlElementList(field)) {
                return decorateHtmlElementList(loader, field);
            }
            if (isWebElementList(field)) {
                return decorateWebElementList(loader, field);
            }
            return null;
        } catch (ClassCastException ignore) {
            return null; // See bug #94 and NonElementFieldsTest
        }
    }

    protected <T extends TypifiedElement> T decorateTypifiedElement(ClassLoader loader, Field field) {
        WebElement elementToWrap = decorateWebElement(loader, field);

        //noinspection unchecked
        return createTypifiedElement((Class<T>) field.getType(), elementToWrap, getElementName(field));
    }

    protected <T extends HtmlElement> T decorateHtmlElement(ClassLoader loader, Field field) {
        WebElement elementToWrap = decorateWebElement(loader, field);

        //noinspection unchecked
        return createHtmlElement((Class<T>) field.getType(), elementToWrap, getElementName(field));
    }

    protected WebElement decorateWebElement(ClassLoader loader, Field field) {
        ElementLocator locator = factory.createLocator(field);
        InvocationHandler handler = new WebElementNamedProxyHandler(locator, getElementName(field));

        return createWebElementProxy(loader, handler);
    }

    protected <T extends TypifiedElement> List<T> decorateTypifiedElementList(ClassLoader loader, Field field) {
        @SuppressWarnings("unchecked")
        Class<T> elementClass = (Class<T>) getGenericParameterClass(field);
        ElementLocator locator = factory.createLocator(field);
        String name = getElementName(field);

        InvocationHandler handler = new TypifiedElementListNamedProxyHandler<>(elementClass, locator, name);

        return createTypifiedElementListProxy(loader, handler);
    }

    protected <T extends HtmlElement> List<T> decorateHtmlElementList(ClassLoader loader, Field field) {
        @SuppressWarnings("unchecked")
        Class<T> elementClass = (Class<T>) getGenericParameterClass(field);
        ElementLocator locator = factory.createLocator(field);
        String name = getElementName(field);

        InvocationHandler handler = new HtmlElementListNamedProxyHandler<>(elementClass, locator, name);

        return createHtmlElementListProxy(loader, handler);
    }

    protected List<WebElement> decorateWebElementList(ClassLoader loader, Field field) {
        ElementLocator locator = factory.createLocator(field);
        InvocationHandler handler = new WebElementListNamedProxyHandler(locator, getElementName(field));

        return createWebElementListProxy(loader, handler);
    }
}
