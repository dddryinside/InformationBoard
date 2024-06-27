package org.msolutions.service;

import org.msolutions.DTO.Event;
import org.msolutions.DTO.TimeInterval;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class EventsManager {
    public static void addEvent(Event event) {
        int id = AppSettingsManager.generateId();
        File newFolder = new File(AppSettingsManager.getPath() + "/files/" + id);
        if (newFolder.mkdir()) {
            System.out.println("Папка для события успешно создана");
        } else {
            System.out.println("Не удалось создать папку для события");
        }

        try {
            File file = getEventsXMLFile();

            // Загрузка существующего XML-файла
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            // Получение корневого элемента
            Element rootElement = document.getDocumentElement();

            // Создание нового элемента "date"
            Element dateElement = document.createElement("date");

            Element idElement = document.createElement("id");
            idElement.setTextContent(String.valueOf(id));
            dateElement.appendChild(idElement);

            Element nameElement = document.createElement("name");
            nameElement.setTextContent(event.getName());
            dateElement.appendChild(nameElement);

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Element start = document.createElement("start");
            start.setTextContent(event.getStartDate().format(formatter));
            dateElement.appendChild(start);

            Element end = document.createElement("end");
            end.setTextContent(event.getEndDate().format(formatter));
            dateElement.appendChild(end);


            // Создаем элемент "children" и добавляем его в корневой элемент
            Element intervalsElement = document.createElement("intervals");
            dateElement.appendChild(intervalsElement);

            List<TimeInterval> timeIntervalsList = event.getTimeIntervals();

            if (timeIntervalsList.size() != 0) {
                for (TimeInterval timeInterval : timeIntervalsList) {
                    Element intervalElement = document.createElement("interval");

                    String startTime = timeInterval.getStartTime();
                    String endTime = timeInterval.getEndTime();

                    Element startTimeElement = document.createElement("startTime");
                    startTimeElement.setTextContent(startTime);

                    intervalsElement.appendChild(startTimeElement);

                    Element endTimeElement = document.createElement("endTime");
                    endTimeElement.setTextContent(endTime);

                    intervalElement.appendChild(startTimeElement);
                    intervalElement.appendChild(endTimeElement);

                    intervalsElement.appendChild(intervalElement);
                }
            }

            // Добавление нового элемента "date" в корневой элемент
            rootElement.appendChild(dateElement);

            // Запись обновленного XML-файла
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            // Удаление пустых текстовых узлов для предотвращения накопления пробелов
            removeWhitespaceNodes(document.getDocumentElement());

            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(file);
            transformer.transform(domSource, streamResult);

            System.out.println("XML-файл успешно обновлен!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Метод для удаления пустых текстовых узлов в XML файле
    private static void removeWhitespaceNodes(Element element) {
        NodeList children = element.getChildNodes();
        for (int i = children.getLength() - 1; i >= 0; i--) {
            Node child = children.item(i);
            if (child.getNodeType() == Node.TEXT_NODE && child.getTextContent().trim().isEmpty()) {
                element.removeChild(child);
            } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                removeWhitespaceNodes((Element) child);
            }
        }
    }

    public static void deleteEvent(int id) {
        try {
            // Создаем документ из XML файла
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(AppSettingsManager.getPath() + "/files/events.xml"));

            // Получаем список всех элементов <date>
            NodeList dateList = doc.getElementsByTagName("date");

            for (int i = 0; i < dateList.getLength(); i++) {
                Node dateNode = dateList.item(i);
                if (dateNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element dateElement = (Element) dateNode;
                    String currentId = dateElement.getElementsByTagName("id").item(0).getTextContent();

                    // Если id совпадает, удаляем элемент
                    if (currentId.equals(String.valueOf(id))) {
                        dateElement.getParentNode().removeChild(dateElement);
                        break;
                    }
                }
            }

            // Сохранение изменений в файл
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(AppSettingsManager.getPath() + "/files/events.xml"));
            transformer.transform(source, result);

            deleteFolder(AppSettingsManager.getPath() + "/files", String.valueOf(id));

            System.out.println("Элемент с id " + id + " был удален.");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void deleteFolder(String directoryPath, String folderName) {
        File directory = new File(directoryPath);
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory() && file.getName().equals(folderName)) {
                        deleteRecursively(file);
                        System.out.println("Папка " + folderName + " успешно удалена.");
                        return;
                    }
                }
            }
            System.out.println("Папка " + folderName + " не найдена в директории " + directoryPath);
        } else {
            System.out.println("Директория " + directoryPath + " не существует или не является директорией.");
        }
    }

    private static void deleteRecursively(File file) {
        if (file.isDirectory()) {
            for (File child : file.listFiles()) {
                deleteRecursively(child);
            }
        }
        file.delete();
    }

    public static List<Event> getAllEvents() {
        List<Event> events = new ArrayList<>();
        File file = getEventsXMLFile();

        try {
            // Создаем фабрику для построения документа
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Загружаем XML-файл
            Document doc = docBuilder.parse(new File(file.getAbsolutePath()));

            // Получаем список элементов "date"
            NodeList dateNodes = doc.getElementsByTagName("date");

            for (int i = 0; i < dateNodes.getLength(); i++) {
                Element dateElement = (Element) dateNodes.item(i);

                // Извлекаем данные о дате
                int id = Integer.parseInt(dateElement.getElementsByTagName("id").item(0).getTextContent());
                String name = dateElement.getElementsByTagName("name").item(0).getTextContent();
                String start = dateElement.getElementsByTagName("start").item(0).getTextContent();
                String end = dateElement.getElementsByTagName("end").item(0).getTextContent();

                LocalDate startDate = LocalDate.parse(start, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                LocalDate endDate = LocalDate.parse(end, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
                Event event = new Event(id, name, startDate, endDate);

                // Извлекаем данные об интервалах времени (если они есть)
                NodeList intervalNodes = dateElement.getElementsByTagName("interval");
                if (intervalNodes.getLength() > 0) {
                    List<TimeInterval> intervals = new ArrayList<>();
                    for (int j = 0; j < intervalNodes.getLength(); j++) {
                        Element intervalElement = (Element) intervalNodes.item(j);
                        String stringStartTime = intervalElement.getElementsByTagName("startTime").item(0).getTextContent();
                        String stringEndTime = intervalElement.getElementsByTagName("endTime").item(0).getTextContent();

                        LocalTime startTime = LocalTime.parse(stringStartTime, DateTimeFormatter.ofPattern("HH:mm"));
                        LocalTime endTime = LocalTime.parse(stringEndTime, DateTimeFormatter.ofPattern("HH:mm"));

                        intervals.add(new TimeInterval(startTime, endTime));
                    }
                    event.setIntervals(intervals);
                }

                events.add(event);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }

        return events;
    }

    public static List<Event> getActualEvents() {
        List<Event> events = getAllEvents();
        LocalDate today = LocalDate.now();

        List<Event> currentEvents = new ArrayList<>();

        for (Event event : events) {
            if ((event.getStartDate().equals(today) || event.getStartDate().isBefore(today)) &&
                    (event.getEndDate().equals(today) || event.getEndDate().isAfter(today))) {

                if (event.getTimeIntervals() != null) {
                    for (TimeInterval timeInterval : event.getTimeIntervals()) {
                        if (isInTimeInterval(timeInterval)) {
                            currentEvents.add(event);
                            break;
                        }
                    }
                } else {
                    currentEvents.add(event);
                }
            }
        }

        return currentEvents;
    }

    private static boolean isInTimeInterval(TimeInterval timeInterval) {
        LocalTime time = LocalTime.now();

        return (time.isAfter(timeInterval.getLocalTimeStartTime()) && time.isBefore(timeInterval.getLocalTimeEndTime())) ||
                (time.equals(timeInterval.getLocalTimeStartTime())) || time.equals(timeInterval.getLocalTimeEndTime());
    }

    public static File getEventsXMLFile() {
        File file = null;

        try {
            file = new File(AppSettingsManager.getPath() + "/files/events.xml");

            // Если файл не существует, создаем его
            if (!file.exists()) {
                // Создание нового XML-документа
                DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
                Document document = documentBuilder.newDocument();

                // Создание корневого элемента "dates"
                Element rootElement = document.createElement("dates");
                document.appendChild(rootElement);

                // Сохранение нового XML-файла
                TransformerFactory transformerFactory = TransformerFactory.newInstance();
                Transformer transformer = transformerFactory.newTransformer();
                DOMSource domSource = new DOMSource(document);
                StreamResult streamResult = new StreamResult(file);
                transformer.transform(domSource, streamResult);

                System.out.println("XML-файл успешно создан.");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return file;
    }
}
