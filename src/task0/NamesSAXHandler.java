package task0;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NamesSAXHandler extends DefaultHandler {
    private Set<String> allTags = new HashSet<>();
    private Set<String> ethnicities = new HashSet<>();
    private List<BabyName> allNames = new ArrayList<>();

    private String currentTag = "";
    private String nm = "", gndr = "", ethcty = "";
    private int cnt = 0, rnk = 0;

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentTag = qName;
        allTags.add(qName); // Зберігаємо всі теги, які зустрічаємо [cite: 19]
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String value = new String(ch, start, length).trim();
        if (value.isEmpty()) return;

        if (currentTag.equals("nm")) nm = value;
        else if (currentTag.equals("gndr")) gndr = value;
        else if (currentTag.equals("ethcty")) {
            ethcty = value;
            ethnicities.add(value); // Збираємо всі національні групи [cite: 21-22]
        }
        else if (currentTag.equals("cnt")) cnt = Integer.parseInt(value);
        else if (currentTag.equals("rnk")) rnk = Integer.parseInt(value);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        currentTag = "";
        if (qName.equals("row")) {
            allNames.add(new BabyName(nm, gndr, ethcty, cnt, rnk));
        }
    }

    public Set<String> getAllTags() { return allTags; }
    public Set<String> getEthnicities() { return ethnicities; }
    public List<BabyName> getAllNames() { return allNames; }
}