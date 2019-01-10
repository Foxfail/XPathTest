import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;


public class XPathTinkoffTest {

    private static WebDriver driver;
    private static List<WebElement> menuArray; // здесь будут содержаться все кнопки из второго меню
    private static WebElement menu_div; // тут будет содержаться контейнер второго меню, который содержит все кнопки

    public static void main(String[] args) throws MalformedURLException, InterruptedException {
        URL chromeDriverUrl = new URL("http://localhost:9515");

        driver = new RemoteWebDriver(chromeDriverUrl, new ChromeOptions()); // открываем хром
        driver.manage().window().maximize(); // разворачиваем окно
        driver.get("https://www.tinkoff.ru/"); // идем на сайт

        // 1. На странице https://www.tinkoff.ru/ выбрать массив всех элементов второго меню (Кред карты, ***, платежи)
        refreshLinks(); // вынес в отдельный метод т.к. ссылки теряют актуальность при переходе на разные страницы

        // 2. Обратиться к последнему из них
        // я так понял что щелкнуть.
        System.out.println("пробую нажать последний");
        menuArray.get(menuArray.size() - 1).click();
        System.out.println("нажато");
        Thread.sleep(5000); // хочу увидеть что происходит в браузере

        // Последний элемент это карта банкоматов, в которой нет найденного ранее меню
        // поэтому ищу кнопку назад и щелкаю её и далее взаимодействую с меню
        System.out.println("пробую нажать назад");
        driver.findElement(By.xpath("//div[@class = \"Icon__icon_3c1E8\"]")).click();
        System.out.println("нажато");
        waitPageToLoad();
        // ну и поскольку все ссылки более не действительны на новой странице нахожу их заново
        refreshLinks();

        // 3. Обратиться ко второму
        System.out.println("пробую нажать второй элемент");
        menuArray.get(1).click();
        System.out.println("нажато");

        // если отсюда убрать метод waitPageToLoad() то будет интересная ошибка о просроченных ссылках
        // я так понял: поскольку на старой странице есть меню то пока новая страница не загрузилась
        // ссылки находятся из старой страницы, далее в п.4 я получаю ошибку о просроченных ссылках
        // интересно что implicityWait не срабатывает, т.к. не понимает чего нужно ждать,
        // потому что явной ошибки не происходит немедленно
        waitPageToLoad();

        refreshLinks();


        // 4. Найти отцовский элемент второго
        WebElement parentOfSecondElement = menuArray.get(1).findElement(By.xpath("..")); // родитель - div
        // 5. Найти все ссылки в родителях(в родителе?) второго элемента
        System.out.println(parentOfSecondElement.getTagName());
        List<WebElement> linksList = parentOfSecondElement.findElements(By.xpath(".//a"));
        System.out.println("Ссылок найдено: " + linksList.size()); // 7 штук
        // Таким образом можно получить возможность обращения к объекту не зная его точного расположения.
    }

    private static void refreshLinks() {
        // находим контейнер для кнопок
        menu_div = driver.findElement(By.xpath("//div[@class=\"header__9V1so header__3rtwn\"]"));
        // выбираем из него все ссылки элементы
//        menuArray = menu_div.findElements(By.xpath("//a[@class = \"header__3E29o header__DX3-q\"]"));
        // когда пытаюсь нажать на <a> селениум ругается что другой элемент на пути поэтому щелкаю на <div>
        menuArray = menu_div.findElements(By.xpath("//div[@class = \"header__1AlOP\"]"));
    }

    // Ожидание полной загрузки страницы. Взято из гугла
    // https://www.testingexcellence.com/webdriver-wait-page-load-example-java/
    private static void waitPageToLoad() {
        ExpectedCondition<Boolean> expectation = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState").toString().equals("complete");
        try {
            Thread.sleep(1000);
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(expectation);
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Timeout waiting for Page Load Request to complete.");
        }
    }
}
