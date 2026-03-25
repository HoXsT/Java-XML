package task0;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.XMLConstants;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainTask0 {
    public static void main(String[] args) {
        String inputFile = "names_data.xml";
        String schemaFile = "names_schema.xsd";
        String outputFile = "names_result.xml";

        try {
            // 1. Валідація за схемою (окремим валідатором)
            System.out.println("1. Перевірка правильності структури XSD...");
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new File(schemaFile));
            Validator validator = schema.newValidator();
            validator.validate(new javax.xml.transform.stream.StreamSource(new File(inputFile)));
            System.out.println("-> Структура датасету вірна!");

            // 2. Парсинг SAX (читання тегів, етнічностей та об'єктів) [cite: 18, 19, 21-23]
            System.out.println("\n2. SAX Аналіз документа...");
            SAXParserFactory saxFactory = SAXParserFactory.newInstance();
            SAXParser saxParser = saxFactory.newSAXParser();
            NamesSAXHandler handler = new NamesSAXHandler();
            saxParser.parse(new File(inputFile), handler);

            System.out.println("-> Всі теги в документі: " + handler.getAllTags());
            System.out.println("-> Національні групи: " + handler.getEthnicities());

            // 3. Фільтрація і сортування
            String targetEthnicity = "HISPANIC";
            int limit = 2; // кількість найбільш популярних імен

            List<BabyName> filteredNames = handler.getAllNames().stream()
                    .filter(n -> n.getEthnicity().equals(targetEthnicity))
                    .collect(Collectors.toList());

            Collections.sort(filteredNames); // сортування по рейтингу
            List<BabyName> topNames = filteredNames.subList(0, Math.min(limit, filteredNames.size()));

            System.out.println("\n-> Відібрано топ-" + limit + " імен для групи " + targetEthnicity + ".");

            // 4. Збереження за допомогою DOM [cite: 24-26]
            System.out.println("\n3. Збереження відсортованих даних за допомогою DOM...");
            saveToDom(topNames, outputFile);

            // 5. Прочитати новий документ за допомогою DOM та вивести на екран [cite: 27, 841-851]
            System.out.println("\n4. Зчитаний новий файл " + outputFile + ":");
            readAndPrintDOM(outputFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveToDom(List<BabyName> names, String outputFile) throws Exception {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
        Document doc = docBuilder.newDocument();

        Element root = doc.createElement("topNames");
        doc.appendChild(root);

        for (BabyName bn : names) {
            Element nameElement = doc.createElement("baby");
            root.appendChild(nameElement);

            Element nm = doc.createElement("name");
            nm.appendChild(doc.createTextNode(bn.getName()));
            nameElement.appendChild(nm);

            Element gender = doc.createElement("gender");
            gender.appendChild(doc.createTextNode(bn.getGender()));
            nameElement.appendChild(gender);

            Element cnt = doc.createElement("count");
            cnt.appendChild(doc.createTextNode(String.valueOf(bn.getCount())));
            nameElement.appendChild(cnt);

            Element rnk = doc.createElement("rating");
            rnk.appendChild(doc.createTextNode(String.valueOf(bn.getRating())));
            nameElement.appendChild(rnk);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(new File(outputFile));
        transformer.transform(source, result);
    }

    private static void readAndPrintDOM(String inputFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(new File(inputFile));

        NodeList babies = doc.getElementsByTagName("baby");
        for (int i = 0; i < babies.getLength(); i++) {
            Element baby = (Element) babies.item(i);
            String name = baby.getElementsByTagName("name").item(0).getTextContent();
            String gender = baby.getElementsByTagName("gender").item(0).getTextContent();
            String cnt = baby.getElementsByTagName("count").item(0).getTextContent();
            String rnk = baby.getElementsByTagName("rating").item(0).getTextContent();

            System.out.printf("Рейтинг %s: %s (Стать: %s, Кількість: %s)\n", rnk, name, gender, cnt);
        }
    }
}